import { maveGraph } from "./mave-graph.js";
import { getAi } from "./ai.js";
import { WebSocket } from "ws";
import logger from "../config/logger.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getRedis, saveMaveSession, getMaveSession } from "../config/redis.js";
import { rtdb } from "../config/firebase.js";

export interface MaveEvent {
  type: string;
  vision?: string;
  prompts?: string[];
  script?: string;
  feedback?: string;
  error?: string;
  trackId?: string;
  chunk?: string;
  isThinking?: boolean;
}

export class MusicService {
  /**
   * Processes vision event with Gemini Thinking SDK streaming and RTDB state sync.
   * Professional Grade: Interleaves WebSocket delivery with RTDB source-of-truth updates.
   */
  async processVisionEvent(ws: WebSocket, session: any, image: string, sessionId: string) {
    try {
      const previousState = await this.safeGetSession(sessionId) || {};

      // Real-time piping of chunks from the graph nodes
      const config = {
        configurable: {
          onChunk: (chunk: { type: string; text: string }) => {
            const isThinking = chunk.type === "vision_thinking";
            const eventType = isThinking ? "mave_thinking" : "mave_chunk";

            // 1. Direct pipe to active WebSocket for absolute minimal latency
            this.safeSend(ws, {
              type: eventType,
              chunk: chunk.text,
              isThinking
            });

            // 2. Synchronize to RTDB for multi-surface consistency and state persistence
            this.syncToRtdb(sessionId, { chunk: chunk.text, isThinking });
          }
        }
      };

      // Execute graph as a stream to handle node-level parallelism
      const stream = await maveGraph.stream({
        ...previousState,
        image
      }, config);

      let finalState = { ...previousState };

      for await (const update of stream) {
        if (update.visualAnalyzer) {
          const vision = update.visualAnalyzer.visionDescription;
          this.safeSend(ws, { type: "agent_update", vision });
          await this.syncToRtdb(sessionId, { vision, isThinking: false });
          finalState.visionDescription = vision;
        }

        if (update.musicDirector) {
          const prompts = update.musicDirector.musicalPrompts;
          this.safeSend(ws, { type: "agent_update", prompts });
          await this.syncToRtdb(sessionId, { prompts, isThinking: false });
          finalState.musicalPrompts = prompts;

          if (session?.setWeightedPrompts) {
            await session.setWeightedPrompts({ weightedPrompts: prompts });
          }
        }

        if (update.podcastNarrator) {
          const script = update.podcastNarrator.podcastScript;
          this.safeSend(ws, { type: "agent_update", script });
          await this.syncToRtdb(sessionId, { script, isThinking: false });
          finalState.podcastScript = script;
        }
      }

      await this.safeSaveSession(sessionId, finalState);
      return finalState;
    } catch (err) {
      logger.error("[MAVE_SERVICE] Failed to process vision event", { error: err });
      this.safeSend(ws, { type: "error", error: "AI Orchestration failed" });
      throw err;
    }
  }

  /**
   * Handles real-time audio chunks for intent detection.
   */
  async handleRealTimeAudio(ws: WebSocket, session: any, base64Audio: string, sessionId: string) {
    try {
      if (!session) return;
      logger.info("[MAVE_SERVICE] Streaming audio chunk to Gemini", { sessionId, size: base64Audio.length });

      // Production: Pipe multimodal audio chunks directly to the Gemini Live session
      if (session.send) {
        await session.send({
          audio: base64Audio
        });
      }
    } catch (err) {
      logger.error("[MAVE_SERVICE] Audio processing failed", { error: err });
    }
  }

  /**
   * Handles specific playback commands (Skip, Shuffle, Repeat).
   */
  async handlePlaybackCommand(type: string, data: any, session: any, sessionId: string) {
    try {
      if (!session) return;
      logger.info("[MAVE_SERVICE] Playback command", { sessionId, type, data });

      switch (type) {
        case "skip_next":
          // Logic to generate next variation
          break;
        case "toggle_shuffle":
          await session.setMusicGenerationConfig({ musicGenerationConfig: { shuffle: data.enabled } });
          break;
        case "toggle_repeat":
          await session.setMusicGenerationConfig({ musicGenerationConfig: { repeat: data.enabled } });
          break;
        case "seek_to":
          // Logic for time-based seeking in Lyria
          break;
      }

      await this.syncToRtdb(sessionId, { [type === "seek_to" ? "progress" : "playbackState"]: data });
    } catch (err) {
      logger.error("[MAVE_SERVICE] Playback command failed", { error: err, type });
    }
  }

  /**
   * Actively steers the Lyria engine (BPM, Density, Brightness).
   * Synchronized via Redis and RTDB for horizontal scalability.
   */
  async applySteering(ws: WebSocket, session: any, params: { bpm?: number, density?: number, brightness?: number, mutes?: string[] }, sessionId: string) {
    try {
      if (!session) return;

      const config: any = {};
      if (params.bpm) config.bpm = params.bpm;
      if (params.density) config.density = params.density;
      if (params.brightness) config.brightness = params.brightness;

      if (params.mutes) {
        config.mute_drums = params.mutes.includes("drums");
        config.mute_bass = params.mutes.includes("bass");
      }

      await session.setMusicGenerationConfig({ musicGenerationConfig: config });

      // Persistence: Update session state in Redis for cluster-wide consistency
      const state = await this.safeGetSession(sessionId) || {};
      const updatedState = { ...state, musicConfig: config };
      await this.safeSaveSession(sessionId, updatedState);

      // Real-time: Sync warped state to RTDB for immediate multi-client updates
      await this.syncToRtdb(sessionId, { type: "steering_update", ...params } as any);

      logger.info("[MAVE_SERVICE] Lyria Steering applied", { sessionId, config });
    } catch (err) {
      logger.error("[MAVE_SERVICE] Failed to apply steering", { error: err });
    }
  }

  async handleUserFeedback(ws: WebSocket, session: any, feedback: string, sessionId: string) {
    try {
      const previousState = await this.safeGetSession(sessionId) || {};

      const result = await (maveGraph as any).invoke({
        ...previousState,
        userFeedback: feedback
      });

      const updatePayload: MaveEvent = {
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
      logger.error("[MAVE_SERVICE] Failed to handle feedback", { error: err });
      throw err;
    }
  }

  // --- Enterprise RTDB Sync ---

  private async syncToRtdb(sessionId: string, data: Partial<MaveEvent>) {
    try {
      const ref = rtdb.ref(`sessions/${sessionId}/state`);
      // Update is transactional and lightweight for state sync
      await ref.update({
        ...data,
        updatedAt: Date.now()
      });
    } catch (err) {
      logger.error("[MAVE_SERVICE] RTDB Sync failed", { error: err });
    }
  }

  // --- Resilience Helpers ---

  private async safeGetSession(sessionId: string) {
    try {
      return await getMaveSession(sessionId);
    } catch (e) {
      logger.warn("[MAVE_SERVICE] Session recovery failed (Redis down), starting fresh", { sessionId });
      return null;
    }
  }

  private async safeSaveSession(sessionId: string, state: any) {
    try {
      await saveMaveSession(sessionId, state);
    } catch (e) {
      logger.error("[MAVE_SERVICE] Session persistence failed", { sessionId, error: e });
    }
  }

  private safeSend(ws: WebSocket, payload: MaveEvent) {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify(payload));
    }
  }
}

export const musicService = new MusicService();
