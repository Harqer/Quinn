import { initSecrets } from './src/config/secrets.js';
import { musicService } from './src/services/MusicService.js';
import { initAi } from './src/services/ai.js';

async function test() {
  await initSecrets();
  await initAi();
  try {
    const result = await musicService.generateMusicDirectly(undefined, "A fast electronic song");
    console.log("SUCCESS:");
    console.log(JSON.stringify(result, null, 2));
  } catch (e: any) {
    console.error("FAILED:");
    console.error(e.message || e);
  }
}
test();
