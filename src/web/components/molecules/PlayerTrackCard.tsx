import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

export interface PlayerTrackCardProps {
  isEmpty?: boolean;
  trackName?: string;
  artistName?: string;
  albumArtUrl?: string;
}

export const PlayerTrackCard: React.FC<PlayerTrackCardProps> = ({
  isEmpty = false,
  trackName,
  artistName = "",
  albumArtUrl
}) => {
  return (
    <div className="flex items-center gap-3 justify-start overflow-hidden flex-1 md:flex-none md:w-[30%]">
      <div className="w-10 h-10 md:w-14 md:h-14 rounded bg-surface overflow-hidden flex-shrink-0 flex items-center justify-center">
        {albumArtUrl ? (
          <img src={albumArtUrl} alt="Album Art" className="w-full h-full object-cover" />
        ) : (
          <Icon name="music_note" color="secondary" />
        )}
      </div>
      
      <div className="flex flex-col flex-1 overflow-hidden">
        <Typography variant="body-md" className="font-bold truncate text-white hover:underline cursor-pointer">
          {isEmpty ? "Nothing playing" : trackName}
        </Typography>
        {artistName && !isEmpty && (
          <Typography variant="body-sm" color="secondary" className="truncate hover:underline cursor-pointer hover:text-white">
            {artistName}
          </Typography>
        )}
      </div>
    </div>
  );
};