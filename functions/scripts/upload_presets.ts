import * as fs from 'fs';
import * as path from 'path';
import { execSync } from 'child_process';

const PRESETS_FILE = path.join(__dirname, 'presets.json');

function runUpload() {
    if (!fs.existsSync(PRESETS_FILE)) {
        console.error("Presets file not found!");
        return;
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

        try {
            console.log("  Executing SeedTrack mutation...");
            execSync(`npx -y firebase-tools@latest dataconnect:execute dataconnect/connector/mutations.gql SeedTrack --variables '${JSON.stringify(trackMutationVars).replace(/'/g, "'\\''")}'`, { encoding: 'utf-8', stdio: 'pipe' });
            console.log("  Successfully added to Tracks.");
        } catch (err: any) {
            console.error("  Failed to insert Track:", err.stderr || err.message);
        }

        // Insert as AIPreset
        const aiPresetVars = {
            name: preset.genre,
            promptFragment: preset.description,
            imageUrl: preset.imageUrl || null
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

runUpload();
