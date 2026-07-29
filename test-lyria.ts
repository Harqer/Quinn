import { GoogleGenAI } from '@google/genai';
import { initSecrets, getSecret } from './src/config/secrets.js';

async function run() {
  await initSecrets();
  const apiKey = getSecret('GEMINI_API_KEY');
  console.log('API Key loaded:', !!apiKey);
  const ai = new GoogleGenAI({ apiKey, httpOptions: { apiVersion: 'v1alpha' } });
  try {
    const res = await ai.models.generateContent({ model: 'lyria-3-pro-preview', contents: 'a test song' });
    console.log('Success:', res.text);
  } catch (err) {
    console.error('Error name:', (err as Error).name);
    console.error('Error status:', (err as any).status);
    console.error('Error message:', (err as Error).message);
  }
}
run();
