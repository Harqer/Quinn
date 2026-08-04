import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from '../../App';
import { useMave } from '../../hooks/useMave';
import { usePlayerContext } from '../../contexts/PlayerContext';
import { LiveSessionHeader } from '../../components/molecules/LiveSessionHeader';
import { LiveCameraView } from '../../components/organisms/LiveCameraView';
import { LiveSessionInput } from '../../components/organisms/LiveSessionInput';
import { LiveChatMessage } from '../../components/molecules/LiveChatMessage';
import { Typography } from '../../components/atoms/Typography';

export const LiveSessionScreen: React.FC = () => {
  const [input, setInput] = useState('');
  const [showCamera, setShowCamera] = useState(false);
  const videoRef = useRef<HTMLVideoElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  const {
    messages,
    isConnected,
    isRecording,
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
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
        streamRef.current = null;
      }
      setShowCamera(false);
    } else {
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

  const captureAndSendFrame = (isAuto: boolean | React.MouseEvent = false) => {
    const isAutomatic = isAuto === true;
    if (!videoRef.current || !showCamera || isGenerating) return;
    
    const canvas = document.createElement('canvas');
    canvas.width = videoRef.current.videoWidth;
    canvas.height = videoRef.current.videoHeight;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    
    ctx.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
    const dataUrl = canvas.toDataURL('image/jpeg', 0.8);
    sendVisionFrame(dataUrl);
    
    if (isAutomatic) {
       sendText("Tweak the instrumentation based on this new visual.");
    } else {
       sendText("Make a song based on this picture");
       toggleCamera();
    }
  };

  useEffect(() => {
    let intervalId: NodeJS.Timeout;
    if (showCamera) {
      intervalId = setInterval(() => {
        captureAndSendFrame(true);
      }, 5000); // Capture frame every 5 seconds
    }
    return () => {
      if (intervalId) clearInterval(intervalId);
    };
  }, [showCamera, isGenerating]);

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-hidden relative pb-[80px]">
      <LiveSessionHeader isConnected={isConnected} onNavigateHome={() => navigate('home')} />

      <LiveCameraView 
        videoRef={videoRef} 
        showCamera={showCamera} 
        isGenerating={isGenerating} 
        onCapture={captureAndSendFrame} 
        onToggleCamera={toggleCamera} 
      />

      <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-4">
        {messages.length === 0 && (
           <div className="flex w-full justify-start">
             <div className="max-w-[85%] rounded-2xl px-4 py-3 bg-surface-container text-on-surface rounded-bl-sm">
               <Typography variant="body-md">Hi! Ready to create some music? Tell me what kind of vibe you want.</Typography>
             </div>
           </div>
        )}
        
        {messages.map((msg, index) => (
          <LiveChatMessage 
             key={msg.id} 
             msg={msg} 
             isGenerating={isGenerating} 
             isFirst={index === 0} 
          />
        ))}
        <div ref={messagesEndRef} />
      </div>

      <LiveSessionInput 
        input={input} 
        setInput={setInput} 
        isGenerating={isGenerating} 
        isRecording={isRecording} 
        showCamera={showCamera} 
        onSend={handleSend} 
        onMicToggle={handleMicToggle} 
        onCameraToggle={toggleCamera} 
      />
    </div>
  );
};
