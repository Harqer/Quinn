import { z } from "genkit";
import { ai } from "./genkit";
import { GoogleGenAI } from "@google/genai";
import { getStorage } from "firebase-admin/storage";
import { executeMutation } from "../dataconnect";
import * as path from "path";
import * as os from "os";
import * as fs from "fs";

export const omniFlashAgent = ai.defineTool(
  {
    name: "generate_music_video",
    description: "Generates a music video using Gemini Omni Flash.",
    inputSchema: z.object({
      prompt: z.string().describe("The description of the music video to generate."),
      audioUrl: z.string().optional().describe("The URL of the track to generate a video for."),
      apiKey: z.string().describe("The Gemini API key."),
      uid: z.string().describe("The user ID requesting the video."),
    }),
    outputSchema: z.object({
      result: z.string(),
      videoUrl: z.string(),
    }),
  },
  async (input) => {
    const googleGenAi = new GoogleGenAI({ apiKey: input.apiKey });
    console.log(`Generating music video with Gemini Omni Flash for prompt: ${input.prompt} and audio: ${input.audioUrl}`);
    
    try {
      const interaction = await googleGenAi.interactions.create({
        model: "gemini-3.5-flash",
        input: input.prompt,
      });
      
      const outputVideo = (interaction as any).output_video || (interaction as any).outputVideo;
      let base64Video = "";
      if (outputVideo && outputVideo.data) {
         base64Video = outputVideo.data;
      } else if (interaction.steps) {
         for (const step of interaction.steps.slice().reverse()) {
           if ((step as any).modelOutput && (step as any).modelOutput.parts) {
             const videoPart = (step as any).modelOutput.parts.find((p: any) => p.video || p.inlineData?.mimeType?.startsWith('video/'));
             if (videoPart) {
               base64Video = videoPart.video?.data || videoPart.inlineData?.data;
               break;
             }
           }
         }
      }
      
      if (!base64Video) {
        throw new Error("No video returned from Gemini Omni Flash model.");
      }
      
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
        ownerUid: input.uid
      });
      
      return { result: "success", videoUrl: url };
    } catch (e: any) {
      console.error("Gemini Omni Flash video generation failed", e);
      throw new Error(e instanceof Error ? e.message : 'Unknown error');
    }
  }
);
