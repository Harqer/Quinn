import { useState, useEffect, useRef, useCallback } from 'react';
import { getAuth } from 'firebase/auth';

export type MaveMode = 'music' | 'podcast';

export interface MaveMessage {
  id: string;
  sender: 'user' | 'mave';
  text: string;
  isAudio?: boolean;
  trackId?: string;
}

export function useMave() {
  const [messages, setMessages] = useState<MaveMessage[]>([]);
  const [mode, setMode] = useState<MaveMode>('music');
  const [isConnected, setIsConnected] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [thinkingText, setThinkingText] = useState("");
  const wsRef = useRef<WebSocket | null>(null);
  const recorderRef = useRef<MediaRecorder | null>(null);

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
        ws.send(JSON.stringify({ type: 'switch_mode', mode }));
      };

      ws.onmessage = (event) => {
        try {
          const msg = JSON.parse(event.data);

          if (msg.type === 'mave_thinking' || msg.type === 'mave_chunk') {
            setThinkingText(prev => prev + msg.chunk);
          } else if (msg.type === 'agent_update') {
            setThinkingText("");
            const text = msg.prompts ? `New vibe: ${msg.prompts[0]}` : (msg.script || msg.vision);
            if (text) {
              setMessages(prev => [{ id: Date.now().toString(), text, sender: 'mave', trackId: msg.trackId }, ...prev].slice(0, 15));
            }
          } else if (msg.type === 'message') {
            setMessages(prev => [{ id: Date.now().toString(), text: msg.data, sender: 'mave' }, ...prev].slice(0, 15));
          }
        } catch (err) {
          console.error('Error parsing Mave event', err);
        }
      };

      ws.onclose = () => setIsConnected(false);
      wsRef.current = ws;
    } catch (err) {
      console.error('Failed to connect to Mave Studio', err);
    }
  }, [mode]);

  useEffect(() => {
    connect();
    return () => wsRef.current?.close();
  }, [connect]);

  const switchMode = (newMode: MaveMode) => {
    if ('vibrate' in navigator) navigator.vibrate(10);
    setMode(newMode);
    wsRef.current?.send(JSON.stringify({ type: 'switch_mode', mode: newMode }));
  };

  const sendText = (text: string) => {
    setMessages(prev => [{ id: Date.now().toString(), text, sender: 'user' }, ...prev].slice(0, 15));
    setThinkingText("");
    const type = mode === 'podcast' ? 'text_command' : 'feedback';
    wsRef.current?.send(JSON.stringify({ type, text }));
  };

  const toggleRecording = async () => {
    if (isRecording) {
      recorderRef.current?.stop();
      setIsRecording(false);
      wsRef.current?.send(JSON.stringify({ type: 'stop_voice' }));
    } else {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
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
    toggleRecording,
    warp
  };
}
