import { getSecret } from "../config/secrets.js";
import { GoogleGenAI } from "@google/genai";
import { genkit, z } from "genkit";
import { googleAI } from "@genkit-ai/googleai";
import logger from "../config/logger.js";
import { getRedis } from "../config/redis.js";

let ai: GoogleGenAI;
let gk: ReturnType<typeof genkit>;

const MASSIVE_DEVELOPER_INSTRUCTIONS = `
You are Mave, the Executive Creative Director, Master Musical Orchestrator, and AI companion for Billboard Top-100 productions and Vogue high-fashion audio-visual experiences.
You possess a professional mastery of harmony, arrangement, sound design, spatial audio, and cinematic aesthetics.
Your directive is to translate visual atmospheres, camera motion, and user intent into world-class, chart-topping audio-visual steering for the Lyria Real-Time and Lyria 3 generative engines.

Key Creative Principles:
1. Vogue/Billboard Aesthetic Mapping: Map colors, lighting contrast, 35mm optical depth, and visual motion into pristine, studio-mastered musical textures and dynamic polyphonic arrangements.
2. Emotional & Harmonic Steering: Steer the Lyria engine with precision—controlling tempo, density, brightness, and harmonic progressions with zero acoustic distortion or latency.
3. Multimodal Precision: Seamlessly interpret live video frames and visual inputs, producing rich, atmospheric soundscapes that match high-fashion editorial standards.
4. Polyglot Communication: Speak in terms of pure creative art design, musical harmony, and atmosphere, naturally supporting 70+ languages without technical jargon.
5. Real-Time Performance: Every output is engineered for sub-200ms real-time delivery and sync across Google Cloud AI and Firebase RTDB.
`;

export const initAi = async () => {
  if (process.env.GOOGLE_CLOUD_PROJECT) {
    ai = new GoogleGenAI({
      vertexai: {
        project: process.env.GOOGLE_CLOUD_PROJECT,
        location: process.env.GOOGLE_CLOUD_LOCATION || "us-central1"
      }
    } as any);
  } else if (getSecret("GEMINI_API_KEY")) {
    ai = new GoogleGenAI({
      apiKey: getSecret("GEMINI_API_KEY"),
    });
  } else {
    throw new Error("GEMINI_API_KEY or GOOGLE_CLOUD_PROJECT is not set in environment.");
  }

  // Initialize Genkit for Prompt Abstraction and Structured Output
  gk = genkit({
    plugins: [googleAI({ apiKey: getSecret("GEMINI_API_KEY") })],
    model: "googleai/gemini-3.5-flash", 
  });

  await ensureContextCache();
};

/**
 * Ensures the massive instructions are cached on Google's servers.
 * Implements a rolling cache strategy to handle the 1-hour default TTL.
 */
export const ensureContextCache = async () => {
  try {
    const redis = getRedis();
    const now = Date.now();
    
    let cachedId: string | null = null;
    if (redis) {
      cachedId = await redis.get("gemini_context_cache_id") as string | null;
    }

    // If cache exists, skip (TTL handles expiry)
    if (cachedId) return;

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

    const newCacheId = response.name;
    const ttlSeconds = 3600;
    
    if (redis) {
      await redis.set("gemini_context_cache_id", newCacheId, { ex: ttlSeconds });
    }
    
    logger.info("[AI] Context Cache Synchronized", { cacheId: newCacheId, expires: new Date(Date.now() + (ttlSeconds * 1000)).toLocaleTimeString() });
  } catch (err) {
    logger.warn("[AI] Context Caching failed. Mave will operate in stateless mode.", { error: err });
  }
};

export const getAi = () => {
  if (!ai) throw new Error("AI not initialized. Call initAi() first.");
  return ai;
};

export const getGenkit = () => {
  if (!gk) throw new Error("Genkit not initialized. Call initAi() first.");
  return gk;
};

export const getContextCacheId = async (): Promise<string | null> => {
  const redis = getRedis();
  if (!redis) return null;
  return await redis.get("gemini_context_cache_id") as string | null;
};

/**
 * Generates an ephemeral token for the Gemini Live API for client-side deployments.
 */
export const generateLiveEphemeralToken = async (
  model = "gemini-3.1-flash-live-preview",
  systemInstruction?: string,
  voice?: string
): Promise<string> => {
  try {
    const aiInstance = getAi();
    const config: any = {
      responseModalities: ["audio"],
    };
    if (systemInstruction) {
      config.systemInstruction = { parts: [{ text: systemInstruction }] };
    }
    if (voice) {
      config.speechConfig = { voiceConfig: { prebuiltVoiceConfig: { voiceName: voice } } };
    }
    const token = await (aiInstance as any).authTokens.create({
      config: {
        uses: 1, // Restrict to one session
        expireTime: new Date(Date.now() + 30 * 60 * 1000).toISOString(),
        liveConnectConstraints: {
          model,
          config,
        }
      }
    });
    
    logger.info("[AI] Generated Ephemeral Token for Live API", { model });
    return token.name;
  } catch (err) {
    logger.error("[AI] Failed to generate ephemeral token", { error: err });
    throw err;
  }
};

