import { Router, Response } from "express";
import { verifyFirebaseToken, AuthenticatedRequest } from "../middlewares/auth.js";
import { LogGestureSchema, LogBatterySchema } from "../schemas/api.js";
import { logRepository } from "../repositories/LogRepository.js";
import xss from "xss";

const router = Router();

router.post("/gesture", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  const result = LogGestureSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.errors });

  try {
    const id = await logRepository.logGesture(xss(result.data.gesture));
    res.status(201).json({ success: true, id });
  } catch (err) {
    res.status(500).json({ error: "Internal Server Error" });
  }
});

router.post("/battery", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  const result = LogBatterySchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.errors });

  try {
    const id = await logRepository.logBattery(result.data.batteryLevel, String(result.data.isWearDetected));
    res.status(201).json({ success: true, id });
  } catch (err) {
    res.status(500).json({ error: "Internal Server Error" });
  }
});

router.get("/", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  try {
    const logs = await logRepository.getRecentLogs();
    res.json(logs);
  } catch (err) {
    res.status(500).json({ error: "Internal Server Error" });
  }
});

export default router;
