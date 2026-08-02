import re
import os

with open("server.ts", "r") as f:
    content = f.read()

# 1. Create directories
os.makedirs("server/middleware", exist_ok=True)
os.makedirs("server/routes", exist_ok=True)

# 2. Extract verifyFirebaseToken and remove local-dev-user
auth_middleware = """import { Request, Response, NextFunction } from "express";
import { getAuth } from "firebase-admin/auth";

export interface AuthenticatedRequest extends Request {
  user?: any;
}

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
    f.write(auth_middleware)

rateLimit_middleware = """import { rateLimit } from "express-rate-limit";

export const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  limit: 100,
  standardHeaders: "draft-7",
  legacyHeaders: false,
  message: { error: "Too many requests from this IP, please try again after 15 minutes." },
});
"""
with open("server/middleware/rateLimit.ts", "w") as f:
    f.write(rateLimit_middleware)

# Now I'll create a new server.ts that will be smaller, and I will manually move the routes over to new files.
# Or rather, maybe I should do it in a simpler way: copy server.ts and rip out parts.
print("Done creating basic middlewares")
