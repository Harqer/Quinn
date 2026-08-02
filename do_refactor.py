import re
import os

with open("server.ts", "r", encoding="utf-8") as f:
    content = f.read()

# 1. Middlewares
os.makedirs("server/middleware", exist_ok=True)
os.makedirs("server/routes", exist_ok=True)

auth_ts = """import { Request, Response, NextFunction } from "express";
import { getAuth } from "firebase-admin/auth";

export interface AuthenticatedRequest extends Request {
  user?: any;
}

const isGcpEnvironment = process.env.NODE_ENV === "production" || !!process.env.K_SERVICE || !!process.env.GOOGLE_APPLICATION_CREDENTIALS;

export const verifyFirebaseToken = async (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
) => {
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader.startsWith("Bearer ")) {
    const idToken = authHeader.split("Bearer ")[1];
    if (idToken) {
      try {
        const decodedToken = await getAuth().verifyIdToken(idToken);
        req.user = decodedToken;
        next();
        return;
      } catch (err) {
        res.status(401).json({ error: "Unauthorized: Invalid Firebase token." });
        return;
      }
    }
  }

  res.status(401).json({ error: "Unauthorized: Missing Firebase token." });
  return;
};
"""
with open("server/middleware/auth.ts", "w") as f:
    f.write(auth_ts)

rate_limit_ts = """import { rateLimit } from "express-rate-limit";

export const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  limit: 100,
  standardHeaders: "draft-7",
  legacyHeaders: false,
  message: { error: "Too many requests from this IP, please try again after 15 minutes." },
});
"""
with open("server/middleware/rateLimit.ts", "w") as f:
    f.write(rate_limit_ts)

app_check_ts = """import { Request, Response, NextFunction } from "express";
import { getAppCheck } from "firebase-admin/app-check";

export const verifyAppCheck = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const appCheckToken = req.header("X-Firebase-AppCheck");
  if (!appCheckToken) {
    if (process.env.NODE_ENV === "production") {
      res.status(401).json({ error: "Unauthorized: Missing App Check token." });
      return;
    } else {
      next();
      return;
    }
  }

  try {
    const appCheckResponse = await getAppCheck().verifyToken(appCheckToken);
    (req as any).appCheck = appCheckResponse;
    next();
  } catch (err: any) {
    if (process.env.NODE_ENV === "production") {
      res.status(401).json({ error: "Unauthorized: Invalid App Check token." });
    } else {
      next();
    }
  }
};
"""
with open("server/middleware/appCheck.ts", "w") as f:
    f.write(app_check_ts)

quota_ts = """import { Response, NextFunction } from "express";
import { getFirestore, FieldValue } from "firebase-admin/firestore";
import { AuthenticatedRequest } from "./auth.js";

export const checkDailyQuota = async (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
) => {
  const uid = req.user?.uid;
  if (!uid) {
    res.status(401).json({ error: "Unauthorized" });
    return;
  }
  const today = new Date().toISOString().split("T")[0];
  
  try {
    const db = getFirestore();
    const quotaDocRef = db.collection("user_quotas").doc(uid);
    const quotaSnap = await quotaDocRef.get();
    let currentQuota = { count: 0, lastUpdated: today };
    
    if (quotaSnap.exists) {
      const data = quotaSnap.data() as any;
      if (data.lastUpdated === today) {
        currentQuota = { count: data.count || 0, lastUpdated: today };
      }
    }

    const DAILY_LIMIT = 50;
    if (currentQuota.count >= DAILY_LIMIT) {
      res.status(429).json({
        error: {
          message: "Daily generation limit reached today. Please try again tomorrow.",
          code: "QUOTA_EXCEEDED"
        }
      });
      return;
    }

    currentQuota.count += 1;
    await quotaDocRef.set(currentQuota);
    next();
  } catch (err) {
    next();
  }
};
"""
with open("server/middleware/quota.ts", "w") as f:
    f.write(quota_ts)


# Now extracting routes. This is tricky because the file has 2000 lines.
# I will use a simple heuristic: split the file on `app.use` or `app.post`/`app.get` etc, but it's better to just write a simple AST or Regex based parser.
# Or better yet, we can execute a script that uses typescript compiler API!
