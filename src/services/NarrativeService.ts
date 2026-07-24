import { getAi, generateCoverMedia } from "./ai.js";
import { WebSocket } from "ws";
import logger from "../config/logger.js";
import { maveGraph } from "./mave-graph.js";

export type NarrativeMode = 'podcast' | 'audiobook';

export class NarrativeService {
  private getModeConfig(mode: NarrativeMode, locale: string) {
    if (mode === 'audiobook') {
      return {
        model: "gemini-3.1-flash-live-preview",
        voice: "KORE",
        instruction: `You are an elite Audiobook Narrator. You must speak and respond primarily in this locale/language: ${locale}.`
      };
    }
    return {
      model: "gemini-3.1-flash-live-preview",
      voice: "AOEDE",
      instruction: `You are an elite Podcast Narrator. You must speak and respond primarily in this locale/language: ${locale}.`
    };
  }

  async startSession(ws: WebSocket, uid: string, mode: NarrativeMode, locale: string = "en") {
    const config = this.getModeConfig(mode, locale);

    try {
      const { generateLiveEphemeralToken } = await import('./ai.js');
      const token = await generateLiveEphemeralToken(config.model, config.instruction, config.voice);
      
      if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ type: "ephemeral_token", token }));
      }
      return token;
    } catch (err) {
      logger.error("[NARRATIVE_SERVICE] Failed to start session", { error: err });
      if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ type: "error", error: "Gemini session failed" }));
      }
      throw err;
    }
  }

  async processVision(ws: WebSocket, image: string, mode: NarrativeMode, locale: string = "en") {
    try {
      const result = await (maveGraph as any).invoke({ image, mode, locale });

      if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({
          type: `${mode}_update`,
          vision: result.visionDescription,
          script: mode === 'audiobook' ? result.audiobookScript : result.podcastScript
        }));
      }

      const script = mode === 'audiobook' ? result.audiobookScript : result.podcastScript;
      // (Legacy support: removed session.send as we use ephemeral tokens now. The client will send the script text to Gemini Live directly.)

      // Generate a dynamic live background visual for this narrative segment using Omni Flash
      try {
        if (result.visionDescription) {
          const backgroundVideoUri = await this.generateOmniVisuals(`Generate an aesthetic background loop matching: ${result.visionDescription}`, 'video');
          if (ws.readyState === WebSocket.OPEN && backgroundVideoUri) {
            ws.send(JSON.stringify({ type: "omni_visual_update", uri: backgroundVideoUri }));
          }
        }
      } catch (omniErr) {
        logger.warn("[NARRATIVE_SERVICE] Non-fatal: Omni visual generation failed", { error: omniErr });
      }

      return result;
    } catch (err) {
      logger.error("[NARRATIVE_SERVICE] Vision processing failed", { error: err });
      throw err;
    }
  }

  async generateStream(prompt: string, mode: NarrativeMode, voice: string, locale: string = "en", res: any) {
    try {
      const ai = getAi();
      const instruction = mode === 'audiobook'
        ? `You are a legendary Audiobook Narrator. Generate an immersive 3-5 paragraph chapter. Speak with steady cadence. Output in locale: ${locale}. Topic: "${prompt}"`
        : `You are an elite Podcast Narrator. Generate an engaging 3-5 sentence narrative. Speak with cinematic cadence. Output in locale: ${locale}. Topic: "${prompt}"`;

      // Start cover generation in parallel
      const coverPromise = generateCoverMedia(prompt, 'cover_art', 'flash')
        .then(res => res.url)
        .catch(e => {
           logger.error("Failed to generate cover", { error: e });
           return "";
        });

      const stream = await ai.models.generateContentStream({
        model: "gemini-3.5-flash",
        contents: instruction,
        config: {
          responseModalities: ["AUDIO"],
          speechConfig: {
            voiceConfig: {
              prebuiltVoiceConfig: {
                voiceName: voice || (mode === 'audiobook' ? "Kore" : "Aoede")
              }
            }
          }
        }
      });

      let fullScript = "";
      let fullAudioBase64 = "";
      let audioMimeType = "audio/wav";

      for await (const chunk of stream as any) {
        const textChunk = chunk.text;
        if (textChunk) {
          fullScript += textChunk;
          res.write(`data: ${JSON.stringify({ type: 'chunk', text: textChunk })}\n\n`);
        }
        const audioPart = chunk.candidates?.[0]?.content?.parts?.find((p: any) => p.inlineData)?.inlineData;
        if (audioPart) {
          audioMimeType = audioPart.mimeType || audioMimeType;
          fullAudioBase64 += audioPart.data;
          res.write(`data: ${JSON.stringify({ type: 'audio_chunk', data: audioPart.data, mimeType: audioMimeType })}\n\n`);
        }
      }

      const script = fullScript.trim() || `Welcome to our story: ${prompt}`;
      const coverUrl = await coverPromise;
      
      const track = {
        id: `${mode}_${Date.now()}`,
        title: prompt.length > 30 ? prompt.substring(0, 30) + '...' : prompt,
        artist: "Mave AI Studio",
        album: mode === 'audiobook' ? "Audiobooks" : "Narratives",
        script: script,
        voice: voice || (mode === 'audiobook' ? "KORE" : "AOEDE"),
        coverUrl: coverUrl,
        duration: "0:00",
        audioUrl: fullAudioBase64 ? `data:${audioMimeType};base64,${fullAudioBase64}` : undefined,
        createdAt: new Date().toISOString()
      };

      logger.info("[NARRATIVE_SERVICE] Generated Track via Stream", { title: track.title, voice: track.voice });
      res.write(`data: ${JSON.stringify({ type: 'complete', track })}\n\n`);
      res.end();
    } catch (err) {
      logger.error("[NARRATIVE_SERVICE] generateStream failed", { error: err });
      throw err;
    }
  }

  async generateFromPrompt(prompt: string, mode: NarrativeMode, voice: string, locale: string = "en") {
    try {
      const ai = getAi();
      const instruction = mode === 'audiobook'
        ? `You are a legendary Audiobook Narrator. Generate an immersive 3-5 paragraph chapter. Speak with steady cadence. Output in locale: ${locale}. Topic: "${prompt}"`
        : `You are an elite Podcast Narrator. Generate an engaging 3-5 sentence narrative. Speak with cinematic cadence. Output in locale: ${locale}. Topic: "${prompt}"`;

      // Start cover generation in parallel
      const coverPromise = generateCoverMedia(prompt, 'cover_art', 'flash')
        .then(res => res.url)
        .catch(e => {
           logger.error("Failed to generate cover", { error: e });
           return "";
        });

      const response = await ai.models.generateContent({
        model: "gemini-3.5-flash",
        contents: instruction,
        config: {
          responseModalities: ["AUDIO"],
          speechConfig: {
            voiceConfig: {
              prebuiltVoiceConfig: {
                voiceName: voice || (mode === 'audiobook' ? "Kore" : "Aoede")
              }
            }
          }
        }
      });

      const script = response.text?.trim() || `Welcome to our story: ${prompt}`;
      
      const audioPart = response.candidates?.[0]?.content?.parts?.find((p: any) => p.inlineData)?.inlineData;
      let audioUrl;
      if (audioPart) {
        audioUrl = `data:${audioPart.mimeType || 'audio/wav'};base64,${audioPart.data}`;
      }
      
      const coverUrl = await coverPromise;

      const track = {
        id: `${mode}_${Date.now()}`,
        title: prompt.length > 30 ? prompt.substring(0, 30) + '...' : prompt,
        artist: "Mave AI Studio",
        album: mode === 'audiobook' ? "Audiobooks" : "Narratives",
        script: script,
        voice: voice || (mode === 'audiobook' ? "KORE" : "AOEDE"),
        coverUrl: coverUrl,
        duration: "0:00",
        audioUrl: audioUrl,
        createdAt: new Date().toISOString()
      };

      logger.info("[NARRATIVE_SERVICE] Generated Track via Model Garden", { title: track.title, voice: track.voice });
      return track;
    } catch (err) {
      logger.error("[NARRATIVE_SERVICE] generateFromPrompt failed", { error: err });
      throw err;
    }
  }

  /**
   * Implements video/image generation using gemini-omni-flash-preview
   * via the Interactions API and Files API for live background visuals.
   */
  async generateOmniVisuals(prompt: string, modality: 'video' | 'image' = 'video'): Promise<string | null> {
    try {
      const ai = getAi();
      const response = await ai.models.generateContent({
        model: "gemini-omni-flash-preview",
        contents: prompt,
        // Optional depending on SDK version, requesting specific modality output
        // @ts-ignore
        config: {
           responseModalities: [modality === 'video' ? 'VIDEO' : 'IMAGE']
        }
      });

      // The response via Interactions API typically returns a file reference (Files API)
      const parts = response.candidates?.[0]?.content?.parts || [];
      for (const part of parts) {
        if (part.fileData && part.fileData.fileUri) {
          return part.fileData.fileUri;
        }
      }
      
      logger.warn("[NARRATIVE_SERVICE] No fileUri returned in Omni visual response");
      return null;
    } catch (err) {
      logger.error("[NARRATIVE_SERVICE] generateOmniVisuals failed", { error: err });
      throw err;
    }
  }
}

export const narrativeService = new NarrativeService();
