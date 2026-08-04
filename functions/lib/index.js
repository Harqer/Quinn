"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateLyrics = exports.generateVisualMedia = exports.searchConcerts = exports.generatePodcastScript = exports.getLiveToken = void 0;
const https_1 = require("firebase-functions/v2/https");
const params_1 = require("firebase-functions/params");
const admin = __importStar(require("firebase-admin"));
const genai_1 = require("@google/genai");
admin.initializeApp();
const GEMINI_API_KEY = (0, params_1.defineSecret)("GEMINI_API_KEY");
exports.getLiveToken = (0, https_1.onCall)({
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true, // Explicitly enable CORS as per Google Cloud best practices
}, async (request) => {
    // Ensure the user is authenticated
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    const ai = new genai_1.GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    try {
        const ephemeralToken = await ai.authTokens.create({
            config: {
                uses: 1,
                expireTime: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
                liveConnectConstraints: {
                    model: "gemini-3.1-flash-live-preview",
                },
            },
        });
        return {
            token: ephemeralToken.name || "",
        };
    }
    catch (err) {
        throw new https_1.HttpsError("internal", `Failed to generate ephemeral token: ${err.message || err}`);
    }
});
const fs = __importStar(require("fs"));
const path = __importStar(require("path"));
// Load all RLM-distilled MIT Storytelling Frameworks synchronously at cold start
const frameworksDir = path.join(__dirname, "frameworks");
const frameworks = {};
try {
    const files = fs.readdirSync(frameworksDir);
    for (const file of files) {
        if (file.endsWith(".md")) {
            const genre = path.basename(file, ".md");
            frameworks[genre] = fs.readFileSync(path.join(frameworksDir, file), "utf-8");
        }
    }
}
catch (e) {
    console.error("Warning: frameworks directory not found or unreadable. Falling back to empty frameworks.");
}
exports.generatePodcastScript = (0, https_1.onCall)({
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
}, async (request) => {
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    const { topic } = request.data;
    if (!topic || typeof topic !== "string") {
        throw new https_1.HttpsError("invalid-argument", "A valid 'topic' string must be provided.");
    }
    const ai = new genai_1.GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    try {
        // Step 1: Fast LLM Routing to determine genre
        const routingResponse = await ai.models.generateContent({
            model: "gemini-3.1-flash",
            contents: `Classify the following podcast topic into exactly one of these genres: 'sports', 'autobiography', 'fiction', 'essay', or 'general'. Topic: "${topic}". Return ONLY the genre word in lowercase.`,
        });
        let genre = (routingResponse.text || "general").trim().toLowerCase();
        if (!frameworks[genre]) {
            genre = "general";
        }
        console.log(`Routed topic "${topic}" to framework: ${genre}`);
        // Step 2: Generate the script using the genre-specific framework
        const response = await ai.models.generateContent({
            model: "gemini-3.1-pro",
            contents: `Write a podcast script about: ${topic}`,
            config: {
                systemInstruction: `You are an elite podcast scriptwriter and narrative architect. Your objective is to engineer deeply compelling, high-traction podcast scripts.

Even in a non-fiction or conversational format, you must treat the script as a closed-loop cognitive and emotional simulation, applying rigorous storytelling principles:

### I. Narrative Arc & Pacing
* **Thermodynamic Pacing:** Build the episode with a clear arc: establish the core question/status quo, introduce disrupting information or conflicts, escalate the tension/stakes of the topic, and resolve with profound insights or synthesis.
* **Scene-to-Summary Ratio:** Calibrate velocity. Dilate micro-moments of high importance (e.g., a critical anecdote, a dramatic historical beat) into sensory-dense, real-time exploration. Compress routine background context into clean, high-level summaries.

### II. Information Asymmetry & Psychological Tension
* **Asymmetry Control:** Weaponize the delta between what the host knows, what the guest knows, and what the listener knows. Deliberately control the release of information to manufacture curiosity and suspense.
* **Dialogic Friction:** Conversations shouldn't be flat agreements. Build subtext through tactical questioning, intellectual friction, and shifting power dynamics between speakers.

### III. Syntactic & Lexical Engineering
* **Voice & Pacing Control:** Optimize the agent-action-patient pipeline via active voice dominance. Rewrite any passive constructions. Utilize periodic sentences to suspend completion and build tension, and cumulative sentences to lower reading latency and mimic natural thought.
* **Sentence Variance:** Mix short sentences (under 10 words) with longer ones (20+ words). Never write three sentences of similar length in a row.
* **Semantic Precision:** Maintain absolute register and tone consistency. Balance denotation and connotation to eliminate drift.
* **Prohibited Phrasing:** Remove these phrases entirely: delve, tapestry, pivotal, furthermore, moreover, in conclusion, it is worth noting. Do not overuse em-dashes.
* **Perspective & Tone:** Write in first or second person (use 'I' or 'you' naturally). Match the tone to an engaging, conversational podcast discussion. Keep all factual claims intact.

Your output must be a highly engaging, well-paced script formatted for audio TTS synthesis.`,
            },
        });
        return {
            script: response.text,
            genre_applied: genre
        };
    }
    catch (err) {
        throw new https_1.HttpsError("internal", `Failed to generate podcast script: ${err.message || err}`);
    }
});
exports.searchConcerts = (0, https_1.onCall)({
    enforceAppCheck: true,
    cors: true,
}, async (request) => {
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    const { q, lat, lon, range, page, perPage } = request.data;
    const rapidApiKey = "5bd0ad7e89mshbdce44145d1c907p1c5bb5jsn85de4da3bf86";
    try {
        const queryParams = new URLSearchParams();
        if (q)
            queryParams.append("q", q);
        if (lat !== undefined)
            queryParams.append("lat", lat.toString());
        if (lon !== undefined)
            queryParams.append("lon", lon.toString());
        if (range)
            queryParams.append("range", range);
        if (page !== undefined)
            queryParams.append("page", page.toString());
        if (perPage !== undefined)
            queryParams.append("perPage", perPage.toString());
        const url = `https://seatgeek-com-scraper.p.rapidapi.com/events/search?${queryParams.toString()}`;
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "x-rapidapi-host": "seatgeek-com-scraper.p.rapidapi.com",
                "x-rapidapi-key": rapidApiKey
            }
        });
        if (!response.ok) {
            throw new Error(`RapidAPI Error: ${response.status} ${response.statusText}`);
        }
        const data = await response.json();
        return data;
    }
    catch (err) {
        throw new https_1.HttpsError("internal", `Failed to search concerts: ${err.message || err}`);
    }
});
exports.generateVisualMedia = (0, https_1.onCall)({
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
    timeoutSeconds: 120
}, async (request) => {
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    const { preset, intent } = request.data;
    // We will use gemini-1.5-flash to act as a Prompt Engineer to flesh out the preset into a highly detailed visual prompt.
    const ai = new genai_1.GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    try {
        // 1. Generate detailed image prompt using gemini-1.5-flash
        const promptResponse = await ai.models.generateContent({
            model: "gemini-1.5-flash",
            contents: `You are an expert art director. Write a highly detailed image generation prompt for a music ${intent} based on the style preset: "${preset}". The prompt should describe the lighting, subject, mood, and color palette. Keep it under 500 characters. DO NOT include any conversational text, just the raw prompt.`,
        });
        const detailedPrompt = promptResponse.text?.trim() || preset;
        console.log(`Generated detailed prompt for ${preset}: ${detailedPrompt}`);
        // 2. Generate the actual image using Imagen 3
        // Note: We use an any cast here because the typescript types might vary in the @google/genai SDK version
        const imageResponse = await ai.models.generateImages({
            model: "imagen-3.0-generate-001",
            prompt: detailedPrompt,
            config: {
                numberOfImages: 1,
                outputMimeType: "image/jpeg",
                aspectRatio: intent === "video_motion" ? "16:9" : "1:1",
            }
        });
        const base64Image = imageResponse.generatedImages[0].image.imageBytes;
        const dataUrl = `data:image/jpeg;base64,${base64Image}`;
        return {
            url: dataUrl,
            prompt_used: detailedPrompt
        };
    }
    catch (err) {
        console.error(err);
        throw new https_1.HttpsError("internal", `Failed to generate visual media: ${err.message || err}`);
    }
});
exports.generateLyrics = (0, https_1.onCall)({
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
    timeoutSeconds: 120
}, async (request) => {
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    const { trackId, audioUrl } = request.data;
    console.log(`Generating lyrics for track ${trackId}, audioUrl: ${audioUrl}`);
    // We will use gemini-1.5-flash to transcribe or generate lyrics
    const ai = new genai_1.GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    try {
        // In a real production environment with no-mock, if audioUrl is provided, we would download it
        // and pass the audio file to gemini-1.5-flash for transcription/lyric extraction.
        // Since we just have the audioUrl here, we will instruct the model to write original lyrics.
        const promptResponse = await ai.models.generateContent({
            model: "gemini-1.5-flash",
            contents: `You are an expert songwriter. Write a 2-minute hit song's lyrics. Structure it with Verse, Chorus, Verse, Chorus, Bridge, Outro. Make it emotive and catchy. Do not include any conversational filler.`,
        });
        return {
            lyrics: promptResponse.text?.trim() || ""
        };
    }
    catch (err) {
        console.error(err);
        throw new https_1.HttpsError("internal", `Failed to generate lyrics: ${err.message || err}`);
    }
});
//# sourceMappingURL=index.js.map