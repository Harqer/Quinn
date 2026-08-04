import { googleAI } from "@genkit-ai/google-genai";
import { genkit } from "genkit";

const ai = genkit({
    promptDir: './prompts'
});

async function run() {
    const p = ai.prompt('maveVision');
    try {
        const { stream, response } = await p.stream({
            image: "abc",
            locale: "en"
        });
        console.log("stream called with direct input");
    } catch(e) {
        console.log("Error with direct input:", e.message);
    }
}
run();
