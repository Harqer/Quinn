import { getAi } from "./ai.js";
import { WebSocket } from "ws";
import logger from "../config/logger.js";
import { quinnGraph } from "./quinn-graph.js";

export class PodcastService {
  async startPodcastSession(ws: WebSocket, uid: string) {
    const ai = getAi();
    let session: any = null;

    try {
      // establishes a multimodal live session for audio output
      // @ts-ignore
      session = await ai.live.connect({
        model: "gemini-2.0-flash-exp",
        generationConfig: {
          responseModalities: ["audio"],
          speechConfig: { voice: "AOEDE" } // Musical/Expressive voice
        },
        callbacks: {
          onmessage: (e: any) => {
            if (ws.readyState === WebSocket.OPEN) {
              // Forward audio chunks and transcript to client
              ws.send(JSON.stringify({ type: "podcast_chunk", data: e }));
            }
          },
          onclose: () => ws.close(),
          onerror: (err: any) => {
            logger.error("[PODCAST_SESSION] Gemini error", { error: err });
            if (ws.readyState === WebSocket.OPEN) {
              ws.send(JSON.stringify({ type: "error", error: "Gemini session failed" }));
            }
          },
        },
      });

      return session;
    } catch (err) {
      logger.error("[PODCAST_SERVICE] Failed to start session", { error: err });
      throw err;
    }
  }

  async processVisionForPodcast(ws: WebSocket, session: any, image: string) {
    try {
      const result = await (quinnGraph as any).invoke({
        image,
        mode: 'podcast'
      });

      if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({
          type: "podcast_update",
          vision: result.visionDescription,
          script: result.podcastScript
        }));
      }

      // Send the script to Gemini Live to be spoken
      if (session && result.podcastScript) {
        await session.send({
          text: `Narrate this segment for the podcast: ${result.podcastScript}`
        });
      }

      return result;
    } catch (err) {
      logger.error("[PODCAST_SERVICE] Vision processing failed", { error: err });
      throw err;
    }
  }
}

export const podcastService = new PodcastService();
