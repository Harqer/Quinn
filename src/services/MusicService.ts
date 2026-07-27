import { maveGraph } from "./mave-graph.js";
import { getAi, generateCoverMedia } from "./ai.js";
import { WebSocket } from "ws";
import logger from "../config/logger.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getRedis, saveMaveSession, getMaveSession } from "../config/redis.js";
import { getRtdbShard } from "../config/firebase.js";

export interface MaveEvent {
  type: string;
  vision?: string;
  prompts?: string[];
  script?: string;
  feedback?: string;
  error?: string;
  token?: string;
  trackId?: string;
  chunk?: string;
  isThinking?: boolean;
  reasoning?: string;
  modality?: string;
  coverArtUrl?: string;
  videoMotionUrl?: string;
  interactionId?: string;
  playbackState?: any;
}

const visionLocks = new Map<string, Promise<any>>();

export class MusicService {
  /**
   * Processes vision event with Gemini Thinking SDK streaming and RTDB state sync.
   * Professional Grade: Interleaves WebSocket delivery with RTDB source-of-truth updates.
   * Uses a per-session lock to ensure sequential processing of vision streams.
   */
  async processVisionEvent(ws: WebSocket, image: string, sessionId: string) {
    const existingLock = visionLocks.get(sessionId) || Promise.resolve();

    const newLock = existingLock.then(async () => {
      try {
        const previousState = await this.safeGetSession(sessionId) || {};

        // Real-time piping of chunks from the graph nodes
        const config = {
          configurable: {
            onChunk: (chunk: { type: string; text: string }) => {
              // Forward everything from the graph directly as "thinking" for natural feedback
              this.safeSend(ws, {
                type: "mave_thinking",
                chunk: chunk.text,
                isThinking: true
              });

              // Synchronize to RTDB for multi-surface consistency
              this.syncToRtdb(sessionId, { chunk: chunk.text, isThinking: true });
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

          if (update.director) {
            const reasoning = update.director.directorReasoning;
            const modality = update.director.modality;
            this.safeSend(ws, { type: "agent_update", reasoning, modality });
            await this.syncToRtdb(sessionId, { reasoning, modality, isThinking: false });
            finalState.directorReasoning = reasoning;
            finalState.modality = modality;
          }

          if (update.musicDirector) {
            const prompts = update.musicDirector.musicalPrompts;
            const audio = update.musicDirector.generatedAudio;
            const prevId = update.musicDirector.previousInteractionId;

            // 1. Send metadata (prompts) to RTDB for persistent session state
            await this.syncToRtdb(sessionId, {
              prompts,
              isThinking: false,
              interactionId: prevId
            });

            // 2. Send structured audio block via WebSocket for low-latency playback
            this.safeSend(ws, {
              type: "agent_update",
              prompts,
              chunk: audio, // Forward structured audio as a "chunk"
              interactionId: prevId
            });

            finalState.musicalPrompts = prompts;
            finalState.previousInteractionId = prevId;
          }

          if (update.podcastNarrator) {
            const script = update.podcastNarrator.podcastScript;
            this.safeSend(ws, { type: "agent_update", script });
            await this.syncToRtdb(sessionId, { script, isThinking: false });
            finalState.podcastScript = script;
          }

          if (update.mediaGenerator) {
            const cover = update.mediaGenerator.coverArtUrl;
            const video = update.mediaGenerator.videoMotionUrl;
            if (cover) {
              this.safeSend(ws, { type: "cover_art_update", coverArtUrl: cover });
              await this.syncToRtdb(sessionId, { coverArtUrl: cover, isThinking: false });
              finalState.coverArtUrl = cover;
            }
            if (video) {
              this.safeSend(ws, { type: "video_motion_update", videoMotionUrl: video });
              await this.syncToRtdb(sessionId, { videoMotionUrl: video, isThinking: false });
              finalState.videoMotionUrl = video;
            }
          }
        }

        await this.safeSaveSession(sessionId, finalState);
        return finalState;
      } catch (err) {
        logger.error("[MAVE_SERVICE] Failed to process vision event", { error: err });
        this.safeSend(ws, { type: "error", error: "AI Orchestration failed" });
        throw err;
      }
    });

    visionLocks.set(sessionId, newLock);
    return newLock;
  }

  /**
   * Handles specific playback commands (Skip, Shuffle, Repeat).
   */
  async handlePlaybackCommand(type: string, data: any, sessionId: string) {
    try {
      logger.info("[MAVE_SERVICE] Playback command", { sessionId, type, data });

      switch (type) {
        case "skip_next":
          // Request a new variation based on current description
          await this.handleUserFeedback(null as any, "Make a variation of this vibe", sessionId);
          break;
        case "toggle_shuffle":
        case "toggle_repeat":
        case "seek_to":
          // Client handles Gemini Live configuration; we just sync state to RTDB
          break;
      }

      await this.syncToRtdb(sessionId, { [type === "seek_to" ? "progress" : "playbackState"]: data });
    } catch (err) {
      logger.error("[MAVE_SERVICE] Playback command failed", { error: err, type });
    }
  }

  /**
   * Generates a persistent share link for a track.
   */
  async shareTrack(uid: string, trackId: string) {
    try {
      const shortCode = await trackRepository.createShortLink(trackId);
      logger.info("[MAVE_SERVICE] Track shared with short link", { uid, trackId, shortCode });
      
      const baseUrl = process.env.APP_URL;
      if (!baseUrl) {
        throw new Error("APP_URL environment variable is not configured");
      }
      
      return `${baseUrl}/s/${shortCode}`;
    } catch (err) {
      logger.error("[MAVE_SERVICE] Share failed", { error: err });
      throw err;
    }
  }

  /**
   * Actively steers the Lyria engine (BPM, Density, Brightness).
   * Synchronized via Redis and RTDB for horizontal scalability.
   */
  async applySteering(params: { bpm?: number, density?: number, brightness?: number, mutes?: string[] }, sessionId: string) {
    try {
      const config: any = {};
      if (params.bpm) config.bpm = params.bpm;
      if (params.density) config.density = params.density;
      if (params.brightness) config.brightness = params.brightness;

      if (params.mutes) {
        config.mute_drums = params.mutes.includes("drums");
        config.mute_bass = params.mutes.includes("bass");
      }

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

  async handleUserFeedback(ws: WebSocket, feedback: string, sessionId: string) {
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
        script: result.podcastScript,
        reasoning: result.directorReasoning,
        modality: result.modality
      };

      this.safeSend(ws, updatePayload);
      await this.syncToRtdb(sessionId, updatePayload);
      await this.safeSaveSession(sessionId, result);

      return result;
    } catch (err) {
      logger.error("[MAVE_SERVICE] Failed to handle feedback", { error: err });
      throw err;
    }
  }

  async generateMusicDirectly(image?: string, type?: string, variant?: 'latest' | 'flash') {
    if (type === 'cover_art' || type === 'video_motion') {
      const userPrompt = image || "Aesthetic vibe soundtrack";
      const coverResult = await generateCoverMedia(userPrompt, type, variant || 'latest');
      return { url: coverResult.url, prompt: coverResult.prompt, type, modelUsed: coverResult.modelUsed };
    }

    const ai = getAi();
    const prompt = `Analyze vision stream and generate music prompts for ${type || 'ambient'}`;
    const { LYRIA_REGISTRY } = await import("./ai.js");

    let inputToModel: any = prompt;
    if (image) {
      const mimeType = image.startsWith('data:image/png') ? 'image/png' : 'image/jpeg';
      const base64Data = image.includes(',') ? image.split(',')[1] : image;
      inputToModel = [
        prompt, 
        { image: { data: base64Data, mime_type: mimeType } } // Interactions API format
      ];
    }
    
    const interaction = await (ai as any).interactions.create({
      model: LYRIA_REGISTRY.FULL_TRACK,
      input: inputToModel
    });
    return { response: interaction.output_text };
  }


  // --- Enterprise RTDB Sync ---
  private syncTimers: Map<string, NodeJS.Timeout> = new Map();
  private pendingSyncData: Map<string, Partial<MaveEvent>> = new Map();

  private async syncToRtdb(sessionId: string, data: Partial<MaveEvent>) {
    const rtdbInstance = getRtdbShard(sessionId);
    
    // Merge pending data
    const existing = this.pendingSyncData.get(sessionId) || {};
    const merged = { ...existing, ...data };
    this.pendingSyncData.set(sessionId, merged);

    // Debounce high-frequency streaming events (chunks/thinking) to prevent maxing out RTDB write limits
    const isHighFrequency = data.chunk !== undefined || data.isThinking !== undefined;
    const isImportant = data.prompts || data.script || data.type === 'steering_update' || data.reasoning || data.playbackState;

    if (isHighFrequency && !isImportant) {
      if (!this.syncTimers.has(sessionId)) {
        const timer = setTimeout(async () => {
          this.syncTimers.delete(sessionId);
          const flushData = this.pendingSyncData.get(sessionId);
          this.pendingSyncData.delete(sessionId);
          if (flushData) {
            try {
              await rtdbInstance.ref(`sessions/${sessionId}/state`).update({
                ...flushData,
                updatedAt: Date.now()
              });
            } catch (err) {
              logger.error("[MAVE_SERVICE] RTDB Batched Sync failed", { error: err });
            }
          }
        }, 500); // 500ms debounce
        this.syncTimers.set(sessionId, timer);
      }
    } else {
      // Flush immediately for structural state changes
      const timer = this.syncTimers.get(sessionId);
      if (timer) clearTimeout(timer);
      this.syncTimers.delete(sessionId);
      this.pendingSyncData.delete(sessionId);
      
      try {
        await rtdbInstance.ref(`sessions/${sessionId}/state`).update({
          ...merged,
          updatedAt: Date.now()
        });
      } catch (err) {
        logger.error("[MAVE_SERVICE] RTDB Immediate Sync failed", { error: err });
      }
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
