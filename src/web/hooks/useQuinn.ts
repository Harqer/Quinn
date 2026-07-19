import { useState, useEffect, useRef, useCallback } from 'react';
import { getAuth } from 'firebase/auth';

export type QuinnMode = 'music' | 'podcast';

export interface QuinnMessage {
  id: string;
  sender: 'user' | 'quinn';
  text: string;
  isAudio?: boolean;
}

export function useQuinn() {
  const [messages, setMessages] = useState<QuinnMessage[]>([]);
  const [mode, setMode] = useState<QuinnMode>('music');
  const [isConnected, setIsConnected] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const wsRef = useRef<WebSocket | null>(null);

  const connect = useCallback(async () => {
    try {
      const auth = getAuth();
      const user = auth.currentUser;
      if (!user) return;

      const token = await user.getIdToken();
      // Connect to WebSocket proxy
      const wsUrl = new URL('/api/music/proxy', window.location.origin);
      wsUrl.protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      wsUrl.searchParams.set('token', token);

      const ws = new WebSocket(wsUrl.toString());

      ws.onopen = () => {
        setIsConnected(true);
        ws.send(JSON.stringify({ type: 'switch_mode', mode }));
      };

      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'message' && data.data?.text) {
            setMessages((prev) => [
              ...prev,
              { id: Date.now().toString(), sender: 'quinn', text: data.data.text }
            ]);
          }
        } catch (err) {
          console.error('Error parsing WS message', err);
        }
      };

      ws.onclose = () => {
        setIsConnected(false);
      };

      wsRef.current = ws;
    } catch (err) {
      console.error('Failed to connect to Quinn', err);
    }
  }, [mode]);

  useEffect(() => {
    connect();
    return () => {
      if (wsRef.current) {
        wsRef.current.close();
      }
    };
  }, [connect]);

  const switchMode = (newMode: QuinnMode) => {
    setMode(newMode);
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({ type: 'switch_mode', mode: newMode }));
    }
  };

  const sendText = (text: string) => {
    setMessages((prev) => [
      ...prev,
      { id: Date.now().toString(), sender: 'user', text }
    ]);
    
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
      if (mode === 'music') {
        wsRef.current.send(JSON.stringify({ type: 'feedback', text }));
      } else {
        wsRef.current.send(JSON.stringify({ type: 'text_command', text }));
      }
    }
  };

  const sendVision = (image: string) => {
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({ type: 'vision', image }));
    }
  };

  const setWeightedPrompts = (prompts: any) => {
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN && mode === 'music') {
      wsRef.current.send(JSON.stringify({ type: 'setWeightedPrompts', prompts }));
    }
  };

  const play = () => {
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN && mode === 'music') {
      wsRef.current.send(JSON.stringify({ type: 'play' }));
    }
  };

  const pause = () => {
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN && mode === 'music') {
      wsRef.current.send(JSON.stringify({ type: 'pause' }));
    }
  };

  const stop = () => {
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN && mode === 'music') {
      wsRef.current.send(JSON.stringify({ type: 'stop' }));
    }
  };

  // Microphone implementation could go here, replacing or supplementing text
  const toggleRecording = () => {
    setIsRecording(!isRecording);
    // Real implementation would stream audio over WS or WebRTC
  };

  return {
    messages,
    mode,
    isConnected,
    isRecording,
    switchMode,
    sendText,
    sendVision,
    setWeightedPrompts,
    play,
    pause,
    stop,
    toggleRecording
  };
}
