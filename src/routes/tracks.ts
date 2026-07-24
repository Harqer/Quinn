import { Router } from "express";
import { trackRepository } from "../repositories/TrackRepository.js";
import logger from "../config/logger.js";

const router = Router();

router.get("/:id", async (req, res) => {
  try {
    const track = await trackRepository.getTrackById(req.params.id);
    if (!track) {
      return res.status(404).json({ error: "Track not found" });
    }
    res.json(track);
  } catch (err) {
    logger.error("[TRACKS] Error fetching track", { error: err });
    res.status(500).json({ error: "Internal Server Error" });
  }
});

export default router;
