import { z } from "genkit";
import { ai } from "./genkit";
import { getStorage } from "firebase-admin/storage";
import { executeMutation } from "../dataconnect";
import * as path from "path";
import * as os from "os";
import * as fs from "fs";
import * as logger from "firebase-functions/logger";

export const omniFlashAgent = ai.defineTool(
  {
    name: "generate_music_video",
    description: "Generates a music video using Gemini Omni Flash.",
    inputSchema: z.object({
      prompt: z.string().describe("The description of the music video to generate."),
      audioUrl: z.string().optional().describe("The URL of the track to generate a video for."),
    }),
    outputSchema: z.object({
      result: z.string(),
      videoUrl: z.string(),
    }),
  },
  async (input, options) => {
    const { uid, apiKey } = options?.context || {};
    if (!uid || !apiKey) throw new Error("Missing auth context (uid, apiKey) for tool execution.");
    logger.info(`Generating music video with Gemini Omni Flash for prompt: ${input.prompt} and audio: ${input.audioUrl}`);
    
    try {
      const response = await ai.generate({
        model: "googleai/omni-flash",
        prompt: input.prompt,
      });
      
      const media = response.media;
      if (!media || !media.url) {
        throw new Error("No video returned from Gemini Omni Flash model.");
      }
      
      const base64Video = media.url.includes(",") ? media.url.split(",")[1] : media.url;
      const videoBuffer = Buffer.from(base64Video, "base64");
      
      const filename = `video_${Date.now()}.mp4`;
      const tempFilePath = path.join(os.tmpdir(), filename);
      fs.writeFileSync(tempFilePath, videoBuffer);
      
      const bucket = getStorage().bucket();
      await bucket.upload(tempFilePath, { destination: `generated_videos/${filename}`, metadata: { contentType: 'video/mp4' } });
      
      const fileRef = bucket.file(`generated_videos/${filename}`);
      await fileRef.makePublic();
      const url = `https://storage.googleapis.com/${bucket.name}/generated_videos/${filename}`;
      
      await executeMutation("SeedTrack", {
        title: input.prompt,
        audioUrl: url,
        prompt: input.prompt,
        isCommunity: false,
        ownerUid: uid
      });
      
      return { result: "success", videoUrl: url };
    } catch (e: any) {
      logger.error("Gemini Omni Flash video generation failed", e);
      throw new Error(e instanceof Error ? e.message : 'Unknown error');
    }
  }
);
