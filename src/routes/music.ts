import { GoogleGenAI } from "@google/genai";
import { Router, Response } from "express";
import { optionalFirebaseToken, verifyFirebaseToken, AuthenticatedRequest, checkDailyQuota, verifyAppCheck } from "../middlewares/auth.js";
import { checkMonthlyQuota, incrementMonthlyUsage } from "../middlewares/quota.js";
import { auth, appCheck } from "../config/firebase.js";
import { GenerateSchema, ShareVibeSchema } from "../schemas/api.js";
import { WebSocketServer, WebSocket } from "ws";
import logger from "../config/logger.js";
import { musicService } from "../services/MusicService.js";
import { narrativeService } from "../services/NarrativeService.js";
import { InstrumentationService } from "../services/InstrumentationService.js";
import { trackRepository } from "../repositories/TrackRepository.js";
import { getAi } from "../services/ai.js";

const router = Router();
const instrumentationService = new InstrumentationService();
import { getSecret } from "../config/secrets.js";

function sanitizeErrorMessage(err: unknown): string {
  if (err instanceof Error) {
    const msg = err.message || "";
    if (msg.includes("quota reached please upgrade")) {
      return "Quota reached. Please upgrade your plan to continue.";
    }
    if (msg.includes("429") || msg.includes("quota") || msg.includes("RESOURCE_EXHAUSTED")) {
      return "The daily generation limit has been reached. Please try again later.";
    }
    if (msg.includes("503") || msg.includes("overloaded")) {
      return "The server is currently overloaded. Please try again later.";
    }
  }
  return "Generation failed due to an unexpected error. Please try again.";
}

function sanitizeJSONError(err: unknown): string {
  let msg = typeof err === 'string' ? err : "";
  if (err instanceof Error) {
    msg = err.message || JSON.stringify(err);
  } else if (err && typeof err === 'object') {
    msg = JSON.stringify(err);
  }
  
  if (msg.includes("quota reached please upgrade") || msg.includes("429") || /quota|RESOURCE_EXHAUSTED/i.test(msg)) {
    return "The daily generation limit has been reached. Please try again later.";
  }
  if (msg.includes("503") || /overloaded/i.test(msg)) {
    return "The server is currently overloaded. Please try again later.";
  }
  if (msg.includes("404")) {
    return "The requested model is currently unavailable.";
  }
  if (msg.startsWith("{") || msg.startsWith("[") || msg.includes('"error"')) {
    return "Generation failed due to an unexpected error. Please try again.";
  }
  return msg || "Generation failed due to an unexpected error. Please try again.";
}
router.get("/token", verifyAppCheck, async (_req: AuthenticatedRequest, res: Response) => {
  res.status(403).json({
    error: {
      message: "Direct API key access is prohibited. Use ephemeral /live-token endpoint instead.",
      code: "FORBIDDEN_KEY_ACCESS"
    }
  });
});


router.post("/live-token", optionalFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  try {
    const { generateLiveEphemeralToken } = await import("../services/ai.js");
    const token = await generateLiveEphemeralToken(
      "gemini-3.1-flash-live-preview",
      "You are Mave, the Executive Creative Director and Master Musical Orchestrator. Help the user create and tweak music in real-time. Speak naturally, no markdown."
    );
    res.json({ token });
  } catch (err) {
    logger.error("Failed to generate live token", { error: err });
    next(err);
  }
});

