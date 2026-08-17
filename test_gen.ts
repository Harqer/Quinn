import { GoogleGenAI } from "@google/genai";
import { execSync } from 'child_process';
const apiKey = execSync("gcloud secrets versions access latest --secret=GEMINI_API_KEY", { encoding: 'utf-8' }).trim();
const ai = new GoogleGenAI({ apiKey: apiKey });
async function test() {
  try {
    const response = await ai.models.generateContent({
      model: "gemini-1.5-flash",
      contents: "hello"
    });
    console.log(response.text);
  } catch (e) {
    console.error(e);
  }
}
test();
