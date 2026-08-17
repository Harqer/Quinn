import * as admin from 'firebase-admin';
import { lyriaProAgent } from "./agents/lyriaProAgent";
import { imageGenAgent } from "./agents/imageGenAgent";
import * as logger from "firebase-functions/logger";

const SERVICE_ACCOUNT_PATH = process.env.GOOGLE_APPLICATION_CREDENTIALS;
if (!SERVICE_ACCOUNT_PATH) {
    logger.error("Missing GOOGLE_APPLICATION_CREDENTIALS. Needed for execution.");
    process.exit(1);
}

admin.initializeApp({
    credential: admin.credential.cert(SERVICE_ACCOUNT_PATH)
});

async function runSeeder() {
    try {
        logger.info("Starting preset seeder...");
        const apiKey = process.env.GEMINI_API_KEY;
        if (!apiKey) throw new Error("GEMINI_API_KEY is required");

        const presets = [
            "A smooth jazz saxophone solo over a lofi hiphop beat, 80bpm",
            "A high-energy synthwave track with driving bass and retro drums",
            "A gentle acoustic guitar and piano ballad with ethereal female vocals"
        ];

        for (const prompt of presets) {
            logger.info(`\nGenerating track for prompt: "${prompt}"...`);
            
            // Generate Track
            const trackResponse = await lyriaProAgent({ prompt }, { context: { apiKey, uid: "SYSTEM_SEEDER" } });
            logger.info("Track generated:", trackResponse.trackId);
            
            if (trackResponse.trackId) {
                // Generate Cover Art
                logger.info(`Generating cover art for track ${trackResponse.trackId}...`);
                await imageGenAgent({
                    prompt: `A beautiful Spotify-style album cover, gradient background, matching the vibe of: ${prompt}`,
                    trackId: trackResponse.trackId
                }, { context: { apiKey, uid: "SYSTEM_SEEDER" } });
                logger.info("Cover art generated successfully.");
            }
        }
        
        logger.info("\nSeeding complete!");
        process.exit(0);
    } catch (e) {
        logger.error("Seeder failed:", e);
        process.exit(1);
    }
}

runSeeder();
