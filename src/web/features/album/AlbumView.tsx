import React, { useState, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { TrackListItem } from '../../components/molecules/TrackListItem';
import { EmptyState } from '../../components/molecules/EmptyState';
import { useAppContext } from '../../contexts/AppContext';
import { getAuth } from 'firebase/auth';
import { logger } from "../../lib/logger";
import { musicService, Track } from '../../services/MusicService';
import { usePlayerContext } from '../../contexts/PlayerContext';

export const AlbumView: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const { activeAlbumId: id } = useAppContext();
  const [tracks, setTracks] = useState<Track[]>([]);
  const [loading, setLoading] = useState(true);
  const [showOptions, setShowOptions] = useState(false);
  const { playTrack } = usePlayerContext();

  useEffect(() => {
    if (id) {
      setLoading(true);
      musicService.getAlbumTracks(id)
        .then(setTracks)
        .catch(err => logger.error("Failed to fetch album tracks", err))
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [id]);

  const handleAction = async (action: 'like' | 'share' | 'options', targetId: string) => {
    if ('vibrate' in navigator) navigator.vibrate([50, 50, 50]);
    try {
      if (action === 'like') {
        const auth = getAuth();
        const user = auth.currentUser;
        const token = user ? await user.getIdToken() : '';
        const baseUrl = import.meta.env.VITE_API_URL || '';
        await fetch(`${baseUrl}/api/spotify/music/save`, {
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {})
          },
          body: JSON.stringify({ id: targetId, type: 'album' })
        });
        window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Album saved to library!' }));
      } else if (action === 'share') {
        const url = `${window.location.origin}/album/${targetId}`;
        if (navigator.share) {
          navigator.share({
            title: 'Mave Album',
            text: 'Check out this album on Mave!',
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
          icon="album" 
          title="Album not found" 
          description="We couldn't find the album you were looking for." 
          action={{ label: "Go Back", onClick: onBack }}
        />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
       <div className="relative w-full h-64 bg-surface-container shrink-0">
          {/* Dynamic Album Cover */}
          {tracks.length > 0 && tracks[0].albumArtUrl ? (
            <img src={tracks[0].albumArtUrl} alt="Album Cover" className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center bg-surface">
              <Icon name="album" size="3xl" />
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
             <button onClick={() => handleAction('like', id)} className="p-2.5 bg-black/40 backdrop-blur-md rounded-full hover:bg-black/60 transition-colors text-white shadow-lg" title="Like">
                <Icon name="favorite_border" size="md" />
             </button>
             <button onClick={() => handleAction('share', id)} className="p-2.5 bg-black/40 backdrop-blur-md rounded-full hover:bg-black/60 transition-colors text-white shadow-lg" title="Share">
                <Icon name="share" size="md" />
             </button>

          </div>
       </div>
       <div className="flex-1 flex flex-col px-4 pt-4" onClick={() => setShowOptions(false)}>
         {loading ? (
            <div className="flex-1 flex justify-center items-center">
               <Typography variant="body-md">Loading album...</Typography>
            </div>
         ) : tracks.length > 0 ? (
            tracks.map(track => (
              <div key={track.id} onClick={() => playTrack(track)}>
                <TrackListItem 
                  title={track.title} 
                  artist={track.artist || 'Unknown Artist'} 
                />
              </div>
            ))
         ) : (
            <EmptyState 
              icon="album" 
              title="Album empty" 
              description="There are no tracks in this album." 
            />
         )}
       </div>
    </div>
  );
};
