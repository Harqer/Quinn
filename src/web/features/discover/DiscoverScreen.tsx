import React from 'react';
import { Carousel } from '../../components/organisms/Carousel';
import { useNavigate } from '../../App';
import { useAppContext } from '../../contexts/AppContext';
import { logger } from '../../lib/logger';
import { Typography } from '../../components/atoms/Typography';
import { Shimmer } from '../../components/atoms/Shimmer';
import { ErrorAlert } from '../../components/molecules/ErrorAlert';
import { useDiscover } from '../../hooks/useDiscover';

export const DiscoverScreen: React.FC = () => {
  const { tracks, categories, playlists, loading, error, retry } = useDiscover();
  const navigate = useNavigate();
  const { setActiveAlbumId, setActivePlaylistId, setActiveCategoryId } = useAppContext();

  const handleTrackClick = (id: string) => {
    logger.info('User navigating to track/album from discover', { id });
    setActiveAlbumId(id);
    navigate('album');
  };

  const handlePlaylistClick = (id: string) => {
    logger.info('User navigating to playlist', { id });
    setActivePlaylistId?.(id);
    navigate('playlist' as any);
  };

  const handleCategoryClick = (id: string) => {
    logger.info('User navigating to category', { id });
    setActiveCategoryId?.(id);
    navigate('category' as any);
  };

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="flex flex-col gap-4 md:gap-6 px-4 md:px-6 pt-12 md:pt-6 pb-2 sticky top-0 bg-background/90 backdrop-blur-md z-10">
        <Typography variant="headline" className="font-bold tracking-tight">
          Discover
        </Typography>
      </div>
      
      {loading ? (
        <div className="flex flex-col gap-6 w-full px-4 md:px-6">
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
      ) : error ? (
        <div className="p-4">
          <ErrorAlert message={error} onRetry={retry} />
        </div>
      ) : (
        <>
          {tracks.length > 0 && (
            <Carousel title="Fresh Releases">
              {tracks.slice(0, 10).map(track => (
                <div 
                  key={`fresh-${track.id}`}
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
          )}

          {playlists.length > 0 && (
            <Carousel title="Featured Playlists">
              {playlists.map(playlist => (
                <div 
                  key={`playlist-${playlist.id}`}
                  className="flex flex-col gap-2 w-[120px] flex-shrink-0 cursor-pointer group" 
                  onClick={() => handlePlaylistClick(playlist.id)}
                >
                  <div className="w-full aspect-square bg-surface-container flex items-center justify-center shadow-lg overflow-hidden relative rounded-[4px]">
                    {playlist.coverUrl ? (
                      <img src={playlist.coverUrl} alt={playlist.name} loading="lazy" className="w-full h-full object-cover" />
                    ) : (
                      <Shimmer className="w-full h-full" />
                    )}
                    <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors pointer-events-none" />
                  </div>
                  <div className="flex flex-col">
                    <Typography variant="body-md" className="truncate font-bold">
                      {playlist.name}
                    </Typography>
                    <Typography variant="body-sm" color="secondary" className="truncate">
                      {playlist.creator}
                    </Typography>
                  </div>
                </div>
              ))}
            </Carousel>
          )}

          {categories.length > 0 && (
            <div className="px-4 md:px-6 py-4">
              <Typography variant="title-md" className="font-bold mb-4">Browse Categories</Typography>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {categories.map(category => (
                  <div 
                    key={`cat-${category.id}`} 
                    className="relative aspect-video rounded-lg overflow-hidden cursor-pointer group shadow-sm hover:shadow-md transition-shadow"
                    onClick={() => handleCategoryClick(category.id)}
                  >
                    {category.imageUrl ? (
                      <img src={category.imageUrl} alt={category.title} className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
                    ) : (
                      <div className="w-full h-full bg-primary/20 flex items-center justify-center">
                        <Typography variant="title-md" className="font-bold">{category.title}</Typography>
                      </div>
                    )}
                    <div className="absolute inset-0 bg-black/30 flex items-end p-3 pointer-events-none">
                      <Typography variant="title-md" className="font-bold text-white drop-shadow-md">{category.title}</Typography>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};
