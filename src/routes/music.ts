import { Router, Response } from "express";
import { optionalFirebaseToken, verifyFirebaseToken, AuthenticatedRequest, checkDailyQuota, verifyAppCheck } from "../middlewares/auth.js";
import { auth, appCheck } from "../config/firebase.js";
import { GenerateSchema, ShareVibeSchema } from "../schemas/api.js";
import { WebSocketServer, WebSocket } from "ws";
import logger from "../config/logger.js";
import { musicService } from "../services/MusicService.js";
import { narrativeService } from "../services/NarrativeService.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getAi } from "../services/ai.js";

const router = Router();

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

router.post("/playlist/add", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const { trackId, playlistId } = req.body;
  if (!trackId) return res.status(400).json({ error: "trackId is required" });

  try {
    const bookmarkId = await trackRepository.bookmarkTrack(req.user!.uid, trackId);
    res.json({ success: true, id: bookmarkId, playlistId: playlistId || "favorites", message: "Added to playlist" });
  } catch (err) {
    logger.error("Failed to add to playlist", { error: err });
    res.status(500).json({ error: "Failed to add to playlist" });
  }
});

router.post("/bookmark", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const { trackId } = req.body;
  if (!trackId) return res.status(400).json({ error: "trackId is required" });

  try {
    const bookmarkId = await trackRepository.bookmarkTrack(req.user!.uid, trackId);
    res.status(201).json({ id: bookmarkId, message: "Track bookmarked" });
  } catch (err) {
    logger.error("Failed to bookmark track", { error: err });
    res.status(500).json({ error: "Failed to bookmark track" });
  }
});

router.get("/community/tracks", verifyAppCheck, async (req, res) => {
  try {
    const tracks = await musicService.getCommunityTracks();
    res.json({ tracks });
  } catch (err) {
    logger.error("Failed to fetch community tracks", { error: err });
    res.status(500).json({ error: "Failed to fetch tracks" });
  }
});

router.get("/user/tracks", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  try {
    const tracks = await trackRepository.getUserTracks(req.user!.uid);
    res.json({ tracks });
  } catch (err) {
    logger.error("Failed to fetch user tracks", { error: err });
    res.status(500).json({ error: "Failed to fetch user tracks" });
  }
});

// WebSocket Server for Mave Studio Engine (Music & Podcast)
export const setupMusicWebSocket = (wss: WebSocketServer) => {
  // Ping/Pong Heartbeat to prune dead connections
  const interval = setInterval(() => {
    wss.clients.forEach((ws: any) => {
      if (ws.isAlive === false) return ws.terminate();
      ws.isAlive = false;
      ws.ping();
    });
  }, 30000);

  wss.on("close", () => clearInterval(interval));

  wss.on("connection", async (ws: any, request) => {
    ws.isAlive = true;
    ws.on("pong", () => { ws.isAlive = true; });

    const url = new URL(request.url || "", `http://${request.headers.host}`);
    const token = url.searchParams.get("token");
    const locale = request.headers["accept-language"] || "en";

    let uid = "";
    if (!token) {
      uid = `guest_${Math.random().toString(36).substring(7)}`;
      logger.info(`[WS_STUDIO] Guest user connected.`, { uid });
    } else {
      try {
        const decodedToken = await auth.verifyIdToken(token);
        uid = decodedToken.uid;
        logger.info(`[WS_STUDIO] Authenticated user connected.`, { uid });
      } catch (err) {
        logger.error(`[WS_STUDIO] Token verification failed. Closing connection.`, { error: err });
        ws.close(4001, "Unauthorized: Invalid Auth Token");
        return;
      }
    }

    const appCheckToken = url.searchParams.get("appCheck");

    if (!appCheckToken) {
      logger.error(`[WS_STUDIO] Missing App Check token. Closing connection.`);
      ws.close(4001, "Unauthorized: Missing App Check Token");
      return;
    }

    try {
      await appCheck.verifyToken(appCheckToken);
    } catch (err) {
      logger.error(`[WS_STUDIO] Invalid App Check token.`, { error: err });
      ws.close(4001, "Unauthorized: Invalid App Check Token");
      return;
    }

    let musicSessionInitialized = false;
    let podcastSessionInitialized = false;
    let audiobookSessionInitialized = false;
    let currentMode: 'music' | 'podcast' | 'audiobook' = 'music';
    let isInitializingMusic = false;

    const initMusic = async () => {
      if (musicSessionInitialized || isInitializingMusic) return;
      isInitializingMusic = true;
      try {
        await musicService.generateMusicLiveToken(ws, uid);
        musicSessionInitialized = true;
      } finally {
        isInitializingMusic = false;
      }
    };

    ws.on("message", async (data: any) => {
      try {
        const msg = JSON.parse(data.toString());

        if (msg.type === "switch_mode") {
          currentMode = msg.mode;
          if (currentMode === 'podcast' && !podcastSessionInitialized) {
            await narrativeService.startSession(ws, uid, "podcast", locale);
            podcastSessionInitialized = true;
          } else if (currentMode === 'audiobook' && !audiobookSessionInitialized) {
            await narrativeService.startSession(ws, uid, "audiobook", locale);
            audiobookSessionInitialized = true;
          } else if (currentMode === 'music' && !musicSessionInitialized) {
            await initMusic();
          }
          logger.info(`[WS_STUDIO] User switched to ${currentMode} mode.`, { uid });
          return;
        }

        if (currentMode === 'music') {
          if (!musicSessionInitialized) await initMusic();
          if (msg.type === "vision") {
            await musicService.processVisionEvent(ws, msg.image, uid);
          } else if (msg.type === "feedback") {
            await musicService.handleUserFeedback(ws, msg.text, uid);
          } else if (msg.type === "steering_action") {
            await musicService.applySteering(msg.params, uid);
          } else if (["skip_next", "skip_previous", "toggle_shuffle", "toggle_repeat", "seek_to"].includes(msg.type)) {
            await musicService.handlePlaybackCommand(msg.type, msg, uid);
          } else if (["play", "pause", "stop"].includes(msg.type)) {
            // The client manages playback directly, we just sync state to RTDB if needed
            logger.info(`[WS_STUDIO] Playback state changed to ${msg.type}`, { uid });
          }
        } else if (currentMode === 'podcast') {
          // Podcast Mode
          if (!podcastSessionInitialized) {
            await narrativeService.startSession(ws, uid, "podcast", locale);
            podcastSessionInitialized = true;
          }
          if (msg.type === "vision") {
            await narrativeService.processVision(ws, msg.image, "podcast", locale);
          } else if (msg.type === "text_command") {
            // Text commands are sent directly by the client to Gemini, we can log them or sync to RTDB
            logger.info(`[WS_STUDIO] Received text command: ${msg.text}`, { uid });
          }
        } else if (currentMode === 'audiobook') {
          // Audiobook Mode
          if (!audiobookSessionInitialized) {
            await narrativeService.startSession(ws, uid, "audiobook", locale);
            audiobookSessionInitialized = true;
          }
          if (msg.type === "vision") {
            await narrativeService.processVision(ws, msg.image, "audiobook", locale);
          } else if (msg.type === "text_command") {
            // Text commands are sent directly by the client to Gemini, we can log them or sync to RTDB
            logger.info(`[WS_STUDIO] Received text command: ${msg.text}`, { uid });
          }
        }
      } catch (msgErr) {
        logger.error("[WS_MESSAGE_ERR]", { error: msgErr });
      }
    });

    ws.on("close", () => {
      musicSessionInitialized = false;
      podcastSessionInitialized = false;
      audiobookSessionInitialized = false;
    });
  });
};

export default router;
