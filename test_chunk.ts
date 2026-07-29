import { GoogleGenAI } from '@google/genai';
import * as dotenv from 'dotenv';
dotenv.config();

const ai = new GoogleGenAI({ apiKey: process.env.VITE_GEMINI_API_KEY });
async function test() {
  const stream = await ai.models.generateContentStream({
    model: 'gemini-3.6-flash',
    contents: 'tell me a short joke'
  });
  for await (const chunk of stream) {
    console.log("CHUNK.text:", chunk.text);
    console.log("CHUNK object keys:", Object.keys(chunk));
    if (chunk.candidates) {
       console.log("CANDIDATES:", JSON.stringify(chunk.candidates, null, 2));
    }
  }
}
test().catch(console.error);
