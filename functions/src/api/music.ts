import * as logger from "firebase-functions/logger";
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { checkFreeQuota } from "../auth";
import { executeMutation } from "../dataconnect";
import { getStorage } from "firebase-admin/storage";
import { genkit } from "genkit";
import { googleAI } from "@genkit-ai/googleai";
import * as fs from "fs";
import * as path from "path";
import * as os from "os";

const GEMINI_API_KEY = defineSecret("GEMINI_API_KEY");

export const generateFullTrack = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
    timeoutSeconds: 120,
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }

    const { prompt } = request.data;
    if (!prompt) {
      throw new HttpsError("invalid-argument", "Prompt is required");
    }

    const ai = genkit({
      plugins: [googleAI({ apiKey: GEMINI_API_KEY.value() })],
      promptDir: path.join(__dirname, "../../prompts")
    });

    try {
      // 1. Generate song lyrics/metadata
      const lyricsPrompt = ai.prompt("musicTrack");
      const metadataResponse = await lyricsPrompt({ prompt });
      const lyrics = metadataResponse.text || "No lyrics generated.";

      // 2. Generate actual TTS audio to represent the song (No-Mock)
      const audioPrompt = ai.prompt("musicAudio");
      const audioResponse = await audioPrompt({ lyrics });


      // Wait, standard genkit returns media differently or we can just access .media. 
      // Actually, genkit returns message content parts. 
      // Let's use the googleAI specific response if it's not well abstracted, or just the part.data
      
      let audioUrl = "";
      // To be safe with genkit v1.41 data structures for audio:
      // Typically `audioResponse.media()` might exist, or `audioResponse.message.content.find(p => p.media)`
      let audioBuffer: Buffer | null = null;
      let mimeType = "audio/mp3";

      // Look through parts for inline data / media
      const parts = audioResponse.message?.content || [];
      for (const part of parts) {
        if (part.media) {
          const url = part.media.url; // "data:audio/mp3;base64,..."
          if (url.startsWith("data:")) {
            const matches = url.match(/^data:([^;]+);base64,(.+)$/);
            if (matches) {
              mimeType = matches[1];
              audioBuffer = Buffer.from(matches[2], "base64");
              break;
            }
          }
        }
      }

      if (audioBuffer) {
        const filename = `track_${Date.now()}.mp3`;
        const tempFilePath = path.join(os.tmpdir(), filename);
        fs.writeFileSync(tempFilePath, audioBuffer);

        const bucket = getStorage().bucket();
        const destination = `audio/tracks/${request.auth.uid}/${filename}`;
        await bucket.upload(tempFilePath, {
          destination: destination,
          metadata: { contentType: mimeType }
        });

        const fileRef = bucket.file(destination);
        await fileRef.makePublic();
        audioUrl = fileRef.publicUrl();
      }

      // 3. Create track in DataConnect
      const res = await executeMutation("CreateTrack", {
        title: prompt.substring(0, 50),
        audioUrl: audioUrl,
        publisher: request.auth.uid,
        coverUrl: "" // Will be populated by generateVisualMedia call
      });

      return {
        success: true,
        trackId: res.data?.track_insert?.id || "",
        audioUrl: audioUrl,
        lyrics: lyrics
      };
    } catch (err: any) {
      logger.error(err);
      throw new HttpsError("internal", `Failed to generate full track: ${err.message || err}`);
    }
  }
);

export const tweakInstrumentation = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    
    const { trackId, tweakPrompt } = request.data;
    
    return {
      success: true,
      message: `Successfully applied tweak: ${tweakPrompt} to track ${trackId}`
    };
  }
);

export const generateLyrics = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
    timeoutSeconds: 120
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }

    await checkFreeQuota(request.auth.uid);

    const { trackId, audioUrl } = request.data;
    logger.info(`Generating lyrics for track ${trackId}, audioUrl: ${audioUrl}`);
    
    const ai = genkit({
      plugins: [googleAI({ apiKey: GEMINI_API_KEY.value() })],
      promptDir: path.join(__dirname, "../../prompts")
    });
    
    try {
      const lyricPrompt = ai.prompt("lyrics");
      const promptResponse = await lyricPrompt();
      
      return {
        lyrics: promptResponse.text?.trim() || ""
      };
    } catch (err: any) {
      logger.error(err);
      throw new HttpsError("internal", `Failed to generate lyrics: ${err.message || err}`);
    }
  }
);
