import * as logger from "firebase-functions/logger";
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { checkFreeQuota } from "../auth";
import { executeMutation } from "../dataconnect";
import { getStorage } from "firebase-admin/storage";
import { genkit } from "genkit";
import { googleAI } from "@genkit-ai/googleai";
import * as fs from "fs";
import * as path from "path";
import * as os from "os";

const GEMINI_API_KEY = defineSecret("GEMINI_API_KEY");

export const generateVisualMedia = onCall(
  {
    secrets: [GEMINI_API_KEY],
    enforceAppCheck: true,
    cors: true,
    timeoutSeconds: 120
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }

    await checkFreeQuota(request.auth.uid);

    const { preset, intent } = request.data;
    
    const ai = genkit({
      plugins: [googleAI({ apiKey: GEMINI_API_KEY.value() })]
    });
    
    try {
      const detailedPrompt = await import("../agents/imageGenAgent").then(m => m.buildExpertArtPrompt(GEMINI_API_KEY.value(), preset || "album cover art", preset));
      logger.info(`Generated detailed prompt for ${preset}: ${detailedPrompt}`);

      const response = await ai.generate({
        model: "googleai/imagen-3.0-generate-001",
        prompt: detailedPrompt,
        config: {
          outputMimeType: "image/jpeg",
          aspectRatio: intent === "video_motion" ? "16:9" : "1:1",
        } as any
      });
      
      let base64Image = "";
      const mediaUrl = response.media?.url;
      if (mediaUrl && mediaUrl.startsWith("data:")) {
        const matches = mediaUrl.match(/^data:image\/jpeg;base64,(.+)$/);
        if (matches) {
          base64Image = matches[1];
        }
      }

      if (!base64Image) {
        throw new Error("No image generated or unrecognized format");
      }

      const imageBuffer = Buffer.from(base64Image, "base64");
      const filename = `${Date.now()}.jpg`;
      const tempFilePath = path.join(os.tmpdir(), filename);
      fs.writeFileSync(tempFilePath, imageBuffer);
      
      const bucket = getStorage().bucket();
      const destination = `visual-media/${request.auth.uid}/${filename}`;
      await bucket.upload(tempFilePath, {
        destination: destination,
        metadata: {
          contentType: 'image/jpeg',
        }
      });
      
      const fileRef = bucket.file(destination);
      await fileRef.makePublic();
      const publicUrl = fileRef.publicUrl();

      await executeMutation("CreatePodcast", {
        title: preset,
        publisher: request.auth.uid,
        description: detailedPrompt,
        storyContext: publicUrl
      });

      return {
        url: publicUrl,
        prompt_used: detailedPrompt
      };
    } catch (err: any) {
      logger.error(err);
      throw new HttpsError("internal", `Failed to generate visual media: ${err.message || err}`);
    }
  }
);
