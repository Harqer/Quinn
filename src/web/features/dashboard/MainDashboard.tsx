import React, { useState, useRef, useEffect } from 'react';
import { logger } from '@/web/lib/logger';
import { MusicVisualizer } from './MusicVisualizer';
import { GesturePad } from './GesturePad';
import { getAuth } from "firebase/auth";

export const MainDashboard: React.FC = () => {
  const [videoActive, setVideoActive] = useState(true);
  const [playbackState, setPlaybackState] = useState<'playing' | 'stopped'>('stopped');
  const [voiceStatus, setVoiceStatus] = useState<'idle' | 'listening' | 'processing' | 'speaking'>('idle');
  const [messages, setMessages] = useState<any[]>([]);
  const [mode, setMode] = useState<'music' | 'podcast'>('music');
  const [inputText, setInputText] = useState("");
  const wsRef = useRef<WebSocket | null>(null);
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const connectWs = async () => {
      try {
        const user = getAuth().currentUser;
        if (!user) return;
        const token = await user.getIdToken();
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const ws = new WebSocket(`${protocol}//${window.location.host}/api/music/ws?token=${token}`);

        ws.onmessage = (event) => {
          try {
            const msg = JSON.parse(event.data);
            if (msg.type === 'message') {
              setMessages(prev => [{ text: msg.data, type: 'quinn' }, ...prev].slice(0, 10));
            } else if (msg.type === 'agent_update') {
              const text = msg.prompts ? `New vibe: ${msg.prompts[0]}` : msg.vision;
              setMessages(prev => [{ text, type: 'quinn', trackId: msg.trackId }, ...prev].slice(0, 10));
            } else if (msg.type === 'podcast_update') {
              setMessages(prev => [{ text: msg.script, type: 'quinn', trackId: msg.trackId }, ...prev].slice(0, 10));
            }
          } catch (e) {
            setMessages(prev => [{ text: event.data, type: 'quinn' }, ...prev].slice(0, 10));
          }
        };
        wsRef.current = ws;
      } catch (err) {
        logger.error("WS Connection failed", err);
      }
    };

    connectWs();
    return () => wsRef.current?.close();
  }, []);

  useEffect(() => {
    let interval: any;
    if (videoActive && videoRef.current) {
      navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
          if (videoRef.current) videoRef.current.srcObject = stream;

          interval = setInterval(() => {
            if (wsRef.current?.readyState === WebSocket.OPEN && videoRef.current && canvasRef.current) {
              const canvas = canvasRef.current;
              const ctx = canvas.getContext('2d');
              if (ctx) {
                canvas.width = 320;
                canvas.height = 240;
                ctx.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
                const base64Frame = canvas.toDataURL('image/jpeg', 0.5).split(',')[1];
                wsRef.current.send(JSON.stringify({ type: 'vision', image: base64Frame }));
              }
            }
          }, 1000);
        })
        .catch(err => logger.error("Camera access failed", err));
    }
    return () => clearInterval(interval);
  }, [videoActive]);

  const toggleListening = () => {
    if (voiceStatus === 'idle') {
      setVoiceStatus('listening');
      wsRef.current?.send(JSON.stringify({ type: 'start_voice' }));
    } else {
      setVoiceStatus('idle');
      wsRef.current?.send(JSON.stringify({ type: 'stop_voice' }));
    }
  };

  const handleSend = () => {
    if (!inputText.trim()) return;
    setMessages(prev => [{ text: inputText, type: 'user' }, ...prev].slice(0, 10));
    const type = mode === 'podcast' ? 'text_command' : 'feedback';
    wsRef.current?.send(JSON.stringify({ type, text: inputText }));
    setInputText("");
  };

  const switchMode = (newMode: 'music' | 'podcast') => {
    setMode(newMode);
    wsRef.current?.send(JSON.stringify({ type: 'switch_mode', mode: newMode }));
  };

  return (
    <div className="flex flex-col h-full bg-surface text-on-surface relative">
      <canvas ref={canvasRef} className="hidden" />

      {/* Main Workspace */}
      <div className="flex-1 flex overflow-hidden">
        <div className="flex-1 relative overflow-hidden bg-surface-container-lowest">
          {videoActive ? (
            <video ref={videoRef} autoPlay playsInline muted className="w-full h-full object-cover grayscale opacity-40" />
          ) : (
            <div className="w-full h-full bg-gradient-to-tr from-primary/10 via-surface to-secondary/10" />
          )}

          <div className="absolute inset-0 pointer-events-none">
            <MusicVisualizer isPlaying={playbackState === 'playing'} />
          </div>

          {/* Mode Switch Overlay */}
          <div className="absolute top-4 left-4 flex bg-black/40 backdrop-blur-md p-1 rounded-full border border-outline/10">
            <button
              onClick={() => switchMode('music')}
              className={`px-4 py-1.5 rounded-full text-xs font-bold transition-all ${mode === 'music' ? 'bg-primary text-on-primary shadow-lg' : 'text-white hover:bg-white/10'}`}
            >
              Music
            </button>
            <button
              onClick={() => switchMode('podcast')}
              className={`px-4 py-1.5 rounded-full text-xs font-bold transition-all ${mode === 'podcast' ? 'bg-primary text-on-primary shadow-lg' : 'text-white hover:bg-white/10'}`}
            >
              Podcast
            </button>
          </div>

          <div className="absolute top-4 right-4 flex gap-2">
            <button
              onClick={() => setVideoActive(true)}
              className={`px-4 py-1.5 rounded-full text-xs font-bold transition-all ${videoActive ? 'bg-surface text-on-surface' : 'bg-white/10 text-white hover:bg-white/20'}`}
            >
              Webcam
            </button>
            <button
              onClick={() => setVideoActive(false)}
              className={`px-4 py-1.5 rounded-full text-xs font-bold transition-all ${!videoActive ? 'bg-surface text-on-surface' : 'bg-white/10 text-white hover:bg-white/20'}`}
            >
              Cosmic Feed
            </button>
          </div>
        </div>

        {/* Side History Panel */}
        <div className="w-96 flex flex-col gap-4 p-4 border-l border-outline/10 bg-surface-container-low/30 backdrop-blur-xl">
          <div className="flex-1 overflow-y-auto space-y-4 custom-scrollbar pr-2">
            {messages.length === 0 && (
              <div className="text-center py-20 text-on-surface-variant text-xs italic">
                Quinn is analyzing your world...
              </div>
            )}
            {messages.map((m, i) => (
              <div key={i} className={`flex ${m.type === 'user' ? 'justify-end' : 'justify-start'}`}>
                <div className={`max-w-[85%] p-3 rounded-2xl text-xs font-medium ${
                  m.type === 'user' ? 'bg-primary text-on-primary' : 'bg-surface-container border border-outline/5'
                }`}>
                  {m.text}
                </div>
              </div>
            ))}
          </div>

          <div className="h-48 relative rounded-xl bg-surface-container-lowest overflow-hidden border border-outline/5 group">
            <GesturePad />
            <div className="absolute inset-0 pointer-events-none border border-primary/0 group-hover:border-primary/20 transition-all" />
          </div>
        </div>
      </div>

      {/* Global Bottom Chat Bar */}
      <div className="p-6 bg-surface-container-low border-t border-outline/10">
        <div className="max-w-4xl mx-auto flex gap-4 items-center bg-surface-container-high p-2 rounded-full shadow-lg border border-outline/5">
          <button
            onClick={toggleListening}
            className={`w-12 h-12 rounded-full flex items-center justify-center transition-all ${
              voiceStatus === 'listening' ? 'bg-error text-on-error scale-110 shadow-lg' : 'text-on-surface-variant hover:bg-surface-container-highest'
            }`}
          >
            <span className="material-icons-round">mic</span>
          </button>
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder={`Tell Quinn to adjust the ${mode === 'music' ? 'musical vibe' : 'story'}...`}
            className="flex-1 bg-transparent border-none outline-none text-sm font-medium px-2"
          />
          <button
            onClick={handleSend}
            disabled={!inputText.trim()}
            className="w-12 h-12 bg-primary text-on-primary rounded-full flex items-center justify-center disabled:opacity-50 transition-all hover:brightness-110"
          >
            <span className="material-icons-round">send</span>
          </button>
        </div>
      </div>
    </div>
  );
};
