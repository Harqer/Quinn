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
import { ErrorAlert } from '../../components/molecules/ErrorAlert';
import { Shimmer } from '../../components/atoms/Shimmer';

export const LibraryScreen: React.FC = () => {
  const { userTracks, communityTracks, spotifyTracks, loading, error, retry } = useTracks();
  const auth = getAuth();
  const user = auth.currentUser;
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
           {user?.photoURL ? (
             <img src={user.photoURL} alt="Profile" className="w-full h-full object-cover" />
           ) : (
             <Icon name="account_circle" size="md" />
           )}
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
        <div className="flex flex-col px-0 gap-0">
          <Shimmer className="h-5 w-40 mx-4 my-2 rounded" />
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="flex items-center justify-between py-2 px-4">
              <div className="flex items-center gap-3 w-full">
                <Shimmer className="w-12 h-12 rounded flex-shrink-0" />
                <div className="flex flex-col gap-2 w-full max-w-[200px]">
                  <Shimmer className="h-4 w-full rounded" />
                  <Shimmer className="h-3 w-2/3 rounded" />
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : error ? (
        <ErrorAlert message={error} onRetry={retry} />
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
            title="No saved songs yet" 
            description="Create your first song in the Studio and it will appear here." 
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
