import { Router, Response } from "express";
import { verifyFirebaseToken, AuthenticatedRequest } from "../middlewares/auth.js";
import { userRepository } from "../repositories/UserRepository.js";
import { credentialVerifier } from "../services/CredentialVerifier.js";
import { auth } from "../config/firebase.js";
import logger from "../config/logger.js";
import { z } from "zod";

const router = Router();

const ProfileSchema = z.object({
  name: z.string().min(1),
  birthday: z.string().optional(),
  gender: z.string().optional(),
});

const PreferencesSchema = z.object({
  artists: z.array(z.string()).min(3),
});

router.post("/profile", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  const result = ProfileSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.issues });

  try {
    await userRepository.createProfile(req.user!.uid, {
      ...result.data,
      email: req.user!.email,
    });
    res.status(200).json({ success: true, message: "Profile created" });
  } catch (err) {
    logger.error("Failed to create user profile", { error: err });
    res.status(500).json({ error: "Failed to create profile" });
  }
});

router.post("/preferences", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  const result = PreferencesSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.issues });

  try {
    await userRepository.updatePreferences(req.user!.uid, result.data.artists);
    res.status(200).json({ success: true, message: "Preferences updated" });
  } catch (err) {
    logger.error("Failed to update user preferences", { error: err });
    res.status(500).json({ error: "Failed to update preferences" });
  }
});

router.post("/verify-credential", async (req: any, res: any) => {
  const { credentialJson, nonce } = req.body;
  if (!credentialJson || !nonce) return res.status(400).json({ error: "Missing credential data or nonce" });

  try {
    // 1. Verify Digital Credential
    const verified = await credentialVerifier.verifyGoogleCredential(credentialJson, nonce);

    // 2. Map to Firebase User
    let userRecord;
    try {
      userRecord = await auth.getUserByEmail(verified.email);
    } catch (err: any) {
      if (err.code === "auth/user-not-found") {
        // Create new verified user
        userRecord = await auth.createUser({
          email: verified.email,
          displayName: verified.name,
          emailVerified: true
        });
        logger.info("[AUTH] Created new verified user", { uid: userRecord.uid });
      } else throw err;
    }

    // 3. Generate Custom Token
    const customToken = await auth.createCustomToken(userRecord.uid);
    res.json({ success: true, token: customToken, user: { email: verified.email, name: verified.name } });
  } catch (err: any) {
    logger.error("[AUTH] Credential Verification Failed", { error: err.message });
    res.status(401).json({ error: "Verification Failed", details: err.message });
  }
});

export default router;
