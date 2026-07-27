import { getSecret } from "../config/secrets.js";
import { StateGraph, Annotation, START, END } from "@langchain/langgraph";
import { ChatGoogleGenerativeAI } from "@langchain/google-genai";
import { GoogleGenAI } from "@google/genai";
import { cacheVisionResult, getCachedVisionResult } from "../config/redis.js";
import { getContextCacheId, ensureContextCache, getAi } from "./ai.js";
import crypto from "crypto";
import logger from "../config/logger.js";

// Define the state schema
const MaveState = Annotation.Root({
  image: Annotation<string>(),
  visionDescription: Annotation<string>(),
  directorReasoning: Annotation<string>(),
  musicalPrompts: Annotation<string[]>(),
  podcastScript: Annotation<string>(),
  audiobookScript: Annotation<string>(),
  userFeedback: Annotation<string>(),
  modality: Annotation<'music' | 'podcast' | 'audiobook' | 'mixed'>(),
  previousInteractionId: Annotation<string>(),
  generatedAudio: Annotation<string>(), // Base64 audio block
  coverArtUrl: Annotation<string>(),
  videoMotionUrl: Annotation<string>(),
  locale: Annotation<string>(),
});

// Helper to create model instance with context caching
const getCachedModel = async (modelName: string, temperature: number = 0.7) => {
  const cacheId = await getContextCacheId();
  return new ChatGoogleGenerativeAI({
    model: modelName,
    apiKey: getSecret("GEMINI_API_KEY"),
    temperature: temperature,
    // Support for cached content ID
    // @ts-ignore
    cachedContent: cacheId || undefined,
  });
};

function encodeWAV(samples: Int16Array, sampleRate: number = 48000, numChannels: number = 1) {
  const buffer = new ArrayBuffer(44 + samples.length * 2);
  const view = new DataView(buffer);
  
  const writeString = (view: DataView, offset: number, string: string) => {
    for (let i = 0; i < string.length; i++) {
      view.setUint8(offset + i, string.charCodeAt(i));
    }
  };
  
  writeString(view, 0, 'RIFF');
  view.setUint32(4, 36 + samples.length * 2, true);
  writeString(view, 8, 'WAVE');
  
  writeString(view, 12, 'fmt ');
  view.setUint32(16, 16, true);
  view.setUint16(20, 1, true);
  view.setUint16(22, numChannels, true);
  view.setUint32(24, sampleRate, true);
  view.setUint32(28, sampleRate * numChannels * 2, true);
  view.setUint16(32, numChannels * 2, true);
  view.setUint16(34, 16, true);
  
  writeString(view, 36, 'data');
  view.setUint32(40, samples.length * 2, true);
  
  const data = new Int16Array(buffer, 44);
  data.set(samples);
  
  return Buffer.from(buffer);
}

const createMusicInteraction = async (input: string, image?: string, previousId?: string, onChunk?: any) => {
    const ai = getAi();
    
    return new Promise<any>(async (resolve, reject) => {
        const audioChunks: number[][] = [];
        let fullText = "";
        
        try {
            const stream = await ai.models.generateContentStream({
                model: "lyria-realtime-exp",
                contents: [
                    "You are Lyria RealTime, generating ambient music. " + input,
                    ...(image ? [image] : [])
                ],
                config: {
                    responseModalities: ["AUDIO"],
                    thinkingConfig: {
                        includeThoughts: true
                    }
                }
            });

            for await (const chunk of stream) {
                const parts = chunk.candidates?.[0]?.content?.parts || [];
                for (const part of parts) {
                    if (part.thought && part.text) {
                        if (onChunk) {
                            onChunk({ type: "mave_thinking", text: `\n[THINKING] ${part.text}\n` });
                        }
                    } else if (part.text) {
                        fullText += part.text;
                        if (onChunk) {
                            onChunk({ type: "mave_thinking", text: part.text });
                        }
                    }
                    
                    if (part.inlineData && part.inlineData.mimeType?.startsWith("audio/") && part.inlineData.data) {
                        const audioBuffer = Buffer.from(part.inlineData.data, "base64");
                        const intArray = new Int16Array(
                            audioBuffer.buffer,
                            audioBuffer.byteOffset,
                            audioBuffer.length / Int16Array.BYTES_PER_ELEMENT
                        );
                        audioChunks.push(Array.from(intArray));
                        
                        if (onChunk) {
                            onChunk({ type: "mave_thinking", text: "." }); 
                        }
                    }
                }
            }

            if (audioChunks.length > 0) {
                const flatArray = new Int16Array(audioChunks.flat());
                const wavBuffer = encodeWAV(flatArray, 48000, 1);
                const base64Audio = wavBuffer.toString("base64");
                resolve({
                    output_text: fullText || "Lyria RealTime Ambient Generate",
                    output_audio: { data: `data:audio/wav;base64,${base64Audio}` }
                });
            } else {
                resolve({ output_text: fullText || "No audio generated", output_audio: null });
            }
        } catch (err) {
            logger.error("Lyria RealTime generateContentStream error", { error: err });
            reject(err);
        }
    });
};

