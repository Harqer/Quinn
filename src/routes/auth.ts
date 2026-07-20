import { Router, Response } from "express";
import { verifyFirebaseToken, AuthenticatedRequest } from "../middlewares/auth.js";
import { userRepository } from "../repositories/UserRepository.js";
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

export default router;
