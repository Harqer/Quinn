import { quinnGraph } from "./quinn-graph.js";
import { getAi } from "./ai.js";
import { WebSocket } from "ws";
import logger from "../config/logger.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getRedis } from "../config/redis.js";

export class MusicService {
  async processVisionEvent(ws: WebSocket, session: any, image: string) {
    try {
      const result = await (quinnGraph as any).invoke({ image });

      const updatePayload = {
        type: "agent_update",
        vision: result.visionDescription,
        lyrics: result.lyrics,
        prompts: result.musicalPrompts
      };

      if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(updatePayload));
      }

      // Propagate update via Redis Stream for other potential observers/nodes
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

  async generateMusicDirectly(image: string) {
    const ai = getAi() as any;

    // Fallback to models.generateContent for @google/genai v2
    const response = await ai.models.generateContent({
      model: "gemini-2.0-flash",
      contents: [{
        parts: [
          { inlineData: { mimeType: "image/jpeg", data: image } },
          { text: "You are a creative music director. Generate 3 short music prompts based on this image." }
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