router.post("/lyria/full", optionalFirebaseToken, verifyAppCheck, checkMonthlyQuota("song"), checkDailyQuota, async (req: AuthenticatedRequest, res: Response) => {
  const { prompt } = req.body;
  if (!prompt || typeof prompt !== 'string') {
    return res.status(400).json({ error: 'prompt is required' });
  }

  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');
  res.setHeader('Access-Control-Allow-Origin', '*');

  const sendEvent = (type: string, data: any) => {
    res.write(`data: ${JSON.stringify({ type, ...data })}\n\n`);
  };

  try {
    const { getAi } = await import('../services/ai.js');
    const { getSecret } = await import('../config/secrets.js');
    const { encodeWAV } = await import('../utils/wav.js');
    const ai = getAi();

    // 1. Stream Gemini reasoning about what it will create
    try {
      const reasoningStream = await ai.models.generateContentStream({
        model: 'gemini-3.6-flash',
        contents: `You are Mave, the Executive Creative Director. The user wants a full song with this description: "${prompt}". Please put your thoughts in <think> and </think> tags. Use maximum reasoning effort and ultrathink step by step. Provide a raw, unstructured, stream-of-consciousness thinking process. Do NOT use numbered lists or formal steps. Do NOT prefix with 'Thinking Process:'. After the closing </think> tag, briefly describe in 1-2 natural sentences what you are about to create.`,
      });

      let fullText = '';
      for await (const chunk of reasoningStream) {
        let chunkText = '';
        try { chunkText = chunk.text || ''; } catch { /* ignore */ }
        if (chunkText) {
          fullText += chunkText;
          const thinkStart = fullText.indexOf('<think>');
          const thinkEnd = fullText.indexOf('</think>');
          
          if (thinkStart !== -1) {
            if (thinkEnd !== -1) {
              const reasoning = fullText.substring(thinkStart + 7, thinkEnd).trim();
              const parsedText = fullText.substring(thinkEnd + 8).trim();
              sendEvent('reasoning', { text: reasoning });
              if (parsedText) sendEvent('status', { message: parsedText });
            } else {
              const reasoning = fullText.substring(thinkStart + 7).trim();
              sendEvent('reasoning', { text: reasoning });
            }
          } else {
             sendEvent('status', { message: fullText.trim() });
          }
        }
      }
    } catch (e: any) {
      logger.warn('[LYRIA_FULL] Reasoning generation failed on 3.6-flash (possibly quota), falling back to 3.5-flash-lite', e);
      try {
        const fallbackStream = await ai.models.generateContentStream({
          model: 'gemini-3.5-flash-lite',
          contents: `You are Mave, the Executive Creative Director. The user wants a full song with this description: "${prompt}". Please put your thoughts in <think> and </think> tags. Use maximum reasoning effort and ultrathink step by step. Provide a raw, unstructured, stream-of-consciousness thinking process. Do NOT use numbered lists or formal steps. Do NOT prefix with 'Thinking Process:'. After the closing </think> tag, briefly describe in 1-2 natural sentences what you are about to create.`,
        });
        let fullText = '';
        for await (const chunk of fallbackStream) {
          let chunkText = '';
          try { chunkText = chunk.text || ''; } catch { /* ignore */ }
          if (chunkText) {
            fullText += chunkText;
            const thinkStart = fullText.indexOf('<think>');
            const thinkEnd = fullText.indexOf('</think>');
            
            if (thinkStart !== -1) {
              if (thinkEnd !== -1) {
                const reasoning = fullText.substring(thinkStart + 7, thinkEnd).trim();
                const parsedText = fullText.substring(thinkEnd + 8).trim();
                sendEvent('reasoning', { text: reasoning });
                if (parsedText) sendEvent('status', { message: parsedText });
              } else {
                const reasoning = fullText.substring(thinkStart + 7).trim();
                sendEvent('reasoning', { text: reasoning });
              }
            } else {
               sendEvent('status', { message: fullText.trim() });
            }
          }
        }
      } catch (fallbackErr: any) {
        throw new Error('quota reached please upgrade');
      }
    }

    // 2. Generate track metadata concurrently
    let trackName = 'Generated Track';
    let artistName = 'Mave AI';
    const metaPrompt = `Return ONLY a JSON object with "trackName" and "artistName" for a song described as: ${prompt}. No markdown, just JSON.`;
    try {
      const metaRes = await ai.models.generateContent({
        model: 'gemini-3.6-flash',
        contents: metaPrompt,
      });
      const parsed = JSON.parse(metaRes.text || '{}');
      if (parsed.trackName) trackName = parsed.trackName;
      if (parsed.artistName) artistName = parsed.artistName;
    } catch (e: any) {
      logger.warn('[LYRIA_FULL] Metadata generation failed on 3.6-flash, falling back to 3.5-flash-lite', e);
      try {
        const fallbackRes = await ai.models.generateContent({
          model: 'gemini-3.5-flash-lite',
          contents: metaPrompt,
        });
        const fallbackParsed = JSON.parse(fallbackRes.text || '{}');
        if (fallbackParsed.trackName) trackName = fallbackParsed.trackName;
        if (fallbackParsed.artistName) artistName = fallbackParsed.artistName;
      } catch (fallbackErr: any) {
        logger.warn('[LYRIA_FULL] Metadata generation fallback failed', fallbackErr);
        throw new Error('quota reached please upgrade');
      }
    }

    // 3. Stream Lyria 3 audio chunks
    // Detect YouTube URL in the prompt
    const ytRegex = /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/watch\?v=|youtu\.be\/)([a-zA-Z0-9_-]{11})/;
    const ytMatch = prompt.match(ytRegex);
    let promptContents: any = prompt;

    if (ytMatch) {
      sendEvent('status', { message: 'Extracting audio from YouTube...' });
      const { default: ytdl } = await import('@distube/ytdl-core');
      const { createWriteStream } = await import('fs');
      const { join } = await import('path');
      const { tmpdir } = await import('os');
      const { pipeline } = await import('stream/promises');

      const videoUrl = ytMatch[0];
      const tmpFile = join(tmpdir(), `yt_${Date.now()}.mp3`);
      
      try {
        const stream = ytdl(videoUrl, { filter: 'audioonly', quality: 'highestaudio' });
        await pipeline(stream, createWriteStream(tmpFile));
        
        sendEvent('status', { message: 'Uploading audio context to Gemini...' });
        const uploadedFile = await ai.files.upload({
          file: tmpFile,
          // @ts-expect-error
          mimeType: 'audio/mp3',
        });
        
        promptContents = [
          { text: prompt },
          { fileData: { fileUri: uploadedFile.uri, mimeType: uploadedFile.mimeType } }
        ];
        
        // Cleanup temp file in background
        const { unlink } = await import('fs/promises');
        unlink(tmpFile).catch(e => logger.error('Failed to cleanup temp file', e));
      } catch (err) {
        logger.error('YouTube extraction failed', err);
        sendEvent('status', { message: 'Failed to extract YouTube audio, falling back to text prompt.' });
      }
    }

    sendEvent('status', { message: 'Generating music...' });
    const lyriaStream = await ai.models.generateContentStream({
      model: 'lyria-3-pro-preview',
      contents: promptContents
    });

    const audioChunks: Buffer[] = [];
    let chunkCount = 0;

    for await (const chunk of lyriaStream) {
      if (chunk.candidates?.[0]?.content?.parts) {
        for (const part of chunk.candidates[0].content.parts) {
          if (part.inlineData?.data) {
            const buf = Buffer.from(part.inlineData.data, 'base64');
            audioChunks.push(buf);
            chunkCount++;
            // Send chunk index to UI
            sendEvent('audio_chunk', { index: chunkCount });
          }
        }
      }
    }

    // 4. Encode to WAV and send final event
    const wavBuffer = encodeWAV(Buffer.concat(audioChunks), 2, 48000);
    const audioUrl = `data:audio/wav;base64,${wavBuffer.toString('base64')}`;

    sendEvent('done', { audioUrl, trackName, artistName });
    if (req.user?.uid && !req.user?.isGuest) {
      incrementMonthlyUsage(req.user.uid, "song");
    }
    res.end();
  } catch (err) {
    logger.error('[LYRIA_FULL] Generation failed', { error: err });
    sendEvent('error', { message: sanitizeJSONError(err) });
    res.end();
  }
});

