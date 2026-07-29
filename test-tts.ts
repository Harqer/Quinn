import { GoogleGenAI } from "@google/genai";
import { getSecret, initSecrets } from "./src/config/secrets.js";

async function test() {
  process.env.GOOGLE_CLOUD_PROJECT = "lyria-dev";
  await initSecrets();
  const ai = new GoogleGenAI({ apiKey: getSecret("GEMINI_API_KEY"), httpOptions: { apiVersion: "v1beta" } });
  
  try {
    const res = await ai.models.generateContent({
      model: "gemini-2.5-flash-preview-tts",
      contents: "Say hello",
      config: {
        responseModalities: ["AUDIO"],
        speechConfig: {
          voiceConfig: {
            prebuiltVoiceConfig: {
              voiceName: "Aoede"
            }
          }
        }
      }
    });
    console.log("2.5-flash-preview-tts Success:", !!res.candidates?.[0]?.content?.parts?.[0]?.inlineData);
  } catch(e) {
    console.log("2.5-flash-preview-tts Error:", (e as Error).message);
  }

  try {
    const res = await ai.models.generateContent({
      model: "gemini-3.1-flash-live-preview",
      contents: "Say hello",
      config: {
        responseModalities: ["AUDIO"],
        speechConfig: {
          voiceConfig: {
            prebuiltVoiceConfig: {
              voiceName: "Aoede"
            }
          }
        }
      }
    });
    console.log("3.1-flash-live-preview Success:", !!res.candidates?.[0]?.content?.parts?.[0]?.inlineData);
  } catch(e) {
    console.log("3.1-flash-live-preview Error:", (e as Error).message);
  }
}
test();
