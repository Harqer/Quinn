import { Router, Response } from "express";
import { verifyFirebaseToken, AuthenticatedRequest, checkDailyQuota, verifyAppCheck } from "../middlewares/auth.js";
import { auth } from "../config/firebase.js";
import { GenerateSchema, ShareVibeSchema } from "../schemas/api.js";
import { WebSocketServer, WebSocket } from "ws";
import logger from "../config/logger.js";
import { musicService } from "../services/MusicService.js";
import { podcastService } from "../services/PodcastService.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getAi } from "../services/ai.js";

const router = Router();

router.post("/generate", verifyFirebaseToken, verifyAppCheck, checkDailyQuota, async (req: AuthenticatedRequest, res: Response) => {
  const result = GenerateSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.errors });

  try {
    const data = await musicService.generateMusicDirectly(result.data.image, result.data.type);
    res.json(data);
  } catch (err) {
    logger.error("Generation Failed", { error: err });
    res.status(500).json({ error: "Generation Failed" });
  }
});

router.post("/share", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const result = ShareVibeSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.errors });

  try {
    const trackId = await musicService.saveTrack(req.user!.uid, result.data);
    res.status(201).json({ id: trackId, message: "Track shared with community" });
  } catch (err) {
    logger.error("Failed to share track", { error: err });
    res.status(500).json({ error: "Failed to share track" });
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

// WebSocket Server for Musically Proxy (Music & Podcast)
export const setupMusicWebSocket = (wss: WebSocketServer) => {
  wss.on("connection", async (ws: WebSocket, request) => {
    const url = new URL(request.url || "", `http://${request.headers.host}`);
    const token = url.searchParams.get("token");

    if (!token) {
      ws.close(4001, "Unauthorized: Token required");
      return;
    }

    let uid = "";
    try {
      const decodedToken = await auth.verifyIdToken(token);
      uid = decodedToken.uid;
      logger.info(`[WS_PROXY] User connected for secure Quinn session.`, { uid });
    } catch (err) {
      ws.close(4001, "Unauthorized: Invalid token");
      return;
    }

    let musicSession: any = null;
    let podcastSession: any = null;
    let currentMode: 'music' | 'podcast' = 'music';

    const initMusic = async () => {
      const ai = getAi();
      // @ts-ignore
      musicSession = await ai.live.music.connect({
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
          logger.info(`[WS_PROXY] User switched to ${currentMode} mode.`, { uid });
          return;
        }

        if (currentMode === 'music') {
          if (!musicSession) await initMusic();
          if (msg.type === "vision") {
            await musicService.processVisionEvent(ws, musicSession, msg.image, uid);
          } else if (msg.type === "feedback") {
            await musicService.handleUserFeedback(ws, musicSession, msg.text, uid);
          } else if (msg.type === "setWeightedPrompts") {
            await musicSession.setWeightedPrompts({ weightedPrompts: msg.prompts });
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
