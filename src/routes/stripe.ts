import express, { Request, Response, NextFunction } from "express";
import { optionalFirebaseToken, AuthenticatedRequest } from "../middlewares/auth.js";
import logger from "../config/logger.js";
import { stripeService } from "../services/StripeService.js";
import { getSecret } from "../config/secrets.js";

const router = express.Router();

router.post("/checkout-session", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const uid = req.user?.uid;
    const email = req.user?.email;
    if (!uid) {
      return res.status(401).json({ error: "Unauthorized" });
    }
    
    const { returnUrl, tier } = req.body;
    const url = await stripeService.createCheckoutSession(uid, email, tier || "basic", returnUrl || "http://localhost:5173");
    
    return res.json({ url });
  } catch (err) {
    logger.error("Failed to create checkout session", { error: err });
    next(err);
  }
});

router.post("/portal-session", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const uid = req.user?.uid;
    const email = req.user?.email;
    if (!uid) {
      return res.status(401).json({ error: "Unauthorized" });
    }
    
    const { returnUrl } = req.body;
    const fallbackUrl = returnUrl || req.headers.origin || "http://localhost:5173";
    const url = await stripeService.createPortalSession(uid, email, fallbackUrl);
    
    return res.json({ url });
  } catch (err) {
    logger.error("Failed to create portal session", { error: err });
    next(err);
  }
});

/**
 * POST /api/stripe/webhook
 * Handles incoming raw Stripe webhook notifications.
 * Validates Stripe signature using raw request body.
 */
router.post(
  "/webhook",
  express.raw({ type: "application/json" }),
  async (req: Request, res: Response) => {
    const signature = req.headers["stripe-signature"];
    const webhookSecret = process.env.STRIPE_WEBHOOK_SECRET || getSecret("STRIPE_WEBHOOK_SECRET");

    if (!signature || !webhookSecret) {
      logger.warn("[STRIPE_WEBHOOK] Missing Stripe signature or webhook secret");
      return res.status(400).send("Webhook Error: Signature or secret missing");
    }

    try {
      const stripe = stripeService.getStripe();
      const event = stripe.webhooks.constructEvent(req.body, signature, webhookSecret);

      await stripeService.handleWebhookEvent(event);
      res.json({ received: true });
    } catch (err: any) {
      logger.error("[STRIPE_WEBHOOK] Webhook signature verification or execution failed:", { error: err.message });
      res.status(400).send(`Webhook Error: ${err.message}`);
    }
  }
);

export default router;
