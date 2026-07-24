import { useState, useEffect, useRef, useCallback } from 'react';
import { getAuth } from 'firebase/auth';

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
}

export function useMave() {
  const [messages, setMessages] = useState<MaveMessage[]>([]);
  const [mode, setMode] = useState<MaveMode>('music');
  const [isConnected, setIsConnected] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [thinkingText, setThinkingText] = useState("");
  const wsRef = useRef<WebSocket | null>(null);
  const recorderRef = useRef<MediaRecorder | null>(null);
  const reconnectAttempts = useRef(0);

  const connect = useCallback(async () => {
    try {
      const auth = getAuth();
      const user = auth.currentUser;
      if (!user) return;

      const token = await user.getIdToken();
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const wsUrl = new URL('/api/music/ws', window.location.origin);
      wsUrl.protocol = protocol;
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
            const rawText = msg.prompts ? `New style: ${msg.prompts[0]}` : (msg.script || msg.vision);
            const text = rawText && !/firestore|redis|database|deployed|caching|generate|vibe/i.test(rawText) ? rawText : null;
            if (text) {
              setMessages(prev => [{ id: Date.now().toString(), text, sender: 'mave' as const, trackId: msg.trackId }, ...prev].slice(0, 15));
            }
          } else if (msg.type === 'message') {
            const rawText = msg.data;
            const text = rawText && !/firestore|redis|database|deployed|caching|generate|vibe/i.test(rawText) ? rawText : null;
            if (text) {
              setMessages(prev => [{ id: Date.now().toString(), text, sender: 'mave' as const }, ...prev].slice(0, 15));
            }
          }
        } catch (err) {
          console.error('Error parsing Mave event', err);
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
      console.error('Failed to connect to Mave Studio', err);
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
        const response = await fetch('/api/music/podcast/generate', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ prompt: text })
        });
        if (response.ok) {
          const reader = response.body?.getReader();
          const decoder = new TextDecoder();
          let done = false;
          let scriptBuffer = '';

          while (!done && reader) {
            const { value, done: doneReading } = await reader.read();
            done = doneReading;
            if (value) {
              const chunkStr = decoder.decode(value, { stream: true });
              const lines = chunkStr.split('\n\n');
              for (const line of lines) {
                if (line.startsWith('data: ')) {
                  try {
                    const data = JSON.parse(line.replace('data: ', ''));
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
                        coverUrl: data.track.coverUrl
                      } as any, ...prev].slice(0, 15));
                    }
                  } catch (e) {
                    console.error("Failed to parse SSE data", e);
                  }
                }
              }
            }
          }
          return;
        }
      } catch (err) {
        console.warn('Podcast API endpoint error, falling back to WebSocket stream', err);
      }
    }

    const type = mode === 'podcast' ? 'text_command' : 'feedback';
    wsRef.current?.send(JSON.stringify({ type, text }));
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
        console.error('Microphone access denied', err);
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
    switchMode,
    sendText,
    sendVisionFrame,
    toggleRecording,
    warp
  };
};
