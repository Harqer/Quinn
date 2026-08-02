import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';
import { CopyButton } from '../atoms/CopyButton';
import { ReasoningStream } from '../../features/chat/ReasoningStream';
import { Track } from '../../services/MusicService';

interface MessageBubbleProps {
  msg: any;
  index: number;
  isGenerating: boolean;
  playTrack: (track: Track) => void;
}

export const MessageBubble: React.FC<MessageBubbleProps> = ({ msg, index, isGenerating, playTrack }) => {
  if (msg.sender === 'mave') {
    return (
      <div className="flex items-start gap-3">
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
                {msg.coverUrl && <img src={msg.coverUrl} className="w-full h-full object-cover rounded-lg" alt="Cover" />}
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
    );
  }

  return (
    <div className="flex flex-col items-end w-full">
      <div className="message-bubble bg-primary-container p-4 rounded-xl rounded-tr-none text-on-primary-container shadow-lg shadow-primary-container/10 max-w-[85%]">
        <div className="flex items-start gap-2">
          <Typography variant="body-md">{msg.text}</Typography>
          <CopyButton text={msg.text} variant="chat" />
        </div>
      </div>
      <span className="text-[10px] mt-1 text-text-secondary uppercase tracking-widest mr-1">Delivered</span>
    </div>
  );
};
