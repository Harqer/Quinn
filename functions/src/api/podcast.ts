import * as logger from "firebase-functions/logger";
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { checkFreeQuota } from "../auth";
import { executeMutation } from "../dataconnect";
import { genkit } from "genkit";
import { googleAI } from "@genkit-ai/googleai";
import * as fs from "fs";
import * as path from "path";

const GEMINI_API_KEY = defineSecret("GEMINI_API_KEY");

// Load all RLM-distilled MIT Storytelling Frameworks synchronously at cold start
const frameworksDir = path.join(__dirname, "../frameworks");
const frameworks: Record<string, string> = {};

try {
  const files = fs.readdirSync(frameworksDir);
  for (const file of files) {
    if (file.endsWith(".md")) {
      const genre = path.basename(file, ".md");
      frameworks[genre] = fs.readFileSync(path.join(frameworksDir, file), "utf-8");
    }
  }
} catch (e) {
  logger.error("Warning: frameworks directory not found or unreadable. Falling back to empty frameworks.");
}

export const generatePodcastScript = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    
    await checkFreeQuota(request.auth.uid);

    const { topic } = request.data;
    if (!topic || typeof topic !== "string") {
      throw new HttpsError("invalid-argument", "A valid 'topic' string must be provided.");
    }

    const ai = genkit({
      plugins: [googleAI({ apiKey: GEMINI_API_KEY.value() })],
      promptDir: path.join(__dirname, "../../prompts")
    });
    
    try {
      const routerPrompt = ai.prompt("podcastRouter");
      const routingResponse = await routerPrompt({ topic });
      
      let genre = (routingResponse.text || "general").trim().toLowerCase();
      if (!frameworks[genre]) {
        genre = "general";
      }
      
      logger.info(`Routed topic "${topic}" to framework: ${genre}`);

      const scriptPrompt = ai.prompt("podcastScript");
      const response = await scriptPrompt({ topic });

      const scriptData = {
        script: response.text,
        genre_applied: genre
      };
      
      await executeMutation("SeedEpisode", {
        showId: "1", // General podcast show
        title: topic,
        description: scriptData.script?.substring(0, 200) || "",
        publishDate: new Date().toISOString()
      });

      return scriptData;
    } catch (err: any) {
      throw new HttpsError("internal", `Failed to generate podcast script: ${err.message || err}`);
    }
  }
);

