import React, { useState, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { TrackListItem } from '../../components/molecules/TrackListItem';
import { EmptyState } from '../../components/molecules/EmptyState';
import { ErrorAlert } from '../../components/molecules/ErrorAlert';
import { TrackListSkeleton } from '../../components/molecules/TrackListSkeleton';
import { useAppContext } from '../../contexts/AppContext';
import { getAuth } from 'firebase/auth';
import { logger } from "../../lib/logger";
import { musicService, Track } from '../../services/MusicService';
import { usePlayerContext } from '../../contexts/PlayerContext';

export const PlaylistViewScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const { activePlaylistId: id } = useAppContext();
  const [tracks, setTracks] = useState<Track[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { playQueue } = usePlayerContext();

  const fetchPlaylist = () => {
    if (id) {
      setLoading(true);
      setError(null);
      musicService.getPlaylistTracks(id)
        .then(setTracks)
        .catch(err => {
          logger.error("Failed to fetch playlist tracks", err);
          setError("Unable to load playlist tracks. Please check your connection.");
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlaylist();
  }, [id]);

  const handleAction = async (action: 'like' | 'share', targetId: string) => {
    if ('vibrate' in navigator) navigator.vibrate([50, 50, 50]);
    try {
      if (action === 'share') {
        const url = `${window.location.origin}/playlist/${targetId}`;
        if (navigator.share) {
          navigator.share({
            title: 'Mave Playlist',
            text: 'Check out this playlist on Mave!',
            url: url
          }).catch(console.error);
        } else {
          navigator.clipboard.writeText(url);
          window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Share link copied to clipboard!' }));
        }
      }
    } catch (err) {
      window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Failed to complete action' }));
    }
  };
  
  if (!id) {
    return (
      <div className="flex flex-col h-full w-full bg-background">
        <div className="pt-12 px-4 sticky top-0 z-10">
          <button onClick={onBack} className="drop-shadow-md p-2 -ml-2 text-white">
            <Icon name="chevron_left" size="3xl" />
          </button>
        </div>
        <EmptyState 
          icon="queue_music" 
          title="Playlist not found" 
          description="We couldn't find the playlist you were looking for." 
          action={{ label: "Go Back", onClick: onBack }}
        />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
       <div className="relative w-full h-64 bg-surface-container shrink-0">
          {/* Dynamic Playlist Cover */}
          {tracks.length > 0 && tracks[0].albumArtUrl ? (
            <img src={tracks[0].albumArtUrl} alt="Playlist Cover" className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center bg-surface">
              <Icon name="queue_music" size="3xl" />
            </div>
          )}
          
          {/* Top Nav Overlay */}
          <div className="absolute top-0 left-0 right-0 pt-12 px-4 bg-gradient-to-b from-black/60 to-transparent z-10 pb-4">
            <button onClick={onBack} className="drop-shadow-md p-2 -ml-2 text-white bg-black/20 rounded-full hover:bg-black/40 transition-colors">
              <Icon name="chevron_left" size="3xl" />
            </button>
          </div>
          
          {/* Overlay Actions at Bottom */}
          <div className="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-black/80 to-transparent flex justify-end gap-3 items-center z-10">
             <button onClick={() => handleAction('share', id)} className="p-2.5 bg-black/40 backdrop-blur-md rounded-full hover:bg-black/60 transition-colors text-white shadow-lg" title="Share">
                <Icon name="share" size="md" />
             </button>
          </div>
       </div>
       <div className="flex-1 flex flex-col px-4 pt-4">
         {loading ? (
            <div className="w-full">
               <TrackListSkeleton count={6} />
            </div>
         ) : error ? (
            <ErrorAlert message={error} onRetry={fetchPlaylist} />
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
              icon="queue_music" 
              title="Playlist empty" 
              description="There are no tracks in this playlist." 
            />
         )}
       </div>
    </div>
  );
};
