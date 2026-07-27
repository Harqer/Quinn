import { useState, useEffect, useRef, useCallback } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";
import { GoogleGenAI, Type, Modality } from '@google/genai';

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
}

const functionDeclarations = [
  {
    name: 'generate_full_track',
    description: 'Generate a new, full professional music track or background score (Lyria 3).',
    parameters: {
      type: Type.OBJECT,
      properties: {
        prompt: { type: Type.STRING, description: 'Musical style and description' }
      },
      required: ['prompt']
    }
  },
  {
    name: 'tweak_instrumentation',
    description: 'Modify or tweak the instruments, density, or style of the current playing track (Lyria RealTime).',
    parameters: {
      type: Type.OBJECT,
      properties: {
        prompt: { type: Type.STRING, description: 'What to tweak (e.g. add more bass, make it faster)' }
      },
      required: ['prompt']
    }
  },
  {
    name: 'jam_live',
    description: 'Enter live jamming mode using a MIDI controller or live instrument input (MRT2).',
    parameters: {
      type: Type.OBJECT,
      properties: {
        intent: { type: Type.STRING, description: 'The user intent for jamming' }
      },
      required: ['intent']
    }
  }
];

export function useMave() {
  const [messages, setMessages] = useState<MaveMessage[]>([]);
  const [mode, setMode] = useState<MaveMode>('music');
  const [isConnected, setIsConnected] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [thinkingText, setThinkingText] = useState("");
  const [coverArtUrl, setCoverArtUrl] = useState<string | null>(null);
  const [videoMotionUrl, setVideoMotionUrl] = useState<string | null>(null);
  
  const aiRef = useRef<GoogleGenAI | null>(null);
  const sessionRef = useRef<any>(null);
  const recorderRef = useRef<MediaRecorder | null>(null);
  const audioStreamRef = useRef<MediaStream | null>(null);

  // Audio playback queue for Gemini responses
  const audioQueue = useRef<Blob[]>([]);
  const isPlaying = useRef(false);

  const initAI = useCallback(() => {
    if (!aiRef.current) {
      aiRef.current = new GoogleGenAI({ apiKey: import.meta.env.VITE_GEMINI_API_KEY || 'MISSING_API_KEY' });
    }
    return aiRef.current;
  }, []);

  const handleToolCall = async (toolCall: any) => {
    logger.info("Tool Call Received", toolCall);
    try {
      const baseUrl = import.meta.env.VITE_API_URL || '';
      const auth = getAuth();
      const token = await auth.currentUser?.getIdToken();
      
      const res = await fetch(`${baseUrl}/api/music/execute-tool`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          ...(token ? { 'Authorization': `Bearer ${token}` } : {})
        },
        body: JSON.stringify({
          name: toolCall.name,
          args: toolCall.args
        })
      });
      
      const result = await res.json();
      
      if (sessionRef.current) {
        // Send tool response back synchronously
        sessionRef.current.sendRealtimeInput([
          {
            functionResponse: {
              id: toolCall.id,
              name: toolCall.name,
              response: result
            }
          }
        ]);
      }
      
      // Add a message indicating the tool action
      setMessages(prev => [{ 
        id: Date.now().toString(), 
        text: `Executing ${toolCall.name}...`, 
        sender: 'mave' as const 
      }, ...prev].slice(0, 15));

    } catch (err) {
      logger.error("Failed to execute tool", err);
    }
  };

  const playNextAudio = async () => {
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
      logger.error("Audio playback failed", e);
      isPlaying.current = false;
      playNextAudio();
    }
  };

  const connectLiveSession = useCallback(async () => {
    const ai = initAI();
    try {
      const session = await ai.live.connect({
        model: 'gemini-3.1-flash-live-preview',
        config: {
          responseModalities: [Modality.AUDIO],
          systemInstruction: { parts: [{ text: "You are Mave, the Executive Creative Director and Master Musical Orchestrator. Help the user create and tweak music." }] },
          tools: [{ functionDeclarations: functionDeclarations as any }]
        },
        callbacks: {
          onopen: () => {
            setIsConnected(true);
            logger.info("Live API Connected");
          },
          onmessage: (response: any) => {
            const content = response.serverContent;
            if (content?.modelTurn?.parts) {
              for (const part of content.modelTurn.parts) {
                if (part.inlineData) {
                  // Buffer audio for playback
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
          onerror: (error: any) => logger.error("Live API Error:", error),
          onclose: () => {
            setIsConnected(false);
            logger.info("Live API Closed");
          }
        }
      });
      sessionRef.current = session;
    } catch (err) {
      logger.error("Failed to connect to Live API", err);
    }
  }, [initAI]);

  useEffect(() => {
    // Initial connection wait until mic is toggled to open live session
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

  // Text-only execution uses Gemini 3.1 Pro
  const sendText = async (text: string) => {
    const userMsgId = Date.now().toString();
    setMessages(prev => [{ id: userMsgId, text, sender: 'user' as const }, ...prev].slice(0, 15));
    
    // If Live API is active, send text via Live API
    if (sessionRef.current) {
      sessionRef.current.sendRealtimeInput({ text });
      return;
    }

    // Otherwise, fallback to stateless Text generation with 3.1 Pro
    try {
      const ai = initAI();
      const response = await ai.models.generateContent({
        model: 'gemini-3.1-pro',
        contents: text,
        config: {
          systemInstruction: "You are Mave, the Executive Creative Director and Master Musical Orchestrator.",
          tools: [{ functionDeclarations: functionDeclarations as any }]
        }
      });
      
      if (response.functionCalls && response.functionCalls.length > 0) {
        for (const call of response.functionCalls) {
          await handleToolCall(call);
        }
      }

      if (response.text) {
        setMessages(prev => [{ id: Date.now().toString(), text: response.text || '', sender: 'mave' as const }, ...prev].slice(0, 15));
      }
    } catch (err) {
      logger.error("Text generation failed", err);
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
        
        // Custom AudioProcessor or MediaRecorder to get 16kHz PCM data
        // For simplicity, using MediaRecorder here and sending as base64
        const recorder = new MediaRecorder(stream, { mimeType: 'audio/webm' });
        recorderRef.current = recorder;

        recorder.ondataavailable = async (e) => {
          if (e.data.size > 0 && sessionRef.current) {
            // Note: In a production app, extract raw PCM data from AudioWorklet
            // For now, sending as webm or standard data
            const buffer = await e.data.arrayBuffer();
            const base64 = btoa(String.fromCharCode(...new Uint8Array(buffer)));
            sessionRef.current.sendRealtimeInput({
              audio: { data: base64, mimeType: 'audio/pcm;rate=16000' } // Placeholder for actual PCM conversion
            });
          }
        };

        recorder.start(250);
        setIsRecording(true);
      } catch (err) {
        logger.error('Microphone access denied', err);
      }
    }
  };

  const sendVisionFrame = (image: string) => {
    if (sessionRef.current) {
      const mimeType = image.startsWith('data:image/png') ? 'image/png' : 'image/jpeg';
      const base64Data = image.includes(',') ? image.split(',')[1] : image;
      sessionRef.current.sendRealtimeInput({
        video: { data: base64Data, mimeType }
      });
    }
  };

  const sendPlaybackCommand = (commandType: string) => {
    logger.info("Playback command sent to client engine", commandType);
  };

  const warp = (params: { bpm?: number; density?: number }) => {
    if (sessionRef.current) {
      sessionRef.current.sendRealtimeInput({ text: `Please warp the track to BPM: ${params.bpm}, Density: ${params.density}` });
    }
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
    warp
  };
}
