import React, { useState, useRef, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { logger } from '../../lib/logger';
import { MusicVisualizer } from './MusicVisualizer';
import { useMave } from '../../hooks/useMave';
import { useAppContext } from '../../contexts/AppContext';
import { MaveHeaderHub } from '../../components/molecules/MaveHeaderHub';
import { ConversationalSidebar } from '../../components/organisms/ConversationalSidebar';
import { DashboardBottomBar } from '../../components/organisms/DashboardBottomBar';

export const MainDashboard: React.FC = () => {
  const [videoActive, setVideoActive] = useState(true);
  const [playbackState, setPlaybackState] = useState<'playing' | 'stopped'>('stopped');
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  
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
  }, [videoActive, isConnected, sendVisionFrame]);

  const [inputText, setInputText] = useState('');

  const handleSend = () => {
    if (!inputText.trim()) return;
    sendText(inputText);
    setInputText('');
  };

  const handleCameraSnapshot = () => {
    if (videoRef.current && canvasRef.current) {
      const canvas = canvasRef.current;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        canvas.width = 320;
        canvas.height = 240;
        ctx.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
        const base64Frame = canvas.toDataURL('image/jpeg', 0.5).split(',')[1];
        sendVisionFrame(base64Frame);
        showToast("POV snapshot captured and sent to Mave");
      }
    }
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        const base64 = (event.target?.result as string)?.split(',')[1];
        if (base64) {
          sendVisionFrame(base64);
          showToast("Media file attached and sent to Mave");
        }
      };
      reader.readAsDataURL(file);
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

          <MaveHeaderHub 
            mode={mode} 
            videoActive={videoActive} 
            switchMode={switchMode} 
            setVideoActive={setVideoActive} 
          />
        </div>

        <ConversationalSidebar 
          messages={messages} 
          thinkingText={thinkingText} 
          mode={mode} 
          onWarp={warp} 
          showToast={showToast} 
        />
      </div>

      <DashboardBottomBar 
        mode={mode} 
        isRecording={isRecording} 
        inputText={inputText}
        setInputText={setInputText}
        handleSend={handleSend}
        toggleRecording={toggleRecording} 
        handleCameraSnapshot={handleCameraSnapshot}
        handleFileUpload={handleFileUpload}
      />
    </div>
  );
};

