import { GoogleGenAI } from "@google/genai";
import * as fs from "fs";
import * as path from "path";

const apiKey = process.env.GEMINI_API_KEY;
if (!apiKey) {
  console.error("Please set GEMINI_API_KEY environment variable.");
  process.exit(1);
}

const ai = new GoogleGenAI({ apiKey });

async function generatePresets() {
  console.log("Generating Spotify Top 2026 Preset Prompts...");
  
  // We use gemini-2.5-flash as the fallback due to quota limits on pro models
  const response = await ai.models.generateContent({
    model: "gemini-2.5-flash",
    contents: `Generate 5 top chart music genres/styles that would be on Spotify's Top 100 in 2026. 
For each, provide a 'name' (the genre/style), a 'promptFragment' (a highly detailed music generation prompt for an AI audio model like Lyria, under 200 chars), and a 'visionPrompt' (a visual description for the album cover art).
Format as JSON array.`,
    config: {
      responseMimeType: "application/json",
      responseSchema: {
        type: "array",
        items: {
          type: "object",
          properties: {
            name: { type: "string" },
            promptFragment: { type: "string" },
            visionPrompt: { type: "string" }
          },
          required: ["name", "promptFragment", "visionPrompt"]
        }
      } as any
    }
  });

  const presets = JSON.parse(response.text || "[]");
  console.log(`Generated ${presets.length} presets.`);
  
  for (const preset of presets) {
    console.log(`\nProcessing: ${preset.name}`);
    console.log(`Generating cover art for: ${preset.name}`);
    
    try {
      const imageResponse: any = await (ai.models as any).generateImages({
        model: "imagen-3.0-generate-001",
        prompt: preset.visionPrompt,
        config: {
          numberOfImages: 1,
          outputMimeType: "image/jpeg",
          aspectRatio: "1:1",
        }
      });
      
      const base64Image = imageResponse.generatedImages[0].image.imageBytes;
      const dataUrl = `data:image/jpeg;base64,${base64Image}`;
      preset.imageUrl = dataUrl;
      console.log("Image generated successfully.");
      
    } catch (err: any) {
      console.error(`Failed to generate image for ${preset.name}:`, err.message || err);
      preset.imageUrl = "";
    }
  }

  const outputPath = path.join(process.cwd(), "presets.json");
  fs.writeFileSync(outputPath, JSON.stringify(presets, null, 2));
  console.log(`\nPresets saved to ${outputPath}`);
}

generatePresets().catch(console.error);
