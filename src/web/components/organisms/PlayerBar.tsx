import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

export interface PlayerBarProps {
  onClick?: () => void;
  trackName?: string;
  artistName?: string;
  albumArtUrl?: string;
  isPlaying?: boolean;
  onPlayPause?: (e: React.MouseEvent) => void;
}

export const PlayerBar: React.FC<PlayerBarProps> = ({ 
  onClick,
  trackName,
  artistName = "",
  albumArtUrl,
  isPlaying = false,
  onPlayPause
}) => {
  if (!trackName || trackName === "Not playing") {
    return null;
  }

  return (
    <div 
      className="absolute bottom-[72px] left-2 right-2 h-14 bg-surface-container/90 backdrop-blur-[20px] rounded-md flex items-center px-2 shadow-lg cursor-pointer z-50 overflow-hidden border border-white/10"
      onClick={onClick}
    >
      <div className="w-10 h-10 rounded bg-surface overflow-hidden flex-shrink-0 flex items-center justify-center">
        {albumArtUrl ? (
          <img src={albumArtUrl} alt="Album Art" className="w-full h-full object-cover" />
        ) : (
          <Icon name="music_note" color="secondary" />
        )}
      </div>
      
      <div className="flex flex-col ml-3 flex-1 overflow-hidden">
        <Typography variant="body-md" className="font-bold truncate text-white">
          {trackName}
        </Typography>
        {artistName && (
          <Typography variant="body-sm" color="secondary" className="truncate">
            {artistName}
          </Typography>
        )}
      </div>

      <div className="flex items-center gap-2">
        <button className="text-text-primary p-2 hover:text-white transition-colors">
           <Icon name="devices" />
        </button>
        <button 
          className="text-white p-2 hover:scale-105 active:scale-95 transition-transform"
          onClick={(e) => {
            e.stopPropagation();
            onPlayPause?.(e);
          }}
        >
          <Icon name={isPlaying ? "pause" : "play_arrow"} size="xl" />
        </button>
      </div>

      <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-surface rounded-full overflow-hidden">
         <div className="h-full bg-primary w-1/3 rounded-full shadow-[0_0_8px_rgba(29,185,84,0.5)]" />
      </div>
    </div>
  );
};
