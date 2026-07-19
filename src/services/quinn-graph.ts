import { StateGraph, Annotation, START, END } from "@langchain/langgraph";
import { ChatGoogleGenerativeAI } from "@langchain/google-genai";
import { cacheVisionResult, getCachedVisionResult } from "../config/redis.js";
import { getContextCacheId } from "./ai.js";
import crypto from "crypto";
import logger from "../config/logger.js";

// Define the state schema
const QuinnState = Annotation.Root({
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
    // @ts-ignore - Support for cached content ID in newer SDKs
    cachedContent: cacheId || undefined,
  });
};

// Nodes
const visualAnalyzerNode = async (state: typeof QuinnState.State, config: any) => {
  if (!state.image && state.visionDescription) {
    return { visionDescription: state.visionDescription };
  }

  if (!state.image) return {};

  const imageHash = crypto.createHash("md5").update(state.image).digest("hex");
  const cached = await getCachedVisionResult(imageHash);

  if (cached) {
    logger.info("[QUINN_GRAPH] Cache hit for vision analysis", { hash: imageHash });
    return { visionDescription: cached };
  }

  const model = getCachedModel("gemini-1.5-flash", 0.1);
  const response = await model.invoke([
    ["system", "Analyze the environment, mood, and visual vibes in this POV stream. Use universal musical and narrative terminology for description. Do not generate lyrics."],
    ["human", state.image]
  ]);

  const description = response.content.toString();
  await cacheVisionResult(imageHash, description);

  return { visionDescription: description };
};

const musicDirectorNode = async (state: typeof QuinnState.State) => {
  if (state.mode !== 'music') return {};

  const model = getCachedModel("gemini-1.5-flash-exp", 0.8);
  const feedbackContext = state.userFeedback ? `\nUser Feedback: ${state.userFeedback}` : "";

  const response = await model.invoke([
    ["system", "You are Quinn, the Musically Director. Map the following visual vibe and user feedback into a set of 3-5 weighted musical prompts for the Lyria Real-Time engine. Focus on tempo, instrumentation, and atmospheric textures."],
    ["human", `Visual Vibe: ${state.visionDescription}${feedbackContext}`]
  ]);

  const prompts = (response.content.toString()).split("\n").filter((l: string) => l.trim().length > 0);
  return { musicalPrompts: prompts };
};

const podcastNarratorNode = async (state: typeof QuinnState.State, config: any) => {
  if (state.mode !== 'podcast') return {};

  const model = getCachedModel("gemini-2.0-flash-exp", 0.7);
  const feedbackContext = state.userFeedback ? `\nUser Input/Feedback: ${state.userFeedback}` : "";

  // Real-time text chunking using model.stream
  const stream = await model.stream([
    ["system", "You are Quinn, a podcast host. Based on the visual vibe and user instructions, generate a short, engaging narrative segment (2-4 sentences) for 'Musically POV'. If user gave feedback, acknowledge it naturally in your tone."],
    ["human", `Visual Vibe: ${state.visionDescription}${feedbackContext}`]
  ]);

  let fullScript = "";
  for await (const chunk of stream) {
    const text = chunk.content.toString();
    fullScript += text;

    // Pipe chunks to a provided callback in the config if it exists
    if (config.configurable?.onChunk) {
      config.configurable.onChunk({ type: "podcast_chunk", text });
    }
  }

  return { podcastScript: fullScript };
};

/**
 * Orchestrator Graph (Quinn v2.3)
 * Parallelizes Music and Narrative generation nodes to hit < 200ms latency targets.
 * Optimized with Google Context Caching for massive developer instructions.
 */
const workflow = new StateGraph(QuinnState)
  .addNode("visualAnalyzer", visualAnalyzerNode)
  .addNode("musicDirector", musicDirectorNode)
  .addNode("podcastNarrator", podcastNarratorNode)
  .addEdge(START, "visualAnalyzer")
  .addEdge("visualAnalyzer", "musicDirector")
  .addEdge("visualAnalyzer", "podcastNarrator")
  .addEdge("musicDirector", END)
  .addEdge("podcastNarrator", END);

export const quinnGraph = workflow.compile();
