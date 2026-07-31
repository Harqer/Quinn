import { gemini15Flash, googleAI } from "@genkit-ai/google-genai";
import { genkit, z } from "genkit";
import { cacheVisionResult, getCachedVisionResult } from "../config/redis.js";
import crypto from "crypto";
import logger from "../config/logger.js";
import { getSecret } from "../config/secrets.js";

const ai = genkit({
    plugins: [googleAI({ apiKey: getSecret("GEMINI_API_KEY") as string })],
    model: gemini15Flash, // Setting a default model
});

// Zod schemas for structured output
const DirectorOutputSchema = z.object({
    modality: z.enum(['music', 'podcast', 'audiobook', 'mixed']),
    visualIntent: z.enum(['none', 'cover_art', 'video_motion']),
    reasoning: z.string()
});

const VisualAnalyzerOutputSchema = z.object({
    visionDescription: z.string()
});

const PodcastOutputSchema = z.object({
    script: z.string()
});

export const maveVisionFlow = ai.defineFlow(
    {
        name: "maveVisionFlow",
        inputSchema: z.object({
            image: z.string(),
            locale: z.string().optional()
        }),
        outputSchema: VisualAnalyzerOutputSchema,
    },
    async (input, { sendChunk }) => {
        const imageHash = crypto.createHash("md5").update(input.image).digest("hex");
        const cached = await getCachedVisionResult(imageHash);

        if (cached) {
            logger.info("[GENKIT_FLOW] Cache hit for vision analysis", { hash: imageHash });
            return { visionDescription: cached };
        }

        const prompt = `Analyze the environment, mood, and visual vibes in this POV stream. Use universal musical and narrative terminology for description. Do not generate lyrics. Avoid any technical jargon like 'neon' or 'proxy'. You support 70+ languages and should respond in the language corresponding to this locale: ${input.locale || 'en'}.`;

        const { text } = await ai.generate({
            model: gemini15Flash,
            prompt: [{ text: prompt }, { media: { url: `data:image/jpeg;base64,${input.image}` } }],
            onChunk: (chunk) => {
                sendChunk(chunk.text);
            }
        });

        await cacheVisionResult(imageHash, text);
        return { visionDescription: text };
    }
);

export const directorFlow = ai.defineFlow(
    {
        name: "directorFlow",
        inputSchema: z.object({
            visionDescription: z.string(),
            userFeedback: z.string().optional(),
            locale: z.string().optional()
        }),
        outputSchema: DirectorOutputSchema,
    },
    async (input, { sendChunk }) => {
        const prompt = `You are the Mave Orchestra Director. Your role is to reason about the visual atmosphere and user intent to orchestrate a world-class audio-visual experience.
Reason naturally about what you see and what the user wants.
If the user feedback starts with "Production Request:", prioritize fulfilling that specific media generation intent.
If the user asks for a song, vibe, or instrument, set the modality to 'music'.
If the user asks for a story or narration, set it to 'audiobook' or 'podcast'.
If the user asks for cover art or a music video, identify that intent.
Speak naturally to the user about your creative choices. Support 70+ languages. Locale: ${input.locale || 'en'}.

Visual Atmosphere: ${input.visionDescription}
User Feedback: ${input.userFeedback || "Compose real-time music for this atmosphere"}`;

        const { output } = await ai.generate({
            model: gemini15Flash,
            prompt: prompt,
            output: { schema: DirectorOutputSchema },
            onChunk: (chunk) => {
                // Not sending structured JSON chunking back to client here to avoid parsing issues mid-stream
            }
        });

        if (!output) {
            throw new Error("DirectorFlow produced no output");
        }
        return output;
    }
);

export const podcastNarratorFlow = ai.defineFlow(
    {
        name: "podcastNarratorFlow",
        inputSchema: z.object({
            visionDescription: z.string(),
            userFeedback: z.string().optional(),
            modality: z.enum(['music', 'podcast', 'audiobook', 'mixed']),
            locale: z.string().optional()
        }),
        outputSchema: PodcastOutputSchema,
    },
    async (input, { sendChunk }) => {
        const feedbackContext = input.userFeedback ? `\nUser Input/Feedback: ${input.userFeedback}` : "";
        const isAudiobook = input.modality === 'audiobook';
        const systemPrompt = isAudiobook 
            ? `You are Mave, an elite Audiobook Narrator and Author. Based on the visual vibe and user instructions, generate a highly descriptive and immersive story chapter segment (3-5 paragraphs). No technical jargon. You MUST respond in the language corresponding to this locale: ${input.locale || 'en'}.`
            : `You are Mave, the narrator. Based on the visual vibe and user instructions, generate a short, engaging narrative segment (2-4 sentences) for 'Mave POV'. If user gave feedback, acknowledge it naturally in your tone. No technical jargon. You MUST respond in the language corresponding to this locale: ${input.locale || 'en'}.`;

        const { text } = await ai.generate({
            model: gemini15Flash,
            system: systemPrompt,
            prompt: `Visual Vibe: ${input.visionDescription}${feedbackContext}`,
            onChunk: (chunk) => {
                sendChunk(chunk.text);
            }
        });

        return { script: text };
    }
);

