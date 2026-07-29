import express, { Response, NextFunction } from "express";
import { optionalFirebaseToken, AuthenticatedRequest } from "../middlewares/auth.js";
import logger from "../config/logger.js";
import { stripeService } from "../services/StripeService.js";

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

export default router;
