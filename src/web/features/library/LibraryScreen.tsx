import React from 'react';
import { Typography } from '../../components/atoms/Typography';
import { useNavigate } from '../../App';
import { useAppContext } from '../../contexts/AppContext';
import { logger } from '../../lib/logger';
import { Icon } from '../../components/atoms/Icon';
import { TrackListItem } from '../../components/molecules/TrackListItem';
import { useTracks } from '../../hooks/useTracks';
import { getAuth } from 'firebase/auth';
import { EmptyState } from '../../components/molecules/EmptyState';

export const LibraryScreen: React.FC = () => {
  const { userTracks, communityTracks, spotifyTracks, loading } = useTracks();
  const auth = getAuth();
  const user = auth.currentUser;
  const userInitial = user?.displayName ? user.displayName[0].toUpperCase() : (user?.email ? user.email[0].toUpperCase() : 'M');
  const navigate = useNavigate();
  const { setActiveAlbumId } = useAppContext();

  const displayTracks = userTracks;

  const handleTrackClick = (id: string) => {
    logger.info('User navigating to track/album from library', { id });
    setActiveAlbumId(id);
    navigate('album');
  };

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="flex items-center gap-4 px-4 pt-12 pb-4 sticky top-0 bg-background/90 backdrop-blur-md z-10 border-b border-surface-container">
        <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-xs font-bold text-black overflow-hidden shadow-inner">
           {userInitial}
        </div>
        <Typography variant="headline" className="font-bold tracking-tight flex-1">Your Library</Typography>
        <div className="flex gap-4 text-white">
          <Icon name="search" size="xl" className="cursor-pointer" onClick={() => navigate('search')} />
        </div>
      </div>
      <div className="px-4 py-2 flex items-center justify-between text-white mb-2">
        <div className="flex items-center gap-2">
           <Icon name="swap_vert" size="md" />
           <Typography variant="label-md" className="font-bold">Recently played</Typography>
        </div>
        <Icon name="format_list_bulleted" size="lg" color="secondary" />
      </div>

      {loading ? (
        <div className="p-8 text-center">
          <Typography variant="body-md">Loading Library...</Typography>
        </div>
      ) : displayTracks.length > 0 ? (
        <div className="flex flex-col px-0 gap-0">
          <Typography variant="label-md" className="px-4 py-2 text-primary font-bold">Mave Studio Tracks</Typography>
          {displayTracks.map(track => (
            <div key={track.id} onClick={() => handleTrackClick(track.id)}>
              <TrackListItem 
                title={track.title} 
                artist={track.artist || 'Unknown Artist'} 
                rightElement={<span />}
              />
            </div>
          ))}

          {spotifyTracks && spotifyTracks.length > 0 && (
            <>
              <Typography variant="label-md" className="px-4 py-2 mt-4 text-[#1DB954] font-bold">Spotify Top Tracks</Typography>
              {spotifyTracks.map((item: any) => (
                <div key={item.id}>
                  <TrackListItem 
                    title={item.name} 
                    artist={item.artists?.[0]?.name || 'Unknown Artist'} 
                    rightElement={<Icon name="open_in_new" size="sm" className="opacity-50" />}
                  />
                </div>
              ))}
            </>
          )}
        </div>
      ) : spotifyTracks.length > 0 ? (
        <div className="flex flex-col px-0 gap-0">
          <Typography variant="label-md" className="px-4 py-2 mt-4 text-[#1DB954] font-bold">Spotify Top Tracks</Typography>
          {spotifyTracks.map((item: any) => (
            <div key={item.id}>
              <TrackListItem 
                title={item.name} 
                artist={item.artists?.[0]?.name || 'Unknown Artist'} 
                rightElement={<Icon name="open_in_new" size="sm" className="opacity-50" />}
              />
            </div>
          ))}
        </div>
      ) : (
        <div className="flex-1">
          <EmptyState 
            icon="library_music" 
            title="No saved vibes yet" 
            description="Create your first vibe in the Studio and it will appear here." 
          />
        </div>
      )}
    </div>
  );
};

const FilterPill = ({ icon, title, onClick }: { icon: string, title: string, onClick?: () => void }) => (
  <button onClick={onClick} className="bg-surface-container rounded-full px-4 py-1.5 text-text-primary text-[11px] tracking-wide font-medium flex items-center justify-center hover:bg-surface transition-colors" title={title}>
    <Icon name={icon} size="sm" />
  </button>
);
