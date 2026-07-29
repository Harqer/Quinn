import React, { useState, useRef, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { getAuth } from 'firebase/auth';
import { logger } from '@/web/lib/logger';
import { copyToClipboard } from '../../utils/clipboard';
import { MusicVisualizer } from './MusicVisualizer';
import { GesturePad } from './GesturePad';
import { useMave } from '../../hooks/useMave';
import maveLogoDark from '../../assets/mave_brand_dark.png';
import maveLogoLight from '../../assets/mave_brand_light.png';
import { EmptyState } from '../../components/molecules/EmptyState';
import { useAppContext } from '../../contexts/AppContext';

/**
 * Mave Brand Logo Component.
 * Uses designated Light / Dark mode logos.
 */
const MaveLogo: React.FC<{ variant?: 'light' | 'dark', size?: number }> = ({ variant = 'dark', size = 120 }) => {
  const logoSrc = variant === 'light' ? maveLogoLight : maveLogoDark;
  return (
    <img
      src={logoSrc}
      alt="Mave Logo"
      style={{ width: size, height: 'auto', display: 'block' }}
    />
  );
};

function CopyButton({ text, showToast }: { text: string; showToast: (msg: string) => void }) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    const success = await copyToClipboard(text);
    if (success) {
      setCopied(true);
      showToast('Copied to clipboard');
      setTimeout(() => setCopied(false), 2000);
    } else {
      showToast('Failed to copy');
    }
  };

  return (
    <button 
      onClick={handleCopy}
      className="p-2 text-gray-500 hover:text-white transition-colors mt-2 flex-shrink-0"
      title="Copy text"
    >
      <span className="material-icons-round text-sm">{copied ? "check" : "content_copy"}</span>
    </button>
  );
}

