import { execSync } from 'child_process';
import { generate } from '@genkit-ai/ai';
import { gemini15Flash } from '@genkit-ai/googleai';
import * as fs from 'fs';
import * as path from 'path';

// 1. Fetch Secrets
console.log("Fetching Spotify secrets from GCP Secret Manager...");
const clientId = execSync("gcloud secrets versions access latest --secret=SPOTIFY_CLIENT_ID", { encoding: 'utf-8' }).trim();
const clientSecret = execSync("gcloud secrets versions access latest --secret=SPOTIFY_CLIENT_SECRET", { encoding: 'utf-8' }).trim();

async function getSpotifyToken() {
    const response = await fetch('https://accounts.spotify.com/api/token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Basic ' + Buffer.from(clientId + ':' + clientSecret).toString('base64')
        },
        body: 'grant_type=client_credentials'
    });
    const data = await response.json();
    return data.access_token;
}

async function fetchPlaylistTracks(token: string, playlistId: string) {
    // "Today's Top Hits" = 37i9dQZF1DXcBWIGoYBM5M
    // "Top 50 - USA" = 37i9dQZEVXbLp5XoPON0wI
    const response = await fetch(`https://api.spotify.com/v1/playlists/${playlistId}/tracks?limit=10`, {
        headers: { 'Authorization': 'Bearer ' + token }
    });
    if (!response.ok) {
        const text = await response.text();
        throw new Error(`Spotify API error: ${response.status} ${response.statusText} - ${text}`);
    }
    const data = await response.json();
    return data.items.map((item: any) => item.track);
}

async function runSeeder() {
    try {
        const token = await getSpotifyToken();
        console.log("Got Spotify access token");

        // Top 50 - USA
        const tracks = await fetchPlaylistTracks(token, '37i9dQZEVXbLp5XoPON0wI');
        console.log(`Fetched ${tracks.length} tracks from playlist`);

        const promptTemplate = fs.readFileSync(path.join(__dirname, 'prompts', 'dissectAudio.prompt'), 'utf-8');

        let count = 0;
        for (const track of tracks) {
            if (!track.preview_url) {
                console.log(`Skipping ${track.name} (no preview URL)`);
                continue;
            }

            console.log(`Processing: ${track.name} by ${track.artists[0].name}`);

            // Download MP3 preview
            const previewResponse = await fetch(track.preview_url);
            const arrayBuffer = await previewResponse.arrayBuffer();
            const buffer = Buffer.from(arrayBuffer);
            const base64Audio = buffer.toString('base64');
            const dataUri = `data:audio/mp3;base64,${base64Audio}`;

            console.log("Generating metadata via Genkit...");
            
            // Execute Genkit
            try {
                const response = await generate({
                    model: gemini15Flash,
                    prompt: "Extract the exact prompt, genre, and metadata to replicate this track's musical vibe exactly.",
                    system: promptTemplate,
                    context: [
                        { media: { url: dataUri, contentType: 'audio/mp3' } }
                    ],
                    config: {
                        temperature: 0.2
                    }
                });

                const rawJson = response.text().replace(/```json/g, '').replace(/```/g, '').trim();
                const analysis = JSON.parse(rawJson);

                console.log("Analysis Output:", analysis);

                // Insert into Database
                const durationMs = 30000;
                
                // Note: we can use the original Spotify preview URL or a generated one.
                const mutationVars = {
                    title: analysis.replicatedSong.trackName || track.name,
                    audioUrl: track.preview_url,
                    durationMs: durationMs,
                    prompt: analysis.replicatedSong.generationPrompt,
                    isCommunity: true
                };

                console.log("Executing Data Connect mutation...");
                const execResult = execSync(`npx -y firebase-tools@latest dataconnect:execute dataconnect/connector/mutations.gql SeedTrack --variables '${JSON.stringify(mutationVars)}'`, { encoding: 'utf-8' });
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
