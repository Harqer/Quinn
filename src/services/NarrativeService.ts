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

  private async synthesizeAudio(prompt: string, mode: NarrativeMode, voice: string, res?: any) {
    const ai = getAi();
    const { TextToSpeechClient } = await import('@google-cloud/text-to-speech').then(m => m.v1beta1);
    const ttsClient = new TextToSpeechClient({ projectId: process.env.GOOGLE_CLOUD_PROJECT || 'musically-studio' });
    
    let fullScript = "";
    const audioChunks: Buffer[] = [];
    
    if (mode === 'podcast') {
      const promptText = `Create a podcast dialogue in JSON format based on the topic: "${prompt}".
The dialogue should be a lively back-and-forth between a host (R) and a guest (S).
The host should guide the conversation by asking questions, while the guest provides informative and accessible answers.
JSON structure:
{
  "multiSpeakerMarkup": {
    "turns": [
      {"text": "Podcast script content here...", "speaker": "R"},
      {"text": "...", "speaker": "S"}
    ]
  }
}`;
      let scriptRes;
      try {
        scriptRes = await ai.models.generateContent({
          model: 'gemini-3.6-pro',
          contents: promptText
        });
      } catch (e: any) {
        if (e?.status === 429 || e?.message?.includes('429') || e?.message?.includes('Quota')) {
          logger.warn("[PODCAST] Quota exceeded for Pro, falling back to Flash");
          scriptRes = await ai.models.generateContent({
            model: 'gemini-3.6-flash',
            contents: promptText
          });
        } else {
          throw e;
        }
      }
      
      const rawJson = (scriptRes.text || "").replace(/```json/g, '').replace(/```/g, '').trim();
      let scriptData;
      try {
        scriptData = JSON.parse(rawJson);
      } catch (e) {
        throw new Error("Failed to parse Gemini podcast JSON");
      }
      
      const turns = scriptData.turns || scriptData.multiSpeakerMarkup?.turns || [];
      
      // Determine host and guest voices dynamically
      const hostVoice = voice || 'en-US-Studio-O';
      let guestVoice = 'en-US-Studio-Q';
      if (hostVoice === 'en-US-Studio-Q') guestVoice = 'en-US-Studio-O';
      else if (hostVoice.includes('Casual')) guestVoice = 'en-US-Casual-K';
      else if (hostVoice === 'en-US-Casual-K') guestVoice = 'en-US-Studio-O';
      
      for (const turn of turns) {
        const textChunk = `${turn.speaker === 'R' ? 'Host' : 'Guest'}: ${turn.text}\n\n`;
        fullScript += textChunk;
        
        if (res) res.write(`data: ${JSON.stringify({ type: 'chunk', text: textChunk })}\n\n`);
        
        const voiceName = turn.speaker === 'R' ? hostVoice : guestVoice;
        
        const [ttsRes] = await ttsClient.synthesizeSpeech({
          input: { text: turn.text },
          voice: { languageCode: 'en-US', name: voiceName },
          audioConfig: { audioEncoding: 'MP3' }
        });
        
        if (ttsRes.audioContent) {
          audioChunks.push(Buffer.from(ttsRes.audioContent));
        }
      }
    } else {
      // Audiobook single speaker
      const instruction = `You are a legendary Audiobook Narrator. Generate an immersive 3-5 paragraph chapter. Speak with steady cadence. Topic: "${prompt}"`;
      let scriptRes;
      try {
        scriptRes = await ai.models.generateContent({
          model: 'gemini-3.6-pro',
          contents: instruction
        });
      } catch (e: any) {
        if (e?.status === 429 || e?.message?.includes('429') || e?.message?.includes('Quota')) {
          logger.warn("[AUDIOBOOK] Quota exceeded for Pro, falling back to Flash");
          scriptRes = await ai.models.generateContent({
            model: 'gemini-3.6-flash',
            contents: instruction
          });
        } else {
          throw e;
        }
      }
      
      fullScript = scriptRes.text || "";
      if (res) res.write(`data: ${JSON.stringify({ type: 'chunk', text: fullScript })}\n\n`);
      
      const [ttsRes] = await ttsClient.synthesizeSpeech({
        input: { text: fullScript },
        voice: { languageCode: 'en-US', name: voice || 'en-US-Studio-O' },
        audioConfig: { audioEncoding: 'MP3' }
      });
      
      if (ttsRes.audioContent) {
        audioChunks.push(Buffer.from(ttsRes.audioContent));
      }
    }

    const finalAudioBuffer = Buffer.concat(audioChunks);
    const audioUrl = `data:audio/mp3;base64,${finalAudioBuffer.toString('base64')}`;
    
    return { script: fullScript.trim(), audioUrl };
  }

  async generateStream(prompt: string, mode: NarrativeMode, voice: string, locale: string = "en", res: any) {
    try {
      // Start cover generation in parallel
      const coverPromise = generateCoverMedia(prompt, 'cover_art', 'flash')
        .then(r => r.url)
        .catch(e => {
           logger.error("Failed to generate cover", { error: e });
           return "";
        });

      const { script, audioUrl } = await this.synthesizeAudio(prompt, mode, voice, res);
      const coverUrl = await coverPromise;

      const track = {
        id: `${mode}_${Date.now()}`,
        title: prompt.length > 30 ? prompt.substring(0, 30) + '...' : prompt,
        artist: "Mave AI Studio",
        album: mode === 'audiobook' ? "Audiobooks" : "Narratives",
        script: script,
        voice: "Podcast Hosts (Journey)",
        coverUrl: coverUrl,
        audioUrl: audioUrl,
        createdAt: new Date().toISOString()
      };

      logger.info("[NARRATIVE_SERVICE] Generated Track via TTS API", { title: track.title });
      res.write(`data: ${JSON.stringify({ type: 'complete', track })}\n\n`);
      res.end();
    } catch (err) {
      logger.error("[NARRATIVE_SERVICE] generateStream failed", { error: err });
      throw err;
    }
  }

  async generateFromPrompt(prompt: string, mode: NarrativeMode, voice: string, locale: string = "en") {
    try {
      const coverPromise = generateCoverMedia(prompt, 'cover_art', 'flash')
        .then(r => r.url)
        .catch(e => {
           logger.error("Failed to generate cover", { error: e });
           return "";
        });

      const { script, audioUrl } = await this.synthesizeAudio(prompt, mode, voice);
      const coverUrl = await coverPromise;

      const track = {
        id: `${mode}_${Date.now()}`,
        title: prompt.length > 30 ? prompt.substring(0, 30) + '...' : prompt,
        artist: "Mave AI Studio",
        album: mode === 'audiobook' ? "Audiobooks" : "Narratives",
        script: script,
        voice: "Podcast Hosts (Journey)",
        coverUrl: coverUrl,
        audioUrl: audioUrl,
        createdAt: new Date().toISOString()
      };

      logger.info("[NARRATIVE_SERVICE] Generated Track via TTS API", { title: track.title });
      return track;
    } catch (err) {
      logger.error("[NARRATIVE_SERVICE] generateFromPrompt failed", { error: err });
      throw err;
    }
  }

  async generateOmniVisuals(prompt: string, modality: 'video' | 'image' = 'video'): Promise<string | null> {
    try {
      const ai = getAi();
      const interaction = await (ai as any).interactions.create({
        model: "imagen-3",
        input: prompt,
        response_modalities: [modality === 'video' ? 'VIDEO' : 'IMAGE']
      });

      if (interaction.output_file_uri) {
        return interaction.output_file_uri;
      }
      
      const steps = interaction.steps || [];
      for (const step of steps) {
        const parts = step.content || [];
        for (const part of parts) {
          if (part.file_data && part.file_data.file_uri) {
            return part.file_data.file_uri;
          }
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
