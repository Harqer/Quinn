import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';
import { Track } from '../../services/MusicService';
import { AnimatedGradient } from '../atoms/AnimatedGradient';

interface LiveChatMessageProps {
  msg: any;
  onPlayTrack?: (track: Track) => void;
  isGenerating?: boolean;
  isFirst?: boolean;
}

export const LiveChatMessage: React.FC<LiveChatMessageProps> = ({ msg, onPlayTrack }) => {
  const isUser = msg.sender === 'user';
  
  return (
    <div className={`flex w-full gap-3 ${isUser ? 'justify-end' : 'justify-start'}`}>
      {!isUser && (
        <div className="shrink-0 pt-1">
          <AnimatedGradient className="w-8 h-8 rounded-full shadow-md" />
        </div>
      )}
      
      <div className={`max-w-[85%] rounded-2xl px-4 py-3 ${
        isUser ? 'bg-primary text-on-primary rounded-br-sm' : 'bg-surface-container text-on-surface rounded-bl-sm'
      }`}>
        
        {/* Text / Status */}
        {(msg.text || msg.type === 'generating') && (
          <Typography variant="body-md">{msg.text || '...'}</Typography>
        )}
        
        {/* Cover Art */}
        {(msg.coverUrl || msg.audioUrl) && (
          <div className="mt-2 w-48 h-48 rounded-md overflow-hidden bg-surface-container-high">
            {msg.coverUrl ? (
              <img src={msg.coverUrl} alt="Cover Art" className="w-full h-full object-cover" />
            ) : (
              <AnimatedGradient className="w-full h-full" mood="random" />
            )}
          </div>
        )}
        
        {/* Audio Player for generated track */}
        {msg.audioUrl && (
          <div className="flex items-center gap-3 w-48 mt-2">
            <button 
              onClick={() => {
                if (onPlayTrack) {
                  const track: Track = {
                    id: msg.trackId || msg.id || `track-${Date.now()}`,
                    title: msg.title || 'Generated Track',
                    artist: msg.voice || 'Mave',
                    audioUrl: msg.audioUrl,
                    albumArtUrl: msg.coverUrl,
                  };
                  onPlayTrack(track);
                }
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
  );
};
