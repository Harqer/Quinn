import { Router, Response } from "express";
import { optionalFirebaseToken, verifyFirebaseToken, AuthenticatedRequest, checkDailyQuota, verifyAppCheck } from "../middlewares/auth.js";
import { auth } from "../config/firebase.js";
import { GenerateSchema, ShareVibeSchema } from "../schemas/api.js";
import { WebSocketServer, WebSocket } from "ws";
import logger from "../config/logger.js";
import { musicService } from "../services/MusicService.js";
import { podcastService } from "../services/PodcastService.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getAi } from "../services/ai.js";

const router = Router();

router.post("/generate", optionalFirebaseToken, verifyAppCheck, checkDailyQuota, async (req: AuthenticatedRequest, res: Response) => {
  const result = GenerateSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.issues });

  try {
    const data = await musicService.generateMusicDirectly(result.data.image, result.data.type);
    res.json(data);
  } catch (err) {
    logger.error("Generation Failed", { error: err });
    res.status(500).json({ error: "Generation Failed" });
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

router.get("/community/tracks", async (req, res) => {
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
  wss.on("connection", async (ws: WebSocket, request) => {
    const url = new URL(request.url || "", `http://${request.headers.host}`);
    const token = url.searchParams.get("token");

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
        uid = `guest_${Math.random().toString(36).substring(7)}`;
        logger.warn(`[WS_STUDIO] Invalid token, falling back to guest.`, { uid });
      }
    }

    let musicSession: any = null;
    let podcastSession: any = null;
    let currentMode: 'music' | 'podcast' = 'music';

    const initMusic = async () => {
      const ai = getAi();
      musicSession = await (ai as any).live.connect({
        model: "lyria-realtime-exp",
        callbacks: {
          onmessage: (e: any) => ws.readyState === WebSocket.OPEN && ws.send(JSON.stringify({ type: "message", data: e })),
          onclose: () => ws.close(),
          onerror: (err: any) => logger.error("[MUSIC_SESSION] error", { error: err }),
        },
      });
    };

    ws.on("message", async (data) => {
      try {
        const msg = JSON.parse(data.toString());

        if (msg.type === "switch_mode") {
          currentMode = msg.mode;
          if (currentMode === 'podcast' && !podcastSession) {
            podcastSession = await podcastService.startPodcastSession(ws, uid);
          } else if (currentMode === 'music' && !musicSession) {
            await initMusic();
          }
          logger.info(`[WS_STUDIO] User switched to ${currentMode} mode.`, { uid });
          return;
        }

        if (currentMode === 'music') {
          if (!musicSession) await initMusic();
          if (msg.type === "vision") {
            await musicService.processVisionEvent(ws, musicSession, msg.image, uid);
          } else if (msg.type === "feedback") {
            await musicService.handleUserFeedback(ws, musicSession, msg.text, uid);
          } else if (msg.type === "steering_action") {
            await musicService.applySteering(ws, musicSession, msg.params, uid);
          } else if (msg.type === "setWeightedPrompts") {
            await musicSession.setWeightedPrompts({ weightedPrompts: msg.prompts });
          } else if (msg.type === "audio") {
            await musicService.handleRealTimeAudio(ws, musicSession, msg.data, uid);
          } else if (["skip_next", "skip_previous", "toggle_shuffle", "toggle_repeat", "seek_to"].includes(msg.type)) {
            await musicService.handlePlaybackCommand(msg.type, msg, musicSession, uid);
          } else if (["play", "pause", "stop"].includes(msg.type)) {
            musicSession[msg.type]();
          }
        } else {
          // Podcast Mode
          if (!podcastSession) podcastSession = await podcastService.startPodcastSession(ws, uid);
          if (msg.type === "vision") {
            await podcastService.processVisionForPodcast(ws, podcastSession, msg.image);
          } else if (msg.type === "text_command") {
            await podcastSession.send({ text: msg.text });
          }
        }
      } catch (msgErr) {
        logger.error("[WS_MESSAGE_ERR]", { error: msgErr });
      }
    });

    ws.on("close", () => {
      if (musicSession) musicSession.stop();
      if (podcastSession) podcastSession.stop();
    });
  });
};

export default router;
