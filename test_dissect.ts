import { genkit } from "genkit";
import { googleAI } from "@genkit-ai/google-genai";
import * as fs from 'fs';

const ai = genkit({
    plugins: [googleAI()],
    promptDir: './prompts'
});

async function run() {
    let audioUrl = process.argv[2];
    if (!audioUrl) {
        console.error("Please provide an audio URL, e.g. a local file path or http URL");
        process.exit(1);
    }
    
    if (!audioUrl.startsWith("http") && !audioUrl.startsWith("gs://")) {
        const buffer = fs.readFileSync(audioUrl);
        const base64 = buffer.toString('base64');
        audioUrl = `data:audio/mp4;base64,${base64}`;
        console.log("Loaded local file as base64 data URI");
    }


    try {
        console.log(`Analyzing audio from: ${audioUrl}`);
        const p = ai.prompt('dissectAudio');
        const { text } = await p({ audioUrl });
        console.log("\nDissection complete. Generated Prompt:");
        console.log("-".repeat(60));
        console.log(text);
        console.log("-".repeat(60));
    } catch(e: any) {
        console.log("Error:", e.message);
    }
}
run();
