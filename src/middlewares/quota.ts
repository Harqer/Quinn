import { Response, NextFunction } from "express";
import { AuthenticatedRequest } from "./auth.js";
import { rtdb } from "../config/firebase.js";
import logger from "../config/logger.js";

export interface TierLimits {
  songs: number;
  podcasts: number;
  realtimeMinutes: number;
}

export const TIER_LIMITS: Record<string, TierLimits> = {
  free: { songs: 5, podcasts: 2, realtimeMinutes: 10 },
  premium_basic: { songs: 30, podcasts: 10, realtimeMinutes: 60 },
  premium_pro: { songs: 100, podcasts: 30, realtimeMinutes: 150 },
  premium_ultra: { songs: Infinity, podcasts: Infinity, realtimeMinutes: Infinity },
};

/**
 * Gets current YYYY-MM month key for billing period tracking.
 */
export function getMonthKey(): string {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  return `${year}-${month}`;
}

/**
 * Reads user's current subscription tier from RTDB or returns "free" default.
 */
export async function getUserTier(uid: string): Promise<string> {
  try {
    const snapshot = await rtdb.ref(`users/${uid}/subscription/productId`).get();
    if (snapshot.exists() && snapshot.val()) {
      return snapshot.val();
    }
  } catch (err) {
    logger.warn(`[QUOTA_MIDDLEWARE] Failed to fetch tier for ${uid}, defaulting to free:`, { error: err });
  }
  return "free";
}

/**
 * Middleware factory for server-side monthly quota enforcement.
 * Checks usage against user's subscription tier in RTDB before allowing AI generation requests.
 */
export const checkMonthlyQuota = (mediaType: "song" | "podcast" | "realtime") => {
  return async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
    const uid = req.user?.uid;
    if (!uid) {
      res.status(401).json({ error: { message: "Unauthorized: User context required for quota check.", code: "UNAUTHORIZED" } });
      return;
    }

    const monthKey = getMonthKey();
    const counterField = mediaType === "song" ? "songs_generated" : mediaType === "podcast" ? "podcast_eps_generated" : "realtime_minutes";

    try {
      const tier = await getUserTier(uid);
      const limits = TIER_LIMITS[tier] || TIER_LIMITS.free;
      const limit = mediaType === "song" ? limits.songs : mediaType === "podcast" ? limits.podcasts : limits.realtimeMinutes;

      if (limit === Infinity) {
        return next();
      }

      const usageSnapshot = await rtdb.ref(`users/${uid}/usage/${monthKey}/${counterField}`).get();
      const currentUsage = usageSnapshot.exists() ? Number(usageSnapshot.val()) || 0 : 0;

      if (currentUsage >= limit) {
        logger.warn(`[QUOTA_EXCEEDED] User ${uid} hit monthly ${mediaType} limit (${currentUsage}/${limit}) on tier ${tier}`);
        res.status(429).json({
          error: {
            message: `You've reached your monthly limit of ${limit} ${mediaType}s for your current plan. Upgrade to increase your limit.`,
            code: "MONTHLY_QUOTA_EXCEEDED",
            mediaType,
            currentUsage,
            limit,
            tier
          }
        });
        return;
      }

      next();
    } catch (err) {
      logger.error(`[QUOTA_MIDDLEWARE] Error enforcing monthly quota for ${uid}:`, { error: err });
      // In production, pass through on unhandled database error to avoid locking users out
      next();
    }
  };
};

/**
 * Server-side helper to atomically increment a user's monthly generation count.
 */
export async function incrementMonthlyUsage(uid: string, mediaType: "song" | "podcast" | "realtime", amount: number = 1): Promise<void> {
  const monthKey = getMonthKey();
  const counterField = mediaType === "song" ? "songs_generated" : mediaType === "podcast" ? "podcast_eps_generated" : "realtime_minutes";
  
  try {
    const counterRef = rtdb.ref(`users/${uid}/usage/${monthKey}/${counterField}`);
    await counterRef.transaction((currentValue) => {
      return (currentValue || 0) + amount;
    });
  } catch (err) {
    logger.error(`[QUOTA_INCREMENT_FAILED] Failed to increment ${mediaType} counter for ${uid}:`, { error: err });
  }
}
