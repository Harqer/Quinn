import React, { useState, useRef, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { copyToClipboard } from '../../utils/clipboard';
import { ReasoningStream } from './ReasoningStream';
import maveBrandDark from '../../assets/mave_brand_dark.png';
import { usePlayerContext } from '../../contexts/PlayerContext';
import { Track } from '../../services/MusicService';

interface Message {
  id: string;
  sender: 'user' | 'ai';
  text: string;
  tracks?: Array<{ title: string; artist: string; coverUrl?: string }>;
}

import { getAuth } from 'firebase/auth';

import { useMave } from '../../hooks/useMave';
import { useSidebar } from '../../App';

export const ChatScreen: React.FC = () => {
  const { messages, sendText, toggleRecording, isRecording, sendVisionFrame, requestCoverArt, requestVideo, isGenerating } = useMave();
  const [inputValue, setInputValue] = useState('');
  const [isAddMenuOpen, setIsAddMenuOpen] = useState(false);
  const [promptMode, setPromptMode] = useState<'chat' | 'cover' | 'video'>('chat');
  const endOfMessagesRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const cameraInputRef = useRef<HTMLInputElement>(null);
  const addMenuRef = useRef<HTMLDivElement>(null);
  const { toggleSidebar } = useSidebar();
  const { playTrack } = usePlayerContext();



  useEffect(() => {
    const container = document.querySelector('.custom-scrollbar') as HTMLElement;
    if (container) {
      const isNearBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 200;
      if (isNearBottom || messages.length <= 2) {
        endOfMessagesRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
      }
    } else {
      endOfMessagesRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
    }
  }, [messages]);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (addMenuRef.current && !addMenuRef.current.contains(event.target as Node)) {
        setIsAddMenuOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSend = () => {
    if (!inputValue.trim()) return;
    const text = inputValue.trim();
    setInputValue('');
    if (promptMode === 'cover') {
       requestCoverArt(text);
    } else if (promptMode === 'video') {
       requestVideo(text);
    } else {
       sendText(text);
    }
    setPromptMode('chat');
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (ev) => {
      const base64 = ev.target?.result as string;
      sendVisionFrame(base64);
    };
    reader.readAsDataURL(file);
    // Reset input so the same file can be selected again
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
    if (cameraInputRef.current) {
      cameraInputRef.current.value = '';
    }
  };

  return (
    <div className="absolute inset-0 flex flex-col bg-background overflow-hidden">
      {/* TopAppBar */}
      <header className="absolute top-0 w-full z-50 flex justify-between items-center px-4 h-[56px] backdrop-blur-xl bg-surface/80">
        <button onClick={toggleSidebar} className="text-primary hover:bg-surface-variant/50 p-2 rounded-full transition-colors hidden md:block">
          <Icon name="menu" />
        </button>
        <button onClick={toggleSidebar} className="text-primary hover:bg-surface-variant/50 p-2 rounded-full transition-colors md:hidden">
          <Icon name="menu" />
        </button>
        <Typography variant="title-md" className="font-bold text-on-surface">Mave</Typography>
        <button 
          onClick={() => window.dispatchEvent(new CustomEvent('show-options-menu', { detail: 'chat' }))}
          className="text-primary hover:bg-surface-variant/50 p-2 rounded-full transition-colors"
        >
          <Icon name="more_vert" />
        </button>
      </header>

      {/* Chat Background Logo */}
      <div className="fixed inset-0 z-0 pointer-events-none opacity-[0.05] flex items-center justify-center p-12">
        <img src={maveBrandDark} alt="Mave Background" className="w-full max-w-2xl object-contain opacity-20" />
      </div>

      {/* Main Chat Canvas */}
      <main className="flex-1 overflow-y-auto chat-container pt-[64px] pb-[100px] px-4 flex flex-col space-y-4 relative z-10">
        {messages.length === 0 && (
          <div className="flex flex-col items-center justify-center py-12 text-center opacity-80 mt-8">
            <Typography variant="title-lg" className="font-bold text-on-surface mb-2">Hi, I'm Mave</Typography>
            <Typography variant="body-md" className="text-text-secondary max-w-[250px]">Your personal audio curator. What kind of track should we make today?</Typography>
          </div>
        )}

        {messages.slice().reverse().map((msg, index) => (
          msg.sender === 'mave' ? (
            <div key={msg.id} className="flex items-start gap-3">
              <div className="message-bubble bg-surface-bright p-4 rounded-xl rounded-tl-none max-w-[85%]">
                {msg.reasoning && !msg.isReasoningComplete && (
                  <ReasoningStream reasoning={msg.reasoning} isComplete={msg.isReasoningComplete} />
                )}
                {(msg.text || (isGenerating && index === 0 && msg.isReasoningComplete)) && (
                  <Typography variant="body-md" className="text-on-surface whitespace-pre-wrap">
                    {msg.text}
                    {isGenerating && index === 0 && msg.isReasoningComplete && (
                      <span className="inline-flex items-center align-middle ml-2">
                        <Icon name="progress_activity" className="animate-spin text-primary text-[18px]" />
                      </span>
                    )}
                  </Typography>
                )}
                {msg.trackId && msg.title && (
                  <div className="mt-4 bg-surface-container-high rounded-xl overflow-hidden flex items-center p-3 border border-outline-variant/30 group cursor-pointer active:scale-95 transition-transform">
                    <div className="w-16 h-16 rounded-lg bg-surface-variant flex-shrink-0">
                      {msg.coverUrl && <img src={msg.coverUrl} className="w-full h-full object-cover rounded-lg" />}
                    </div>
                    <div className="ml-4 flex-1">
                      <Typography variant="body-md" className="font-bold text-on-surface">{msg.title}</Typography>
                      {msg.voice && <Typography variant="body-sm" className="text-text-secondary">{msg.voice}</Typography>}
                    </div>
                    <button 
                      onClick={() => {
                        if (msg.audioUrl) {
                          const track: Track = {
                            id: msg.trackId || msg.id,
                            title: msg.title || 'Generated Track',
                            artist: msg.voice || 'Mave',
                            audioUrl: msg.audioUrl,
                            albumArtUrl: msg.coverUrl,
                          };
                          playTrack(track);
                        }
                      }}
                      className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-on-primary shadow-lg shadow-primary/20">
                      <Icon name="play_arrow" />
                    </button>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div key={msg.id} className="flex flex-col items-end w-full">
              <div className="message-bubble bg-primary-container p-4 rounded-xl rounded-tr-none text-on-primary-container shadow-lg shadow-primary-container/10 max-w-[85%]">
                <div className="flex items-start gap-2">
                  <Typography variant="body-md">{msg.text}</Typography>
                  <CopyButton text={msg.text} />
                </div>
              </div>
              <span className="text-[10px] mt-1 text-text-secondary uppercase tracking-widest mr-1">Delivered</span>
            </div>
          )
        ))}
        <div ref={endOfMessagesRef} />
      </main>

      {/* Bottom Input Bar Section */}
      <div className="absolute bottom-0 left-0 w-full px-4 pb-8 pt-4 bg-gradient-to-t from-background via-background to-transparent z-50">
        <div className="max-w-4xl mx-auto flex items-end gap-2 bg-surface-container-high rounded-full p-2 shadow-2xl border border-outline-variant/20 backdrop-blur-xl">
          <input 
            type="file" 
            accept="image/*,video/*" 
            ref={fileInputRef} 
            onChange={handleFileUpload} 
            className="hidden" 
          />
          <input 
            type="file" 
            accept="image/*,video/*" 
            capture="environment"
            ref={cameraInputRef} 
            onChange={handleFileUpload} 
            className="hidden" 
          />
          <div className="relative flex-shrink-0 flex items-center gap-1" ref={addMenuRef}>
            <button 
              onClick={() => setIsAddMenuOpen(!isAddMenuOpen)}
              className={`w-10 h-10 flex items-center justify-center rounded-full transition-colors ${isAddMenuOpen ? 'bg-surface-container-highest text-primary' : 'text-primary hover:bg-surface-container-highest'}`}
            >
              <Icon name="add" />
            </button>
            <button 
              onClick={() => cameraInputRef.current?.click()}
              className="w-10 h-10 flex items-center justify-center rounded-full transition-colors text-primary hover:bg-surface-container-highest"
              title="Take photo or video"
            >
              <Icon name="photo_camera" />
            </button>
            {isAddMenuOpen && (
              <div className="absolute bottom-full left-0 mb-4 w-56 bg-surface-container-high rounded-2xl shadow-[0_8px_30px_rgb(0,0,0,0.12)] border border-outline-variant/30 py-2 overflow-hidden z-50 flex flex-col animate-in fade-in zoom-in-95 duration-200">
                <button 
                  onClick={() => { fileInputRef.current?.click(); setIsAddMenuOpen(false); }}
                  className="flex items-center gap-4 px-4 py-3 hover:bg-surface-container-highest transition-colors text-left text-on-surface"
                >
                  <Icon name="upload_file" className="text-text-secondary" />
                  <Typography variant="body-md">Upload files</Typography>
                </button>
                <div className="h-[1px] bg-outline-variant/20 my-1 mx-4" />
                <button 
                  onClick={() => {
                    setPromptMode('cover');
                    setIsAddMenuOpen(false);
                  }}
                  className="flex items-center gap-4 px-4 py-3 hover:bg-surface-container-highest transition-colors text-left text-on-surface"
                >
                  <Icon name="image" className="text-text-secondary" />
                  <Typography variant="body-md">Create Image</Typography>
                </button>
                <button 
                  onClick={() => {
                    setPromptMode('video');
                    setIsAddMenuOpen(false);
                  }}
                  className="flex items-center gap-4 px-4 py-3 hover:bg-surface-container-highest transition-colors text-left text-on-surface"
                >
                  <Icon name="movie" className="text-text-secondary" />
                  <Typography variant="body-md">Create video</Typography>
                </button>
              </div>
            )}
          </div>
          
          <textarea 
            className="flex-1 bg-transparent border-none focus:ring-0 text-on-surface text-body-md py-2 resize-none placeholder:text-text-secondary max-h-32 min-h-[40px] outline-none" 
            placeholder={promptMode === 'cover' ? "Describe the cover art..." : promptMode === 'video' ? "Describe the music video scene..." : "Ask Mave anything..."} 
            rows={1}
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSend();
              }
            }}
          />
          
          {inputValue.trim() ? (
             <button onClick={handleSend} className="w-10 h-10 flex items-center justify-center bg-primary text-on-primary rounded-full transition-all shadow-lg shadow-primary/20 flex-shrink-0">
               <Icon name="send" />
             </button>
          ) : (
            <div className="flex items-center flex-shrink-0">
              <button 
                onClick={toggleRecording} 
                className={`w-10 h-10 flex items-center justify-center rounded-full transition-colors ${isRecording ? 'bg-red-500 text-white' : 'text-text-secondary hover:bg-surface-container-highest'}`}
              >
                <Icon name="mic" />
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function CopyButton({ text }: { text: string }) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    const success = await copyToClipboard(text);
    if (success) {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <button
      onClick={handleCopy}
      className="text-on-primary-container/70 hover:text-on-primary-container transition-colors p-1 -mt-1 rounded-full active:bg-black/10 flex-shrink-0"
      title="Copy text"
    >
      <Icon name={copied ? "check" : "content_copy"} className="text-[16px]" />
    </button>
  );
}
;
