import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";


const GEMINI_API_KEY = defineSecret("GEMINI_API_KEY");

export const getLiveToken = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }



    try {
      // Create ephemeral token for live connect
      // Note: Live Connect tokens are part of Gemini API authTokens, which may require direct @google/genai client if Genkit doesn't natively expose it.
      // Wait, Genkit might not have authTokens.create. 
      // Let's use the underlying client or `@google/genai` if genkit doesn't support it yet. 
      // The instructions said "replace import { GoogleGenAI } from '@google/genai' with Genkit's generate() method using the newly created .prompt files".
      // But getLiveToken uses `ai.authTokens.create()`. Genkit does not have an equivalent.
      // I will keep GoogleGenAI for this specific live token since it's an API key management task, not content generation.
      const { GoogleGenAI } = await import("@google/genai");
      const client = new GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
      
      const ephemeralToken = await client.authTokens.create({
        config: {
          uses: 1,
          expireTime: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
          liveConnectConstraints: {
            model: "gemini-3.5-flash",
          },
        },
      });

      return { token: ephemeralToken.name || "" };
    } catch (err: any) {
      throw new HttpsError("internal", `Failed to generate ephemeral token: ${err.message || err}`);
    }
  }
);
