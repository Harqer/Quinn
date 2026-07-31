import React from 'react';
import { Carousel } from '../../components/organisms/Carousel';
import { useNavigate } from '../../App';
import { useAppContext } from '../../contexts/AppContext';
import { logger } from '../../lib/logger';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { useTracks } from '../../hooks/useTracks';
import { getAuth } from 'firebase/auth';
import { Shimmer } from '../../components/atoms/Shimmer';
import maveLogoDark from '../../assets/mave_brand_dark.png';
import { ErrorAlert } from '../../components/molecules/ErrorAlert';
import { usePlayerContext } from '../../contexts/PlayerContext';

export const HomeScreen: React.FC = () => {
  const { communityTracks, userTracks, loading, error, retry } = useTracks();
  const auth = getAuth();
  const user = auth.currentUser;
  const navigate = useNavigate();
  const { setActiveAlbumId } = useAppContext();
  const { playQueue } = usePlayerContext();

  const handleTrackClick = (trackList: typeof communityTracks, index: number, id: string) => {
    logger.info('User playing track from home', { id });
    if (trackList[index]?.audioUrl) {
      playQueue(trackList, index);
    } else {
      // Fall back to album view for tracks without a direct audio URL
      setActiveAlbumId(id);
      navigate('album');
    }
  };

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="flex flex-col gap-4 md:gap-6 px-4 md:px-6 pt-12 md:pt-6 pb-2 sticky top-0 bg-background/90 backdrop-blur-md z-10">
        <div className="flex items-center gap-3">
          <div 
            onClick={() => navigate('profile')}
            className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-xs font-bold text-black overflow-hidden shadow-inner flex-shrink-0 cursor-pointer hover:opacity-80 transition-opacity"
          >
            {user?.photoURL ? (
              <img src={user.photoURL} alt="Profile" className="w-full h-full object-cover" />
            ) : (
              <Icon name="account_circle" size="md" />
            )}
          </div>
          <Typography variant="headline" className="font-bold tracking-tight flex-1">
            Good {new Date().getHours() < 12 ? 'morning' : new Date().getHours() < 18 ? 'afternoon' : 'evening'}{user?.displayName ? `, ${user.displayName.split(' ')[0]}` : ''}
          </Typography>
        </div>
      </div>
      
      {loading ? (
        <div className="flex flex-col gap-6 w-full">
          <div className="px-4 md:px-6 py-2">
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-2 md:gap-6">
              {Array.from({ length: 6 }).map((_, i) => (
                <div key={`skeleton-recent-${i}`} className="bg-surface-container rounded-[4px] flex items-center gap-2 overflow-hidden h-[56px]">
                  <Shimmer className="w-14 h-14 flex-shrink-0" />
                  <Shimmer className="h-4 w-20 ml-2 rounded" />
                </div>
              ))}
            </div>
          </div>
          
          <div className="px-4 md:px-6">
            <Shimmer className="h-6 w-32 mb-4 rounded" />
            <div className="flex gap-4 overflow-hidden">
              {Array.from({ length: 5 }).map((_, i) => (
                <div key={`skeleton-carousel-${i}`} className="flex flex-col gap-2 w-[120px] flex-shrink-0">
                  <Shimmer className="w-full aspect-square rounded-[4px]" />
                  <Shimmer className="h-4 w-3/4 rounded mt-1" />
                  <Shimmer className="h-3 w-1/2 rounded" />
                </div>
              ))}
            </div>
          </div>
        </div>
      ) : error ? (
        <div className="p-4">
          <ErrorAlert message={error} onRetry={retry} />
        </div>
      ) : (
        <>
          <div className="px-4 md:px-6 py-2">
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-2 md:gap-6">
              {(userTracks.length > 0 ? userTracks : communityTracks).slice(0, 6).map((track, index) => {
                const list = userTracks.length > 0 ? userTracks : communityTracks;
                return (
                  <div key={`recent-${track.id}`} className="bg-surface-container hover:bg-surface rounded-[4px] flex items-center gap-2 overflow-hidden cursor-pointer transition-colors" onClick={() => handleTrackClick(list, index, track.id)}>
                    {track.albumArtUrl ? (
                      <img src={track.albumArtUrl} alt={track.title} className="w-14 h-14 object-cover" />
                    ) : (
                      <Shimmer className="w-14 h-14" />
                    )}
                    <Typography variant="label-md" className="font-bold line-clamp-2 pr-2 leading-tight">
                      {track.title}
                    </Typography>
                  </div>
                );
              })}
            </div>
          </div>
          
          <Carousel title="Made for you" seeAllAction={() => navigate('library')}>
            {(userTracks.length > 0 ? userTracks : communityTracks).slice(0, 5).map((track, index) => {
              const list = userTracks.length > 0 ? userTracks : communityTracks;
              return (
                <div 
                  key={track.id}
                  className="flex flex-col gap-2 w-[120px] flex-shrink-0 cursor-pointer group" 
                  onClick={() => handleTrackClick(list, index, track.id)}
              >
                <div className="w-full aspect-square bg-surface-container flex items-center justify-center shadow-lg overflow-hidden relative rounded-[4px]">
                  {track.albumArtUrl ? (
                    <img src={track.albumArtUrl} alt={track.title} loading="lazy" className="w-full h-full object-cover" />
                  ) : (
                    <Shimmer className="w-full h-full" />
                  )}
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors pointer-events-none" />
                </div>
                <div className="flex flex-col">
                  <Typography variant="body-md" className="truncate font-bold">
                    {track.title}
                  </Typography>
                  {track.artist && (
                    <Typography variant="body-sm" color="secondary" className="truncate">
                      {track.artist}
                    </Typography>
                  )}
                </div>
              </div>
              );
            })}
          </Carousel>
          
          <Carousel title="Community Vibes" seeAllAction={() => navigate('library')}>
            {communityTracks.map((track, index) => (
              <div 
                key={track.id}
                className="flex flex-col gap-2 w-[120px] flex-shrink-0 cursor-pointer group" 
                onClick={() => handleTrackClick(communityTracks, index, track.id)}
              >
                <div className="w-full aspect-square bg-surface-container flex items-center justify-center shadow-lg overflow-hidden relative rounded-[4px]">
                  {track.albumArtUrl ? (
                    <img src={track.albumArtUrl} alt={track.title} loading="lazy" className="w-full h-full object-cover" />
                  ) : (
                    <Shimmer className="w-full h-full" />
                  )}
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors pointer-events-none" />
                </div>
                <div className="flex flex-col">
                  <Typography variant="body-md" className="truncate font-bold">
                    {track.title}
                  </Typography>
                  {track.artist && (
                    <Typography variant="body-sm" color="secondary" className="truncate">
                      {track.artist}
                    </Typography>
                  )}
                </div>
              </div>
            ))}
          </Carousel>

          {userTracks.length > 0 && (
            <Carousel title="Recently played" seeAllAction={() => navigate('library')}>
              {userTracks.map((track, index) => (
                <div 
                  key={track.id}
                  className="flex flex-col gap-2 w-[120px] flex-shrink-0 cursor-pointer group" 
                  onClick={() => handleTrackClick(userTracks, index, track.id)}
                >
                  <div className="w-full aspect-square bg-surface-container flex items-center justify-center shadow-lg overflow-hidden relative rounded-[4px]">
                    {track.albumArtUrl ? (
                      <img src={track.albumArtUrl} alt={track.title} loading="lazy" className="w-full h-full object-cover" />
                    ) : (
                      <Shimmer className="w-full h-full" />
                    )}
                    <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors pointer-events-none" />
                  </div>
                  <div className="flex flex-col">
                    <Typography variant="body-md" className="truncate font-bold">
                      {track.title}
                    </Typography>
                    <Typography variant="body-sm" color="secondary" className="truncate">
                      {track.artist || 'Unknown Artist'}
                    </Typography>
                  </div>
                </div>
              ))}
            </Carousel>
          )}
        </>
      )}
    </div>
  );
};
