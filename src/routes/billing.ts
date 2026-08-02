import express, { Request, Response } from "express";
import crypto from "crypto";
import logger from "../config/logger.js";
import { rtdb } from "../config/firebase.js";
import { quotaResetService } from "../services/QuotaResetService.js";
import { getSecret } from "../config/secrets.js";

const router = express.Router();

function productIdToTier(subscriptionId: string): string {
  if (subscriptionId.includes("ultra")) return "premium_ultra";
  if (subscriptionId.includes("pro")) return "premium_pro";
  if (subscriptionId.includes("basic")) return "premium_basic";
  return "free";
}

/**
 * POST /api/billing/play-webhook
 */
router.post("/play-webhook", async (req: Request, res: Response) => {
  try {
    const authHeader = req.header("Authorization");
    const expectedWebhookToken = process.env.PUBSUB_VERIFICATION_TOKEN || getSecret("PUBSUB_VERIFICATION_TOKEN");
    
    if (expectedWebhookToken && (!authHeader || authHeader !== `Bearer ${expectedWebhookToken}`)) {
      logger.warn("[PLAY_WEBHOOK] Unauthorized Pub/Sub webhook access attempt");
      res.status(401).json({ error: "Unauthorized" });
      return;
    }

    const { message } = req.body || {};
    if (!message || !message.data) {
      logger.warn("[PLAY_WEBHOOK] Received invalid Pub/Sub payload structure");
      res.status(400).json({ error: "Invalid Pub/Sub message format" });
      return;
    }

    const decodedString = Buffer.from(message.data, "base64").toString("utf-8");
    const payload = JSON.parse(decodedString);
    const subscriptionNotification = payload.subscriptionNotification;

    if (!subscriptionNotification) {
      res.status(200).json({ status: "ignored_non_subscription_event" });
      return;
    }

    const { notificationType, purchaseToken, subscriptionId } = subscriptionNotification;
    logger.info(`[PLAY_WEBHOOK] Received RTDN event type ${notificationType} for subscription ${subscriptionId}`);

    // Include 6 (IN_GRACE_PERIOD) and 7 (RESTARTED) as active states
    const isActive = [1, 2, 4, 6, 7].includes(notificationType);
    const tier = isActive ? productIdToTier(subscriptionId) : "free";

    const tokenSnapshot = await rtdb.ref(`purchases/${purchaseToken}`).once("value");
    let targetUid: string | null = tokenSnapshot.exists() ? tokenSnapshot.val()?.uid : null;

    if (targetUid) {
      await rtdb.ref(`users/${targetUid}/subscription`).set({
        isPremium: isActive,
        productId: tier,
        lastUpdated: new Date().toISOString(),
        purchaseToken
      });

      logger.info(`[PLAY_WEBHOOK] Updated subscription for user ${targetUid}: isPremium=${isActive}, tier=${tier}`);
    } else {
      logger.warn(`[PLAY_WEBHOOK] No user mapping found for purchaseToken ${purchaseToken.substring(0, 10)}...`);
    }

    res.status(200).json({ status: "processed" });
  } catch (err) {
    logger.error("[PLAY_WEBHOOK] Error processing Google Play RTDN notification:", { error: err });
    res.status(500).json({ error: "Internal processing error" });
  }
});

/**
 * POST /api/billing/cron/reset-monthly-usage
 */
router.post("/cron/reset-monthly-usage", async (req: Request, res: Response) => {
  const cronSecret = req.header("X-Cron-Secret") || "";
  const expectedSecret = process.env.CRON_SECRET || getSecret("CRON_SECRET") || "";

  if (!expectedSecret) {
    logger.error("[CRON_SECURITY] CRON_SECRET is not configured on the server.");
    res.status(500).json({ error: "Cron service misconfigured." });
    return;
  }

  const cronBuffer = Buffer.from(cronSecret);
  const expectedBuffer = Buffer.from(expectedSecret);

  if (cronBuffer.length !== expectedBuffer.length || !crypto.timingSafeEqual(cronBuffer, expectedBuffer)) {
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
