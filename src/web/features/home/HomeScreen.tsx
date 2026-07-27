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

export const HomeScreen: React.FC = () => {
  const { communityTracks, userTracks, loading, error, retry } = useTracks();
  const auth = getAuth();
  const user = auth.currentUser;
  const userInitial = user?.displayName ? user.displayName[0].toUpperCase() : (user?.email ? user.email[0].toUpperCase() : 'M');
  const navigate = useNavigate();
  const { setActiveAlbumId } = useAppContext();

  const handleTrackClick = (id: string) => {
    logger.info('User navigating to track/album', { id });
    setActiveAlbumId(id);
    navigate('album');
  };

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="flex flex-col gap-4 md:gap-6 px-4 md:px-6 pt-12 md:pt-6 pb-2 sticky top-0 bg-background/90 backdrop-blur-md z-10">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-xs font-bold text-black overflow-hidden shadow-inner flex-shrink-0">
            {userInitial}
          </div>
          <Typography variant="headline" className="font-bold tracking-tight flex-1">
            Good {new Date().getHours() < 12 ? 'morning' : new Date().getHours() < 18 ? 'afternoon' : 'evening'}, {user?.displayName?.split(' ')[0] || 'User'}
          </Typography>
        </div>
      </div>
      
      {loading ? (
        <div className="flex-1 flex items-center justify-center">
           <Typography variant="body-lg">Loading Studio...</Typography>
        </div>
      ) : error ? (
        <div className="p-4">
          <ErrorAlert message={error} onRetry={retry} />
        </div>
      ) : (
        <>
          <div className="px-4 md:px-6 py-2">
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-2 md:gap-6">
              {(userTracks.length > 0 ? userTracks : communityTracks).slice(0, 6).map(track => (
                <div key={`recent-${track.id}`} className="bg-surface-container hover:bg-surface rounded-[4px] flex items-center gap-2 overflow-hidden cursor-pointer transition-colors" onClick={() => handleTrackClick(track.id)}>
                  {track.albumArtUrl ? (
                    <img src={track.albumArtUrl} alt={track.title} className="w-14 h-14 object-cover" />
                  ) : (
                    <Shimmer className="w-14 h-14" />
                  )}
                  <Typography variant="label-md" className="font-bold line-clamp-2 pr-2 leading-tight">
                    {track.title}
                  </Typography>
                </div>
              ))}
            </div>
          </div>
          
          <Carousel title="Made for you">
            {(userTracks.length > 0 ? userTracks : communityTracks).slice(0, 5).map(track => (
              <div 
                key={track.id}
                className="flex flex-col gap-2 w-[120px] flex-shrink-0 cursor-pointer group" 
                onClick={() => handleTrackClick(track.id)}
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
          
          <Carousel title="Community Vibes">
            {communityTracks.map(track => (
              <div 
                key={track.id}
                className="flex flex-col gap-2 w-[120px] flex-shrink-0 cursor-pointer group" 
                onClick={() => handleTrackClick(track.id)}
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
            <Carousel title="Recently played">
              {userTracks.map(track => (
                <div 
                  key={track.id}
                  className="flex flex-col gap-2 w-[120px] flex-shrink-0 cursor-pointer group" 
                  onClick={() => handleTrackClick(track.id)}
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