/**
 * Context-Aware Executive Art Director Prompt Engine for Imagen 3 Models.
 * Dynamically deconstructs user input and synthesizes a high-fidelity 5-part visual prompt:
 * [Subject & Composition, Environment & Atmosphere, Contextual Lighting & Optics, Textures & Materiality, Color & Aesthetic Grade].
 */
export const enhanceImagePrompt = async (userPrompt: string): Promise<string> => {
  try {
    const genkitInstance = getGenkit();
    const enhancePrompt = genkitInstance.definePrompt({
      name: 'enhanceImagePrompt',
      model: 'googleai/gemini-3.5-flash',
      system: `You are a world-class Executive Art Director for Billboard and Vogue luxury campaigns.
Your task is to take a user concept and dynamically synthesize a highly detailed, context-aware 5-part visual prompt for Imagen 3 image generation.
Do NOT use static copy-pasted buzzwords. Analyze the specific mood, subject, and medium of the concept, then construct a cohesive prompt containing:
1. SUBJECT & COMPOSITION: Precise focal subject, dynamic framing (rule of thirds/golden ratio), spatial alignment.
2. ENVIRONMENT & ATMOSPHERE: Contextual setting, architectural elements, atmospheric depth, background details.
3. LIGHTING & OPTICS: Tailored lighting design (key/fill/rim, volumetric rays, natural window spill), focal length, optical depth of field.
4. TEXTURES & MATERIALITY: Tactile surface properties, micro-textures, material physics (glass, skin subsurface scattering, metal grain).
5. COLOR PALETTE & GRADE: Curated color harmony, shadow depth, and overall visual mood fit for an iconic album cover.

Return ONLY the synthesized prompt text, without introductory filler or markdown tags.`,
    });

    const response = await enhancePrompt({ prompt: `User Concept: "${userPrompt}"` });
    const enhanced = response.text.trim();
    
    if (!enhanced) {
      throw new Error("Failed to generate an enhanced image prompt");
    }
    
    logger.info("[ART_DIRECTOR] Contextual Dynamic Image Prompt", { original: userPrompt, enhanced });
    return enhanced;
  } catch (err) {
    logger.error("[ART_DIRECTOR] Failed to enhance image prompt", { error: err });
    throw err;
  }
};

/**
 * Context-Aware Master Cinematographer Prompt Engineer for Veo 2.0 Models.
 * Dynamically synthesizes a 5-part 35mm photorealistic motion control video prompt:
 * [Subject Kinetic Pose, Camera Optics & Movement, Environmental Physics & Particles, Dynamic Lighting, Temporal Coherence].
 */
export const enhanceVideoPrompt = async (userPrompt: string): Promise<string> => {
  try {
    const genkitInstance = getGenkit();
    const enhancePrompt = genkitInstance.definePrompt({
      name: 'enhanceVideoPrompt',
      model: 'googleai/gemini-3.5-flash',
      system: `You are a legendary Master Cinematographer and Commercial Video Director.
Your task is to convert a user concept into an in-depth, 35mm photorealistic video motion prompt for Veo video models.
Do NOT use static copy-pasted buzzwords. Analyze the user's intent and construct a cohesive, dynamic video motion prompt containing:
1. SUBJECT & KINETIC POSE: Primary subject and natural motion trajectory.
2. CAMERA OPTICS & MOVEMENT: Specific camera move (orbital tracking shot, push-in dolly, pan, rack focus, 24fps filmic shutter angle).
3. ENVIRONMENTAL PHYSICS & PARTICLES: Atmospheric particle movement (mist, light refraction, floating dust, water dynamics).
4. DYNAMIC LIGHTING & REFLECTIONS: Dynamic light interaction, real-time reflection, volumetric light shafts.
5. TEMPORAL COHERENCE: Seamless looping motion, fluid transitions, cinema-grade color science.

Return ONLY the synthesized video motion prompt text, without introductory filler or markdown tags.`,
    });

    const response = await enhancePrompt({ prompt: `User Concept: "${userPrompt}"` });
    const enhanced = response.text.trim();
    
    if (!enhanced) {
      throw new Error("Failed to generate an enhanced video prompt");
    }
    
    logger.info("[CINEMATOGRAPHER] Contextual Dynamic Video Prompt", { original: userPrompt, enhanced });
    return enhanced;
  } catch (err) {
    logger.error("[CINEMATOGRAPHER] Failed to enhance video prompt", { error: err });
    throw err;
  }
};

/**
 * Genkit Structured Output: Parses hand gestures into typed actions for real-time music control.
 */
