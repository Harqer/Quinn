import { GoogleGenAI } from '@google/genai';
const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });
async function test() {
  const stream = await ai.models.generateContentStream({
    model: 'gemini-3.6-flash',
    contents: 'tell me a joke'
  });
  for await (const chunk of stream) {
    console.log("CHUNK:", chunk.text);
  }
}
test().catch(console.error);
