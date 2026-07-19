import React from 'react';
import { TrackListItem, TrackListItemProps } from '../molecules/TrackListItem';

export interface TrackListProps {
  tracks: Array<TrackListItemProps & { id: string }>;
  onTrackClick?: (id: string) => void;
  className?: string;
}

export const TrackList: React.FC<TrackListProps> = ({
  tracks,
  onTrackClick,
  className = ''
}) => {
  return (
    <div className={`flex flex-col w-full ${className}`}>
      {tracks.map((track) => (
        <TrackListItem
          key={track.id}
          {...track}
          onClick={() => onTrackClick?.(track.id)}
        />
      ))}
    </div>
  );
};