export const generateMusicFlow = ai.defineFlow(
    {
        name: "generateMusicFlow",
        inputSchema: z.object({
            promptText: z.string(),
        }),
        outputSchema: z.object({
            trackName: z.string(),
            artistName: z.string(),
            audioUrl: z.string(),
        }),
    },
    async (input) => {
        const { encodeWAV } = await import("../utils/wav.js");
        
        let trackName = "Generated Track";
        let artistName = "Mave AI";

        try {
            const metaRes = await ai.generate({
                model: gemini15Flash,
                prompt: `Generate a JSON object with 'trackName' and 'artistName' for a song described as: ${input.promptText}. Do not use markdown tags, just return the JSON.`,
                output: { format: "json" },
            });
            const metaData = typeof metaRes.output === 'object' ? metaRes.output as any : JSON.parse(metaRes.text || "{}");
            if (metaData.trackName) trackName = metaData.trackName;
            if (metaData.artistName) artistName = metaData.artistName;
        } catch (e) {
            logger.warn("Failed to generate metadata for track via Genkit", e);
        }

        try {
            // Using ai.generate with the model string to support Lyria
            const lyriaRes = await ai.generate({
                model: "lyria-3-pro-preview",
                prompt: input.promptText,
            });

            const audioChunks: Buffer[] = [];
            // Parse custom parts if needed, or simply extract base64 from response if inlineData is supported in Genkit
            const parts = lyriaRes.message?.content || [];
            
            // Genkit message part structure is different. It uses .media for audio inline data typically, or custom part.
            // But we will access raw candidate if possible, or assume it's in the text if it's base64, or use custom response parsing.
            // Let's use the underlying raw response if available, or just check the parts array.
            
            // Note: If lyria-3-pro-preview returns raw base64 string we can parse it.
            // Let's check for custom parts in the Genkit response object.
            const customParts = (lyriaRes as any).raw?.candidates?.[0]?.content?.parts || [];
            for (const part of customParts) {
                if (part.inlineData?.data) {
                    audioChunks.push(Buffer.from(part.inlineData.data, "base64"));
                }
            }

            let audioUrl = "";
            if (audioChunks.length > 0) {
                const combinedPcm = Buffer.concat(audioChunks);
                const wavBuffer = encodeWAV(combinedPcm, 2, 48000);
                audioUrl = `data:audio/wav;base64,${wavBuffer.toString("base64")}`;
            } else {
                throw new Error("No audio returned from Lyria 3 via Genkit");
            }

            return {
                trackName,
                artistName,
                audioUrl,
            };
        } catch (err) {
            logger.error("Failed to generate music via Lyria 3 Pro in Genkit", err);
            throw err;
        }
    }
);

export const lyriaRealtimeFlow = ai.defineFlow(
    {
        name: "lyriaRealtimeFlow",
        inputSchema: z.object({
            input: z.string(),
            image: z.string().optional(),
        }),
        outputSchema: z.object({
            output_text: z.string(),
            output_audio: z.any(),
        }),
    },
    async (input, { sendChunk }) => {
        const { encodeWAV } = await import("../utils/wav.js");
        const audioChunks: number[][] = [];
        let fullText = "";
        
        try {
            const promptContent: any[] = [
                "You are Lyria RealTime, generating ambient music. " + input.input
            ];
            if (input.image) {
                promptContent.push({ media: { url: `data:image/jpeg;base64,${input.image}` } });
            }

            const stream = await ai.generateStream({
                model: "lyria-realtime-exp",
                prompt: promptContent,
                config: {
                    // Genkit wraps `@google/genai` so we can pass arbitrary config fields 
                    // if the underlying plugin supports it. For now, pass what we can or rely on raw.
                },
                // For raw Lyria realtime config in the googleAI plugin, you might need custom options.
            });

            for await (const chunk of stream.stream) {
                // If it's a raw google-genai chunk, we parse it
                const parts = (chunk as any).raw?.candidates?.[0]?.content?.parts || [];
                for (const part of parts) {
                    if (part.thought && part.text) {
                        sendChunk({ type: "mave_thinking", text: `\n[THINKING] ${part.text}\n` });
                    } else if (part.text) {
                        fullText += part.text;
                        sendChunk({ type: "mave_thinking", text: part.text });
                    }
                    
                    if (part.inlineData && part.inlineData.mimeType?.startsWith("audio/") && part.inlineData.data) {
                        const audioBuffer = Buffer.from(part.inlineData.data, "base64");
                        const intArray = new Int16Array(
                            audioBuffer.buffer,
                            audioBuffer.byteOffset,
                            audioBuffer.length / Int16Array.BYTES_PER_ELEMENT
                        );
                        audioChunks.push(Array.from(intArray));
                        
                        sendChunk({ type: "mave_thinking", text: "." });
                    }
                }
            }

            if (audioChunks.length > 0) {
                const flatArray = new Int16Array(audioChunks.flat());
                const pcmBuffer = Buffer.from(flatArray.buffer, flatArray.byteOffset, flatArray.byteLength);
                const wavBuffer = encodeWAV(pcmBuffer, 1, 48000);
                const base64Audio = wavBuffer.toString("base64");
                return {
                    output_text: fullText || "Lyria RealTime Ambient Generate",
                    output_audio: { data: `data:audio/wav;base64,${base64Audio}` }
                };
            } else {
                return { output_text: fullText || "No audio generated", output_audio: null };
            }
        } catch (err) {
            logger.error("Lyria RealTime flow error", { error: err });
            throw err;
        }
    }
);