// Nodes
const visualAnalyzerNode = async (state: typeof MaveState.State, config: any) => {
  if (!state.image && state.visionDescription) {
    return { visionDescription: state.visionDescription };
  }

  if (!state.image) return {};

  const imageHash = crypto.createHash("md5").update(state.image).digest("hex");
  const cached = await getCachedVisionResult(imageHash);

  if (cached) {
    logger.info("[MAVE_GRAPH] Cache hit for vision analysis", { hash: imageHash });
    return { visionDescription: cached };
  }

  // Ensure context cache is active before large vision task
  await ensureContextCache();

  // Use flagship 3.6 model for ultra-fast, high-fidelity visual interpretation
  const ai = getAi();
  const stream = await ai.models.generateContentStream({
    model: "gemini-3.6-flash",
    contents: [
      `Analyze the environment, mood, and visual vibes in this POV stream. Use universal musical and narrative terminology for description. Do not generate lyrics. Avoid any technical jargon like 'neon' or 'proxy'. You support 70+ languages and should respond in the language corresponding to this locale: ${state.locale || 'en'}.`,
      state.image
    ],
    config: {
      thinkingConfig: { includeThoughts: true }
    }
  });

  let fullDescription = "";
  for await (const chunk of stream) {
    const parts = chunk.candidates?.[0]?.content?.parts || [];
    for (const part of parts) {
      if (part.thought && part.text) {
        if (config.configurable?.onChunk) {
          config.configurable.onChunk({ type: "vision_thinking", text: `\n[THINKING] ${part.text}\n` });
        }
      } else if (part.text) {
        fullDescription += part.text;
        if (config.configurable?.onChunk) {
          config.configurable.onChunk({ type: "vision_thinking", text: part.text });
        }
      }
    }
  }

  await cacheVisionResult(imageHash, fullDescription);
  return { visionDescription: fullDescription };
};

const directorNode = async (state: typeof MaveState.State, config: any) => {
  const userText = (state.userFeedback || "").toLowerCase();
  
  let modality: 'music' | 'podcast' | 'audiobook' | 'mixed' = 'music';
  let visualIntent: 'none' | 'cover_art' | 'video_motion' = 'none';
  let reasoning = "";

  // ensure context cache is active for the director's persona
  await ensureContextCache();

  // Use the 3.1 Pro model for high-fidelity creative orchestration reasoning
  const ai = getAi();
  const stream = await ai.models.generateContentStream({
    model: "gemini-3.1-pro-preview",
    contents: [
      `You are the Mave Orchestra Director. Your role is to reason about the visual atmosphere and user intent to orchestrate a world-class audio-visual experience.
Reason naturally about what you see and what the user wants.
If the user feedback starts with "Production Request:", prioritize fulfilling that specific media generation intent.
If the user asks for a song, vibe, or instrument, set the modality to 'music'.
If the user asks for a story or narration, set it to 'audiobook' or 'podcast'.
If the user asks for cover art or a music video, identify that intent.

At the end of your response, you MUST include a JSON block with the final orchestration parameters:
\`\`\`json
{
  "modality": "music" | "podcast" | "audiobook" | "mixed",
  "visualIntent": "none" | "cover_art" | "video_motion"
}
\`\`\`
Speak naturally to the user about your creative choices. Support 70+ languages. Locale: ${state.locale || 'en'}.`,
      `Visual Atmosphere: ${state.visionDescription}\nUser Feedback: ${state.userFeedback || "Compose real-time music for this atmosphere"}`
    ],
    config: {
      thinkingConfig: { includeThoughts: true }
    }
  });

  let fullResponse = "";
  let isJsonDetected = false;
  for await (const chunk of stream) {
    const parts = chunk.candidates?.[0]?.content?.parts || [];
    for (const part of parts) {
      if (part.thought && part.text) {
        if (config.configurable?.onChunk) {
          config.configurable.onChunk({ type: "mave_thinking", text: `\n[THINKING] ${part.text}\n` });
        }
      } else if (part.text) {
        const text = part.text;
        fullResponse += text;

        // Direct pipe of natural reasoning and dialogue to the user
        // We attempt to stop streaming if we detect the start of the JSON block
        if (config.configurable?.onChunk) {
          if (!isJsonDetected && fullResponse.includes("```json")) {
            isJsonDetected = true;
            // Emit only the part before the JSON block
            const preJson = text.split("```json")[0];
            if (preJson) {
               config.configurable.onChunk({ type: "mave_thinking", text: preJson });
            }
          } else if (!isJsonDetected) {
            config.configurable.onChunk({ type: "mave_thinking", text });
          }
        }
      }
    }
  }

  // Parse the JSON block from the natural response
  const jsonMatch = fullResponse.match(/```json\n([\s\S]*?)\n```/);
  if (jsonMatch) {
    try {
      const params = JSON.parse(jsonMatch[1]);
      modality = params.modality || 'music';
      visualIntent = params.visualIntent || 'none';
      // Clean up the response for the reasoning field if needed
      reasoning = fullResponse.replace(jsonMatch[0], "").trim();
    } catch (e) {
      logger.warn("[MAVE_GRAPH] Failed to parse params from reasoning", { error: e });
    }
  } else {
    reasoning = fullResponse;
  }

  return { directorReasoning: reasoning, modality, visualIntent };
};

