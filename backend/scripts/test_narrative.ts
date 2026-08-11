import { GoogleGenAI } from "@google/genai";
import * as fs from "fs";
import * as path from "path";

// Initialize Gemini SDK with the API key from environment variable
const apiKey = process.env.GEMINI_API_KEY;
if (!apiKey) {
  console.error("Please set GEMINI_API_KEY environment variable.");
  process.exit(1);
}

const ai = new GoogleGenAI({ apiKey });

async function testNarrative(type: "podcast" | "audiobook", topic: string, targetEpisodes: number = 3) {
  console.log(`\n--- Generating ${type.toUpperCase()} about "${topic}" ---`);
  try {
    const promptFile = type === "podcast" ? "podcastNarrator.prompt" : "audiobookNarrator.prompt";
    // Adjust path to point to lyria/prompts
    const promptPath = path.join(process.cwd(), "prompts", promptFile);
    
    if (!fs.existsSync(promptPath)) {
      console.error(`Prompt file not found at: ${promptPath}`);
      return;
    }

    const fileContent = fs.readFileSync(promptPath, "utf-8");
    const parts = fileContent.split("---");
    const contentPart = parts.length > 2 ? parts[2] : fileContent;
    
    const systemMatch = contentPart.match(/\{\{role "system"\}\}([\s\S]*?)\{\{role "user"\}\}/);
    let systemInstruction = systemMatch ? systemMatch[1].trim() : "";
    
    systemInstruction = systemInstruction.replace(/\{\{systemInstruction\}\}/g, "");
    systemInstruction = systemInstruction.replace(/\{\{targetEpisodes\}\}/g, targetEpisodes.toString());
    systemInstruction = systemInstruction.replace(/\{\{#if previousContext\}\}[\s\S]*?\{\{\/if\}\}/, "");

    const response = await ai.models.generateContent({
      model: "gemini-2.5-flash",
      contents: `Generate a narrative series about: ${topic}`,
      config: {
        systemInstruction,
        responseMimeType: "application/json",
        responseSchema: {
          type: "object",
          properties: {
            newContext: { type: "string" },
            episodes: {
              type: "array",
              items: {
                type: "object",
                properties: {
                  title: { type: "string" },
                  script: { type: "string" }
                },
                required: ["title", "script"]
              }
            }
          },
          required: ["newContext", "episodes"]
        } as any
      },
    });

    const jsonText = response.text || "{}";
    const result = JSON.parse(jsonText);
    
    console.log(`Successfully generated ${result.episodes?.length || 0} episodes/chapters!`);
    console.log(`Context: ${result.newContext.substring(0, 100)}...`);
    
    result.episodes.forEach((ep: any, index: number) => {
      console.log(`\n[Episode ${index + 1}: ${ep.title}]`);
      const scriptText = ep.script || "";
      console.log(`Length: ${scriptText.length} characters`);
      console.log(`Excerpt: ${scriptText.substring(0, 150)}...\n`);
    });

  } catch (err: any) {
    console.error("Failed:", err.message || err);
  }
}

async function run() {
  await testNarrative("podcast", "The history of electronic music", 3);
  await testNarrative("audiobook", "A cyberpunk detective hunting a rogue AI", 3);
}

run();
