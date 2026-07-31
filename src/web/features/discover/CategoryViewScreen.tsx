import React, { useState, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { TrackListItem } from '../../components/molecules/TrackListItem';
import { EmptyState } from '../../components/molecules/EmptyState';
import { ErrorAlert } from '../../components/molecules/ErrorAlert';
import { TrackListSkeleton } from '../../components/molecules/TrackListSkeleton';
import { useAppContext } from '../../contexts/AppContext';
import { logger } from "../../lib/logger";
import { musicService, Track } from '../../services/MusicService';
import { usePlayerContext } from '../../contexts/PlayerContext';

export const CategoryViewScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const { activeCategoryId: id } = useAppContext();
  const [tracks, setTracks] = useState<Track[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { playQueue } = usePlayerContext();

  const fetchCategory = () => {
    if (id) {
      setLoading(true);
      setError(null);
      musicService.getCategoryTracks(id)
        .then(setTracks)
        .catch(err => {
          logger.error("Failed to fetch category tracks", err);
          setError("Unable to load category tracks. Please check your connection.");
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategory();
  }, [id]);
  
  if (!id) {
    return (
      <div className="flex flex-col h-full w-full bg-background">
        <div className="pt-12 px-4 sticky top-0 z-10">
          <button onClick={onBack} className="drop-shadow-md p-2 -ml-2 text-white">
            <Icon name="chevron_left" size="3xl" />
          </button>
        </div>
        <EmptyState 
          icon="category" 
          title="Category not found" 
          description="We couldn't find the category you were looking for." 
          action={{ label: "Go Back", onClick: onBack }}
        />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
       <div className="relative w-full h-48 bg-surface-container shrink-0 flex items-center justify-center">
          <div className="absolute top-0 left-0 right-0 pt-12 px-4 z-10 pb-4">
            <button onClick={onBack} className="drop-shadow-md p-2 -ml-2 text-white bg-black/20 rounded-full hover:bg-black/40 transition-colors">
              <Icon name="chevron_left" size="3xl" />
            </button>
          </div>
          <Icon name="category" size="3xl" className="opacity-50" />
       </div>
       <div className="flex-1 flex flex-col px-4 pt-4">
         {loading ? (
            <div className="w-full">
               <TrackListSkeleton count={6} />
            </div>
         ) : error ? (
            <ErrorAlert message={error} onRetry={fetchCategory} />
         ) : tracks.length > 0 ? (
            tracks.map((track, index) => (
              <div key={track.id} onClick={() => playQueue(tracks, index)}>
                <TrackListItem 
                  title={track.title} 
                  artist={track.artist || 'Unknown Artist'} 
                />
              </div>
            ))
         ) : (
            <EmptyState 
              icon="music_note" 
              title="Category empty" 
              description="There are no tracks in this category yet." 
            />
         )}
       </div>
    </div>
  );
};
