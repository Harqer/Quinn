import Stripe from "stripe";
import { getSecret } from "../config/secrets.js";
import { getFirestore } from "firebase-admin/firestore";
import { rtdb } from "../config/firebase.js";
import logger from "../config/logger.js";

export class StripeService {
  private stripeClient: Stripe | null = null;

  public getStripe(): Stripe {
    if (!this.stripeClient) {
      const key = getSecret("STRIPE_SECRET_KEY");
      if (!key) {
        throw new Error("STRIPE_SECRET_KEY not found in secrets");
      }
      this.stripeClient = new Stripe(key);
    }
    return this.stripeClient;
  }

  // Helper to get or create a Stripe customer for a Firebase user
  async getOrCreateCustomer(uid: string, email?: string): Promise<string> {
    const db = getFirestore();
    const userRef = db.collection("users").doc(uid);
    const userDoc = await userRef.get();
    
    if (userDoc.exists && userDoc.data()?.stripeCustomerId) {
      return userDoc.data()!.stripeCustomerId;
    }
    
    const stripe = this.getStripe();
    const customer = await stripe.customers.create({
      metadata: { firebaseUID: uid },
      email: email || undefined,
    });
    
    await userRef.set({ stripeCustomerId: customer.id }, { merge: true });
    return customer.id;
  }

  // Helper to get or create a standard Mave Premium price
  async getOrCreatePrice(tier: "basic" | "pro" | "ultra" = "basic"): Promise<string> {
    const stripe = this.getStripe();
    
    const tierConfig = {
      basic: { name: "Lyria Basic Creator", amount: 2000 },
      pro: { name: "Lyria Pro Studio", amount: 5000 },
      ultra: { name: "Lyria Ultra Unlimited", amount: 10000 },
    };
    
    const config = tierConfig[tier] || tierConfig.basic;
    
    // Search for the product
    const products = await stripe.products.search({ query: `name:"${config.name}"` });
    let productId;
    
    if (products.data.length > 0) {
      productId = products.data[0].id;
    } else {
      const product = await stripe.products.create({
        name: config.name,
        description: `Lyria ${tier.charAt(0).toUpperCase() + tier.slice(1)} subscription`,
      });
      productId = product.id;
    }
    
    // Search for price
    const prices = await stripe.prices.list({ product: productId, active: true, limit: 1 });
    if (prices.data.length > 0 && prices.data[0].unit_amount === config.amount) {
      return prices.data[0].id;
    }
    
    // Create the price
    const price = await stripe.prices.create({
      product: productId,
      unit_amount: config.amount,
      currency: "usd",
      recurring: { interval: "month" },
    });
    
    return price.id;
  }

  async createCheckoutSession(uid: string, email: string | undefined, tier: string, returnUrl: string): Promise<string> {
    const customerId = await this.getOrCreateCustomer(uid, email);
    const priceId = await this.getOrCreatePrice(tier as "basic" | "pro" | "ultra");
    const stripe = this.getStripe();
    
    const session = await stripe.checkout.sessions.create({
      customer: customerId,
      metadata: { firebaseUID: uid, tier },
      payment_method_types: ["card"],
      mode: "subscription",
      line_items: [
        {
          price: priceId,
          quantity: 1,
        },
      ],
      success_url: `${returnUrl}?session_id={CHECKOUT_SESSION_ID}`,
      cancel_url: returnUrl,
    });
    
    if (!session.url) {
      throw new Error("Failed to generate checkout session URL");
    }

    return session.url;
  }

  async createPortalSession(uid: string, email: string | undefined, returnUrl: string): Promise<string> {
    const customerId = await this.getOrCreateCustomer(uid, email);
    const stripe = this.getStripe();
    
    const session = await stripe.billingPortal.sessions.create({
      customer: customerId,
      return_url: returnUrl,
    });
    
    return session.url;
  }

  /**
   * Helper to map a Stripe unit amount (in cents) to a Mave product tier string.
   */
  private amountToTier(amountInCents?: number | null): string {
    if (!amountInCents) return "free";
    if (amountInCents >= 10000) return "premium_ultra";
    if (amountInCents >= 5000) return "premium_pro";
    if (amountInCents >= 2000) return "premium_basic";
    return "free";
  }

