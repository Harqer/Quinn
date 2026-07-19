import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

export interface TrackListItemProps {
  title: string;
  artist: string;
  albumArtUrl?: string;
  isExplicit?: boolean;
  isPlaying?: boolean;
  onClick?: () => void;
  rightElement?: React.ReactNode;
}

export const TrackListItem: React.FC<TrackListItemProps> = ({
  title,
  artist,
  albumArtUrl,
  isExplicit = false,
  isPlaying = false,
  onClick,
  rightElement
}) => {
  return (
    <div 
      className="flex items-center justify-between py-2 px-4 hover:bg-surface-container active:bg-surface-container/80 transition-colors cursor-pointer group"
      onClick={onClick}
    >
      <div className="flex items-center gap-3 overflow-hidden">
        {albumArtUrl ? (
          <img src={albumArtUrl} alt={`${title} art`} className="w-12 h-12 rounded bg-surface object-cover flex-shrink-0" />
        ) : (
          <div className="w-12 h-12 rounded bg-surface-container flex items-center justify-center flex-shrink-0">
            <Icon name="music_note" color="secondary" />
          </div>
        )}
        
        <div className="flex flex-col overflow-hidden">
          <Typography variant="body-lg" className={`truncate ${isPlaying ? 'text-primary' : 'text-text-primary'}`}>
            {title}
          </Typography>
          <div className="flex items-center gap-1">
            {isExplicit && (
              <span className="bg-text-secondary text-[#121212] text-[9px] font-bold px-1 rounded-sm flex items-center justify-center h-3.5">
                E
              </span>
            )}
            <Typography variant="body-sm" color="secondary" className="truncate">
              {artist}
            </Typography>
          </div>
        </div>
      </div>

      <div className="flex items-center">
        {rightElement ? rightElement : (
          <button className="text-text-secondary hover:text-text-primary p-2">
            <Icon name="more_vert" />
          </button>
        )}
      </div>
    </div>
  );
};
