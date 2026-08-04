import { useState, useEffect, useRef, useCallback } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";
import { GoogleGenAI, Modality } from '@google/genai';
import { readSSE } from '../utils/sse';
import { audioService } from '../services/AudioService';


export type MaveMode = 'music' | 'podcast' | 'audiobook';

export interface MaveMessage {
  id: string;
  sender: 'user' | 'mave';
  text: string;
  isAudio?: boolean;
  trackId?: string;
  type?: string;
  title?: string;
  voice?: string;
  coverUrl?: string;
  script?: string;
  audioUrl?: string;
  videoUrl?: string;
  reasoning?: string;
  isReasoningComplete?: boolean;
}

// Tool declarations: Lyria 3 = full songs, Lyria RealTime = instrument tweaking
const functionDeclarations = [
  {
    name: 'generate_full_track',
    description: 'Generate a new, complete professional music track or song (Lyria 3). Use this when the user wants a full song created from scratch.',
    parameters: {
      type: 'OBJECT',
      properties: {
        prompt: { type: 'STRING', description: 'Musical style, genre, and description of the full song to create' }
      },
      required: ['prompt']
    }
  },
  {
    name: 'tweak_instrumentation',
    description: 'Modify or tweak the instruments, density, BPM, brightness, or style of the currently playing track in real-time (Lyria RealTime). Use when the user wants to change how the song sounds without regenerating from scratch.',
    parameters: {
      type: 'OBJECT',
      properties: {
        prompt: { type: 'STRING', description: 'What to tweak (e.g. add more bass, make it faster, add jazz piano)' },
        bpm: { type: 'NUMBER', description: 'Target beats per minute' },
        density: { type: 'NUMBER', description: 'Note density 0.0-1.0' },
        brightness: { type: 'NUMBER', description: 'Tonal brightness 0.0-1.0' }
      },
      required: ['prompt']
    }
  },
  {
    name: 'generate_cover_art',
    description: 'Generate or update the album cover art for the current track. Use when the user asks for cover art.',
    parameters: {
      type: 'OBJECT',
      properties: {
        prompt: { type: 'STRING', description: 'Visual description for the cover art' },
        hq: { type: 'BOOLEAN', description: 'Set true for high-quality Pro model, false for fast default' }
      },
      required: ['prompt']
    }
  },
  {
    name: 'generate_video',
    description: 'Generate a music video for the current track. Only use when the user explicitly asks for a video.',
    parameters: {
      type: 'OBJECT',
      properties: {
        prompt: { type: 'STRING', description: 'Visual and cinematic description for the music video' }
      },
      required: ['prompt']
    }
  }
];

// SSE helper has been extracted to utils/sse.ts

