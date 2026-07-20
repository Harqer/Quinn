import { StateGraph, Annotation, START, END } from "@langchain/langgraph";
import { ChatGoogleGenerativeAI } from "@langchain/google-genai";
import { cacheVisionResult, getCachedVisionResult } from "../config/redis.js";
import { getContextCacheId, ensureContextCache } from "./ai.js";
import crypto from "crypto";
import logger from "../config/logger.js";

// Define the state schema
const MaveState = Annotation.Root({
  image: Annotation<string>(),
  visionDescription: Annotation<string>(),
  musicalPrompts: Annotation<string[]>(),
  podcastScript: Annotation<string>(),
  userFeedback: Annotation<string>(),
  mode: Annotation<'music' | 'podcast'>(),
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

  // Use Thinking model for deep visual interpretation
  const model = getCachedModel("gemini-2.0-flash-thinking-exp", 0.1);

  const stream = await model.stream([
    ["system", "Analyze the environment, mood, and visual vibes in this POV stream. Use universal musical and narrative terminology for description. Do not generate lyrics."],
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

const musicDirectorNode = async (state: typeof MaveState.State, config: any) => {
  if (state.mode !== 'music') return {};

  const model = getCachedModel("gemini-2.0-flash-exp", 0.8);
  const feedbackContext = state.userFeedback ? `\nUser Feedback: ${state.userFeedback}` : "";

  // Strategy: Stream the music director reasoning for immediate UX feedback
  const stream = await model.stream([
    ["system", "You are Mave, the Mave Studio Director. Map the following visual vibe and user feedback into a set of 3-5 weighted musical prompts for the Lyria Real-Time engine. Focus on tempo, instrumentation, and atmospheric textures."],
    ["human", `Visual Vibe: ${state.visionDescription}${feedbackContext}`]
  ]);

  let reasoningText = "";
  for await (const chunk of stream) {
    const text = chunk.content.toString();
    reasoningText += text;
    if (config.configurable?.onChunk) {
      config.configurable.onChunk({ type: "director_thinking", text });
    }
  }

  const prompts = reasoningText.split("\n").filter((l: string) => l.trim().length > 0);
  return { musicalPrompts: prompts };
};

const podcastNarratorNode = async (state: typeof MaveState.State, config: any) => {
  if (state.mode !== 'podcast') return {};

  // Use Thinking model for natural storytelling
  const model = getCachedModel("gemini-2.0-flash-thinking-exp", 0.7);
  const feedbackContext = state.userFeedback ? `\nUser Input/Feedback: ${state.userFeedback}` : "";

  const stream = await model.stream([
    ["system", "You are Mave, a podcast host. Based on the visual vibe and user instructions, generate a short, engaging narrative segment (2-4 sentences) for 'Mave POV'. If user gave feedback, acknowledge it naturally in your tone."],
    ["human", `Visual Vibe: ${state.visionDescription}${feedbackContext}`]
  ]);

  let fullScript = "";
  for await (const chunk of stream) {
    const text = chunk.content.toString();
    fullScript += text;

    if (config.configurable?.onChunk) {
      config.configurable.onChunk({ type: "mave_thinking", text });
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
  .addNode("musicDirector", musicDirectorNode)
  .addNode("podcastNarrator", podcastNarratorNode)
  .addEdge(START, "visualAnalyzer")
  .addEdge("visualAnalyzer", "musicDirector")
  .addEdge("visualAnalyzer", "podcastNarrator")
  .addEdge("musicDirector", END)
  .addEdge("podcastNarrator", END);

export const maveGraph = workflow.compile();
