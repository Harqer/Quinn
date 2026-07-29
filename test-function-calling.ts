import { getSecret, initSecrets } from './src/config/secrets.js';
import { musicService } from './src/services/MusicService.js';
import { initAi } from './src/services/ai.js';

async function main() {
  console.log("Loading secrets...");
  await initSecrets();
  
  const apiKey = getSecret("GEMINI_API_KEY") as string;
  if (!apiKey) {
    console.error("GEMINI_API_KEY not found in secrets.");
    process.exit(1);
  }
  
  console.log("Initializing AI...");
  await initAi();

  console.log("Secret loaded successfully. Testing generateMusicDirectly...");

  try {
    const result = await musicService.generateMusicDirectly(
      undefined,
      "Fast-paced high-energy electronic track with driving synth basslines."
    );
    console.log("Generation Result:", result);
  } catch (error) {
    console.error("Error generating music:", error);
  }
}

main().catch(console.error);
