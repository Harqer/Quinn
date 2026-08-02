import { Request, Response, NextFunction } from "express";
import { auth, appCheck } from "../config/firebase.js";
import { getRedis } from "../config/redis.js";
import logger from "../config/logger.js";

export interface AuthenticatedRequest extends Request {
  user?: any;
  appCheck?: any;
}

export const verifyFirebaseToken = async (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    res.status(401).json({ error: "Unauthorized: Missing or invalid Authorization header." });
    return;
  }

  const idToken = authHeader.split("Bearer ")[1];
  try {
    const decodedToken = await auth.verifyIdToken(idToken);
    req.user = decodedToken;
    next();
  } catch (err) {
    logger.warn("Invalid Firebase token provided:", { error: err });
    res.status(401).json({ error: "Unauthorized: Invalid session." });
  }
};

export const optionalFirebaseToken = async (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    req.user = null;
    next();
    return;
  }

  const idToken = authHeader.split("Bearer ")[1];
  try {
    const decodedToken = await auth.verifyIdToken(idToken);
    const isAnonymous = decodedToken.firebase?.sign_in_provider === "anonymous";
    req.user = { ...decodedToken, isGuest: isAnonymous };
    next();
  } catch (err) {
    logger.warn("Invalid Firebase token provided in optional auth:", { error: err });
    req.user = null;
    next();
  }
};

export const verifyAppCheck = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const token = req.header("X-Firebase-AppCheck");

  if (!token) {
    if (process.env.NODE_ENV !== "production") {
      logger.warn("[APP_CHECK] Missing token. Bypassing in development mode.");
      return next();
    }
    res.status(401).json({ error: "Unauthorized: Missing App Check token." });
    return;
  }

  try {
    const appCheckResponse = await appCheck.verifyToken(token);
    (req as AuthenticatedRequest).appCheck = appCheckResponse;
    next();
  } catch (err: any) {
    logger.error("[APP_CHECK] App Check token validation failed:", { error: err.message || err });
    if (process.env.NODE_ENV !== "production") {
      logger.warn("[APP_CHECK] Invalid token. Bypassing in development mode.");
      return next();
    }
    res.status(401).json({ error: "Unauthorized: Invalid App Check token." });
  }
};

export const checkDailyQuota = async (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
) => {
  const uid = req.user?.uid;
  if (!uid) {
    res.status(401).json({ error: "Unauthorized: User context required for quota check." });
    return;
  }

  const today = new Date().toISOString().split("T")[0]; // YYYY-MM-DD
  const redis = getRedis();

  try {
    if (!redis) {
      // If Redis is not configured, we allow requests to pass (or we could deny them).
      logger.warn("[QUOTA] Redis unavailable, bypassing quota check.");
      return next();
    }

    const quotaKey = `quota:${uid}:${today}`;
    const currentCount = await redis.get(quotaKey);
    const count = currentCount ? parseInt(currentCount, 10) : 0;

    const DAILY_LIMIT = req.user?.isGuest ? 50 : 5000;
    
    if (count >= DAILY_LIMIT) {
      logger.warn(`[QUOTA] User exceeded daily generation limit.`, { uid, limit: DAILY_LIMIT, isGuest: req.user?.isGuest });
      res.status(429).json({
        error: {
          message: req.user?.isGuest
            ? "Guest limit reached. Sign up for 50+ generations per day!"
            : "Daily generation limit reached today. Please try again tomorrow.",
          code: "QUOTA_EXCEEDED"
        }
      });
      return;
    }

    // Increment and set expiry for 24 hours if it's the first request
    const pipeline = redis.pipeline();
    pipeline.incr(quotaKey);
    if (count === 0) {
      pipeline.expire(quotaKey, 86400); // 24 hours
    }
    await pipeline.exec();
    
    next();
  } catch (err) {
    logger.error("[QUOTA] Error checking daily quota limits:", { error: err });
    // In production, log quota service unavailability and allow request to pass to not break app
    next();
  }
};
