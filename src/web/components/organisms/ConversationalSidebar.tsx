import React from 'react';
import { useTranslation } from 'react-i18next';
import { EmptyState } from '../molecules/EmptyState';
import { CopyButton } from '../atoms/CopyButton';
import { GesturePad } from '../../features/dashboard/GesturePad';

interface ConversationalSidebarProps {
  messages: any[];
  thinkingText: string | null;
  showToast: (msg: string) => void;
  handleAction: (action: 'like' | 'bookmark', trackId?: string) => void;
  warp: any;
}

export const ConversationalSidebar: React.FC<ConversationalSidebarProps> = ({
  messages,
  thinkingText,
  showToast,
  handleAction,
  warp
}) => {
  const { t } = useTranslation();

  return (
    <div className="w-[400px] flex flex-col gap-4 p-6 border-l border-white/5 bg-[#121212] backdrop-blur-3xl shadow-2xl relative z-10">
      <div className="flex-1 overflow-y-auto space-y-6 custom-scrollbar pr-2">
        {messages.length === 0 && !thinkingText && (
          <EmptyState 
            icon="chat_bubble_outline" 
            title={(t('dashboard.readingImage') as string) || "Ready to Chat"} 
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
              <CopyButton text={m.text} showToast={showToast} variant="dashboard" />
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
  );
};