export const HandGestureSchema = z.object({
  action: z.enum(["modify_pitch", "modify_tempo", "add_instrument", "remove_instrument", "play", "pause", "unknown"]),
  value: z.string().optional().describe("E.g. '+2', '1.5x', 'guitar'"),
  target: z.string().optional().describe("E.g. 'synth', 'drums', 'master'"),
});

export const parseHandGesture = async (gestureDescription: string) => {
  try {
    const genkitInstance = getGenkit();
    const parsePrompt = genkitInstance.definePrompt({
      name: 'parseHandGesture',
      model: 'googleai/gemini-3.5-flash',
      output: { schema: HandGestureSchema },
      system: `You are the orchestration controller. You will receive a description of a user's hand gesture or physical motion.
Map this gesture to a specific music control action, such as modifying pitch, tempo, or adding/removing instruments.
Be precise and return ONLY the JSON object.`,
    });

    const response = await parsePrompt({ prompt: `Gesture: "${gestureDescription}"` });
    return response.output;
  } catch (err) {
    logger.error("[GESTURE_CONTROL] Failed to parse hand gesture", { error: err });
    throw err;
  }
};

/**
 * Google Cloud AI Model Garden Registry for Image & Video Generation.
 */
export const MODEL_GARDEN_REGISTRY = {
  IMAGE: {
    LATEST: "imagen-3.0-generate-002",
    FLASH: "imagen-3.0-fast-generate-001"
  },
  VIDEO: {
    LATEST: "veo-2.0-generate-001",
    FLASH: "gemini-omni-flash-preview"
  }
};

/**
 * Generates dynamic visual or video cover via Google Cloud AI Model Garden endpoints.
 * Supports both Latest (High-Fidelity Pro) and Flash (Low-Latency Fast) model variants.
 */
export const generateCoverMedia = async (
  userPrompt: string, 
  type: 'cover_art' | 'video_motion',
  modelVariant: 'latest' | 'flash' = 'latest'
): Promise<{ url: string; prompt: string; modelUsed: string }> => {
  const enhancedPrompt = type === 'cover_art' 
    ? await enhanceImagePrompt(userPrompt) 
    : await enhanceVideoPrompt(userPrompt);

  const modelName = type === 'cover_art'
    ? (modelVariant === 'flash' ? MODEL_GARDEN_REGISTRY.IMAGE.FLASH : MODEL_GARDEN_REGISTRY.IMAGE.LATEST)
    : (modelVariant === 'flash' ? MODEL_GARDEN_REGISTRY.VIDEO.FLASH : MODEL_GARDEN_REGISTRY.VIDEO.LATEST);

  try {
    const aiInstance = getAi();
    // Primary invocation via Google Cloud AI Model Garden endpoint
    const response = await (aiInstance as any).models.generateImages({
      model: modelName,
      prompt: enhancedPrompt,
      config: {
        numberOfImages: 1,
        aspectRatio: "1:1",
        outputMimeType: type === 'cover_art' ? "image/jpeg" : "image/png"
      }
    });

    const imageBytes = response?.generatedImages?.[0]?.image?.imageBytes;
    if (imageBytes) {
      const url = `data:image/jpeg;base64,${imageBytes}`;
      logger.info("[GOOGLE_CLOUD_AI_GARDEN] Cover Media Generated", { model: modelName, type, promptLength: enhancedPrompt.length });
      return { url, prompt: enhancedPrompt, modelUsed: modelName };
    }
  } catch (err) {
    logger.warn("[GOOGLE_CLOUD_AI_GARDEN] Primary model call failed, trying Flash model fallback", { primaryModel: modelName, error: err });
    
    // Fallback to Flash model variant if primary call fails
    try {
      const fallbackModel = type === 'cover_art' ? MODEL_GARDEN_REGISTRY.IMAGE.FLASH : MODEL_GARDEN_REGISTRY.VIDEO.FLASH;
      const aiInstance = getAi();
      const response = await (aiInstance as any).models.generateImages({
        model: fallbackModel,
        prompt: enhancedPrompt,
        config: {
          numberOfImages: 1,
          aspectRatio: "1:1",
          outputMimeType: type === 'cover_art' ? "image/jpeg" : "image/png"
        }
      });

      const imageBytes = response?.generatedImages?.[0]?.image?.imageBytes;
      if (imageBytes) {
        const url = `data:image/jpeg;base64,${imageBytes}`;
        logger.info("[GOOGLE_CLOUD_AI_GARDEN] Flash Fallback Cover Media Generated", { model: fallbackModel, type });
        return { url, prompt: enhancedPrompt, modelUsed: fallbackModel };
      }
    } catch (fallbackErr) {
      logger.warn("[GOOGLE_CLOUD_AI_GARDEN] Fallback model call failed", { error: fallbackErr });
      throw new Error(`Media generation failed: ${fallbackErr instanceof Error ? fallbackErr.message : String(fallbackErr)}`);
    }
  }

  throw new Error("Media generation returned no image data.");
};


