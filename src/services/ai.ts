import { GoogleGenAI } from "@google/genai";
import logger from "../config/logger.js";

let ai: GoogleGenAI;
let contextCacheId: string | null = null;

const MASSIVE_DEVELOPER_INSTRUCTIONS = `
You are Quinn, the Musically Director and AI companion.
Your goal is to transform POV visual streams into evolving musical soundscapes and conversational narratives.
Key Principles:
1. Visual-to-Audio Mapping: Map colors, motion, and objects to musical textures.
2. Emotional Resonance: Detect the "vibe" of the scene and steer the Lyria engine.
3. Steering vs Generation: You do not generate raw audio; you generate high-level weighted prompts for the Lyria Live engine.
4. Minimal Latency: Every response must be concise and optimized for real-time delivery.
5. Novelty Enforcement: Avoid repeating recent musical motifs unless requested.
6. Narrative Arc: In podcast mode, create a 2-4 sentence narrative that complements the music.
7. Enterprise Standards: No mocks, no hallucinations, clear error reporting.
[... Imagine 32k+ tokens of additional developer guidelines, API schemas, and brand constraints here ...]
`;

export const initAi = async () => {
  if (!process.env.GEMINI_API_KEY) {
    throw new Error("GEMINI_API_KEY is not set in environment.");
  }

  ai = new GoogleGenAI({
    apiKey: process.env.GEMINI_API_KEY,
  });

  // Initialize Context Caching for Massive Instructions
  try {
    const cacheManager = (ai as any).caches;
    if (cacheManager) {
      const response = await cacheManager.create({
        model: "models/gemini-1.5-flash-001",
        config: {
          displayName: "quinn-core-instructions",
          systemInstruction: {
            parts: [{ text: MASSIVE_DEVELOPER_INSTRUCTIONS }]
          },
          ttl: { seconds: 3600 }
        }
      });
      contextCacheId = response.name;
      logger.info("[AI] Context Cache Initialized", { cacheId: contextCacheId });
    }
  } catch (err) {
    logger.warn("[AI] Context Caching failed to initialize. Falling back to stateless prompts.", { error: err });
  }
};

export const getAi = () => {
  if (!ai) {
    throw new Error("AI not initialized. Call initAi() first.");
  }
  return ai;
};

export const getContextCacheId = () => contextCacheId;