export function useMave() {
  const [messages, setMessages] = useState<MaveMessage[]>([]);
  const [mode, setMode] = useState<MaveMode>('music');
  const [isConnected, setIsConnected] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [thinkingText, setThinkingText] = useState("");
  const [coverArtUrl, setCoverArtUrl] = useState<string | null>(null);
  const [videoMotionUrl, setVideoMotionUrl] = useState<string | null>(null);
  const [isGenerating, setIsGenerating] = useState(false);

  const aiRef = useRef<GoogleGenAI | null>(null);
  const aiRestRef = useRef<GoogleGenAI | null>(null);
  const sessionRef = useRef<any>(null);
  const recorderRef = useRef<MediaRecorder | null>(null);
  const audioStreamRef = useRef<MediaStream | null>(null);

  const audioQueue = useRef<Blob[]>([]);
  const isPlaying = useRef(false);
  const initialLoadDone = useRef(false);

  // Returns the backend base URL, always with a value
  const getBaseUrl = useCallback(() => {
    return (import.meta.env.VITE_API_URL as string) || 'http://127.0.0.1:8081';
  }, []);

  // Returns the Firebase auth token if logged in
  const getAuthToken = useCallback(async (): Promise<string | undefined> => {
    try {
      const auth = getAuth();
      return await auth.currentUser?.getIdToken();
    } catch {
      return undefined;
    }
  }, []);

  // Load chat history on mount
  useEffect(() => {
    const loadHistory = async () => {
      try {
        const baseUrl = getBaseUrl();
        const authToken = await getAuthToken();
        const headers: Record<string, string> = { 'Content-Type': 'application/json' };
        if (authToken) headers['Authorization'] = `Bearer ${authToken}`;
        
        const res = await fetch(`${baseUrl}/api/chat/history`, { headers });
        if (res.ok) {
          const data = await res.json();
          if (data.messages && data.messages.length > 0) {
            setMessages(data.messages);
          }
        }
      } catch (err) {
        logger.warn("Failed to load chat history", err);
      } finally {
        initialLoadDone.current = true;
      }
    };
    loadHistory();
  }, [getBaseUrl, getAuthToken]);

  // Save chat history on change
  useEffect(() => {
    if (!initialLoadDone.current || messages.length === 0) return;
    const saveHistory = async () => {
      try {
        const baseUrl = getBaseUrl();
        const authToken = await getAuthToken();
        const headers: Record<string, string> = { 'Content-Type': 'application/json' };
        if (authToken) headers['Authorization'] = `Bearer ${authToken}`;
        
        await fetch(`${baseUrl}/api/chat/history`, {
          method: 'POST',
          headers,
          body: JSON.stringify({ messages })
        });
      } catch (err) {
        logger.warn("Failed to save chat history", err);
      }
    };
    // Debounce slightly to avoid rapid saves
    const t = setTimeout(saveHistory, 1000);
    return () => clearTimeout(t);
  }, [messages, getBaseUrl, getAuthToken]);

  // Fetches an ephemeral Gemini Live token from the backend (never exposes raw key)
  const initAI = useCallback(async () => {
    if (!aiRef.current) {
      try {
        const baseUrl = getBaseUrl();
        const authToken = await getAuthToken();
        const res = await fetch(`${baseUrl}/api/music/live-token`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            ...(authToken ? { 'Authorization': `Bearer ${authToken}` } : {})
          }
        });
        if (!res.ok) throw new Error(`Token endpoint returned ${res.status}`);
        const data = await res.json();
        if (!data.token) throw new Error('No token returned from server');
        aiRef.current = new GoogleGenAI({ apiKey: data.token });
      } catch (e) {
        throw new Error(`Failed to initialize AI: ${e instanceof Error ? e.message : String(e)}`);
      }
    }
    return aiRef.current;
  }, [getBaseUrl, getAuthToken]);

  // Fetches a standard token for non-live REST API calls
  const initAIRest = useCallback(async () => {
    if (!aiRestRef.current) {
      try {
        const baseUrl = getBaseUrl();
        const authToken = await getAuthToken();
        const res = await fetch(`${baseUrl}/api/music/token`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            ...(authToken ? { 'Authorization': `Bearer ${authToken}` } : {})
          }
        });
        if (!res.ok) throw new Error(`Rest Token endpoint returned ${res.status}`);
        const data = await res.json();
        if (!data.token) throw new Error('No rest token returned from server');
        aiRestRef.current = new GoogleGenAI({ apiKey: data.token });
      } catch (e) {
        throw new Error(`Failed to initialize AI Rest: ${e instanceof Error ? e.message : String(e)}`);
      }
    }
    return aiRestRef.current;
  }, [getBaseUrl, getAuthToken]);

  // Handles tool calls dispatched from Gemini
  const handleToolCall = useCallback(async (toolCall: { name: string; args?: any; id?: string }) => {
    logger.info("Tool Call Received", toolCall);
    const baseUrl = getBaseUrl();
    const authToken = await getAuthToken();
    const headers: Record<string, string> = { 'Content-Type': 'application/json' };
    if (authToken) headers['Authorization'] = `Bearer ${authToken}`;

    try {
      switch (toolCall.name) {
        case 'generate_full_track': {
          // Lyria 3: Full song generation via SSE
          const responseId = Date.now().toString();
          let reasoningText = '';
          setMessages(prev => [{ id: responseId, text: '', sender: 'mave' as const }, ...prev].slice(0, 15));

          for await (const event of readSSE(`${baseUrl}/api/music/lyria/full`, { prompt: toolCall.args?.prompt }, authToken)) {
            if (event.type === 'reasoning' && event.text) {
              reasoningText += event.text;
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, reasoning: reasoningText } : m));
            } else if (event.type === 'status' || event.type === 'audio_chunk') {
              if (event.type === 'status') {
                 setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: event.message, isReasoningComplete: true } : m));
              }
            } else if (event.type === 'done' && event.audioUrl) {
              setMessages(prev => prev.map(m => m.id === responseId ? {
                ...m,
                text: reasoningText || 'Here is your track!',
                audioUrl: event.audioUrl,
                title: event.trackName,
                voice: event.artistName,
                type: 'track'
              } : m));
              // Auto-play generated audio
              const audio = new Audio(event.audioUrl);
              audio.play().catch(e => logger.error('Autoplay failed', e));
            } else if (event.type === 'error') {
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: `Error: ${event.message}` } : m));
            }
          }
          break;
        }

        case 'tweak_instrumentation': {
          // Lyria RealTime: Instrument steering via SSE
          const responseId = Date.now().toString();
          let reasoningText = '';
          setMessages(prev => [{ id: responseId, text: '', sender: 'mave' as const }, ...prev].slice(0, 15));

          const currentAudioUrl = audioService.currentTrack?.audioUrl;

          for await (const event of readSSE(`${baseUrl}/api/music/lyria/steer`, {
            prompt: toolCall.args?.prompt,
            bpm: toolCall.args?.bpm,
            density: toolCall.args?.density,
            brightness: toolCall.args?.brightness,
            reference_audio_url: currentAudioUrl
          }, authToken)) {
            if (event.type === 'reasoning' && event.text) {
              reasoningText += event.text;
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, reasoning: reasoningText } : m));
            } else if (event.type === 'status' || event.type === 'audio_chunk') {
              if (event.type === 'status') {
                 setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: event.message, isReasoningComplete: true } : m));
              }
            } else if (event.type === 'done' && event.audioUrl) {
              setMessages(prev => prev.map(m => m.id === responseId ? {
                ...m,
                text: reasoningText || 'Instrumentation updated.',
                audioUrl: event.audioUrl,
                type: 'track'
              } : m));
              
              audioService.addToQueue({
                id: responseId,
                title: 'Tweaked Track',
                artist: 'Lyria',
                audioUrl: event.audioUrl,
                duration: 0
              });
              audioService.skipNext(); // crossfade to the new track
            } else if (event.type === 'error') {
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: `Error: ${event.message}` } : m));
            }
          }
          break;
        }

        case 'generate_cover_art': {
          const res = await fetch(`${baseUrl}/api/music/cover`, {
            method: 'POST', headers,
            body: JSON.stringify({ prompt: toolCall.args?.prompt, hq: toolCall.args?.hq })
          });
          const data = await res.json();
          if (data.url) {
            setCoverArtUrl(data.url);
            setMessages(prev => [{ id: Date.now().toString(), text: 'Cover art updated!', sender: 'mave' as const, coverUrl: data.url, type: 'cover_art' }, ...prev].slice(0, 15));
          } else {
            throw new Error(data.error || 'Cover art generation failed');
          }
          break;
        }

        case 'generate_video': {
          const responseId = Date.now().toString();
          setMessages(prev => [{ id: responseId, text: 'Generating your music video...', sender: 'mave' as const }, ...prev].slice(0, 15));
          const res = await fetch(`${baseUrl}/api/music/video`, {
            method: 'POST', headers,
            body: JSON.stringify({ prompt: toolCall.args?.prompt })
          });
          const data = await res.json();
          if (data.url) {
            setVideoMotionUrl(data.url);
            setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: 'Your music video is ready!', videoUrl: data.url, type: 'video' } : m));
          } else {
            throw new Error(data.error || 'Video generation failed');
          }
          break;
        }

        default:
          logger.warn('Unknown tool call', toolCall.name);
      }
    } catch (err: any) {
      logger.error('Failed to execute tool', err);
      setMessages(prev => [{ id: Date.now().toString(), text: `Error: Tool execution failed: ${err?.message || 'Unknown error'}`, sender: 'mave' as const }, ...prev].slice(0, 15));
    }
  }, [getBaseUrl, getAuthToken]);

  const playNextAudio = useCallback(async () => {
    if (isPlaying.current || audioQueue.current.length === 0) return;
    isPlaying.current = true;
    const blob = audioQueue.current.shift();
    if (!blob) return;
    try {
      const url = URL.createObjectURL(blob);
      const audio = new Audio(url);
      audio.onended = () => {
        URL.revokeObjectURL(url);
        isPlaying.current = false;
        playNextAudio();
      };
      await audio.play();
    } catch (e) {
      logger.error('Audio playback failed', e);
      isPlaying.current = false;
      playNextAudio();
    }
  }, []);

  const connectLiveSession = useCallback(async () => {
    const ai = await initAI();
    try {
      const session = await ai.live.connect({
        model: 'gemini-3.1-flash-live-preview',
        config: {
          responseModalities: [Modality.AUDIO],
          systemInstruction: {
            parts: [{ text: "You are Mave, the Executive Creative Director and Master Musical Orchestrator. Help the user create and tweak music. Respond naturally, conversationally. Do NOT use markdown formatting." }]
          },
          tools: [{ functionDeclarations: functionDeclarations as any }]
        },
        callbacks: {
          onopen: () => {
            setIsConnected(true);
            logger.info('Live API Connected');
          },
          onmessage: (response: any) => {
            const content = response.serverContent;
            if (content?.modelTurn?.parts) {
              for (const part of content.modelTurn.parts) {
                if (part.inlineData) {
                  const audioBytes = Uint8Array.from(atob(part.inlineData.data), c => c.charCodeAt(0));
                  audioQueue.current.push(new Blob([audioBytes], { type: 'audio/pcm;rate=24000' }));
                  playNextAudio();
                }
                if (part.functionCall) {
                  handleToolCall(part.functionCall);
                }
              }
            }
            if (content?.outputTranscription) {
              setMessages(prev => [{ id: Date.now().toString(), text: content.outputTranscription.text, sender: 'mave' as const }, ...prev].slice(0, 15));
            }
            if (content?.interrupted) {
              audioQueue.current = [];
            }
          },
          onerror: (error: any) => logger.error('Live API Error:', error),
          onclose: () => {
            setIsConnected(false);
            logger.info('Live API Closed');
          }
        }
      });
      sessionRef.current = session;
    } catch (err: any) {
      logger.error('Failed to connect to Live API', err);
      const errorMessage = err?.message || 'An unknown error occurred.';
      setMessages(prev => [{ id: Date.now().toString(), text: `Error connecting live session: ${errorMessage}`, sender: 'mave' as const }, ...prev].slice(0, 15));
    }
  }, [initAI, handleToolCall, playNextAudio]);

  useEffect(() => {
    return () => {
      sessionRef.current?.close();
      if (audioStreamRef.current) {
        audioStreamRef.current.getTracks().forEach(track => track.stop());
      }
    };
  }, []);

  const switchMode = (newMode: MaveMode) => {
    if ('vibrate' in navigator) navigator.vibrate(10);
    setMode(newMode);
  };

  // Text chat using Interactions API streaming with fallback to generateContentStream
  const sendText = async (text: string) => {
    setIsGenerating(true);
    const userMsgId = Date.now().toString();
    setMessages(prev => [{ id: userMsgId, text, sender: 'user' as const }, ...prev].slice(0, 15));

    // If Live API is active, send text via Live API
    if (sessionRef.current) {
      sessionRef.current.sendRealtimeInput({ text });
      return;
    }

    const responseId = (Date.now() + 1).toString();
    try {
      const ai = await initAIRest();
      let fullText = '';
      let addedMessage = false;

      let usedModel = 'gemini-3.6-flash';
      let responseStream;
      
      const createStream = async (modelName: string) => {
        return await ai.models.generateContentStream({
          model: modelName,
          contents: text,
          config: {
            systemInstruction: 'You are Mave, the Executive Creative Director and Master Musical Orchestrator. First, you MUST provide a raw, unstructured, stream-of-consciousness thinking process inside <think>...</think> XML tags. Do NOT use numbered lists or formal steps in your thinking; just think aloud naturally. After the closing </think> tag, generate your final conversational response. Do NOT use any markdown formatting in your final response. Speak as a natural voice assistant.',
            tools: [{ functionDeclarations: functionDeclarations as any }]
          }
        });
      };

      try {
        responseStream = await createStream('gemini-3.6-flash');
        // Await the first chunk to catch immediate 429 errors from the lazy iterator
        const iterator = responseStream[Symbol.asyncIterator]();
        const first = await iterator.next();
        
        // If it succeeds, we yield the first chunk manually and then iterate the rest
        const processChunk = async (chunk: any) => {
          if (chunk.functionCalls && chunk.functionCalls.length > 0) {
            for (const call of chunk.functionCalls) {
              await handleToolCall(call);
            }
          }
          let chunkText = '';
          try { chunkText = chunk.text || ''; } catch { /* ignore */ }
          if (chunkText) {
            fullText += chunkText;
            
            let reasoning = '';
            let parsedText = '';
            let isReasoningComplete = false;
            
            const thinkStart = fullText.indexOf('<think>');
            const thinkEnd = fullText.indexOf('</think>');
            
            if (thinkStart !== -1) {
              if (thinkEnd !== -1) {
                reasoning = fullText.substring(thinkStart + 7, thinkEnd).trim();
                parsedText = fullText.substring(thinkEnd + 8).trim();
                isReasoningComplete = true;
              } else {
                reasoning = fullText.substring(thinkStart + 7).trim();
              }
            } else {
              parsedText = fullText.trim();
              isReasoningComplete = true;
            }

            if (!addedMessage) {
              if (parsedText || reasoning) {
                setMessages(prev => [{ id: responseId, text: parsedText, reasoning, isReasoningComplete, sender: 'mave' as const }, ...prev].slice(0, 15));
                addedMessage = true;
              }
            } else {
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: parsedText, reasoning, isReasoningComplete } : m));
            }
          }
        };

        if (!first.done) {
          await processChunk(first.value);
          for await (const chunk of iterator) {
            await processChunk(chunk);
          }
        }
      } catch (e: any) {
        console.warn('Mave orchestration on 3.6-flash failed, falling back to 3.5-flash-lite', e);
        usedModel = 'gemini-3.5-flash-lite';
        responseStream = await createStream('gemini-3.5-flash-lite');
        
        const processChunk = async (chunk: any) => {
          if (chunk.functionCalls && chunk.functionCalls.length > 0) {
            for (const call of chunk.functionCalls) {
              await handleToolCall(call);
            }
          }
          let chunkText = '';
          try { chunkText = chunk.text || ''; } catch { /* ignore */ }
          if (chunkText) {
            fullText += chunkText;
            
            let reasoning = '';
            let parsedText = '';
            let isReasoningComplete = false;
            
            const thinkStart = fullText.indexOf('<think>');
            const thinkEnd = fullText.indexOf('</think>');
            
            if (thinkStart !== -1) {
              if (thinkEnd !== -1) {
                reasoning = fullText.substring(thinkStart + 7, thinkEnd).trim();
                parsedText = fullText.substring(thinkEnd + 8).trim();
                isReasoningComplete = true;
              } else {
                reasoning = fullText.substring(thinkStart + 7).trim();
              }
            } else {
              parsedText = fullText.trim();
              isReasoningComplete = true;
            }

            if (!addedMessage) {
              if (parsedText || reasoning) {
                setMessages(prev => [{ id: responseId, text: parsedText, reasoning, isReasoningComplete, sender: 'mave' as const }, ...prev].slice(0, 15));
                addedMessage = true;
              }
            } else {
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: parsedText, reasoning, isReasoningComplete } : m));
            }
          }
        };

        for await (const chunk of responseStream) {
          await processChunk(chunk);
        }
      }
    } catch (err: any) {
      console.error('Failed to send text to Mave:', err);
      let errorMsg = 'Failed due to an unexpected error. Please try again.';
      const msg = typeof err === 'string' ? err : (err.message || JSON.stringify(err) || "");
      let cleanMsg = msg;
      
      try {
        if (cleanMsg.startsWith('{') || cleanMsg.startsWith('[')) {
          const parsed = JSON.parse(cleanMsg);
          if (parsed?.error?.message) {
            cleanMsg = typeof parsed.error.message === 'string' ? parsed.error.message : JSON.stringify(parsed.error.message);
            if (cleanMsg.startsWith('{') || cleanMsg.startsWith('[')) {
              const innerParsed = JSON.parse(cleanMsg);
              if (innerParsed?.error?.message) {
                cleanMsg = typeof innerParsed.error.message === 'string' ? innerParsed.error.message : JSON.stringify(innerParsed.error.message);
              }
            }
          }
        }
      } catch (e) {
        // ignore parse errors
      }

      const lowerCleanMsg = cleanMsg.toLowerCase();
      if (lowerCleanMsg.includes("429") || lowerCleanMsg.includes("quota") || lowerCleanMsg.includes("resource_exhausted")) {
        errorMsg = "Quota reached. Please upgrade your plan to continue.";
      } else if (lowerCleanMsg.includes("503") || lowerCleanMsg.includes("overloaded")) {
        errorMsg = "The server is currently overloaded. Please try again later.";
      } else if (lowerCleanMsg.includes("404")) {
        errorMsg = "The requested model is currently unavailable.";
      } else if (cleanMsg.startsWith("{") || cleanMsg.startsWith("[") || lowerCleanMsg.includes('"error"')) {
        errorMsg = "An unexpected error occurred. Please try again.";
      } else if (cleanMsg.length > 0) {
        errorMsg = cleanMsg; // Show the clean extracted message
      }

      setMessages(prev => {
        const existing = prev.find(m => m.id === responseId);
        if (existing) {
          return prev.map(m => m.id === responseId ? { ...m, text: `Error: ${errorMsg}`, isError: true } : m);
        }
        return [{
          id: responseId,
          text: `Error: ${errorMsg}`,
          sender: 'mave',
          isError: true
        }, ...prev];
      });
    } finally {
      setIsGenerating(false);
    }
  };

  const toggleRecording = async () => {
    if (isRecording) {
      recorderRef.current?.stop();
      if (audioStreamRef.current) {
        audioStreamRef.current.getTracks().forEach(track => track.stop());
        audioStreamRef.current = null;
      }
      setIsRecording(false);
    } else {
      if (!sessionRef.current) {
        await connectLiveSession();
      }
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: { sampleRate: 16000, channelCount: 1 } });
        audioStreamRef.current = stream;
        const recorder = new MediaRecorder(stream, { mimeType: 'audio/webm' });
        recorderRef.current = recorder;
        recorder.ondataavailable = async (e) => {
          if (e.data.size > 0 && sessionRef.current) {
            const buffer = await e.data.arrayBuffer();
            const base64 = btoa(String.fromCharCode(...new Uint8Array(buffer)));
            sessionRef.current.sendRealtimeInput({
              audio: { data: base64, mimeType: 'audio/pcm;rate=16000' }
            });
          }
        };
        recorder.start(250);
        setIsRecording(true);
      } catch (err) {
        logger.error('Microphone access denied', err);
        setMessages(prev => [{ id: Date.now().toString(), text: 'Error: Microphone access denied. Please allow mic access and try again.', sender: 'mave' as const }, ...prev].slice(0, 15));
      }
    }
  };

  const sendVisionFrame = async (image: string) => {
    let actualMimeType = 'image/jpeg';
    const match = image.match(/^data:([^;]+);/);
    if (match) actualMimeType = match[1];

    const base64Data = image.includes(',') ? image.split(',')[1] : image;
    
    // Add loading message
    const responseId = Date.now().toString();
    setMessages(prev => [{ id: responseId, text: 'Analyzing media and composing track...', sender: 'mave' as const, isReasoningComplete: true }, ...prev].slice(0, 15));
    setIsGenerating(true);

    try {
      const baseUrl = getBaseUrl();
      const authToken = await getAuthToken();
      const headers: Record<string, string> = { 'Content-Type': 'application/json' };
      if (authToken) headers['Authorization'] = `Bearer ${authToken}`;

      const res = await fetch(`${baseUrl}/api/music/generate-from-media`, {
        method: 'POST',
        headers,
        body: JSON.stringify({ data: base64Data, mimeType: actualMimeType })
      });

      if (!res.ok) throw new Error('Failed to generate music from media');
      const data = await res.json();
      
      setMessages(prev => prev.map(m => m.id === responseId ? { 
        ...m, 
        text: 'Here is the track inspired by your media!',
        trackId: data.id || data.trackId || responseId,
        title: data.title || data.trackName || "Media Inspired Track",
        audioUrl: data.url || data.audioUrl,
        artist: data.artist || data.artistName || "Mave",
        coverUrl: data.coverUrl
      } : m));

    } catch (err) {
      logger.error('Media to Music Failed', err);
      setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: 'Failed to analyze media and generate music.' } : m));
    } finally {
      setIsGenerating(false);
    }
  };

  const sendPlaybackCommand = (commandType: string) => {
    logger.info('Playback command', commandType);
  };

  const warp = (params: { bpm?: number; density?: number }) => {
    if (sessionRef.current) {
      sessionRef.current.sendRealtimeInput({ text: `Tweak the track: BPM ${params.bpm}, Density ${params.density}` });
    }
  };

  // Directly request cover art without going through chat
  const requestCoverArt = async (prompt: string, hq = false) => {
    await handleToolCall({ name: 'generate_cover_art', args: { prompt, hq } });
  };

  // Directly request video generation without going through chat
  const requestVideo = async (prompt: string) => {
    await handleToolCall({ name: 'generate_video', args: { prompt } });
  };

  return {
    messages,
    mode,
    isConnected,
    isRecording,
    thinkingText,
    coverArtUrl,
    videoMotionUrl,
    switchMode,
    sendText,
    sendPlaybackCommand,
    sendVisionFrame,
    toggleRecording,
    warp,
    requestCoverArt,
    requestVideo,
    isGenerating
  };
}
