import { Router, Response } from "express";
import { optionalFirebaseToken, verifyFirebaseToken, AuthenticatedRequest, checkDailyQuota, verifyAppCheck } from "../middlewares/auth.js";
import { auth, appCheck } from "../config/firebase.js";
import { GenerateSchema, ShareVibeSchema } from "../schemas/api.js";
import { WebSocketServer, WebSocket } from "ws";
import logger from "../config/logger.js";
import { musicService } from "../services/MusicService.js";
import { narrativeService } from "../services/NarrativeService.js";
import { InstrumentationService } from "../services/InstrumentationService.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getAi } from "../services/ai.js";

const router = Router();
const instrumentationService = new InstrumentationService();

router.post("/generate", optionalFirebaseToken, verifyAppCheck, checkDailyQuota, async (req: AuthenticatedRequest, res: Response) => {
  const result = GenerateSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.issues });

  try {
    const data = await musicService.generateMusicDirectly(result.data.image, result.data.type, req.body.variant);
    res.json(data);
  } catch (err) {
    logger.error("Generation Failed", { error: err });
    res.status(500).json({ error: "Generation Failed" });
  }
});

router.post("/podcast/generate", optionalFirebaseToken, verifyAppCheck, checkDailyQuota, async (req: AuthenticatedRequest, res: Response) => {
  const { prompt, voice } = req.body;
  const locale = req.headers["accept-language"] || "en";
  if (!prompt || typeof prompt !== "string" || prompt.trim().length === 0) {
    return res.status(400).json({ error: "Valid prompt string is required" });
  }

  res.setHeader("Content-Type", "text/event-stream");
  res.setHeader("Cache-Control", "no-cache");
  res.setHeader("Connection", "keep-alive");

  try {
    await narrativeService.generateStream(prompt.trim(), "podcast", voice || "AOEDE", locale, res);
  } catch (err) {
    logger.error("[PODCAST_ROUTE] Generation Stream Failed", { error: err });
    res.write(`data: ${JSON.stringify({ type: 'error', error: "Podcast Generation Failed" })}\n\n`);
    res.end();
  }
});

router.post("/audiobook/generate", optionalFirebaseToken, verifyAppCheck, checkDailyQuota, async (req: AuthenticatedRequest, res: Response) => {
  const { prompt, voice } = req.body;
  const locale = req.headers["accept-language"] || "en";
  if (!prompt || typeof prompt !== "string" || prompt.trim().length === 0) {
    return res.status(400).json({ error: "Valid prompt string is required" });
  }

  try {
    const audiobookTrack = await narrativeService.generateFromPrompt(prompt.trim(), "audiobook", voice || "KORE", locale);
    res.json(audiobookTrack);
  } catch (err) {
    logger.error("[AUDIOBOOK_ROUTE] Generation Failed", { error: err });
    res.status(500).json({ error: "Audiobook Generation Failed" });
  }
});

router.post("/share", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const { trackId } = req.body;
  if (!trackId) return res.status(400).json({ error: "trackId is required" });

  try {
    const url = await musicService.shareTrack(req.user!.uid, trackId);
    res.json({ url });
  } catch (err) {
    logger.error("Failed to share track", { error: err });
    res.status(500).json({ error: "Failed to share track" });
  }
});

router.post("/execute-tool", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const { name, args } = req.body;
  if (!name) return res.status(400).json({ error: "Tool name is required" });

  try {
    logger.info(`Executing tool ${name}`, { args, uid: req.user?.uid });
    
    // Process different tool calls based on their name
    switch (name) {
      case 'generate_full_track':
        // Generate via Lyria 3
        const lyriaResult = await musicService.generateMusicDirectly(undefined, args.prompt);
        return res.json({ status: "success", message: "Track generated", result: lyriaResult });
      
      case 'tweak_instrumentation':
        // Warp or tweak instrumentation via Lyria RealTime
        await musicService.applySteering(args, req.user!.uid);
        return res.json({ status: "success", message: "Instrumentation tweaked" });
        
      case 'jam_live':
        // Handle MRT2 live jamming intent
        return res.json({ status: "success", message: "Entered live jamming mode", instructions: "Connect MIDI controller to MRT2" });

      default:
        return res.status(400).json({ error: "Unknown tool" });
    }
  } catch (err) {
    logger.error("Failed to execute tool", { error: err });
    res.status(500).json({ error: "Failed to execute tool" });
  }
});





// WebSocket Server removed in favor of Firebase AI SDK native connections

export default router;
