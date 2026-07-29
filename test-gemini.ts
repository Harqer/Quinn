import { GoogleGenAI } from '@google/genai';
import { getSecret, initSecrets } from './src/config/secrets.js';

async function test() {
  await initSecrets();
  const apiKey = getSecret('GEMINI_API_KEY');
  console.log("API Key found:", !!apiKey);
  
  if (!apiKey) {
    console.error("No API key");
    process.exit(1);
  }

  const ai = new GoogleGenAI({ apiKey });
  try {
    const response = await ai.models.generateContent({
      model: 'gemini-3.6-flash',
      contents: "Say hello",
    });
    console.log("Response:", response.text);
    console.log("SUCCESS!");
  } catch (e: any) {
    console.error("FAILED:");
    console.error(e.message || e);
  }
}
test();
