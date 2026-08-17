import { genkit } from "genkit";
import { googleAI } from "@genkit-ai/googleai";

const ai = genkit({ plugins: [googleAI()] });

async function main() {
  try {
    const res = await ai.generate({
      model: "googleai/gemini-3.5-flash",
      prompt: "hello",
      config: { apiKey: "test" } // maybe?
    });
    console.log("3.5-flash OK");
  } catch(e) { console.error("3.5-flash Error", (e as any).message); }
}
main();
