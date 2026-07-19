import { Router, Response } from "express";
import { verifyFirebaseToken, AuthenticatedRequest, checkDailyQuota, verifyAppCheck } from "../middlewares/auth.js";
import { auth } from "../config/firebase.js";
import { GenerateSchema } from "../schemas/api.js";
import { WebSocketServer, WebSocket } from "ws";
import logger from "../config/logger.js";
import { musicService } from "../services/MusicService.js";
import { getAi } from "../services/ai.js";

const router = Router();

router.post("/generate", verifyFirebaseToken, verifyAppCheck, checkDailyQuota, async (req: AuthenticatedRequest, res: Response) => {
  const result = GenerateSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.errors });

  try {
    const data = await musicService.generateMusicDirectly(result.data.image);
    res.json(data);
  } catch (err) {
    logger.error("Generation Failed", { error: err });
    res.status(500).json({ error: "Generation Failed" });
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

// WebSocket Server for Music Proxy
export const setupMusicWebSocket = (wss: WebSocketServer) => {
  wss.on("connection", async (ws: WebSocket, request) => {
    const url = new URL(request.url || "", `http://${request.headers.host}`);
    const token = url.searchParams.get("token");

    if (!token) {
      ws.close(4001, "Unauthorized: Token required");
      return;
    }

    try {
      const decodedToken = await auth.verifyIdToken(token);
      logger.info(`[WS_PROXY] User connected for secure Quinn Live proxying.`, { uid: decodedToken.uid });
    } catch (err) {
      ws.close(4001, "Unauthorized: Invalid token");
      return;
    }

    const ai = getAi();
    let session: any = null;

    try {
      // establishes a basic session for standard playback commands
      // @ts-ignore
      session = await ai.live.music.connect({
        model: "lyria-realtime-exp",
        callbacks: {
          onmessage: (e: any) => {
            if (ws.readyState === WebSocket.OPEN) {
              ws.send(JSON.stringify({ type: "message", data: e }));
            }
          },
          onclose: () => ws.close(),
          onerror: (err: any) => {
            if (ws.readyState === WebSocket.OPEN) {
              ws.send(JSON.stringify({ type: "error", error: "Gemini connection error" }));
            }
          },
        },
      });

      ws.on("message", async (data) => {
        try {
          const msg = JSON.parse(data.toString());

          if (msg.type === "vision") {
            await musicService.processVisionEvent(ws, session, msg.image);
          } else if (msg.type === "setWeightedPrompts") {
            await session.setWeightedPrompts({ weightedPrompts: msg.prompts });
          } else if (["play", "pause", "stop"].includes(msg.type)) {
            session[msg.type]();
          }
        } catch (msgErr) {
          logger.error("[WS_MESSAGE_ERR]", { error: msgErr });
        }
      });

      ws.on("close", () => {
        if (session) session.stop();
      });

    } catch (err: any) {
      ws.close();
    }
  });
};

export default router;
