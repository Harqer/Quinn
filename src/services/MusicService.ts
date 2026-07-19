import { quinnGraph } from "./quinn-graph.js";
import { getAi } from "./ai.js";
import { WebSocket } from "ws";
import logger from "../config/logger.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getRedis, saveQuinnSession, getQuinnSession } from "../config/redis.js";

export interface QuinnEvent {
  type: string;
  vision?: string;
  prompts?: string[];
  script?: string;
  feedback?: string;
  error?: string;
  trackId?: string;
}

export class MusicService {
  async processVisionEvent(ws: WebSocket, session: any, image: string, sessionId: string) {
    try {
      const previousState = await this.safeGetSession(sessionId) || {};

      const result = await (quinnGraph as any).invoke({
        ...previousState,
        image
      });

      const updatePayload: QuinnEvent = {
        type: "agent_update",
        vision: result.visionDescription,
        prompts: result.musicalPrompts,
        feedback: result.userFeedback || ""
      };

      this.safeSend(ws, updatePayload);
      await this.safeSaveSession(sessionId, result);

      const redis = getRedis();
      if (redis) {
        await redis.xadd("quinn_updates", "*", { data: JSON.stringify(updatePayload) });
      }

      if (session?.setWeightedPrompts) {
        await session.setWeightedPrompts({ weightedPrompts: result.musicalPrompts });
      }

      return result;
    } catch (err) {
      logger.error("[MUSIC_SERVICE] Failed to process vision event", { error: err });
      this.safeSend(ws, { type: "error", error: "Visual analysis failed" });
      throw err;
    }
  }

  async handleUserFeedback(ws: WebSocket, session: any, feedback: string, sessionId: string) {
    try {
      const previousState = await this.safeGetSession(sessionId) || {};

      const result = await (quinnGraph as any).invoke({
        ...previousState,
        userFeedback: feedback
      });

      const updatePayload: QuinnEvent = {
        type: "agent_update",
        prompts: result.musicalPrompts,
        feedback: feedback
      };

      this.safeSend(ws, updatePayload);
      await this.safeSaveSession(sessionId, result);

      if (session?.setWeightedPrompts) {
        await session.setWeightedPrompts({ weightedPrompts: result.musicalPrompts });
      }

      return result;
    } catch (err) {
      logger.error("[MUSIC_SERVICE] Failed to handle feedback", { error: err });
      throw err;
    }
  }

  async generateMusicDirectly(image: string, type: 'music' | 'podcast' = 'music') {
    const ai = getAi() as any;

    const response = await ai.models.generateContent({
      model: "lyria",
      contents: [{
        parts: [
          { inlineData: { mimeType: "image/jpeg", data: image } },
          { text: `You are a creative music director. Generate 3 short ${type} prompts based on this image. Use universal terminology.` }
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
      try {
        const cached = await redis.get("community_tracks");
        if (cached) return JSON.parse(cached as string);
      } catch (e) {}
    }

    const tracks = await trackRepository.getCommunityTracks();

    if (redis) {
      try {
        await redis.set("community_tracks", JSON.stringify(tracks), { ex: 60 });
      } catch (e) {}
    }

    return tracks;
  }

  private async safeGetSession(sessionId: string) {
    try {
      return await getQuinnSession(sessionId);
    } catch (e) {
      return null;
    }
  }

  private async safeSaveSession(sessionId: string, state: any) {
    try {
      await saveQuinnSession(sessionId, state);
    } catch (e) {}
  }

  private safeSend(ws: WebSocket, payload: QuinnEvent) {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify(payload));
    }
  }
}

export const musicService = new MusicService();
