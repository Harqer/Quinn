import { useState, useEffect, useRef, useCallback } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";

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

export function useMave() {
  const [messages, setMessages] = useState<MaveMessage[]>([]);
  const [mode, setMode] = useState<MaveMode>('music');
  const [isConnected, setIsConnected] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [thinkingText, setThinkingText] = useState("");
  const [coverArtUrl, setCoverArtUrl] = useState<string | null>(null);
  const [videoMotionUrl, setVideoMotionUrl] = useState<string | null>(null);
  const wsRef = useRef<WebSocket | null>(null);
  const recorderRef = useRef<MediaRecorder | null>(null);
  const reconnectAttempts = useRef(0);

  const connect = useCallback(async () => {
    try {
      const auth = getAuth();
      const user = auth.currentUser;
      if (!user) return;

      const token = await user.getIdToken();
      
      const baseUrl = import.meta.env.VITE_API_URL || '';
      
      // If baseUrl is provided (e.g. https://api.example.com), use it to determine the WS URL
      const isSecure = baseUrl ? baseUrl.startsWith('https') : window.location.protocol === 'https:';
      const protocol = isSecure ? 'wss:' : 'ws:';
      const hostOrigin = baseUrl ? baseUrl.replace(/^https?:\/\//, '') : window.location.host;
      
      const wsUrl = new URL(`/api/music/ws`, `${protocol}//${hostOrigin}`);
      wsUrl.searchParams.set('token', token);

      const ws = new WebSocket(wsUrl.toString());

      ws.onopen = () => {
        setIsConnected(true);
        reconnectAttempts.current = 0;
        ws.send(JSON.stringify({ type: 'switch_mode', mode }));
      };

      ws.onmessage = (event) => {
        try {
          const msg = JSON.parse(event.data);

          if (msg.type === 'mave_thinking' || msg.type === 'mave_chunk') {
            setThinkingText(prev => prev + msg.chunk);
          } else if (msg.type === 'agent_update') {
            setThinkingText("");
            const rawText = msg.prompts ? msg.prompts[0] : (msg.script || msg.vision);
            const text = rawText && !/firestore|redis|database|deployed|caching|vibe/i.test(rawText) ? rawText : null;
            if (text || msg.chunk) {
              setMessages(prev => [{ id: Date.now().toString(), text: text || "Generated Audio", sender: 'mave' as const, trackId: msg.trackId, type: 'music_card', audioUrl: msg.chunk }, ...prev].slice(0, 15));
            }
          } else if (msg.type === 'cover_art_update') {
            setCoverArtUrl(msg.coverArtUrl);
          } else if (msg.type === 'video_motion_update') {
            setVideoMotionUrl(msg.videoMotionUrl);
          } else if (msg.type === 'message') {
            const rawText = msg.data;
            const text = rawText && !/firestore|redis|database|deployed|caching|generate|vibe/i.test(rawText) ? rawText : null;
            if (text) {
              setMessages(prev => [{ id: Date.now().toString(), text, sender: 'mave' as const }, ...prev].slice(0, 15));
            }
          }
        } catch (err) {
          logger.error('Error parsing Mave event', err);
        }
      };

      ws.onclose = () => {
        setIsConnected(false);
        const backoff = Math.min(1000 * Math.pow(2, reconnectAttempts.current), 30000);
        setTimeout(() => {
          reconnectAttempts.current += 1;
          connect();
        }, backoff);
      };
      wsRef.current = ws;
    } catch (err) {
      logger.error('Failed to connect to Mave Studio', err);
    }
  }, [mode]);

  const audioStreamRef = useRef<MediaStream | null>(null);

  useEffect(() => {
    connect();
    return () => {
      wsRef.current?.close();
      if (audioStreamRef.current) {
        audioStreamRef.current.getTracks().forEach(track => track.stop());
      }
    };
  }, [connect]);

  const switchMode = (newMode: MaveMode) => {
    if ('vibrate' in navigator) navigator.vibrate(10);
    setMode(newMode);
    wsRef.current?.send(JSON.stringify({ type: 'switch_mode', mode: newMode }));
  };

  const sendText = async (text: string) => {
    const userMsgId = Date.now().toString();
    setMessages(prev => [{ id: userMsgId, text, sender: 'user' as const }, ...prev].slice(0, 15));
    setThinkingText("");

    if (mode === 'podcast') {
      try {
        const baseUrl = import.meta.env.VITE_API_URL || '';
        const response = await fetch(`${baseUrl}/api/music/podcast/generate`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ prompt: text })
        });
        if (response.ok) {
          const reader = response.body?.getReader();
          const decoder = new TextDecoder();
          let done = false;
          let scriptBuffer = '';
          let streamBuffer = '';

          while (!done && reader) {
            const { value, done: doneReading } = await reader.read();
            done = doneReading;
            if (value) {
              const chunkStr = decoder.decode(value, { stream: true });
              streamBuffer += chunkStr;
              
              let eolIndex;
              while ((eolIndex = streamBuffer.indexOf('\n\n')) >= 0) {
                const line = streamBuffer.slice(0, eolIndex).trim();
                streamBuffer = streamBuffer.slice(eolIndex + 2);
                
                if (line.startsWith('data: ')) {
                  try {
                    const data = JSON.parse(line.substring(6));
                    if (data.type === 'chunk' || data.type === 'thought') {
                      scriptBuffer += data.text;
                      setThinkingText(scriptBuffer);
                    } else if (data.type === 'complete') {
                      setThinkingText("");
                      setMessages(prev => [{
                        id: data.track.id || Date.now().toString(),
                        sender: 'mave' as const,
                        text: data.track.script,
                        type: 'podcast_card',
                        title: data.track.title,
                        script: data.track.script,
                        voice: data.track.voice,
                        coverUrl: data.track.coverUrl,
                        audioUrl: data.track.audioUrl
                      } as any, ...prev].slice(0, 15));
                    }
                  } catch (e) {
                    logger.error("Failed to parse SSE data", e);
                  }
                }
              }
            }
          }
          return;
        }
      } catch (err) {
        logger.warn('Podcast API endpoint error, falling back to WebSocket stream', err);
      }
    }

    const type = mode === 'podcast' ? 'text_command' : 'feedback';
    wsRef.current?.send(JSON.stringify({ type, text }));
  };

  const sendPlaybackCommand = (commandType: string) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({ type: commandType }));
    }
  };

  const sendVisionFrame = (image: string) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({ type: 'vision', image }));
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
      wsRef.current?.send(JSON.stringify({ type: 'stop_voice' }));
    } else {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        audioStreamRef.current = stream;
        const recorder = new MediaRecorder(stream);
        recorderRef.current = recorder;

        recorder.ondataavailable = (e) => {
          if (e.data.size > 0 && wsRef.current?.readyState === WebSocket.OPEN) {
            const reader = new FileReader();
            reader.readAsDataURL(e.data);
            reader.onloadend = () => {
              const base64 = (reader.result as string).split(',')[1];
              wsRef.current?.send(JSON.stringify({ type: 'audio', data: base64 }));
            };
          }
        };

        recorder.start(250);
        setIsRecording(true);
        wsRef.current?.send(JSON.stringify({ type: 'start_voice' }));
      } catch (err) {
        logger.error('Microphone access denied', err);
      }
    }
  };

  const warp = (params: { bpm?: number; density?: number }) => {
    wsRef.current?.send(JSON.stringify({ type: 'steering_action', params }));
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
};
