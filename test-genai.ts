import { GoogleGenAI } from '@google/genai';
import * as dotenv from 'dotenv';
dotenv.config({ path: '/home/shaolin/lyria/.env' });

const ai = new GoogleGenAI({ apiKey: process.env.VITE_GEMINI_API_KEY || process.env.GEMINI_API_KEY || 'fake_key' });

async function run() {
  try {
    const stream = await (ai as any).interactions.create({
      model: 'gemini-3.6-flash',
      input: 'Make a song about a cat',
      stream: true,
      tools: [{ type: 'function', functionDeclarations: [{ name: 'generate_full_track', description: 'desc', parameters: { type: 'OBJECT', properties: {} } }] }]
    });
    for await (const chunk of stream) {
      console.log(chunk.event_type);
    }
  } catch (e: any) {
    console.error("ERROR:");
    console.error(JSON.stringify(e));
    if (e.message) console.log(e.message);
  }
}
run();
