import { Request, Response, NextFunction } from "express";
import { auth, appCheck } from "../config/firebase.js";
import logger from "../config/logger.js";
import { quotaRepository } from "../repositories/QuotaRepository.js";

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
    // Assign a guest UID for "Audio First" experience
    const guestId = req.header("X-Guest-ID") || `guest_${Math.random().toString(36).substring(7)}`;
    req.user = { uid: guestId, isGuest: true };
    next();
    return;
  }

  const idToken = authHeader.split("Bearer ")[1];
  try {
    const decodedToken = await auth.verifyIdToken(idToken);
    req.user = { ...decodedToken, isGuest: false };
    next();
  } catch (err) {
    // Fallback to guest if token is invalid but we want "Audio First"
    const guestId = `guest_${Math.random().toString(36).substring(7)}`;
    req.user = { uid: guestId, isGuest: true };
    next();
  }
};

export const optionalFirebaseToken = async (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    // Audio First: Assign guest UID
    const guestId = req.header("X-Guest-ID") || `guest_${Math.random().toString(36).substring(7)}`;
    req.user = { uid: guestId, isGuest: true };
    next();
    return;
  }

  const idToken = authHeader.split("Bearer ")[1];
  try {
    const decodedToken = await auth.verifyIdToken(idToken);
    req.user = { ...decodedToken, isGuest: false };
    next();
  } catch (err) {
    const guestId = `guest_${Math.random().toString(36).substring(7)}`;
    req.user = { uid: guestId, isGuest: true };
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
    if (process.env.NODE_ENV === "production") {
      res.status(401).json({ error: "Unauthorized: Missing App Check token." });
      return;
    } else {
      logger.warn("[APP_CHECK] Warning: Missing App Check token in non-production. Proceeding.");
      next();
      return;
    }
  }

  try {
    const appCheckResponse = await appCheck.verifyToken(token);
    (req as AuthenticatedRequest).appCheck = appCheckResponse;
    next();
  } catch (err: any) {
    if (process.env.NODE_ENV === "production") {
      logger.error("[APP_CHECK] Validation failed:", { error: err.message || err });
      res.status(401).json({ error: "Unauthorized: Invalid App Check token." });
    } else {
      logger.warn("[APP_CHECK] Warning: App Check token validation failed in non-production. Proceeding:", { error: err.message || err });
      next();
    }
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

  try {
    const data = await quotaRepository.getQuota(uid);
    let currentQuota = { count: 0, lastUpdated: today };

    if (data && data.lastUpdated === today) {
      currentQuota = { count: data.count || 0, lastUpdated: today };
    }

    const DAILY_LIMIT = req.user?.isGuest ? 5 : 50;
    if (currentQuota.count >= DAILY_LIMIT) {
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

    currentQuota.count += 1;
    await quotaRepository.updateQuota(uid, currentQuota);
    next();
  } catch (err) {
    logger.error("[QUOTA] Error checking daily quota limits:", { error: err });
    next(); // Gracefully continue
  }
};
