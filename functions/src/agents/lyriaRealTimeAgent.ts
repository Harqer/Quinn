import { z } from "genkit";
import { ai } from "./genkit";
import { getStorage } from "firebase-admin/storage";
import { executeMutation } from "../dataconnect";
import * as path from "path";
import * as os from "os";
import * as fs from "fs";
import * as logger from "firebase-functions/logger";

export const lyriaRealTimeAgent = ai.defineTool(
  {
    name: "jam_live",
    description: "Generates or tweaks a music track using the Lyria RealTime live model.",
    inputSchema: z.object({
      prompt: z.string().describe("The description of the song to generate or tweak."),
      audioUrl: z.string().optional().describe("Optional URL of an existing track to tweak."),
    }),
    outputSchema: z.object({
      result: z.string(),
      audioUrl: z.string(),
    }),
  },
  async (input, options) => {
    const { uid, apiKey } = options?.context || {};
    if (!uid || !apiKey) throw new Error("Missing auth context (uid, apiKey) for tool execution.");
    let actualPrompt = input.prompt;
    if (input.audioUrl) {
      actualPrompt = `Modify the existing track at ${input.audioUrl} by: ${input.prompt}`;
    }

    logger.info(`Generating track with Lyria RealTime for prompt: ${actualPrompt}`);
    
    const response = await ai.generate({
      model: "googleai/lyria-realtime-exp",
      prompt: actualPrompt,
      config: {
        musicGenerationConfig: { bpm: 120, density: 1.0, brightness: 0.5, guidance: 4.0 }
      } as any
    });

    const media = response.media;
    if (!media || !media.url) {
      throw new Error("Lyria RealTime generated no audio.");
    }
    
    const base64Audio = media.url.includes(",") ? media.url.split(",")[1] : media.url;
    const audioBuffer = Buffer.from(base64Audio, "base64");
    
    const filename = `track_${Date.now()}.wav`;
    const tempFilePath = path.join(os.tmpdir(), filename);
    fs.writeFileSync(tempFilePath, audioBuffer);
    
    let url = "";
    try {
      const bucket = getStorage().bucket();
      await bucket.upload(tempFilePath, { destination: `generated_audio/${filename}`, metadata: { contentType: 'audio/wav' } });
      
      const fileRef = bucket.file(`generated_audio/${filename}`);
      await fileRef.makePublic();
      url = `https://storage.googleapis.com/${bucket.name}/generated_audio/${filename}`;
    } finally {
      // Always clean up the temp file to avoid disk leaks across invocations.
      if (fs.existsSync(tempFilePath)) {
        try {
          fs.unlinkSync(tempFilePath);
        } catch (err) {
          logger.error("Failed to clean up temp file:", err);
        }
      }
    }
    
    await executeMutation("SeedTrack", {
      title: input.prompt,
      audioUrl: url,
      prompt: actualPrompt,
      isCommunity: false,
      ownerUid: uid
    });
    
    return { result: "success", audioUrl: url };
  }
);
