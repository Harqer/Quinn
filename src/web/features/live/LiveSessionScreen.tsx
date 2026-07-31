import React, { useState, useRef, useEffect, useCallback } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { useNavigate } from '../../App';
import { Shimmer } from '../../components/atoms/Shimmer';
import { useMave } from '../../hooks/useMave';
import { usePlayerContext } from '../../contexts/PlayerContext';
import { Track } from '../../services/MusicService';

export const LiveSessionScreen: React.FC = () => {
  const [input, setInput] = useState('');
  const [showCamera, setShowCamera] = useState(false);
  const videoRef = useRef<HTMLVideoElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  const {
    messages,
    mode,
    isConnected,
    isRecording,
    thinkingText,
    sendText,
    sendVisionFrame,
    toggleRecording,
    isGenerating
  } = useMave();
  const { playTrack } = usePlayerContext();

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Clean up camera on unmount
  useEffect(() => {
    return () => {
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
      }
    };
  }, []);

  const handleSend = () => {
    if (!input.trim() || isGenerating) return;
    sendText(input);
    setInput('');
  };

  const handleMicToggle = () => {
    toggleRecording();
    if (!isRecording) {
      window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Recording started...' }));
    } else {
      window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Recording finished.' }));
    }
  };

  const toggleCamera = async () => {
    if (showCamera) {
      // Turn off camera
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
        streamRef.current = null;
      }
      setShowCamera(false);
    } else {
      // Turn on camera
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'user' } });
        streamRef.current = stream;
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
        setShowCamera(true);
      } catch (err) {
        console.error("Failed to access camera", err);
        window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Camera access denied.' }));
      }
    }
  };

  const captureAndSendFrame = () => {
    if (!videoRef.current || !showCamera || isGenerating) return;
    
    const canvas = document.createElement('canvas');
    canvas.width = videoRef.current.videoWidth;
    canvas.height = videoRef.current.videoHeight;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    
    ctx.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
    const dataUrl = canvas.toDataURL('image/jpeg', 0.8);
    sendVisionFrame(dataUrl);
    
    // Optionally close camera after capture
    toggleCamera();
  };

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-hidden relative pb-[80px]">
      {/* Top Nav */}
      <div className="flex items-center gap-3 px-4 py-4 bg-surface-container sticky top-0 z-10 shadow-sm shrink-0">
        <button onClick={() => navigate('home')} className="text-on-surface hover:opacity-80 transition-opacity">
          <Icon name="close" size="2xl" />
        </button>
        <Typography variant="title-md" className="font-bold flex-1">
          Live Session {isConnected ? '(Connected)' : ''}
        </Typography>
        <button 
          onClick={() => window.dispatchEvent(new CustomEvent('show-options-menu', { detail: 'live-session' }))}
          className="text-on-surface hover:opacity-80 transition-opacity"
        >
          <Icon name="more_vert" size="2xl" />
        </button>
      </div>

      {/* Camera View */}
      {showCamera && (
        <div className="relative w-full h-64 bg-black flex-shrink-0">
          <video 
            ref={videoRef} 
            autoPlay 
            playsInline 
            muted 
            className="w-full h-full object-cover"
          />
          <button 
            onClick={captureAndSendFrame}
            disabled={isGenerating}
            className="absolute bottom-4 left-1/2 -translate-x-1/2 w-14 h-14 bg-white rounded-full flex items-center justify-center border-4 border-primary/50 shadow-lg disabled:opacity-50"
          >
            <div className="w-10 h-10 bg-primary rounded-full"></div>
          </button>
          <button 
            onClick={toggleCamera}
            className="absolute top-4 right-4 w-10 h-10 bg-black/50 text-white rounded-full flex items-center justify-center"
          >
            <Icon name="close" size="md" />
          </button>
        </div>
      )}

      {/* Chat Area */}
      <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-4">
        {messages.length === 0 && (
           <div className="flex w-full justify-start">
             <div className="max-w-[85%] rounded-2xl px-4 py-3 bg-surface-container text-on-surface rounded-bl-sm">
               <Typography variant="body-md">Hi! Ready to create some music? Tell me what kind of vibe you want.</Typography>
             </div>
           </div>
        )}
        
        {messages.map(msg => (
          <div key={msg.id} className={`flex w-full ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-[85%] rounded-2xl px-4 py-3 ${
              msg.sender === 'user' ? 'bg-primary text-on-primary rounded-br-sm' : 'bg-surface-container text-on-surface rounded-bl-sm'
            }`}>
              
              {/* Text / Status */}
              {(msg.text || msg.type === 'generating') && (
                <Typography variant="body-md">{msg.text || '...'}</Typography>
              )}
              
              {/* Cover Art */}
              {msg.coverUrl && (
                <img src={msg.coverUrl} alt="Cover Art" className="w-48 h-48 rounded-md mt-2 object-cover" />
              )}
              
              {/* Audio Player for generated track */}
              {msg.audioUrl && (
                <div className="flex items-center gap-3 w-48 mt-2">
                  <button 
                    onClick={() => {
                      const track: Track = {
                        id: msg.trackId || msg.id,
                        title: msg.title || 'Generated Track',
                        artist: msg.voice || 'Mave',
                        audioUrl: msg.audioUrl,
                        albumArtUrl: msg.coverUrl,
                      };
                      playTrack(track);
                    }}
                    className="w-10 h-10 bg-primary/20 rounded-full flex items-center justify-center text-primary shrink-0"
                  >
                    <Icon name="play_arrow" size="md" />
                  </button>
                  <div className="flex-1 overflow-hidden">
                    <Typography variant="label-md" className="font-bold truncate">{msg.title || 'Generated Track'}</Typography>
                    <Typography variant="body-sm" color="secondary" className="truncate">{msg.voice || 'Mave'}</Typography>
                  </div>
                </div>
              )}
            </div>
          </div>
        ))}
        {isGenerating && (
          <div className="flex w-full justify-start">
            <div className="max-w-[85%] rounded-2xl px-4 py-3 bg-surface-container text-on-surface rounded-bl-sm">
              <div className="flex items-center gap-2">
                <Shimmer className="w-2 h-2 rounded-full" />
                <Shimmer className="w-2 h-2 rounded-full" />
                <Shimmer className="w-2 h-2 rounded-full" />
              </div>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Bottom Input Area */}
      <div className="bg-surface-container border-t border-white/5 p-3 px-4 flex items-center gap-2 shrink-0">
        <button 
          onClick={toggleCamera}
          className={`w-10 h-10 flex items-center justify-center transition-colors ${showCamera ? 'text-primary' : 'text-secondary hover:text-white'}`}
        >
          <Icon name="camera_alt" size="xl" />
        </button>
        <div className="flex-1 bg-surface rounded-full flex items-center px-4 py-2 border border-white/10 focus-within:border-primary transition-colors">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Describe a vibe..."
            disabled={isGenerating}
            className="bg-transparent w-full outline-none text-white placeholder-white/40 disabled:opacity-50"
          />
        </div>
        {input.trim() ? (
          <button 
            onClick={handleSend} 
            disabled={isGenerating}
            className="w-10 h-10 flex items-center justify-center bg-primary text-black rounded-full shadow-md hover:scale-105 transition-transform disabled:opacity-50 disabled:hover:scale-100"
          >
            <Icon name="send" size="md" />
          </button>
        ) : (
          <button 
            onClick={handleMicToggle} 
            disabled={isGenerating && !isRecording}
            className={`w-10 h-10 flex items-center justify-center rounded-full shadow-md transition-all ${isRecording ? 'bg-error text-white animate-pulse' : 'bg-primary text-black hover:scale-105'} disabled:opacity-50 disabled:hover:scale-100`}
          >
            <Icon name={isRecording ? 'stop' : 'mic'} size="md" />
          </button>
        )}
      </div>
    </div>
  );
};
