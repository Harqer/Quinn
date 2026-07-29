import { GoogleGenAI } from '@google/genai';
import { initSecrets, getSecret } from './src/config/secrets.js';

async function run() {
  await initSecrets();
  const apiKey = getSecret('GEMINI_API_KEY');
  const ai = new GoogleGenAI({ apiKey });
  try {
    const res = await ai.models.generateContent({
      model: 'gemini-2.0-flash',
      contents: 'Generate a short 5-second drum beat.',
      config: {
        responseModalities: ['AUDIO'] // Let's test if 'AUDIO' works
      }
    });
    console.log('Success!');
    const parts = res.candidates?.[0]?.content?.parts || [];
    const audioPart = parts.find(p => p.inlineData?.mimeType?.startsWith('audio/'));
    console.log('Audio part found:', !!audioPart);
  } catch (err) {
    console.error('Error:', (err as Error).message);
  }
}
run();
