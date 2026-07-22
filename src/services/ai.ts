import { GoogleGenAI } from "@google/genai";
import logger from "../config/logger.js";

let ai: GoogleGenAI;
let contextCacheId: string | null = null;
let cacheExpiry: number = 0;

const MASSIVE_DEVELOPER_INSTRUCTIONS = `
You are Mave, the Expert Musical Orchestrator and AI companion.
You possess a professional mastery of harmony, vibrato, and instrumental notes.
Your goal is to capture the atmosphere of a location and environment, and construct music to fit the vibe perfectly.
Key Principles:
1. Visual-to-Audio Mapping: Map colors, motion, and depth to professional musical textures.
2. Emotional Resonance: Detect the semantic tone of the scene and steer the Lyria engine accordingly.
3. Steering vs Generation: You generate high-level structured music output and weighted prompts for the Lyria engine.
4. Minimal Latency: Every response must be optimized for real-time delivery via Firebase RTDB.
5. Multimodal Intelligence: You support 70+ languages naturally within your expert musical persona.
6. Professional Tone: Avoid technical jargon. You speak in terms of harmony, rhythm, and atmosphere.
`;

export const initAi = async () => {
  if (!process.env.GEMINI_API_KEY) {
    throw new Error("GEMINI_API_KEY is not set in environment.");
  }

  ai = new GoogleGenAI({
    apiKey: process.env.GEMINI_API_KEY,
  });

  await ensureContextCache();
};

/**
 * Ensures the massive instructions are cached on Google's servers.
 * Implements a rolling cache strategy to handle the 1-hour default TTL.
 */
export const ensureContextCache = async () => {
  try {
    const now = Date.now();
    // If cache exists and has > 10 mins remaining, skip
    if (contextCacheId && cacheExpiry > now + 600000) return;

    const cacheManager = (ai as any).caches;
    if (!cacheManager) return;

    logger.info("[AI] Initializing Context Caching for massive instructions...");

    const response = await cacheManager.create({
      model: "models/gemini-3.5-flash",
      config: {
        displayName: "mave-core-instructions",
        systemInstruction: {
          parts: [{ text: MASSIVE_DEVELOPER_INSTRUCTIONS }]
        },
        ttl: { seconds: 3600 }
      }
    });

    contextCacheId = response.name;
    cacheExpiry = now + 3600000;
    logger.info("[AI] Context Cache Synchronized", { cacheId: contextCacheId, expires: new Date(cacheExpiry).toLocaleTimeString() });
  } catch (err) {
    logger.warn("[AI] Context Caching failed. Mave will operate in stateless mode.", { error: err });
  }
};

export const getAi = () => {
  if (!ai) throw new Error("AI not initialized. Call initAi() first.");
  return ai;
};

export const getContextCacheId = () => contextCacheId;
