import { onRequest } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { getAuth } from "firebase-admin/auth";
import { getFirestore } from "firebase-admin/firestore";
import Stripe from "stripe";

const STRIPE_SECRET_KEY = defineSecret("STRIPE_SECRET_KEY");
const STRIPE_WEBHOOK_SECRET = defineSecret("STRIPE_WEBHOOK_SECRET");

export const stripeWebhook = onRequest(
  {
    secrets: [STRIPE_SECRET_KEY, STRIPE_WEBHOOK_SECRET],
    cors: true,
    timeoutSeconds: 300,
  },
  async (req, res) => {
    try {
      const stripe = new Stripe(STRIPE_SECRET_KEY.value(), { apiVersion: "2024-06-20" as any });
      const sig = req.headers["stripe-signature"];
      
      if (!sig) {
         res.status(400).json({ error: "Missing stripe-signature header" });
         return;
      }
      
      let event;
      try {
        event = stripe.webhooks.constructEvent(req.rawBody, sig, STRIPE_WEBHOOK_SECRET.value());
      } catch (err: any) {
        console.error("Webhook Error:", err.message);
        res.status(400).send(`Webhook Error: ${err.message}`);
        return;
      }
      
      console.log("Received Stripe Webhook Event:", event.type);
      
      if (
        event.type === "payment_intent.succeeded" ||
        event.type === "invoice.payment_succeeded" ||
        event.type === "checkout.session.completed"
      ) {
        const paymentObj = event.data.object as any;
        const customerId = paymentObj.customer;
        
        // We expect metadata.firebaseUid to be set when creating the customer or checkout session
        const userUid = paymentObj.metadata?.firebaseUid || paymentObj.client_reference_id;
        
        if (userUid) {
          const amount = (paymentObj.amount || paymentObj.amount_paid || paymentObj.amount_total || 0) / 100;
          
          const db = getFirestore();
          
          if (amount > 0) {
            await db.collection("users").doc(userUid).collection("payments").doc(paymentObj.id).set({
               amount: amount,
               currency: paymentObj.currency || "usd",
               status: "succeeded",
               createdAt: new Date()
            });
          }
          
          await db.collection("users").doc(userUid).set({
             stripeCustomerId: customerId
          }, { merge: true });
          
          await getAuth().setCustomUserClaims(userUid, { subscriptionTier: 'premium' });
          await db.collection('users').doc(userUid).update({ subscriptionTier: 'premium' });
        } else {
          console.warn("Could not determine userUid from payment object", paymentObj.id);
        }
      } else if (event.type === "customer.subscription.updated") {
        const sub = event.data.object as any;
        const db = getFirestore();
        const usersSnapshot = await db.collection("users").where("stripeCustomerId", "==", sub.customer).get();
        
        if (!usersSnapshot.empty) {
           const userDoc = usersSnapshot.docs[0];
           const status = sub.status; // active, past_due, canceled, unpaid
           const tier = status === "active" ? "premium" : "free";
           
           await getAuth().setCustomUserClaims(userDoc.id, { subscriptionTier: tier });
           await userDoc.ref.update({ subscriptionTier: tier });
        }
      } else if (event.type === "customer.subscription.deleted") {
        const sub = event.data.object as any;
        const db = getFirestore();
        const usersSnapshot = await db.collection("users").where("stripeCustomerId", "==", sub.customer).get();
        
        if (!usersSnapshot.empty) {
           const userDoc = usersSnapshot.docs[0];
           await getAuth().setCustomUserClaims(userDoc.id, { subscriptionTier: 'free' });
           await userDoc.ref.update({ subscriptionTier: 'free' });
        }
      }
      
      res.status(200).json({ received: true });
    } catch (error) {
      console.error(error);
      const errorMessage = error instanceof Error ? error.message : String(error);
      res.status(500).json({ error: errorMessage });
    }
  }
);

import { onCall, HttpsError } from "firebase-functions/v2/https";
import { processAutomatedPaymentKitesurf } from "./agents/kitesurfPaymentAgent";

export const executeAutomatedPaymentKitesurf = onCall(
  { cors: true },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "User must be authenticated to execute Cloudflare Kitesurf automated payment");
    }

    const { planId = "premium_monthly", amountCents = 999, currency = "USD" } = request.data || {};

    const result = await processAutomatedPaymentKitesurf({
      userId: request.auth.uid,
      planId,
      amountCents,
      currency,
    });

    if (!result.success) {
      throw new HttpsError("internal", result.error || "Automated payment failed via Cloudflare Kitesurf");
    }

    return result;
  }
);

