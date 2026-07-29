import dotenv from "dotenv";
dotenv.config();

import { initAi } from "./src/services/ai.js";
import { MusicService } from "./src/services/MusicService.js";

async function run() {
  await initAi();
  const service = new MusicService();
  console.log("Starting Lyria RealTime generation...");
  const result = await service.generateMusicDirectly(undefined, "A chill lofi beat with a relaxing piano melody");
  console.log("Result keys:", Object.keys(result));
  if (result.audioUrl) {
    console.log("Audio URL starts with:", result.audioUrl.substring(0, 50));
  }
}
run().catch(console.error);
