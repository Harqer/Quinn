import { quinnGraph } from "./quinn-graph.js";
import { getAi } from "./ai.js";
import { WebSocket } from "ws";
import logger from "../config/logger.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getRedis, saveQuinnSession, getQuinnSession } from "../config/redis.js";

export class MusicService {
  async processVisionEvent(ws: WebSocket, session: any, image: string, sessionId: string) {
    try {
      // Load previous session state from Redis to maintain context (like "hipster style")
      const previousState = await getQuinnSession(sessionId) || {};

      const result = await (quinnGraph as any).invoke({
        ...previousState,
        image
      });

      const updatePayload = {
        type: "agent_update",
        vision: result.visionDescription,
        prompts: result.musicalPrompts,
        feedback: result.userFeedback || ""
      };

      if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(updatePayload));
      }

      // Persist the updated state back to Redis JSON
      await saveQuinnSession(sessionId, result);

      // Propagate update via Redis Stream for other potential observers
      const redis = getRedis();
      if (redis) {
        await redis.xadd("quinn_updates", "*", { data: JSON.stringify(updatePayload) });
      }

      // Auto-update the musical session with new prompts
      await session.setWeightedPrompts({ weightedPrompts: result.musicalPrompts });

      return result;
    } catch (err) {
      logger.error("[MUSIC_SERVICE] Failed to process vision event", { error: err });
      throw err;
    }
  }

  async handleUserFeedback(ws: WebSocket, session: any, feedback: string, sessionId: string) {
    try {
      const previousState = await getQuinnSession(sessionId) || {};

      // Re-invoke graph with new feedback
      const result = await (quinnGraph as any).invoke({
        ...previousState,
        userFeedback: feedback
      });

      const updatePayload = {
        type: "agent_update",
        prompts: result.musicalPrompts,
        feedback: feedback
      };

      if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(updatePayload));
      }

      await saveQuinnSession(sessionId, result);
      await session.setWeightedPrompts({ weightedPrompts: result.musicalPrompts });

      return result;
    } catch (err) {
      logger.error("[MUSIC_SERVICE] Failed to handle feedback", { error: err });
      throw err;
    }
  }

  async generateMusicDirectly(image: string) {
    const ai = getAi() as any;

    const response = await ai.models.generateContent({
      model: "lyria", // Using Lyria for visual-to-prompt direct call
      contents: [{
        parts: [
          { inlineData: { mimeType: "image/jpeg", data: image } },
          { text: "You are a creative music director. Generate 3 short music prompts based on this image. Use universal terminology." }
        ]
      }],
      config: {
        responseMimeType: "application/json",
      }
    });

    return JSON.parse(response.value);
  }

  async saveTrack(uid: string, trackData: any) {
    return await trackRepository.saveTrack({
      ...trackData,
      userId: uid
    });
  }

  async getCommunityTracks() {
    const redis = getRedis();
    if (redis) {
      const cached = await redis.get("community_tracks");
      if (cached) return JSON.parse(cached as string);
    }

    const tracks = await trackRepository.getCommunityTracks();

    if (redis) {
      await redis.set("community_tracks", JSON.stringify(tracks), { ex: 60 });
    }

    return tracks;
  }
}

export const musicService = new MusicService();