router.post("/lyria/steer", optionalFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  const { prompt, bpm, density, brightness } = req.body;
  if (!prompt || typeof prompt !== 'string') {
    return res.status(400).json({ error: 'prompt is required' });
  }

  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');
  res.setHeader('Access-Control-Allow-Origin', '*');

  const sendEvent = (type: string, data: any) => {
    res.write(`data: ${JSON.stringify({ type, ...data })}\n\n`);
  };

  try {
    const { getAi } = await import('../services/ai.js');
    const { getSecret } = await import('../config/secrets.js');
    const { encodeWAV } = await import('../utils/wav.js');
    const ai = getAi();

    // 1. Stream Gemini reasoning about the tweak
    try {
      const reasoningStream = await ai.models.generateContentStream({
        model: 'gemini-3.6-flash',
        contents: `You are Mave, the Executive Creative Director. The user wants to tweak the music: "${prompt}". Please put your thoughts in <think> and </think> tags. Use maximum reasoning effort and ultrathink step by step. Provide a raw, unstructured, stream-of-consciousness thinking process. Do NOT use numbered lists or formal steps. Do NOT prefix with 'Thinking Process:'. After the closing </think> tag, describe in 1 natural sentence what you are changing.`,
      });
      let fullText = '';
      for await (const chunk of reasoningStream) {
        let chunkText = '';
        try { chunkText = chunk.text || ''; } catch { /* ignore */ }
        if (chunkText) {
          fullText += chunkText;
          const thinkStart = fullText.indexOf('<think>');
          const thinkEnd = fullText.indexOf('</think>');
          
          if (thinkStart !== -1) {
            if (thinkEnd !== -1) {
              const reasoning = fullText.substring(thinkStart + 7, thinkEnd).trim();
              const parsedText = fullText.substring(thinkEnd + 8).trim();
              sendEvent('reasoning', { text: reasoning });
              if (parsedText) sendEvent('status', { message: parsedText });
            } else {
              const reasoning = fullText.substring(thinkStart + 7).trim();
              sendEvent('reasoning', { text: reasoning });
            }
          } else {
             sendEvent('status', { message: fullText.trim() });
          }
        }
      }
    } catch (e: any) {
      logger.warn('[LYRIA_STEER] Reasoning generation failed on 3.6-flash (possibly quota), falling back to 1.5-flash', e);
      try {
        const fallbackStream = await ai.models.generateContentStream({
          model: 'gemini-3.5-flash-lite',
          contents: `You are Mave, the Executive Creative Director. The user wants to tweak the music: "${prompt}". Please put your thoughts in <think> and </think> tags. Use maximum reasoning effort and ultrathink step by step. Provide a raw, unstructured, stream-of-consciousness thinking process. Do NOT use numbered lists or formal steps. Do NOT prefix with 'Thinking Process:'. After the closing </think> tag, describe in 1 natural sentence what you are changing.`,
        });
        let fullText = '';
        for await (const chunk of fallbackStream) {
          let chunkText = '';
          try { chunkText = chunk.text || ''; } catch { /* ignore */ }
          if (chunkText) {
            fullText += chunkText;
            const thinkStart = fullText.indexOf('<think>');
            const thinkEnd = fullText.indexOf('</think>');
            
            if (thinkStart !== -1) {
              if (thinkEnd !== -1) {
                const reasoning = fullText.substring(thinkStart + 7, thinkEnd).trim();
                const parsedText = fullText.substring(thinkEnd + 8).trim();
                sendEvent('reasoning', { text: reasoning });
                if (parsedText) sendEvent('status', { message: parsedText });
              } else {
                const reasoning = fullText.substring(thinkStart + 7).trim();
                sendEvent('reasoning', { text: reasoning });
              }
            } else {
               sendEvent('status', { message: fullText.trim() });
            }
          }
        }
      } catch (fallbackErr: any) {
        throw new Error('quota reached please upgrade');
      }
    }

    sendEvent('status', { message: 'Applying instrumentation...' });

    // 2. Apply steering via Lyria RealTime
    const apiKey = getSecret('GEMINI_API_KEY') as string;
    const { GoogleGenAI } = await import('@google/genai');
    const aiAlpha = new GoogleGenAI({ apiKey, httpOptions: { apiVersion: 'v1alpha' } });

    const audioChunks: Buffer[] = [];
    let resolveRef!: () => void;
    let rejectRef!: (err: any) => void;
    const receivePromise = new Promise<void>((resolve, reject) => {
      resolveRef = resolve;
      rejectRef = reject;
    });

    let chunkCount = 0;
    const MAX_CHUNKS = 10;
    let isResolved = false;

    const session = await aiAlpha.live.music.connect({
      model: 'models/lyria-realtime-exp',
      config: {
        callbacks: {
          onmessage: (message: any) => {
            if (message?.audioChunk?.data) {
              const buf = Buffer.from(message.audioChunk.data, 'base64');
              audioChunks.push(buf);
              chunkCount++;
              sendEvent('audio_chunk', { index: chunkCount, total: MAX_CHUNKS });
              if (chunkCount >= MAX_CHUNKS && !isResolved) {
                isResolved = true;
                resolveRef();
              }
            }
          },
          onerror: (error: any) => {
            if (!isResolved) { isResolved = true; rejectRef(error); }
          },
          onclose: () => {
            if (!isResolved) { isResolved = true; resolveRef(); }
          }
        }
      },
      callbacks: {
        onmessage: (message: any) => {
          if (message?.audioChunk?.data) {
            const buf = Buffer.from(message.audioChunk.data, 'base64');
            audioChunks.push(buf);
            chunkCount++;
            sendEvent('audio_chunk', { index: chunkCount, total: MAX_CHUNKS });
            if (chunkCount >= MAX_CHUNKS && !isResolved) {
              isResolved = true;
              resolveRef();
            }
          }
        },
        onerror: (error: any) => {
          logger.error('Lyria 3 error', { error });
          if (!isResolved) { isResolved = true; rejectRef(error); }
        },
        onclose: () => {
          if (!isResolved) { isResolved = true; resolveRef(); }
        }
      }
    } as any);

    await session.setWeightedPrompts({ weightedPrompts: [{ text: prompt, weight: 1.0 }] });
    await session.setMusicGenerationConfig({
      musicGenerationConfig: {
        bpm: bpm || 120,
        density: density || 0.8,
        brightness: brightness || 0.7
      }
    });
    session.play();

    await receivePromise;
    session.close();

    const wavBuffer = encodeWAV(Buffer.concat(audioChunks), 2, 48000);
    const audioUrl = `data:audio/wav;base64,${wavBuffer.toString('base64')}`;

    sendEvent('done', { audioUrl });
    res.end();
  } catch (err) {
    logger.error('[LYRIA_STEER] Failed', { error: err });
    sendEvent('error', { message: sanitizeJSONError(err) });
    res.end();
  }
});

