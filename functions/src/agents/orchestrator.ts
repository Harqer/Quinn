import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { logger } from "firebase-functions";
import { lyriaProAgent } from "./lyriaProAgent";
import { lyriaRealTimeAgent } from "./lyriaRealTimeAgent";
import { imageGenAgent } from "./imageGenAgent";
import { omniFlashAgent } from "./omniFlashAgent";
import { concertAgent, triviaAgent } from "./miscAgents";

const GEMINI_API_KEY = defineSecret("GEMINI_API_KEY");
const RAPID_API_KEY = defineSecret("RAPID_API_KEY");

export type OrchestratorTool =
  | "generate_full_track"
  | "jam_live"
  | "tweak_instrumentation"
  | "generate_cover_image"
  | "generate_music_video"
  | "search_concerts"
  | "validate_trivia_guess";

export const executeTool = onCall(
  {
    secrets: [GEMINI_API_KEY, RAPID_API_KEY],
    cors: true,
    timeoutSeconds: 300,
  },
  async (request) => {
    try {
      if (!request.auth) {
        throw new HttpsError("unauthenticated", "User must be logged in");
      }
      
      const { name, args } = request.data as { name: OrchestratorTool; args?: Record<string, any> };
      const uid = request.auth.uid;
      const apiKey = GEMINI_API_KEY.value();

      switch (name) {
        case "generate_full_track":
          if (!args?.prompt) throw new HttpsError("invalid-argument", "Prompt is required");
          return await lyriaProAgent({ prompt: args.prompt, apiKey, uid });

        case "jam_live":
        case "tweak_instrumentation":
          if (!args?.prompt) throw new HttpsError("invalid-argument", "Prompt is required");
          return await lyriaRealTimeAgent({ prompt: args.prompt, audioUrl: args.audioUrl, apiKey, uid });

        case "generate_cover_image":
          if (!args?.prompt) throw new HttpsError("invalid-argument", "Prompt is required");
          return await imageGenAgent({ prompt: args.prompt, apiKey, uid });

        case "generate_music_video":
          if (!args?.prompt) throw new HttpsError("invalid-argument", "Prompt is required");
          return await omniFlashAgent({ prompt: args.prompt, audioUrl: args.audioUrl, apiKey, uid });

        case "search_concerts":
          if (!args?.query) throw new HttpsError("invalid-argument", "Query is required");
          return await concertAgent({ query: args.query, rapidApiKey: RAPID_API_KEY.value() });

        case "validate_trivia_guess":
          if (!args?.guess || !args?.answer) throw new HttpsError("invalid-argument", "Guess and answer are required");
          return await triviaAgent({ guess: args.guess, answer: args.answer });

        default: {
          const _exhaustiveCheck: never = name;
          throw new HttpsError("invalid-argument", `Unknown tool name: ${_exhaustiveCheck}`);
        }
      }
    } catch (error) {
      logger.error("[Orchestrator] Execution failed:", error);
      const errorMessage = error instanceof Error ? error.message : String(error);
      throw new HttpsError("internal", errorMessage);
    }
  }
);
