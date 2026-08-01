import React, { useState, useRef, useEffect } from 'react';
import { useMave } from '../../hooks/useMave';
import { useSidebar } from '../../App';
import maveBrandDark from '../../assets/mave_brand_dark.png';
import { ChatHeader } from '../../components/molecules/ChatHeader';
import { ChatInputBar } from '../../components/organisms/ChatInputBar';
import { LiveChatMessage } from '../../components/molecules/LiveChatMessage';
import { Typography } from '../../components/atoms/Typography';

export const ChatScreen: React.FC = () => {
  const { messages, sendText, toggleRecording, isRecording, sendVisionFrame, requestCoverArt, requestVideo, isGenerating } = useMave();
  const [inputValue, setInputValue] = useState('');
  const [promptMode, setPromptMode] = useState<'chat' | 'cover' | 'video'>('chat');
  const endOfMessagesRef = useRef<HTMLDivElement>(null);
  const { toggleSidebar } = useSidebar();

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
  };

  return (
    <div className="absolute inset-0 flex flex-col bg-background overflow-hidden">
      <ChatHeader onToggleSidebar={toggleSidebar} />

      <div className="fixed inset-0 z-0 pointer-events-none opacity-[0.05] flex items-center justify-center p-12">
        <img src={maveBrandDark} alt="Mave Background" className="w-full max-w-2xl object-contain opacity-20" />
      </div>

      <main className="flex-1 overflow-y-auto chat-container pt-[64px] pb-[100px] px-4 flex flex-col space-y-4 relative z-10">
        {messages.length === 0 && (
          <div className="flex flex-col items-center justify-center py-12 text-center opacity-80 mt-8">
            <Typography variant="title-lg" className="font-bold text-on-surface mb-2">Hi, I'm Mave</Typography>
            <Typography variant="body-md" className="text-text-secondary max-w-[250px]">Your personal audio curator. What kind of track should we make today?</Typography>
          </div>
        )}

        {messages.slice().reverse().map((msg, index) => (
          <LiveChatMessage 
            key={msg.id} 
            msg={msg} 
            isGenerating={isGenerating} 
            isFirst={index === 0} 
          />
        ))}
        <div ref={endOfMessagesRef} />
      </main>

      <ChatInputBar 
        inputValue={inputValue} 
        setInputValue={setInputValue} 
        handleSend={handleSend} 
        handleFileUpload={handleFileUpload} 
        promptMode={promptMode} 
        setPromptMode={setPromptMode} 
        toggleRecording={toggleRecording} 
        isRecording={isRecording} 
      />
    </div>
  );
};