const musicDirectorNode = async (state: typeof MaveState.State, config: any) => {
  if (state.modality !== 'music' && state.modality !== 'mixed') return {};

  const input = `Visual Vibe: ${state.visionDescription}\nUser Feedback: ${state.userFeedback || "Generate music fitting this atmosphere"}`;

  const interaction = await createMusicInteraction(input, state.image, state.previousInteractionId, config.configurable?.onChunk);

  const prompts = interaction.output_text?.split("\n").filter((l: string) => l.trim().length > 0) || [];
  const audio = interaction.output_audio?.data;

  return {
    musicalPrompts: prompts,
    generatedAudio: audio,
    previousInteractionId: interaction.id
  };
};

const podcastNarratorNode = async (state: typeof MaveState.State, config: any) => {
  if (state.modality === 'music') return {};

  // Use flagship 3.1 Pro model for natural storytelling and narration
  const ai = getAi();
  const feedbackContext = state.userFeedback ? `\nUser Input/Feedback: ${state.userFeedback}` : "";

  const isAudiobook = state.modality === 'audiobook';
  const systemPrompt = isAudiobook 
    ? `You are Mave, an elite Audiobook Narrator and Author. Based on the visual vibe and user instructions, generate a highly descriptive and immersive story chapter segment (3-5 paragraphs). No technical jargon. You MUST respond in the language corresponding to this locale: ${state.locale || 'en'}.`
    : `You are Mave, the narrator. Based on the visual vibe and user instructions, generate a short, engaging narrative segment (2-4 sentences) for 'Mave POV'. If user gave feedback, acknowledge it naturally in your tone. No technical jargon. You MUST respond in the language corresponding to this locale: ${state.locale || 'en'}.`;

  const stream = await ai.models.generateContentStream({
    model: "gemini-3.1-pro-preview",
    contents: [
      systemPrompt,
      `Visual Vibe: ${state.visionDescription}${feedbackContext}`
    ],
    config: {
      thinkingConfig: { includeThoughts: true }
    }
  });

  let fullScript = "";
  for await (const chunk of stream) {
    const parts = chunk.candidates?.[0]?.content?.parts || [];
    for (const part of parts) {
      if (part.thought && part.text) {
        if (config.configurable?.onChunk) {
          config.configurable.onChunk({ type: "mave_thinking", text: `\n[THINKING] ${part.text}\n` });
        }
      } else if (part.text) {
        fullScript += part.text;
        if (config.configurable?.onChunk) {
          config.configurable.onChunk({ type: "mave_thinking", text: part.text });
        }
      }
    }
  }

  if (isAudiobook) {
    return { audiobookScript: fullScript };
  }
  return { podcastScript: fullScript };
};

const mediaGeneratorNode = async (state: any, config: any) => {
  const intent = (state as any).visualIntent;
  if (!intent || intent === 'none') return {};

  logger.info("[MAVE_GRAPH] Generating visual media from music context", { intent });
  const { generateCoverMedia } = await import("./ai.js");

  const prompts = (state as any).musicalPrompts;
  const vision = state.visionDescription;

  const visualPrompt = prompts && prompts.length > 0
    ? `Musical Vibe: ${prompts.join(', ')}. Scene: ${vision}`
    : `Scene: ${vision}. Create a visual atmosphere matching this POV.`;

  try {
    const result = await generateCoverMedia(visualPrompt, intent === 'cover_art' ? 'cover_art' : 'video_motion', 'latest');

    if (intent === 'cover_art') {
        return { coverArtUrl: result.url };
    } else {
        return { videoMotionUrl: result.url };
    }
  } catch (e) {
    logger.warn("[MAVE_GRAPH] Visual media generation failed", { error: e });
    return {};
  }
};

/**
 * Orchestrator Graph (Mave v3.2)
 * Parallelizes Music and Narrative generation nodes.
 * Adds sequential visual media generation node.
 */
const workflow = new StateGraph(MaveState)
  .addNode("visualAnalyzer", visualAnalyzerNode)
  .addNode("director", directorNode)
  .addNode("musicDirector", musicDirectorNode)
  .addNode("podcastNarrator", podcastNarratorNode)
  .addNode("mediaGenerator", mediaGeneratorNode)
  .addEdge(START, "visualAnalyzer")
  .addEdge("visualAnalyzer", "director")
  .addEdge("director", "musicDirector")
  .addEdge("director", "podcastNarrator")
  .addEdge("musicDirector", "mediaGenerator")
  .addEdge("podcastNarrator", "mediaGenerator")
  .addEdge("mediaGenerator", END);

export const maveGraph = workflow.compile();
