import { execSync } from 'child_process';
import { lyriaProAgent } from "./functions/src/agents/lyriaProAgent.ts";
import { imageGenAgent } from "./functions/src/agents/imageGenAgent.ts";
import { genkit } from "genkit";
import { googleAI } from "@genkit-ai/google-genai";
import * as dotenv from "dotenv";

dotenv.config();

// Fetch API key dynamically
const apiKey = execSync("gcloud secrets versions access latest --secret=GEMINI_API_KEY --project=musically-studio", { encoding: 'utf-8' }).trim();
process.env.GEMINI_API_KEY = apiKey;

const ai = genkit({
    plugins: [googleAI()],
    promptDir: './prompts'
});

async function fetchTopTracks() {
    const response = await fetch('https://itunes.apple.com/us/rss/topsongs/limit=5/json');
    const data = await response.json();
    return data.feed.entry.map((entry: any) => {
        const previewLink = entry.link.find((l: any) => l.attributes && l.attributes.rel === 'enclosure');
        return {
            name: entry['im:name'].label,
            artist: entry['im:artist'].label,
            preview_url: previewLink ? previewLink.attributes.href : null
        };
    });
}

const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

async function runWithBackoff(fn: () => Promise<any>, retries = 3, backoffMs = 2000) {
    for (let i = 0; i < retries; i++) {
        try {
            return await fn();
        } catch (err) {
            console.error(`Attempt ${i + 1} failed:`, err);
            if (i === retries - 1) throw err;
            await sleep(backoffMs * Math.pow(2, i));
        }
    }
}

async function runSeeder() {
    try {
        const tracks = await fetchTopTracks();
        console.log(`Fetched ${tracks.length} tracks from iTunes Top Charts`);

        let count = 0;
        for (const track of tracks) {
            if (!track.preview_url) {
                console.log(`Skipping ${track.name} (no preview URL)`);
                continue;
            }

            console.log(`\n===========================================`);
            console.log(`Processing: ${track.name} by ${track.artist}`);

            try {
                // Generate a custom prompt based on the track name and artist
                const promptText = `A chart-topping hit track titled "${track.name}" in the style of ${track.artist}.`;
                
                console.log("-> Generating full track using Lyria 3 Pro...");
                const trackResult = await runWithBackoff(() => lyriaProAgent({
                    prompt: promptText,
                    apiKey: apiKey,
                    uid: "yPtzH6t6kPPOieY4uM04viV6czw1" // Community preset owner UID
                }));
                
                const trackId = trackResult.trackId;
                console.log(`   Success! Track ID: ${trackId}, Audio URL: ${trackResult.audioUrl}`);

                if (!trackId) {
                    console.error("   Error: lyriaProAgent did not return a valid trackId. Skipping cover generation.");
                    continue;
                }

                console.log("-> Generating cover image using Nano Banana 2 (Imagen 3)...");
                const imageResult = await runWithBackoff(() => imageGenAgent({
                    prompt: `Album cover for ${track.name} by ${track.artist}`,
                    apiKey: apiKey,
                    uid: "yPtzH6t6kPPOieY4uM04viV6czw1",
                    trackId: trackId,
                    preset: "glassmorphic_lumina"
                }));
                
                console.log(`   Success! Cover URL: ${imageResult.imageUrl}`);

                count++;
                if (count >= 3) break; // Limit to 3 for testing
            } catch (err) {
                console.error(`Failed to process ${track.name}:`, err);
                // Continue to the next track on failure
            }
        }
        
        console.log("Seeding complete!");
        
    } catch (err) {
        console.error("Seeder failed:", err);
    }
}

runSeeder();
