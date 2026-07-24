import { Router, Response } from "express";
import { verifyFirebaseToken, verifyAppCheck, AuthenticatedRequest } from "../middlewares/auth.js";
import { LogGestureSchema, LogBatterySchema } from "../schemas/api.js";
import { db, FieldValue } from "../config/firebase.js";
import xss from "xss";

const router = Router();

router.post("/gesture", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const result = LogGestureSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.issues });

  try {
    const expireAt = new Date();
    expireAt.setDate(expireAt.getDate() + 7);
    const docRef = await db.collection("gesture_logs").add({
      gesture: xss(result.data.gesture),
      timestamp: FieldValue.serverTimestamp(),
      expireAt,
    });
    res.status(201).json({ success: true, id: docRef.id });
  } catch (err) {
    res.status(500).json({ error: "Internal Server Error" });
  }
});

router.post("/battery", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const result = LogBatterySchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.issues });

  try {
    const expireAt = new Date();
    expireAt.setDate(expireAt.getDate() + 7);
    const docRef = await db.collection("battery_logs").add({
      batteryLevel: result.data.batteryLevel,
      isWearDetected: String(result.data.isWearDetected),
      timestamp: FieldValue.serverTimestamp(),
      expireAt,
    });
    res.status(201).json({ success: true, id: docRef.id });
  } catch (err) {
    res.status(500).json({ error: "Internal Server Error" });
  }
});

router.get("/", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  try {
    const gesturesSnap = await db.collection("gesture_logs").orderBy("timestamp", "desc").limit(50).get();
    const batteriesSnap = await db.collection("battery_logs").orderBy("timestamp", "desc").limit(50).get();

    const logs = {
      gestures: gesturesSnap.docs.map(doc => ({ id: doc.id, ...doc.data() })),
      batteries: batteriesSnap.docs.map(doc => ({ id: doc.id, ...doc.data() })),
    };
    res.json(logs);
  } catch (err) {
    res.status(500).json({ error: "Internal Server Error" });
  }
});

export default router;
