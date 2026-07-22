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
  userFeedback: Annotation<string>(),
  modality: Annotation<'music' | 'podcast' | 'audiobook' | 'mixed'>(),
  previousInteractionId: Annotation<string>(),
  generatedAudio: Annotation<string>(), // Base64 audio block
});

// Helper to create model instance with context caching
const getCachedModel = (modelName: string, temperature: number = 0.7) => {
  const cacheId = getContextCacheId();
  return new ChatGoogleGenerativeAI({
    model: modelName,
    apiKey: process.env.GEMINI_API_KEY,
    temperature: temperature,
    // Support for cached content ID
    // @ts-ignore
    cachedContent: cacheId || undefined,
  });
};

/**
 * Interactions API Wrapper for Structured Music Output
 */
const createMusicInteraction = async (input: string, image?: string, previousId?: string) => {
    const ai = getAi();
    const model = previousId ? "lyria-realtime-exp" : "lyria-3-pro-preview";

    const contents: any[] = [{ type: "text", text: input }];
    if (image) contents.push({ type: "image", data: image, mime_type: "image/jpeg" });

    const interaction = await (ai as any).interactions.create({
        model: model,
        input: contents,
        previous_interaction_id: previousId,
        response_format: { type: "audio" }
    });

    return interaction;
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

  // Use flagship 3.5 model for ultra-fast, high-fidelity visual interpretation
  const model = getCachedModel("gemini-3.5-flash", 0.1);

  const stream = await model.stream([
    ["system", "Analyze the environment, mood, and visual vibes in this POV stream. Use universal musical and narrative terminology for description. Do not generate lyrics. Avoid any technical jargon like 'neon' or 'proxy'. You support 70+ languages and should respond in the same language as the user input if possible."],
    ["human", state.image]
  ]);

  let fullDescription = "";
  for await (const chunk of stream) {
    const text = chunk.content.toString();
    fullDescription += text;

    // Pipe chunks to frontend for zero-latency feedback
    if (config.configurable?.onChunk) {
      config.configurable.onChunk({ type: "vision_thinking", text });
    }
  }

  await cacheVisionResult(imageHash, fullDescription);
  return { visionDescription: fullDescription };
};

const directorNode = async (state: typeof MaveState.State, config: any) => {
  const model = getCachedModel("gemini-3.1-pro-preview", 0.5);
  const feedbackContext = state.userFeedback ? `\nUser Intent: ${state.userFeedback}` : "";

  const stream = await model.stream([
    ["system", "You are the Mave Director. Based on the visual vibe and user intent, determine the optimal output modality: 'music' (Lyria Real-Time), 'podcast' (narrative), 'audiobook' (storytelling), or 'mixed'. Provide your reasoning in a natural, fluid tone. Do not use technical terms like 'neon' or 'proxy'. You are a polyglot and support 70+ languages."],
    ["human", `Visual Vibe: ${state.visionDescription}${feedbackContext}`]
  ]);

  let reasoning = "";
  for await (const chunk of stream) {
    const text = chunk.content.toString();
    reasoning += text;
    if (config.configurable?.onChunk) {
      config.configurable.onChunk({ type: "director_thinking", text });
    }
  }

  // Simple heuristic for modality extraction from reasoning, or use a second pass if needed.
  // For brevity, we'll look for keywords.
  let modality: 'music' | 'podcast' | 'audiobook' | 'mixed' = 'music';
  if (reasoning.toLowerCase().includes("audiobook") || reasoning.toLowerCase().includes("story")) modality = 'audiobook';
  else if (reasoning.toLowerCase().includes("podcast") || reasoning.toLowerCase().includes("narrate")) modality = 'podcast';
  else if (reasoning.toLowerCase().includes("mixed")) modality = 'mixed';

  return { directorReasoning: reasoning, modality };
};

const musicDirectorNode = async (state: typeof MaveState.State, config: any) => {
  if (state.modality !== 'music' && state.modality !== 'mixed') return {};

  const input = `Visual Vibe: ${state.visionDescription}\nUser Feedback: ${state.userFeedback || "Generate music fitting this atmosphere"}`;

  const interaction = await createMusicInteraction(input, state.image, state.previousInteractionId);

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
  const model = getCachedModel("gemini-3.1-pro-preview", 0.7);
  const feedbackContext = state.userFeedback ? `\nUser Input/Feedback: ${state.userFeedback}` : "";

  const stream = await model.stream([
    ["system", "You are Mave, the narrator. Based on the visual vibe and user instructions, generate a short, engaging narrative segment (2-4 sentences) for 'Mave POV'. If user gave feedback, acknowledge it naturally in your tone. No technical jargon."],
    ["human", `Visual Vibe: ${state.visionDescription}${feedbackContext}`]
  ]);

  let fullScript = "";
  for await (const chunk of stream) {
    const text = chunk.content.toString();
    fullScript += text;

    if (config.configurable?.onChunk) {
      config.configurable.onChunk({ type: "director_thinking", text });
    }
  }

  return { podcastScript: fullScript };
};

/**
 * Orchestrator Graph (Mave v3.0)
 * Parallelizes Music and Narrative generation nodes to hit < 200ms latency targets.
 * Optimized with Google Context Caching and real-time chunk streaming.
 */
const workflow = new StateGraph(MaveState)
  .addNode("visualAnalyzer", visualAnalyzerNode)
  .addNode("director", directorNode)
  .addNode("musicDirector", musicDirectorNode)
  .addNode("podcastNarrator", podcastNarratorNode)
  .addEdge(START, "visualAnalyzer")
  .addEdge("visualAnalyzer", "director")
  .addEdge("director", "musicDirector")
  .addEdge("director", "podcastNarrator")
  .addEdge("musicDirector", END)
  .addEdge("podcastNarrator", END);

export const maveGraph = workflow.compile();