  /**
   * Processes verified Stripe webhook events and updates database state.
   * Handles checkout success, subscription upgrades/downgrades, cancellations, and payment failures.
   */
  async handleWebhookEvent(event: Stripe.Event): Promise<void> {
    logger.info(`[STRIPE_WEBHOOK] Processing event type: ${event.type}`);

    switch (event.type) {
      case "checkout.session.completed": {
        const session = event.data.object as Stripe.Checkout.Session;
        const uid = session.metadata?.firebaseUID;
        const tier = session.metadata?.tier || "basic";
        
        if (uid) {
          await this.updateUserSubscription(uid, true, `premium_${tier}`, session.customer as string);
          await this.recordPaymentHistory(uid, (session.amount_total || 0) / 100, session.currency || "usd", "succeeded", session.id);
        }
        break;
      }

      case "customer.subscription.updated": {
        const subscription = event.data.object as Stripe.Subscription;
        const customerId = subscription.customer as string;
        const uid = await this.findUidByCustomerId(customerId);

        if (uid) {
          const isActive = ["active", "trialing"].includes(subscription.status);
          const priceAmount = subscription.items.data[0]?.price?.unit_amount;
          const tier = isActive ? this.amountToTier(priceAmount) : "free";

          await this.updateUserSubscription(uid, isActive, tier, customerId);
          logger.info(`[STRIPE_WEBHOOK] Subscription updated for user ${uid}: status=${subscription.status}, tier=${tier}`);
        }
        break;
      }

      case "customer.subscription.deleted": {
        const subscription = event.data.object as Stripe.Subscription;
        const customerId = subscription.customer as string;
        const uid = await this.findUidByCustomerId(customerId);

        if (uid) {
          // Automatic downgrade to Free plan upon cancellation / period expiration
          await this.updateUserSubscription(uid, false, "free", customerId);
          logger.info(`[STRIPE_WEBHOOK] Subscription deleted. User ${uid} downgraded to Free tier.`);
        }
        break;
      }

      case "invoice.payment_failed": {
        const invoice = event.data.object as Stripe.Invoice;
        const customerId = invoice.customer as string;
        const uid = await this.findUidByCustomerId(customerId);

        if (uid) {
          await this.recordPaymentHistory(uid, (invoice.amount_due || 0) / 100, invoice.currency || "usd", "failed", invoice.id);
          // If invoice attempt count > 2, downgrade to free until payment is resolved
          if ((invoice.attempt_count || 1) >= 2) {
            await this.updateUserSubscription(uid, false, "free", customerId);
            logger.warn(`[STRIPE_WEBHOOK] Payment failed repeatedly for ${uid}. Account downgraded to Free tier.`);
          }
        }
        break;
      }
    }
  }

  /** Updates user subscription status in RTDB and Firestore */
  private async updateUserSubscription(uid: string, isPremium: boolean, productId: string, stripeCustomerId: string): Promise<void> {
    try {
      // Update RTDB node
      await rtdb.ref(`users/${uid}/subscription`).set({
        isPremium,
        productId,
        stripeCustomerId,
        updatedAt: new Date().toISOString()
      });

      // Update Firestore user doc
      const db = getFirestore();
      await db.collection("users").doc(uid).set({
        isPremium,
        subscriptionTier: productId,
        stripeCustomerId,
        updatedAt: new Date().toISOString()
      }, { merge: true });

      logger.info(`[STRIPE_SERVICE] Successfully updated subscription for ${uid}: isPremium=${isPremium}, product=${productId}`);
    } catch (err) {
      logger.error(`[STRIPE_SERVICE] Failed updating user subscription for ${uid}:`, { error: err });
    }
  }

  /** Find UID from Firestore using Stripe customer ID */
  private async findUidByCustomerId(stripeCustomerId: string): Promise<string | null> {
    try {
      const db = getFirestore();
      const snapshot = await db.collection("users").where("stripeCustomerId", "==", stripeCustomerId).limit(1).get();
      if (!snapshot.empty) {
        return snapshot.docs[0].id;
      }
    } catch (err) {
      logger.warn(`[STRIPE_SERVICE] Failed finding UID for customer ${stripeCustomerId}:`, { error: err });
    }
    return null;
  }

  /** Inserts payment record into Firestore payment history collection */
  private async recordPaymentHistory(uid: string, amount: number, currency: string, status: string, invoiceId: string): Promise<void> {
    try {
      const db = getFirestore();
      await db.collection("users").doc(uid).collection("paymentHistory").add({
        amount,
        currency,
        status,
        stripeInvoiceId: invoiceId,
        createdAt: new Date().toISOString()
      });
    } catch (err) {
      logger.error(`[STRIPE_SERVICE] Failed recording payment history for ${uid}:`, { error: err });
    }
  }
}

export const stripeService = new StripeService();