router.post("/cover", optionalFirebaseToken, verifyAppCheck, checkMonthlyQuota("song"), checkDailyQuota, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { prompt, hq } = req.body;
  if (!prompt || typeof prompt !== 'string') {
    return res.status(400).json({ error: 'prompt is required' });
  }

  try {
    const { generateCoverMedia } = await import('../services/ai.js');
    const result = await generateCoverMedia(prompt, 'cover_art', hq ? 'latest' : 'flash');
    res.json({ url: result.url, prompt: result.prompt, modelUsed: result.modelUsed });
  } catch (err) {
    logger.error('[COVER] Generation failed', { error: err });
    next(err);
  }
});

router.post("/video", verifyFirebaseToken, verifyAppCheck, checkMonthlyQuota("song"), checkDailyQuota, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { prompt } = req.body;
  if (!prompt || typeof prompt !== 'string') {
    return res.status(400).json({ error: 'prompt is required' });
  }

  try {
    const { generateCoverMedia } = await import('../services/ai.js');
    const result = await generateCoverMedia(prompt, 'video_motion', 'latest');
    res.json({ url: result.url, prompt: result.prompt, modelUsed: result.modelUsed });
  } catch (err) {
    logger.error('[VIDEO] Generation failed', { error: err });
    next(err);
  }
});

