import { GoogleGenAI } from '@google/genai';
import { readFileSync } from 'fs';
const apiKey = readFileSync('/home/shaolin/lyria/.env', 'utf-8').split('\n').find(l => l.startsWith('GEMINI_API_KEY=')).split('=')[1];
const ai = new GoogleGenAI({ apiKey, httpOptions: { apiVersion: 'v1alpha' } });
async function run() {
  const res = await ai.models.generateContent({
    model: 'models/lyria-3-pro-preview',
    contents: 'Generate a short drum beat'
  });
  console.log("Response parts:", JSON.stringify(res.candidates?.[0]?.content?.parts?.map(p => Object.keys(p))));
}
run().catch(console.error);
