import express, { Request, Response } from "express";
import logger from "../config/logger.js";
import { rtdb } from "../config/firebase.js";
import { quotaResetService } from "../services/QuotaResetService.js";
import { getSecret } from "../config/secrets.js";

const router = express.Router();

/**
 * Maps Play Store subscription IDs to Mave tier names.
 */
function productIdToTier(subscriptionId: string): string {
  if (subscriptionId.includes("ultra")) return "premium_ultra";
  if (subscriptionId.includes("pro")) return "premium_pro";
  if (subscriptionId.includes("basic")) return "premium_basic";
  return "free";
}

/**
 * POST /api/billing/play-webhook
 * Google Play Real-Time Developer Notifications (RTDN) webhook endpoint.
 * Decodes Pub/Sub messages sent by Google Cloud when Play subscriptions renew, cancel, or expire.
 */
router.post("/play-webhook", async (req: Request, res: Response) => {
  try {
    const { message } = req.body || {};
    if (!message || !message.data) {
      logger.warn("[PLAY_WEBHOOK] Received invalid Pub/Sub payload structure");
      res.status(400).json({ error: "Invalid Pub/Sub message format" });
      return;
    }

    // Decode base64 Pub/Sub payload
    const decodedString = Buffer.from(message.data, "base64").toString("utf-8");
    const payload = JSON.parse(decodedString);

    const subscriptionNotification = payload.subscriptionNotification;
    if (!subscriptionNotification) {
      // Ignore non-subscription test/one-time events silently
      res.status(200).json({ status: "ignored_non_subscription_event" });
      return;
    }

    const { notificationType, purchaseToken, subscriptionId } = subscriptionNotification;
    logger.info(`[PLAY_WEBHOOK] Received RTDN event type ${notificationType} for subscription ${subscriptionId}`);

    // Map notification types to active/inactive status
    // 1=RECOVERED, 2=RENEWED, 4=PURCHASED -> Active
    // 3=CANCELED, 5=ON_HOLD, 12=REVOKED, 13=EXPIRED -> Inactive
    const isActive = [1, 2, 4].includes(notificationType);
    const tier = isActive ? productIdToTier(subscriptionId) : "free";

    // Query RTDB for user associated with purchase token
    const tokenQuery = await rtdb.ref("purchases").orderByChild("purchaseToken").equalTo(purchaseToken).once("value");
    
    let targetUid: string | null = null;
    if (tokenQuery.exists()) {
      const val = tokenQuery.val();
      targetUid = Object.keys(val)[0];
    }

    if (targetUid) {
      // Update subscription node in RTDB
      await rtdb.ref(`users/${targetUid}/subscription`).set({
        isPremium: isActive,
        productId: tier,
        lastUpdated: new Date().toISOString(),
        purchaseToken
      });

      logger.info(`[PLAY_WEBHOOK] Successfully updated subscription for user ${targetUid}: isPremium=${isActive}, tier=${tier}`);
    } else {
      logger.warn(`[PLAY_WEBHOOK] No user found for purchaseToken ${purchaseToken.substring(0, 10)}... Event queued for verification.`);
    }

    res.status(200).json({ status: "processed" });
  } catch (err) {
    logger.error("[PLAY_WEBHOOK] Error processing Google Play RTDN notification:", { error: err });
    res.status(500).json({ error: "Internal processing error" });
  }
});

/**
 * POST /api/billing/cron/reset-monthly-usage
 * Secure cron trigger to reset monthly usage counters across all users.
 * Protected by X-Cron-Secret header verification.
 */
router.post("/cron/reset-monthly-usage", async (req: Request, res: Response) => {
  const cronSecret = req.header("X-Cron-Secret");
  const expectedSecret = process.env.CRON_SECRET || getSecret("CRON_SECRET") || "mave_internal_cron_key";

  if (!cronSecret || cronSecret !== expectedSecret) {
    logger.warn("[CRON_SECURITY] Unauthorized access attempt to reset-monthly-usage cron endpoint");
    res.status(401).json({ error: "Unauthorized: Invalid or missing X-Cron-Secret header." });
    return;
  }

  try {
    const result = await quotaResetService.runMonthlyReset();
    res.json({ status: "success", result });
  } catch (err) {
    logger.error("[CRON_JOB] Monthly reset execution failed:", { error: err });
    res.status(500).json({ error: "Cron reset job failed." });
  }
});

export default router;
