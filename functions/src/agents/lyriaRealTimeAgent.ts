import { z } from "genkit";
import { ai } from "./genkit";
import { GoogleGenAI } from "@google/genai";
import { getStorage } from "firebase-admin/storage";
import { executeMutation } from "../dataconnect";
import * as path from "path";
import * as os from "os";
import * as fs from "fs";
import { WaveFile } from "wavefile";

const MAX_CHUNKS = 15;

export const lyriaRealTimeAgent = ai.defineTool(
  {
    name: "jam_live",
    description: "Generates or tweaks a music track using the Lyria RealTime live model.",
    inputSchema: z.object({
      prompt: z.string().describe("The description of the song to generate or tweak."),
      audioUrl: z.string().optional().describe("Optional URL of an existing track to tweak."),
      apiKey: z.string().describe("The Gemini API key."),
      uid: z.string().describe("The user ID requesting the song."),
    }),
    outputSchema: z.object({
      result: z.string(),
      audioUrl: z.string(),
    }),
  },
  async (input) => {
    let actualPrompt = input.prompt;
    if (input.audioUrl) {
      actualPrompt = `Modify the existing track at ${input.audioUrl} by: ${input.prompt}`;
    }

    const googleGenAi = new GoogleGenAI({ apiKey: input.apiKey, httpOptions: { apiVersion: "v1alpha" } });
    const responseQueue: any[] = [];
    
    const session = await (googleGenAi.live as any).music.connect({
      model: "models/lyria-realtime-exp",
      callbacks: {
        onmessage: (message: any) => responseQueue.push(message),
        onerror: (error: any) => console.error("music session error:", error),
        onclose: () => console.log("Lyria RealTime stream closed."),
      },
    });

    await session.setWeightedPrompts({ weightedPrompts: [{ text: actualPrompt, weight: 1.0 }] });
    await session.setMusicGenerationConfig({
      musicGenerationConfig: { bpm: 120, density: 1.0, brightness: 0.5, guidance: 4.0 }
    });
    session.play();

    console.log("Receiving audio chunks...");
    let done = false;
    let chunk_count = 0;
    const audioChunks: number[][] = [];
    while (!done) {
      if (responseQueue.length > 0) {
        const response = responseQueue.shift();
        if (response?.audioChunk?.data) {
          const audioBuffer = Buffer.from(response.audioChunk.data, "base64");
          const intArray = new Int16Array(
            audioBuffer.buffer,
            audioBuffer.byteOffset,
            audioBuffer.length / Int16Array.BYTES_PER_ELEMENT
          );
          audioChunks.push(Array.from(intArray));
          chunk_count++;
        }
        if (chunk_count >= MAX_CHUNKS) done = true;
      } else {
        await new Promise((resolve) => setTimeout(resolve, 100));
      }
    }
    session.close();
    
    const flatArray = new Int16Array(audioChunks.flat());
    const wav = new WaveFile();
    wav.fromScratch(2, 48000, "16", flatArray);
    const wavBuffer = wav.toBuffer();
    
    const filename = `track_${Date.now()}.wav`;
    const tempFilePath = path.join(os.tmpdir(), filename);
    fs.writeFileSync(tempFilePath, wavBuffer);
    
    const bucket = getStorage().bucket();
    await bucket.upload(tempFilePath, { destination: `generated_audio/${filename}`, metadata: { contentType: 'audio/wav' } });
    
    const fileRef = bucket.file(`generated_audio/${filename}`);
    await fileRef.makePublic();
    const url = `https://storage.googleapis.com/${bucket.name}/generated_audio/${filename}`;
    
    await executeMutation("SeedTrack", {
      title: input.prompt,
      audioUrl: url,
      prompt: actualPrompt,
      isCommunity: false,
      ownerUid: input.uid
    });
    
    return { result: "success", audioUrl: url };
  }
);
