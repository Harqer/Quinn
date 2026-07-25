import React, { useState, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { getAuth } from 'firebase/auth';
import { logger } from '../../lib/logger';
import { musicService, Category, Track } from '../../services/MusicService';
import { usePlayerContext } from '../../contexts/PlayerContext';
import { TrackListItem } from '../../components/molecules/TrackListItem';

export const SearchScreen: React.FC = () => {
  const auth = getAuth();
  const user = auth.currentUser;
  const userInitial = user?.displayName ? user.displayName[0].toUpperCase() : (user?.email ? user.email[0].toUpperCase() : 'M');

  const [categories, setCategories] = useState<Category[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Track[]>([]);
  const { playTrack } = usePlayerContext();

  useEffect(() => {
    musicService.getCategories().then(setCategories).catch(err => {
      logger.error('Error fetching categories from MusicService', err);
    });
  }, []);

  useEffect(() => {
    if (searchQuery.trim().length > 0) {
      musicService.search(searchQuery).then(setSearchResults);
    } else {
      setSearchResults([]);
    }
  }, [searchQuery]);

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="px-4 pt-12 pb-2 sticky top-0 bg-background/90 backdrop-blur-md z-10 flex items-center gap-3">
        <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-xs font-bold text-black overflow-hidden shadow-inner flex-shrink-0">
          {userInitial}
        </div>
        <Typography variant="title-lg" className="font-bold flex-1">Search</Typography>
      </div>

      <div className="px-4 sticky top-[72px] bg-background/90 backdrop-blur-md z-10 pb-4 border-b border-surface-container">
        <div className="bg-white rounded-[4px] flex items-center p-2.5 gap-2">

          <div className="w-[1px] h-5 bg-black/20 mx-1"></div>
          <Icon name="search" size="md" className="text-black" />
          <input 
            type="text" 
            placeholder="What do you want to listen to?" 
            className="flex-1 bg-transparent border-none outline-none text-black font-medium placeholder-gray-500"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="px-4 py-4">
        {searchQuery.trim().length > 0 ? (
          <div>
            <Typography variant="title-md" className="font-bold mb-4">Top results</Typography>
            {searchResults.length > 0 ? (
              <div className="flex flex-col gap-2">
                {searchResults.map(track => (
                  <div key={track.id} onClick={() => playTrack(track)}>
                    <TrackListItem title={track.title} artist={track.artist} />
                  </div>
                ))}
              </div>
            ) : (
              <Typography variant="body-md" className="text-text-secondary">No results found for "{searchQuery}"</Typography>
            )}
          </div>
        ) : (
          <>
            <Typography variant="title-md" className="font-bold mb-4">Browse all</Typography>
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 xl:grid-cols-6 gap-4">
              {categories.map((cat) => (
                <div 
                  key={cat.id} 
                  style={{ backgroundColor: cat.colorHex }}
                  className={`aspect-[1.5] rounded-[4px] p-3 relative overflow-hidden shadow-md cursor-pointer`}
                  onClick={() => {
                    logger.trackEvent('category_click', { id: cat.id });
                    setSearchQuery(cat.title);
                  }}
                >
                  <Typography variant="title-md" className={`font-bold text-white z-10 relative break-words`}>
                    {cat.title}
                  </Typography>
                  <div className="absolute -bottom-2 -right-4 w-16 h-16 bg-black/20 rounded-[4px] transform rotate-[25deg] shadow-lg"></div>
                </div>
              ))}
              {categories.length === 0 && (
                 <div className="text-sm text-text-secondary italic col-span-2">Loading categories...</div>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
};
