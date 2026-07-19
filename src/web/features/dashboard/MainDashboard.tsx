import React, { useState, useRef, useEffect } from 'react';
import { MusicVisualizer } from './MusicVisualizer';
import { GesturePad } from './GesturePad';
import { getAuth } from "firebase/auth";

export const MainDashboard: React.FC = () => {
  const [videoActive, setVideoActive] = useState(true);
  const [playbackState, setPlaybackState] = useState<'playing' | 'stopped'>('stopped');
  const [voiceStatus, setVoiceStatus] = useState<'idle' | 'listening' | 'processing' | 'speaking'>('idle');
  const [messages, setMessages] = useState<string[]>([]);
  const [instrument, setInstrument] = useState<'piano' | 'clarinet' | 'violin' | 'chimes'>('piano');
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
          const msg = JSON.parse(event.data);
          if (msg.type === 'message') {
            setMessages(prev => [msg.data, ...prev].slice(0, 5));
          } else if (msg.type === 'agent_update') {
            setMessages(prev => [`Composer: ${msg.prompts[0]}`, `Lyricist: ${msg.lyrics?.substring(0, 30)}...`, ...prev].slice(0, 10));
          }
        };
        wsRef.current = ws;
      } catch (err) {
        console.error("WS Connection failed", err);
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
        .catch(err => console.error("Camera access failed", err));
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
    setMessages(prev => [`You: ${inputText}`, ...prev].slice(0, 10));
    wsRef.current?.send(JSON.stringify({ type: 'feedback', text: inputText }));
    setInputText("");
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

          <div className="absolute top-4 left-4 flex gap-2">
            <button
              onClick={() => setVideoActive(true)}
              className={`px-4 py-2 rounded-full text-xs font-bold transition-all ${videoActive ? 'bg-primary text-on-primary' : 'bg-surface-container-high text-on-surface hover:bg-surface-container-highest'}`}
            >
              Webcam
            </button>
            <button
              onClick={() => setVideoActive(false)}
              className={`px-4 py-2 rounded-full text-xs font-bold transition-all ${!videoActive ? 'bg-primary text-on-primary' : 'bg-surface-container-high text-on-surface hover:bg-surface-container-highest'}`}
            >
              Cosmic Feed
            </button>
          </div>
        </div>

        {/* Side Controls */}
        <div className="w-96 flex flex-col gap-4 p-4 border-l border-outline/10 bg-surface-container-low/30 backdrop-blur-xl">
          <div className="p-4 rounded-2xl bg-surface-container border border-outline/5 space-y-4 shadow-sm">
            <div className="flex items-center justify-between">
              <h3 className="flex items-center gap-2 font-bold text-on-surface text-sm">
                <span className="material-icons-round text-primary text-lg">psychology</span>
                Quinn Live
              </h3>
              <span className={`text-[10px] uppercase tracking-widest font-black px-2 py-1 rounded bg-surface-container-high ${
                voiceStatus === 'listening' ? 'text-error animate-pulse' : 'text-primary'
              }`}>
                {voiceStatus}
              </span>
            </div>

            <div className="flex-1 overflow-hidden h-24">
              {messages.length > 0 ? (
                <p className="text-xs text-on-surface-variant font-medium animate-in fade-in slide-in-from-bottom-1">{messages[0]}</p>
              ) : (
                <div className="text-on-surface-variant text-xs italic">"Quinn, make it more chill..."</div>
              )}
            </div>
          </div>

          <div className="flex-1 flex flex-col p-4 rounded-2xl bg-surface-container border border-outline/5 space-y-4 shadow-sm">
            <div className="flex items-center justify-between">
              <h3 className="flex items-center gap-2 font-bold text-on-surface text-sm">
                <span className="material-icons-round text-primary text-lg">music_note</span>
                Chord Orchestrator
              </h3>
            </div>

            <div className="grid grid-cols-4 gap-2">
              <InstrumentTab active={instrument === 'piano'} label="Piano" icon="piano" onClick={() => setInstrument('piano')} />
              <InstrumentTab active={instrument === 'clarinet'} label="Reeds" icon="waves" onClick={() => setInstrument('clarinet')} />
              <InstrumentTab active={instrument === 'violin'} label="Strings" icon="blur_linear" onClick={() => setInstrument('violin')} />
              <InstrumentTab active={instrument === 'chimes'} label="Bells" icon="notifications_active" onClick={() => setInstrument('chimes')} />
            </div>

            <div className="flex-1 relative rounded-xl bg-surface-container-lowest overflow-hidden border border-outline/5 group">
              <GesturePad />
              <div className="absolute inset-0 pointer-events-none border border-primary/0 group-hover:border-primary/20 transition-all" />
            </div>
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
            placeholder="Tell Quinn to change the musical vibe..."
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

const InstrumentTab: React.FC<{ active: boolean; label: string; icon: string; onClick: () => void }> = ({ active, label, icon, onClick }) => (
  <button
    onClick={onClick}
    className={`flex flex-col items-center gap-1 p-2 rounded-xl transition-all ${
      active ? 'bg-primary text-on-primary shadow-md' : 'bg-surface-container-high text-on-surface-variant hover:text-on-surface hover:bg-surface-container-highest'
    }`}
  >
    <span className="material-icons-round text-lg">{icon}</span>
    <span className="text-[10px] font-bold uppercase">{label}</span>
  </button>
);
