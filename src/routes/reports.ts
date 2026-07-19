import { Router, Response } from "express";
import { verifyFirebaseToken, AuthenticatedRequest, verifyAppCheck } from "../middlewares/auth.js";
import { reportRepository } from "../repositories/ReportRepository.js";
import { z } from "zod";
import logger from "../config/logger.js";

const router = Router();

const ReportSchema = z.object({
  targetId: z.string(),
  targetType: z.enum(["track", "comment", "user"]),
  reason: z.string(),
  description: z.string().optional(),
});

router.post("/", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const result = ReportSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.errors });

  try {
    const reportId = await reportRepository.createReport({
      reporterId: req.user!.uid,
      ...result.data,
    });
    res.status(201).json({ id: reportId, message: "Report submitted successfully" });
  } catch (err) {
    logger.error("Failed to submit report", { error: err });
    res.status(500).json({ error: "Failed to submit report" });
  }
});

export default router;
