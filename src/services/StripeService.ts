import Stripe from "stripe";
import { getSecret } from "../config/secrets.js";
import { getFirestore } from "firebase-admin/firestore";
import logger from "../config/logger.js";

export class StripeService {
  private stripeClient: Stripe | null = null;

  private getStripe(): Stripe {
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
}

export const stripeService = new StripeService();
