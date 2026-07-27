import { initAi } from "../src/services/ai.js";
import { musicService } from "../src/services/MusicService.js";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import dotenv from "dotenv";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

dotenv.config({ path: path.join(__dirname, "../.env") });

const PRESET_VIBES = ["Ambient Chill", "Cyberpunk Synthwave", "Cinematic Orchestral"];

async function main() {
  console.log("Initializing AI...");
  await initAi();

  const presetsDir = path.join(__dirname, "../assets/presets");
  fs.mkdirSync(presetsDir, { recursive: true });

  console.log("Generating presets using Lyria model...");
  const generatedPresets = [];

  for (const vibe of PRESET_VIBES) {
    try {
      console.log(`Generating vibe: ${vibe}`);
      const result = await musicService.generateMusicDirectly(undefined, vibe, "latest");
      console.log(`Generated:`, result);

      const presetData = {
        id: `preset_${Date.now()}_${Math.random().toString(36).substring(7)}`,
        vibe,
        data: result,
        createdAt: new Date().toISOString(),
      };

      const filename = `${vibe.toLowerCase().replace(/\s+/g, "_")}.json`;
      const filepath = path.join(presetsDir, filename);
      
      fs.writeFileSync(filepath, JSON.stringify(presetData, null, 2));
      console.log(`Saved preset to ${filepath}`);
      generatedPresets.push(presetData);
    } catch (err) {
      console.error(`Failed to generate preset for ${vibe}:`, err);
    }
  }

  console.log("Finished generating presets.");
}

main().catch((err) => {
  console.error("Script failed:", err);
  process.exit(1);
});