export const generateNarrativeSeries = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
    timeoutSeconds: 300
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    await checkFreeQuota(request.auth.uid);

    const { type, topic, previousContext, targetEpisodes = 3 } = request.data;
    if (!type || (type !== "podcast" && type !== "audiobook")) {
      throw new HttpsError("invalid-argument", "Valid type ('podcast' or 'audiobook') must be provided.");
    }

    const ai = genkit({
      plugins: [googleAI({ apiKey: GEMINI_API_KEY.value() })]
    });
    
    try {
      const promptFile = type === "podcast" ? "podcastNarrator.prompt" : "audiobookNarrator.prompt";
      const promptPath = path.join(__dirname, "../../prompts", promptFile);
      const fileContent = fs.readFileSync(promptPath, "utf-8");
      
      const parts = fileContent.split("---");
      const contentPart = parts.length > 2 ? parts[2] : fileContent;
      
      const systemMatch = contentPart.match(/\{\{role "system"\}\}([\s\S]*?)\{\{role "user"\}\}/);
      let systemInstruction = systemMatch ? systemMatch[1].trim() : "";
      
      systemInstruction = systemInstruction.replace(/\{\{systemInstruction\}\}/g, "");
      systemInstruction = systemInstruction.replace(/\{\{targetEpisodes\}\}/g, targetEpisodes.toString());
      
      if (previousContext) {
        systemInstruction = systemInstruction.replace(/\{\{#if previousContext\}\}([\s\S]*?)\{\{\/if\}\}/, "$1");
        systemInstruction = systemInstruction.replace(/\{\{previousContext\}\}/g, previousContext);
      } else {
        systemInstruction = systemInstruction.replace(/\{\{#if previousContext\}\}[\s\S]*?\{\{\/if\}\}/, "");
      }

      const response = await ai.generate({
        model: "googleai/gemini-3.5-flash",
        prompt: `Generate a narrative series about: ${topic}`,
        config: {
          systemInstruction,
          responseMimeType: "application/json",
          responseSchema: {
            type: "object",
            properties: {
              newContext: { type: "string" },
              episodes: {
                type: "array",
                items: {
                  type: "object",
                  properties: {
                    title: { type: "string" },
                    script: { type: "string" }
                  },
                  required: ["title", "script"]
                }
              }
            },
            required: ["newContext", "episodes"]
          } as any
        },
      });

      let jsonText = response.text || "{}";
      const seriesData = JSON.parse(jsonText);
      
      let parentId: string;
      if (type === "podcast") {
        const res = await executeMutation("CreatePodcast", {
          title: topic,
          publisher: request.auth.uid,
          description: seriesData.newContext || "Generated podcast",
          storyContext: previousContext || ""
        });
        parentId = res.data.show_insert;
        
        for (const ep of seriesData.episodes) {
          await executeMutation("SeedEpisode", {
            showId: parentId,
            title: ep.title,
            description: ep.script.substring(0, 200),
            publishDate: new Date().toISOString()
          });
        }
      } else {
        const res = await executeMutation("CreateAudiobook", {
          title: topic,
          authorId: request.auth.uid,
          storyContext: previousContext || ""
        });
        parentId = res.data.audiobook_insert;
        
        let index = 1;
        for (const ep of seriesData.episodes) {
          await executeMutation("SeedChapter", {
            audiobookId: parentId,
            title: ep.title,
            chapterNumber: index++
          });
        }
      }
      
      return seriesData;
    } catch (err: any) {
      logger.error(err);
      throw new HttpsError("internal", `Failed to generate narrative series: ${err.message || err}`);
    }
  }
);

export const renderNarrativeAudio = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
    timeoutSeconds: 30
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    const { episodeId, script } = request.data;
    if (!episodeId || !script) {
      throw new HttpsError("invalid-argument", "Missing episodeId or script.");
    }
    
    logger.info(`[Background Task] Queuing audio render for episode ${episodeId}...`);
    
    try {
      const { PubSub } = await import("@google-cloud/pubsub");
      const { default: Redis } = await import("ioredis");
      const { v4: uuidv4 } = await import("uuid");

      const pubsub = new PubSub();
      const topicName = "audio-generation-tasks";
      
      const redisHost = process.env.REDIS_HOST || "localhost";
      const redis = new Redis({ host: redisHost, port: 6379 });

      const taskId = uuidv4();
      const payload = {
        taskId,
        uid: request.auth.uid,
        scriptData: { text: script },
        episodeId,
        type: "narrative"
      };

      // Set pending state in Redis
      await redis.hset(`task:${taskId}`, {
        status: "PENDING",
        type: "narrative",
        episodeId
      });
      await redis.expire(`task:${taskId}`, 3600); // 1 hour TTL

      // Publish to Pub/Sub
      const dataBuffer = Buffer.from(JSON.stringify(payload));
      await pubsub.topic(topicName).publishMessage({ data: dataBuffer });

      return { success: true, taskId, status: "PENDING" };
    } catch (err: any) {
      logger.error("Failed to queue audio render:", err);
      throw new HttpsError("internal", `Failed to queue audio render: ${err.message || err}`);
    }
  }
);

export const generatePodcastAudio = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
    timeoutSeconds: 30
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    const { scriptData } = request.data;
    if (!scriptData) {
      throw new HttpsError("invalid-argument", "Missing scriptData.");
    }

    logger.info(`[Background Task] Queuing VibeVoice render...`);

    try {
      const { PubSub } = await import("@google-cloud/pubsub");
      const { default: Redis } = await import("ioredis");
      const { v4: uuidv4 } = await import("uuid");

      const pubsub = new PubSub();
      const topicName = "audio-generation-tasks";
      
      const redisHost = process.env.REDIS_HOST || "localhost";
      const redis = new Redis({ host: redisHost, port: 6379 });

      const taskId = uuidv4();
      const payload = {
        taskId,
        uid: request.auth.uid,
        scriptData,
        type: "podcast"
      };

      // Set pending state in Redis
      await redis.hset(`task:${taskId}`, {
        status: "PENDING",
        type: "podcast"
      });
      await redis.expire(`task:${taskId}`, 3600); // 1 hour TTL

      // Publish to Pub/Sub
      const dataBuffer = Buffer.from(JSON.stringify(payload));
      await pubsub.topic(topicName).publishMessage({ data: dataBuffer });

      return { success: true, taskId, status: "PENDING" };
    } catch (err: any) {
      logger.error("Failed to queue VibeVoice rendering:", err);
      throw new HttpsError("internal", `Failed to queue VibeVoice rendering: ${err.message || err}`);
    }
  }
);

export const checkTaskStatus = onCall(
  {
    enforceAppCheck: true,
    cors: true,
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }
    const { taskId } = request.data;
    if (!taskId) {
      throw new HttpsError("invalid-argument", "Missing taskId.");
    }

    try {
      const { default: Redis } = await import("ioredis");
      const redisHost = process.env.REDIS_HOST || "localhost";
      const redis = new Redis({ host: redisHost, port: 6379 });

      const taskData = await redis.hgetall(`task:${taskId}`);
      if (!taskData || Object.keys(taskData).length === 0) {
        return { status: "UNKNOWN" };
      }

      return taskData;
    } catch (err: any) {
      logger.error("Failed to check task status:", err);
      throw new HttpsError("internal", `Failed to check task status: ${err.message || err}`);
    }
  }
);
