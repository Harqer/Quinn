import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import * as admin from "firebase-admin";
import { GoogleGenAI } from "@google/genai";

admin.initializeApp();

const GEMINI_API_KEY = defineSecret("GEMINI_API_KEY");

export const getLiveToken = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true, // Explicitly enable CORS as per Google Cloud best practices
  },
  async (request) => {
    // Ensure the user is authenticated
    if (!request.auth) {
      throw new HttpsError(
        "unauthenticated",
        "The function must be called while authenticated."
      );
    }

    const ai = new GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    try {
      const ephemeralToken = await ai.authTokens.create({
        config: {
          uses: 1,
          expireTime: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
          liveConnectConstraints: {
            model: "gemini-3.1-flash-live-preview",
          },
        },
      });

      return {
        token: ephemeralToken.name || "",
      };
    } catch (err: any) {
      throw new HttpsError(
        "internal",
        `Failed to generate ephemeral token: ${err.message || err}`
      );
    }
  }
);

