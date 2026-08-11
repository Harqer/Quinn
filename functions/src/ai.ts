import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { GoogleGenAI } from "@google/genai";
import { checkFreeQuota } from "./auth";
import * as fs from "fs";
import * as path from "path";
import * as os from "os";
import { executeMutation } from "./dataconnect";
import { getStorage } from "firebase-admin/storage";

const GEMINI_API_KEY = defineSecret("GEMINI_API_KEY");

// Load all RLM-distilled MIT Storytelling Frameworks synchronously at cold start
const frameworksDir = path.join(__dirname, "frameworks");
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
  console.error("Warning: frameworks directory not found or unreadable. Falling back to empty frameworks.");
}

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

    const ai = new GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    try {
      const ephemeralToken = await ai.authTokens.create({
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

    const ai = new GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    
    try {
      const routingResponse = await ai.models.generateContent({
        model: "gemini-3.5-flash",
        contents: `Classify the following podcast topic into exactly one of these genres: 'sports', 'autobiography', 'fiction', 'essay', or 'general'. Topic: "${topic}". Return ONLY the genre word in lowercase.`,
      });
      
      let genre = (routingResponse.text || "general").trim().toLowerCase();
      if (!frameworks[genre]) {
        genre = "general";
      }
      
      console.log(`Routed topic "${topic}" to framework: ${genre}`);

      const response = await ai.models.generateContent({
        model: "gemini-3.5-flash",
        contents: `Write a podcast script about: ${topic}`,
        config: {
          systemInstruction: `You are an elite podcast scriptwriter and narrative architect. Your objective is to engineer deeply compelling, high-traction podcast scripts.

Even in a non-fiction or conversational format, you must treat the script as a closed-loop cognitive and emotional simulation, applying rigorous storytelling principles:

### I. Narrative Arc & Pacing
* **Thermodynamic Pacing:** Build the episode with a clear arc: establish the core question/status quo, introduce disrupting information or conflicts, escalate the tension/stakes of the topic, and resolve with profound insights or synthesis.
* **Scene-to-Summary Ratio:** Calibrate velocity. Dilate micro-moments of high importance (e.g., a critical anecdote, a dramatic historical beat) into sensory-dense, real-time exploration. Compress routine background context into clean, high-level summaries.

### II. Information Asymmetry & Psychological Tension
* **Asymmetry Control:** Weaponize the delta between what the host knows, what the guest knows, and what the listener knows. Deliberately control the release of information to manufacture curiosity and suspense.
* **Dialogic Friction:** Conversations shouldn't be flat agreements. Build subtext through tactical questioning, intellectual friction, and shifting power dynamics between speakers.

### III. Syntactic & Lexical Engineering
* **Voice & Pacing Control:** Optimize the agent-action-patient pipeline via active voice dominance. Rewrite any passive constructions. Utilize periodic sentences to suspend completion and build tension, and cumulative sentences to lower reading latency and mimic natural thought.
* **Sentence Variance:** Mix short sentences (under 10 words) with longer ones (20+ words). Never write three sentences of similar length in a row.
* **Semantic Precision:** Maintain absolute register and tone consistency. Balance denotation and connotation to eliminate drift.
* **Prohibited Phrasing:** Remove these phrases entirely: delve, tapestry, pivotal, furthermore, moreover, in conclusion, it is worth noting. Do not overuse em-dashes.
* **Perspective & Tone:** Write in first or second person (use 'I' or 'you' naturally). Match the tone to an engaging, conversational podcast discussion. Keep all factual claims intact.

Your output must be a highly engaging, well-paced script formatted for audio TTS synthesis.`,
        },
      });

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
    
    const ai = new GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    
    try {
      const detailedPrompt = await import("./agents/imageGenAgent").then(m => m.buildExpertArtPrompt(GEMINI_API_KEY.value(), preset || "album cover art", preset));
      console.log(`Generated detailed prompt for ${preset}: ${detailedPrompt}`);

      const imageResponse: any = await (ai.models as any).generateImages({
        model: "imagen-3.0-generate-001",
        prompt: detailedPrompt,
        config: {
          numberOfImages: 1,
          outputMimeType: "image/jpeg",
          aspectRatio: intent === "video_motion" ? "16:9" : "1:1",
        }
      });
      
      const base64Image = imageResponse.generatedImages[0].image.imageBytes;
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
      console.error(err);
      throw new HttpsError("internal", `Failed to generate visual media: ${err.message || err}`);
    }
  }
);

export const generateLyrics = onCall(
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

    const { trackId, audioUrl } = request.data;
    console.log(`Generating lyrics for track ${trackId}, audioUrl: ${audioUrl}`);
    
    const ai = new GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    
    try {
      const promptResponse = await ai.models.generateContent({
        model: "gemini-3.5-flash",
        contents: `You are an expert songwriter. Write a 2-minute hit song's lyrics. Structure it with Verse, Chorus, Verse, Chorus, Bridge, Outro. Make it emotive and catchy. Do not include any conversational filler.`,
      });
      
      return {
        lyrics: promptResponse.text?.trim() || ""
      };
    } catch (err: any) {
      console.error(err);
      throw new HttpsError("internal", `Failed to generate lyrics: ${err.message || err}`);
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

    const ai = new GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    
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

      const response = await ai.models.generateContent({
        model: "gemini-3.5-flash",
        contents: `Generate a narrative series about: ${topic}`,
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
      console.error(err);
      throw new HttpsError("internal", `Failed to generate narrative series: ${err.message || err}`);
    }
  }
);



export const renderNarrativeAudio = onCall(
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
    const { episodeId, script } = request.data;
    if (!episodeId || !script) {
      throw new HttpsError("invalid-argument", "Missing episodeId or script.");
    }
    
    console.log(`[Background Task] Starting audio render for episode ${episodeId}...`);
    
    const client = new GoogleGenAI({ apiKey: GEMINI_API_KEY.value() });
    
    try {
      const interaction = await client.interactions.create({
        model: "gemini-3.5-flash-tts-preview",
        input: script,
      });

      const outputAudio = interaction.output_audio;
      if (!outputAudio || !outputAudio.data) {
        throw new HttpsError("internal", "No audio data returned from Gemini TTS.");
      }

      // Buffer from base64
      const audioBuffer = Buffer.from(outputAudio.data, "base64");
      
      const bucket = getStorage().bucket();
      const fileName = `narratives/${episodeId}-${Date.now()}.mp3`;
      const file = bucket.file(fileName);
      
      await file.save(audioBuffer, {
        metadata: {
          contentType: outputAudio.mime_type || "audio/mp3"
        }
      });
      await file.makePublic(); // Depending on privacy rules, or get a signed URL
      const publicUrl = file.publicUrl();

      // Update Firestore via DataConnect mutation
      await executeMutation("UpdateEpisodeAudio", {
        id: episodeId,
        audioUrl: publicUrl
      });
      
      return { success: true, audioUrl: publicUrl };
    } catch (err: any) {
      console.error("Audio rendering failed:", err);
      throw new HttpsError("internal", `Audio rendering failed: ${err.message || err}`);
    }
  }
);