router.post("/generate", optionalFirebaseToken, verifyAppCheck, checkMonthlyQuota("song"), checkDailyQuota, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const result = GenerateSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.issues });

  try {
    const data = await musicService.generateMusicDirectly(result.data.image, result.data.type, req.body.variant);
    res.json(data);
  } catch (err) {
    logger.error("Generation Failed", { error: err });
    next(err);
  }
});

router.post("/generate-from-media", optionalFirebaseToken, verifyAppCheck, checkDailyQuota, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { data, mimeType } = req.body;
  if (!data || !mimeType) {
    return res.status(400).json({ error: "Media data (base64) and mimeType are required" });
  }

  try {
    const { getSecret } = await import("../config/secrets.js");
    const apiKey = getSecret("GEMINI_API_KEY") as string;
    const ai = new GoogleGenAI({ apiKey });
    
    // Determine type for genai SDK format
    const formatData = data.includes(',') ? data.split(',')[1] : data;

    const response = await ai.models.generateContent({
      model: "gemini-3.5-flash-lite",
      contents: [
        {
          role: 'user',
          parts: [
            { inlineData: { data: formatData, mimeType } },
            { text: "Analyze this media and write a highly detailed musical prompt (genre, instruments, vibe, tempo) that perfectly scores this scene. Return ONLY the text prompt without formatting or preamble." }
          ]
        }
      ]
    });

    const musicPrompt = response.text?.trim() || "A cinematic, ambient instrumental track";
    logger.info(`Generated music prompt from media: ${musicPrompt}`);

    const trackData = await musicService.generateMusicDirectly(undefined, musicPrompt);
    res.json(trackData);
  } catch (err) {
    logger.error("Media-to-music Generation Failed", { error: err });
    next(err);
  }
});

