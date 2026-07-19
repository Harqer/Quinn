import { quinnGraph } from "./quinn-graph.js";
import { getAi } from "./ai.js";
import { WebSocket } from "ws";
import logger from "../config/logger.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getRedis, saveQuinnSession, getQuinnSession } from "../config/redis.js";
import { rtdb } from "../config/firebase.js";

export interface QuinnEvent {
  type: string;
  vision?: string;
  prompts?: string[];
  script?: string;
  feedback?: string;
  error?: string;
  trackId?: string;
  chunk?: string;
}

export class MusicService {
  async processVisionEvent(ws: WebSocket, session: any, image: string, sessionId: string) {
    try {
      const previousState = await this.safeGetSession(sessionId) || {};

      const config = {
        configurable: {
          onChunk: (chunk: { type: string; text: string }) => {
            this.safeSend(ws, { type: "quinn_chunk", chunk: chunk.text });
          }
        }
      };

      const stream = await quinnGraph.stream({
        ...previousState,
        image
      }, config);

      let finalState = { ...previousState };

      for await (const update of stream) {
        if (update.visualAnalyzer) {
          const vision = update.visualAnalyzer.visionDescription;
          this.safeSend(ws, { type: "agent_update", vision });
          await this.syncToRtdb(sessionId, { vision });
          finalState.visionDescription = vision;
        }

        if (update.musicDirector) {
          const prompts = update.musicDirector.musicalPrompts;
          this.safeSend(ws, { type: "agent_update", prompts });
          await this.syncToRtdb(sessionId, { prompts });
          finalState.musicalPrompts = prompts;

          if (session?.setWeightedPrompts) {
            await session.setWeightedPrompts({ weightedPrompts: prompts });
          }
        }

        if (update.podcastNarrator) {
          const script = update.podcastNarrator.podcastScript;
          this.safeSend(ws, { type: "agent_update", script });
          await this.syncToRtdb(sessionId, { script });
          finalState.podcastScript = script;
        }
      }

      await this.safeSaveSession(sessionId, finalState);
      return finalState;
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
        feedback: feedback,
        script: result.podcastScript
      };

      this.safeSend(ws, updatePayload);
      await this.syncToRtdb(sessionId, updatePayload);
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
      model: "gemini-1.5-flash",
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

  private async syncToRtdb(sessionId: string, data: Partial<QuinnEvent>) {
    try {
      const ref = rtdb.ref(`sessions/${sessionId}/state`);
      await ref.update({
        ...data,
        updatedAt: Date.now()
      });
    } catch (err) {
      logger.error("[MUSIC_SERVICE] RTDB Sync failed", { error: err });
    }
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
