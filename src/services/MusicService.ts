import { maveVisionFlow, directorFlow, podcastNarratorFlow, generateMusicFlow, lyriaRealtimeFlow } from "./genkit-flows.js";
import { v4 as uuidv4 } from "uuid";
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

function encodeWAV(samples: Int16Array, sampleRate: number = 48000, numChannels: number = 1) {
  const buffer = new ArrayBuffer(44 + samples.length * 2);
  const view = new DataView(buffer);
  
  const writeString = (view: DataView, offset: number, string: string) => {
    for (let i = 0; i < string.length; i++) {
      view.setUint8(offset + i, string.charCodeAt(i));
    }
  };
  
  writeString(view, 0, 'RIFF');
  view.setUint32(4, 36 + samples.length * 2, true);
  writeString(view, 8, 'WAVE');
  
  writeString(view, 12, 'fmt ');
  view.setUint32(16, 16, true);
  view.setUint16(20, 1, true);
  view.setUint16(22, numChannels, true);
  view.setUint32(24, sampleRate, true);
  view.setUint32(28, sampleRate * numChannels * 2, true);
  view.setUint16(32, numChannels * 2, true);
  view.setUint16(34, 16, true);
  
  writeString(view, 36, 'data');
  view.setUint32(40, samples.length * 2, true);
  
  const data = new Int16Array(buffer, 44);
  data.set(samples);
  
  return Buffer.from(buffer);
}

const createMusicInteraction = async (input: string, image?: string, previousId?: string, onChunk?: any) => {
    try {
        const stream = await lyriaRealtimeFlow.stream({ input, image });
        
        for await (const chunk of stream.stream) {
            if (onChunk) {
                onChunk(chunk);
            }
        }
        
        const output = await stream.output;
        return {
            id: previousId || uuidv4(),
            output_text: output.output_text,
            output_audio: output.output_audio,
        };
    } catch (err) {
        logger.error("Lyria RealTime flow error", { error: err });
        throw err;
    }
};

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
        let finalState = { ...previousState };

        // 1. Visual Analyzer Flow
        const visualStream = await maveVisionFlow.stream({ image, locale: previousState.locale });
        let vision = "";
        for await (const chunk of visualStream.stream) {
          this.safeSend(ws, { type: "mave_thinking", chunk, isThinking: true });
          this.syncToRtdb(sessionId, { chunk, isThinking: true });
        }
        const visualOutput = await visualStream.output;
        vision = visualOutput.visionDescription;
        this.safeSend(ws, { type: "agent_update", vision });
        await this.syncToRtdb(sessionId, { vision, isThinking: false });
        finalState.visionDescription = vision;

        // 2. Director Flow
        const directorOutput = await directorFlow({
          visionDescription: vision,
          userFeedback: previousState.userFeedback,
          locale: previousState.locale
        });
        
        const reasoning = directorOutput.reasoning;
        const modality = directorOutput.modality;
        this.safeSend(ws, { type: "agent_update", reasoning, modality });
        await this.syncToRtdb(sessionId, { reasoning, modality, isThinking: false });
        finalState.directorReasoning = reasoning;
        finalState.modality = modality;

        // 3. Modality branching
        if (modality === 'music' || modality === 'mixed') {
          const input = `Visual Vibe: ${vision}\nUser Feedback: ${previousState.userFeedback || "Generate music fitting this atmosphere"}`;
          const interaction = await createMusicInteraction(input, image, previousState.previousInteractionId, (chunk: any) => {
              this.safeSend(ws, { type: "mave_thinking", chunk: chunk.text, isThinking: true });
              this.syncToRtdb(sessionId, { chunk: chunk.text, isThinking: true });
          });
          
          const prompts = interaction.output_text?.split("\n").filter((l: string) => l.trim().length > 0) || [];
          const audio = interaction.output_audio?.data;

          await this.syncToRtdb(sessionId, { prompts, isThinking: false, interactionId: interaction.id });
          this.safeSend(ws, { type: "agent_update", prompts, chunk: audio, interactionId: interaction.id });
          finalState.musicalPrompts = prompts;
          finalState.previousInteractionId = interaction.id;
        }

        if (modality === 'podcast' || modality === 'audiobook' || modality === 'mixed') {
           const podcastStream = await podcastNarratorFlow.stream({
              visionDescription: vision,
              userFeedback: previousState.userFeedback,
              modality,
              locale: previousState.locale
           });

           for await (const chunk of podcastStream.stream) {
               this.safeSend(ws, { type: "mave_thinking", chunk, isThinking: true });
               this.syncToRtdb(sessionId, { chunk, isThinking: true });
           }
           
           const podcastOutput = await podcastStream.output;
           const script = podcastOutput.script;
           this.safeSend(ws, { type: "agent_update", script });
           await this.syncToRtdb(sessionId, { script, isThinking: false });
           finalState.podcastScript = script;
        }

        // 4. Media Generator Node (for cover_art / video_motion)
        if (directorOutput.visualIntent && directorOutput.visualIntent !== 'none') {
            const visualPrompt = `Scene: ${vision}. Create a visual atmosphere matching this POV.`;
            try {
              const result = await generateCoverMedia(visualPrompt, directorOutput.visualIntent === 'cover_art' ? 'cover_art' : 'video_motion', 'latest');
              if (directorOutput.visualIntent === 'cover_art') {
                this.safeSend(ws, { type: "cover_art_update", coverArtUrl: result.url });
                await this.syncToRtdb(sessionId, { coverArtUrl: result.url, isThinking: false });
                finalState.coverArtUrl = result.url;
              } else {
                this.safeSend(ws, { type: "video_motion_update", videoMotionUrl: result.url });
                await this.syncToRtdb(sessionId, { videoMotionUrl: result.url, isThinking: false });
                finalState.videoMotionUrl = result.url;
              }
            } catch (e) {
                logger.warn("[MAVE_SERVICE] Visual media generation failed", { error: e });
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

      const directorOutput = await directorFlow({ visionDescription: previousState.visionDescription || "Unknown", userFeedback: feedback, locale: previousState.locale });

      const updatePayload: MaveEvent = {
        type: "agent_update",
        feedback: feedback,
        reasoning: directorOutput.reasoning,
        modality: directorOutput.modality
      };

      this.safeSend(ws, updatePayload);
      await this.syncToRtdb(sessionId, updatePayload);
      const newState = { ...previousState, ...updatePayload };
      await this.safeSaveSession(sessionId, newState);

      return updatePayload;
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

    const { getSecret } = await import("../config/secrets.js");
    const { GoogleGenAI } = await import("@google/genai");
    const { encodeWAV } = await import("../utils/wav.js");
    
    // Explicitly use v1alpha for Lyria RealTime
    const apiKey = getSecret("GEMINI_API_KEY") as string;
    const aiAlpha = new GoogleGenAI({ apiKey, httpOptions: { apiVersion: "v1alpha" } });

    const promptText = type || "Ambient electronic";
    
    try {
      const result = await generateMusicFlow({ promptText });

      return {
        response: `I've generated a new track based on your prompt: ${promptText}. Hope you enjoy it!`,
        audioUrl: result.audioUrl,
        coverUrl: "",
        trackName: result.trackName,
        artistName: result.artistName,
      };
    } catch (err) {
      logger.error("Failed to generate music via Genkit flow", err);
      throw err;
    }
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
