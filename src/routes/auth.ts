import express from "express";
import { verifiedEmailService } from "../services/verified-email.js";
import logger from "../config/logger.js";

const router = express.Router();

router.post("/verified-email", async (req, res) => {
  try {
    const { responseJsonString, nonce } = req.body;
    
    if (!responseJsonString || !nonce) {
      res.status(400).json({ error: "Missing required fields" });
      return;
    }

    const result = await verifiedEmailService(responseJsonString, nonce);
    res.status(200).json(result);
  } catch (err: any) {
    logger.error("Verified Email error:", err);
    res.status(401).json({ error: err.message || "Failed to verify credential" });
  }
});

export default router;
