import { StateGraph, Annotation, START, END } from "@langchain/langgraph";
import { ChatGoogleGenerativeAI } from "@langchain/google-genai";
import { getAi } from "./ai.js";
import { getRedis, cacheVisionResult, getCachedVisionResult } from "../config/redis.js";
import crypto from "crypto";

// Define the state schema
const QuinnState = Annotation.Root({
  image: Annotation<string>(),
  visionDescription: Annotation<string>(),
  lyrics: Annotation<string>(),
  musicalPrompts: Annotation<string[]>(),
  mood: Annotation<string>(),
});

// Nodes
const visionAnalyzerNode = async (state: typeof QuinnState.State) => {
  const imageHash = crypto.createHash("md5").update(state.image).digest("hex");
  const cached = await getCachedVisionResult(imageHash);

  if (cached) {
    return { visionDescription: cached };
  }

  const model = new ChatGoogleGenerativeAI({
    modelName: "gemini-1.5-flash",
    apiKey: process.env.GEMINI_API_KEY,
  });

  // Multimodal call (conceptually)
  const response = await model.invoke([
    ["system", "Analyze the visual elements, mood, and objects in this scene. Be descriptive for a music producer."],
    ["human", state.image]
  ]);

  const description = response.content.toString();
  await cacheVisionResult(imageHash, description);

  return { visionDescription: description };
};

const lyricistNode = async (state: typeof QuinnState.State) => {
  const model = new ChatGoogleGenerativeAI({
    modelName: "gemini-1.5-flash",
    apiKey: process.env.GEMINI_API_KEY,
  });

  const response = await model.invoke([
    ["system", "You are Quinn's Lyricist Agent. Based on the scene description, generate 4 lines of evocative lyrics."],
    ["human", state.visionDescription]
  ]);

  return { lyrics: response.content.toString() };
};

const composerNode = async (state: typeof QuinnState.State) => {
  const model = new ChatGoogleGenerativeAI({
    modelName: "gemini-1.5-flash",
    apiKey: process.env.GEMINI_API_KEY,
  });

  const response = await model.invoke([
    ["system", "You are Quinn's Composer Agent. Map these lyrics and scene description into 3 weighted musical prompts for the Lyria engine."],
    ["human", `Description: ${state.visionDescription}\nLyrics: ${state.lyrics}`]
  ]);

  // For production, we'd parse this into a structured array
  const prompts = response.content.toString().split("\n").filter(l => l.trim().length > 0);

  return { musicalPrompts: prompts };
};

// Build the graph
const workflow = new StateGraph(QuinnState)
  .addNode("visionAnalyzer", visionAnalyzerNode)
  .addNode("lyricist", lyricistNode)
  .addNode("composer", composerNode)
  .addEdge(START, "visionAnalyzer")
  .addEdge("visionAnalyzer", "lyricist")
  .addEdge("lyricist", "composer")
  .addEdge("composer", END);

export const quinnGraph = workflow.compile();