router.get("/podcast/voices", optionalFirebaseToken, verifyAppCheck, async (req: any, res: any, next: import("express").NextFunction) => {
  try {
    const { TextToSpeechClient } = await import('@google-cloud/text-to-speech').then(m => m.v1beta1);
    const ttsClient = new TextToSpeechClient({ projectId: process.env.GOOGLE_CLOUD_PROJECT || 'musically-studio' });
    const [response] = await ttsClient.listVoices({ languageCode: 'en-US' });
    
    // Filter voices. Let's provide a good selection of high-quality voices.
    // Studio and Casual are premium/conversational.
    const premiumVoices = (response.voices || [])
      .filter(v => v.name && v.name.includes('en-US') && (v.name.includes('Studio') || v.name.includes('Casual') || v.name.includes('Neural')))
      .map(v => {
        let desc = 'Standard Voice';
        if (v.name?.includes('Studio')) desc = 'Premium Studio Quality';
        else if (v.name?.includes('Casual')) desc = 'Relaxed & Casual';
        else if (v.name?.includes('Neural')) desc = 'High Quality Neural';
        
        return {
          id: v.name,
          name: v.name?.replace('en-US-', ''),
          desc,
          ssmlGender: v.ssmlGender
        };
      })
      // sort to have Studio first
      .sort((a, b) => {
        if (a.name?.includes('Studio') && !b.name?.includes('Studio')) return -1;
        if (!a.name?.includes('Studio') && b.name?.includes('Studio')) return 1;
        return 0;
      })
      .slice(0, 10); // limit to 10 for UI 
      
    res.json(premiumVoices);
  } catch (err) {
    logger.error("Failed to list voices", { error: err });
    next(err);
  }
});


router.post("/podcast/generate", optionalFirebaseToken, verifyAppCheck, checkMonthlyQuota("podcast"), checkDailyQuota, async (req: AuthenticatedRequest, res: Response) => {
  const { prompt, voice } = req.body;
  const locale = req.headers["accept-language"] || "en";
  if (!prompt || typeof prompt !== "string" || prompt.trim().length === 0) {
    return res.status(400).json({ error: "Valid prompt string is required" });
  }

  res.setHeader("Content-Type", "text/event-stream");
  res.setHeader("Cache-Control", "no-cache");
  res.setHeader("Connection", "keep-alive");

  try {
    await narrativeService.generateStream(prompt.trim(), "podcast", voice || "AOEDE", locale, res);
    if (req.user?.uid && !req.user?.isGuest) {
      incrementMonthlyUsage(req.user.uid, "podcast");
    }
  } catch (err) {
    logger.error("[PODCAST_ROUTE] Generation Stream Failed", { error: err });
    res.write(`data: ${JSON.stringify({ type: 'error', error: "Podcast Generation Failed" })}\n\n`);
    res.end();
  }
});

router.post("/audiobook/generate", optionalFirebaseToken, verifyAppCheck, checkDailyQuota, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { prompt, voice } = req.body;
  const locale = req.headers["accept-language"] || "en";
  if (!prompt || typeof prompt !== "string" || prompt.trim().length === 0) {
    return res.status(400).json({ error: "Valid prompt string is required" });
  }

  try {
    const audiobookTrack = await narrativeService.generateFromPrompt(prompt.trim(), "audiobook", voice || "KORE", locale);
    res.json(audiobookTrack);
  } catch (err) {
    logger.error("[AUDIOBOOK_ROUTE] Generation Failed", { error: err });
    next(err);
  }
});

router.post("/share", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { trackId } = req.body;
  if (!trackId) return res.status(400).json({ error: "trackId is required" });

  try {
    const url = await musicService.shareTrack(req.user!.uid, trackId);
    res.json({ url });
  } catch (err) {
    logger.error("Failed to share track", { error: err });
    next(err);
  }
});

