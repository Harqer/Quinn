import * as fs from 'fs';
import * as path from 'path';
import { execSync } from 'child_process';
import * as admin from 'firebase-admin';
import { imageGenAgent } from '../src/agents/imageGenAgent';

const PRESETS_FILE = path.join(__dirname, 'presets.json');

const SERVICE_ACCOUNT_PATH = process.env.GOOGLE_APPLICATION_CREDENTIALS;
if (!SERVICE_ACCOUNT_PATH) {
    console.warn("Missing GOOGLE_APPLICATION_CREDENTIALS. Firebase Admin might fail if not in default environment.");
} else if (admin.apps.length === 0) {
    admin.initializeApp({
        credential: admin.credential.cert(SERVICE_ACCOUNT_PATH)
    });
} else {
    admin.initializeApp();
}

async function runUpload() {
    if (!fs.existsSync(PRESETS_FILE)) {
        console.error("Presets file not found!");
        return;
    }

    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey) {
        console.warn("GEMINI_API_KEY is missing. Image generation will fail.");
    }

    const rawData = fs.readFileSync(PRESETS_FILE, 'utf-8');
    const presets = JSON.parse(rawData);

    let count = 0;
    for (const preset of presets) {
        if (!preset.audioUrl) {
            console.log(`Skipping ${preset.genre} (No audio URL)`);
            continue;
        }

        console.log(`Uploading preset: ${preset.genre}`);

        // Insert as Track
        const trackMutationVars = {
            title: preset.genre,
            audioUrl: preset.audioUrl,
            durationMs: 30000,
            prompt: preset.description,
            isCommunity: true,
            ownerUid: "yPtzH6t6kPPOieY4uM04viV6czw1" // Example system/admin UID
        };

        let trackId = `track_${Date.now()}_${count}`; // Fallback ID

        try {
            console.log("  Executing SeedTrack mutation...");
            const output = execSync(`npx -y firebase-tools@latest dataconnect:execute dataconnect/connector/mutations.gql SeedTrack --variables '${JSON.stringify(trackMutationVars).replace(/'/g, "'\\''")}'`, { encoding: 'utf-8', stdio: 'pipe' });
            console.log("  Successfully added to Tracks.");
            
            // Extract generated track ID from CLI JSON output
            const match = output.match(/\{[\s\S]*\}/);
            if (match) {
                const parsed = JSON.parse(match[0]);
                if (parsed.data?.track_insert?.id) {
                    trackId = parsed.data.track_insert.id;
                    console.log(`  Generated track ID: ${trackId}`);
                }
            }
        } catch (err: any) {
            console.error("  Failed to insert Track:", err.stderr || err.message);
        }

        let imageUrl = preset.imageUrl || null;
        if (!imageUrl && apiKey) {
            console.log(`  Generating cover art for ${preset.genre} (track: ${trackId})...`);
            try {
                const response = await imageGenAgent({
                    prompt: `A beautiful Spotify-style album cover, matching the vibe of: ${preset.description}`,
                    trackId: trackId,
                    apiKey: apiKey,
                    uid: "SYSTEM_SEEDER"
                });
                imageUrl = response.imageUrl;
                console.log(`  Successfully generated cover art: ${imageUrl}`);
            } catch (err: any) {
                console.error("  Failed to generate cover art:", err.message);
            }
        }

        // Insert as AIPreset
        const aiPresetVars = {
            name: preset.genre,
            promptFragment: preset.description,
            imageUrl: imageUrl
        };

        try {
            console.log("  Executing SeedAIPreset mutation...");
            execSync(`npx -y firebase-tools@latest dataconnect:execute dataconnect/connector/mutations.gql SeedAIPreset --variables '${JSON.stringify(aiPresetVars).replace(/'/g, "'\\''")}'`, { encoding: 'utf-8', stdio: 'pipe' });
            console.log("  Successfully added to AIPresets.");
        } catch (err: any) {
            console.error("  Failed to insert AIPreset:", err.stderr || err.message);
        }

        count++;
    }

    console.log(`Uploaded ${count} presets successfully!`);
}

runUpload().catch(console.error);