export const MainDashboard: React.FC = () => {
  const [videoActive, setVideoActive] = useState(true);
  const [playbackState, setPlaybackState] = useState<'playing' | 'stopped'>('stopped');
  const [voiceStatus, setVoiceStatus] = useState<'idle' | 'listening' | 'processing' | 'speaking'>('idle');
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [inputText, setInputText] = useState("");
  const { t } = useTranslation();

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  const {
    messages,
    mode,
    isConnected,
    isRecording,
    thinkingText,
    switchMode,
    sendText,
    sendPlaybackCommand,
    sendVisionFrame,
    toggleRecording,
    warp
  } = useMave();

  const { setPlaybackCommandSender } = useAppContext();

  useEffect(() => {
    setPlaybackCommandSender(() => sendPlaybackCommand);
  }, [sendPlaybackCommand, setPlaybackCommandSender]);

  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    let interval: any;
    let activeStream: MediaStream | null = null;

    if (videoActive && videoRef.current) {
      navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
          activeStream = stream;
          if (videoRef.current) videoRef.current.srcObject = stream;

          interval = setInterval(() => {
            if (isConnected && videoRef.current && canvasRef.current) {
              const canvas = canvasRef.current;
              const ctx = canvas.getContext('2d');
              if (ctx) {
                canvas.width = 320;
                canvas.height = 240;
                ctx.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
                const base64Frame = canvas.toDataURL('image/jpeg', 0.5).split(',')[1];
                sendVisionFrame(base64Frame);
              }
            }
          }, 1000);
        })
        .catch(err => logger.error("Camera access failed", err));
    }

    return () => {
      if (interval) clearInterval(interval);
      if (activeStream) {
        activeStream.getTracks().forEach(track => track.stop());
      }
    };
  }, [videoActive, isConnected]);

  const handleSend = () => {
    if (!inputText.trim()) return;
    if ('vibrate' in navigator) navigator.vibrate([15, 30]);
    sendText(inputText);
    setInputText("");
  };

  const handleCameraSnapshot = () => {
    if (canvasRef.current) {
      const base64Frame = canvasRef.current.toDataURL('image/jpeg', 0.8).split(',')[1];
      sendVisionFrame(base64Frame);
      showToast('Snapshot sent to Mave!');
      if ('vibrate' in navigator) navigator.vibrate(20);
    }
  };

  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64 = (reader.result as string).split(',')[1];
        sendVisionFrame(base64);
        showToast('Image uploaded and sent to Mave!');
      };
      reader.readAsDataURL(file);
    } else if (file.type.startsWith('video/')) {
      const video = document.createElement('video');
      video.src = URL.createObjectURL(file);
      video.onloadeddata = () => {
        const canvas = document.createElement('canvas');
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext('2d');
        if (ctx) {
          ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
          const base64 = canvas.toDataURL('image/jpeg', 0.8).split(',')[1];
          sendVisionFrame(base64);
          showToast('Video processed and sent to Mave!');
        }
        URL.revokeObjectURL(video.src);
      };
    } else {
      showToast('Unsupported file type');
    }
  };

  const handleAction = async (action: 'like' | 'bookmark', trackId?: string) => {
    if (!trackId) return;
    if ('vibrate' in navigator) navigator.vibrate([50, 50, 50]);
    try {
      const auth = getAuth();
      const user = auth.currentUser;
      const token = user ? await user.getIdToken() : '';
      
      let endpoint = '/api/music/bookmark';
      if (action === 'like') {
        if (mode === 'podcast') {
          endpoint = '/api/spotify/podcast/save';
        } else if (mode === 'audiobook') {
          endpoint = '/api/spotify/audiobook/save';
        } else {
          endpoint = '/api/spotify/music/save';
        }
      }

      const baseUrl = import.meta.env.VITE_API_URL || '';
      const response = await fetch(`${baseUrl}${endpoint}`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          ...(token ? { 'Authorization': `Bearer ${token}` } : {})
        },
        body: JSON.stringify({ trackId })
      });

      if (!response.ok) throw new Error(`API Error: ${response.status}`);
      showToast('Saved to your library!');
    } catch (err) {
      logger.error(`Failed to ${action} track`, err);
      showToast('Unable to complete action. Please try again later.');
    }
  };

  return (
    <div className="flex flex-col h-full bg-[#121212] text-white relative">
      <canvas ref={canvasRef} className="hidden" />

      {/* Global Toast */}
      {toastMessage && (
        <div className="absolute top-4 left-1/2 -translate-x-1/2 z-50 bg-red-500 text-white px-6 py-2 rounded-full shadow-lg font-bold text-sm transition-all animate-in fade-in slide-in-from-top-4">
          {toastMessage}
        </div>
      )}

      {/* Main Studio Workspace */}
      <div className="flex-1 flex overflow-hidden">
        <div className="flex-1 relative overflow-hidden bg-black">
          {videoActive ? (
            <video ref={videoRef} autoPlay playsInline muted className="w-full h-full object-cover opacity-50 transition-opacity duration-1000" />
          ) : (
            <div className="w-full h-full bg-gradient-to-tr from-[#1DB954]/20 via-[#121212] to-[#1DB954]/5" />
          )}

          <div className="absolute inset-0 pointer-events-none">
            <MusicVisualizer isPlaying={playbackState === 'playing'} />
          </div>

          {/* Mave Header Hub */}
          <div className="absolute top-6 left-8 right-8 flex justify-between items-center">
            <div className="flex items-center gap-6">
              <MaveLogo variant="dark" size={140} />
              <div className="flex bg-[#282828] p-1 rounded-full border border-white/5">
                <button
                  onClick={() => switchMode('music')}
                  className={`px-6 py-1.5 rounded-full text-xs font-bold transition-all ${mode === 'music' ? 'bg-[#1DB954] text-black shadow-lg' : 'text-gray-400 hover:text-white'}`}
                >
                  <span className="material-icons-round text-lg leading-none">music_note</span>
                </button>
                <button
                  onClick={() => switchMode('podcast')}
                  className={`px-6 py-1.5 rounded-full text-xs font-bold transition-all ${mode === 'podcast' ? 'bg-[#1DB954] text-black shadow-lg' : 'text-gray-400 hover:text-white'}`}
                >
                  <span className="material-icons-round text-lg leading-none">podcasts</span>
                </button>
                <button
                  onClick={() => switchMode('audiobook')}
                  className={`px-6 py-1.5 rounded-full text-xs font-bold transition-all ${mode === 'audiobook' ? 'bg-[#1DB954] text-black shadow-lg' : 'text-gray-400 hover:text-white'}`}
                >
                  <span className="material-icons-round text-lg leading-none">menu_book</span>
                </button>
              </div>
            </div>

            <div className="flex gap-3">
              <button
                onClick={() => setVideoActive(!videoActive)}
                className={`p-3 rounded-full transition-all ${videoActive ? 'bg-[#1DB954] text-black' : 'bg-[#282828] text-white hover:bg-[#333333]'}`}
              >
                <span className="material-icons-round text-xl">videocam</span>
              </button>
              <div className="px-4 py-2 bg-red-600 rounded-full flex items-center gap-2 shadow-lg">
                <span className="w-2 h-2 rounded-full bg-white animate-pulse" />
                <span className="text-[10px] font-black uppercase tracking-widest">{t('dashboard.livePov')}</span>
              </div>
            </div>
          </div>

          {/* Removed floating thinking console as per immersive UI guidelines */}
        </div>

        {/* Conversational Sidebar */}
        <div className="w-[400px] flex flex-col gap-4 p-6 border-l border-white/5 bg-[#121212] backdrop-blur-3xl shadow-2xl">
          <div className="flex-1 overflow-y-auto space-y-6 custom-scrollbar pr-2">
            {messages.length === 0 && !thinkingText && (
              <EmptyState 
                icon="chat_bubble_outline" 
                title={t('dashboard.readingImage') || "Ready to Chat"} 
                description="Ask Mave anything about your music, podcasts, or audiobooks." 
              />
            )}
            {thinkingText && (
              <div className="flex flex-col items-start">
                <div className="max-w-[90%] p-4 rounded-3xl text-sm font-bold shadow-xl leading-relaxed bg-[#1DB954] text-black">
                  {thinkingText}
                </div>
              </div>
            )}
            {messages.map((m, i) => (
              <div key={i} className={`flex flex-col ${m.sender === 'user' ? 'items-end' : 'items-start'}`}>
                <div className={`max-w-[90%] flex items-start gap-2 ${m.sender === 'user' ? 'flex-row-reverse' : ''}`}>
                  <div className={`p-4 rounded-3xl text-sm font-bold shadow-xl leading-relaxed select-text ${
                    m.sender === 'user' ? 'bg-[#2E2E2E] text-white' : 'bg-[#1DB954] text-black'
                  }`}>
                    {m.text}
                  </div>
                  <CopyButton text={m.text} showToast={showToast} />
                </div>
                {m.sender === 'mave' && m.trackId && (
                  <div className="flex gap-4 mt-2 ml-2">
                    <button onClick={() => handleAction('like', m.trackId)} className="text-gray-500 hover:text-[#1DB954] transition-colors">
                      <span className="material-icons-round text-lg">favorite_border</span>
                    </button>
                    <button onClick={() => handleAction('bookmark', m.trackId)} className="text-gray-500 hover:text-white transition-colors">
                      <span className="material-icons-round text-lg">bookmark_border</span>
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>

          <div className="h-40 relative rounded-2xl bg-[#181818] overflow-hidden border border-white/5 group shadow-inner">
            <GesturePad onWarp={warp} />
            <div className="absolute inset-0 pointer-events-none border border-[#1DB954]/0 group-hover:border-[#1DB954]/20 transition-all duration-700" />
          </div>
        </div>
      </div>

      {/* Bottom Bar */}
      <div className="p-8 bg-[#121212] border-t border-white/5">
        <div className="max-w-5xl mx-auto flex gap-6 items-center bg-[#282828] p-3 rounded-full shadow-2xl border border-white/10 hover:border-white/20 transition-all">
          <div className="flex gap-2 border-r border-white/10 pr-4 pl-2">
            <button
              onClick={() => fileInputRef.current?.click()}
              className="w-10 h-10 rounded-full flex items-center justify-center text-gray-400 hover:text-white hover:bg-white/5 transition-all"
            >
              <span className="material-icons-round text-xl">add</span>
            </button>
            <input 
              type="file" 
              ref={fileInputRef}
              className="hidden" 
              accept="image/*,video/*" 
              onChange={handleFileUpload} 
            />
            <button
              onClick={handleCameraSnapshot}
              className="w-10 h-10 rounded-full flex items-center justify-center text-gray-400 hover:text-white hover:bg-white/5 transition-all"
            >
              <span className="material-icons-round text-xl">photo_camera</span>
            </button>
          </div>
          
          <button
            onClick={toggleRecording}
            className={`w-12 h-12 rounded-full flex items-center justify-center transition-all ${
              isRecording ? 'bg-red-600 text-white scale-110 shadow-lg' : 'text-gray-400 hover:text-white hover:bg-white/5'
            }`}
          >
            <span className="material-icons-round text-2xl">mic</span>
          </button>
          
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder={mode === 'music' ? t('dashboard.instructMusic') : t('dashboard.instructNarrative')}
            className="flex-1 bg-transparent border-none outline-none text-base font-bold placeholder:text-gray-600 text-white px-2"
          />
          <button
            onClick={handleSend}
            disabled={!inputText.trim()}
            className="w-12 h-12 bg-white text-black rounded-full flex items-center justify-center disabled:opacity-30 disabled:grayscale transition-all hover:scale-105 active:scale-95 shadow-xl"
          >
            <span className="material-icons-round text-2xl">arrow_upward</span>
          </button>
        </div>
      </div>
    </div>
  );
};