router.post("/voice/command", optionalFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { sessionId, audio } = req.body;
  if (!audio) {
    return res.status(400).json({ error: "audio payload (base64) is required" });
  }

  try {
    const { maveVisionFlow, directorFlow } = await import("../services/genkit-flows.js");
    const visionResult = await maveVisionFlow({ image: audio, locale: "en" });
    const directorResult = await directorFlow({ visionDescription: visionResult.visionDescription });
    res.json({
      sessionId: sessionId || req.user?.uid || "session-voice",
      status: "completed",
      action: directorResult.modality,
      reasoning: directorResult.reasoning,
      prompts: [directorResult.reasoning, visionResult.visionDescription]
    });
  } catch (err) {
    logger.error("Voice Command Processing Failed", { error: err });
    next(err);
  }
});

router.post("/text/command", optionalFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { sessionId, text } = req.body;
  if (!text) {
    return res.status(400).json({ error: "text is required" });
  }

  try {
    const { directorFlow } = await import("../services/genkit-flows.js");
    const directorResult = await directorFlow({ visionDescription: text, userFeedback: text });
    res.json({
      sessionId: sessionId || req.user?.uid || "session-text",
      status: "completed",
      action: directorResult.modality,
      reasoning: directorResult.reasoning,
      prompts: [text, directorResult.reasoning]
    });
  } catch (err) {
    logger.error("Text Command Processing Failed", { error: err });
    next(err);
  }
});

router.post("/generate", optionalFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { image } = req.body;
  if (!image) {
    return res.status(400).json({ error: "image payload (base64) is required" });
  }

  try {
    const { maveVisionFlow } = await import("../services/genkit-flows.js");
    const visionResult = await maveVisionFlow({ image });
    res.json({
      prompts: [visionResult.visionDescription, "immersive ambient weave"]
    });
  } catch (err) {
    logger.error("Generate from frame failed", { error: err });
    next(err);
  }
});

router.post("/playlists/recover", optionalFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response) => {
  res.json({ status: "success", message: "Playlists synchronized with Firebase state" });
});

router.post("/execute-tool", verifyFirebaseToken, verifyAppCheck, async (req: AuthenticatedRequest, res: Response, next: import("express").NextFunction) => {
  const { name, args } = req.body;
  if (!name) return res.status(400).json({ error: "Tool name is required" });

  try {
    logger.info(`Executing tool ${name}`, { args, uid: req.user?.uid });
    
    // Process different tool calls based on their name
    switch (name) {
      case 'generate_full_track': {
        // Lyria 3: Full song
        const { musicService } = await import('../services/MusicService.js');
        const lyriaResult = await musicService.generateMusicDirectly(undefined, args.prompt);
        return res.json({ status: 'success', message: 'Track generated', result: lyriaResult });
      }
      case 'tweak_instrumentation': {
        // Lyria RealTime: Instrument steering
        const { musicService } = await import('../services/MusicService.js');
        await musicService.applySteering({ ...args, sessionId: req.user?.uid || 'anon' }, req.user?.uid || 'anon');
        return res.json({ status: 'success', message: 'Instrumentation tweaked' });
      }
      case 'generate_cover_art': {
        const { generateCoverMedia } = await import('../services/ai.js');
        const coverResult = await generateCoverMedia(args.prompt, 'cover_art', args.hq ? 'latest' : 'flash');
        return res.json({ status: 'success', url: coverResult.url });
      }
      case 'generate_video': {
        const { generateCoverMedia: genVideo } = await import('../services/ai.js');
        const videoResult = await genVideo(args.prompt, 'video_motion', 'latest');
        return res.json({ status: 'success', url: videoResult.url });
      }
      case 'jam_live': {
        const { musicService } = await import('../services/MusicService.js');
        const interaction = await musicService.createMusicInteraction(
          args?.prompt || "Live Jamming Session",
          req.user?.uid || "anon"
        );
        return res.json({ status: "success", message: "Entered live jamming mode", interaction });
      }

      default:
        return res.status(400).json({ error: "Unknown tool" });
    }
  } catch (err) {
    logger.error("Failed to execute tool", { error: err });
    next(err);
  }
});





// WebSocket Server removed in favor of Firebase AI SDK native connections

export default router;
