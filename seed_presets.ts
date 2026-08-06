import { execSync } from 'child_process';
import { genkit } from "genkit";
import { googleAI } from "@genkit-ai/google-genai";
import * as fs from 'fs';
import * as path from 'path';

// Fetch API key dynamically
const apiKey = execSync("gcloud secrets versions access latest --secret=GEMINI_API_KEY", { encoding: 'utf-8' }).trim();
process.env.GEMINI_API_KEY = apiKey;

const ai = genkit({
    plugins: [googleAI()],
    promptDir: './prompts'
});

async function fetchTopTracks() {
    const response = await fetch('https://itunes.apple.com/us/rss/topsongs/limit=10/json');
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

            console.log(`Processing: ${track.name} by ${track.artist}`);

            // Download M4A preview
            const previewResponse = await fetch(track.preview_url);
            const arrayBuffer = await previewResponse.arrayBuffer();
            const buffer = Buffer.from(arrayBuffer);
            const base64Audio = buffer.toString('base64');
            const dataUri = `data:audio/mp4;base64,${base64Audio}`;

            console.log("Generating metadata via Genkit...");
            
            // Execute Genkit using the registered prompt
            try {
                const dissectPrompt = ai.prompt('dissectAudio');
                const { text } = await dissectPrompt({ audioUrl: dataUri });

                const rawJson = text.replace(/```json/g, '').replace(/```/g, '').trim();
                const analysis = JSON.parse(rawJson);

                console.log("Analysis Output:", analysis);

                // Insert into Database
                const durationMs = 30000;
                
                const mutationVars = {
                    title: analysis.replicatedSong?.trackName || track.name,
                    audioUrl: track.preview_url,
                    durationMs: durationMs,
                    prompt: analysis.replicatedSong?.generationPrompt || "",
                    isCommunity: true,
                    ownerUid: "yPtzH6t6kPPOieY4uM04viV6czw1"
                };

                console.log("Executing Data Connect mutation...");
                const execResult = execSync(`npx -y firebase-tools@latest dataconnect:execute dataconnect/connector/mutations.gql SeedTrack --variables '${JSON.stringify(mutationVars).replace(/'/g, "'\\''")}'`, { encoding: 'utf-8' });
                console.log("Success");

                count++;
                if (count >= 3) break; // Do 3 for now to test
            } catch (err) {
                console.error("Failed to dissect or insert track:", err);
            }
        }
        
    } catch (err) {
        console.error("Seeder failed:", err);
    }
}

runSeeder();
