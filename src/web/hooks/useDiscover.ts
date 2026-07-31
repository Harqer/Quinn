import { useState, useEffect } from 'react';
import { musicService, Track, Category, Playlist } from '../services/MusicService';
import { logger } from '../lib/logger';

export const useDiscover = () => {
  const [tracks, setTracks] = useState<Track[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [playlists, setPlaylists] = useState<Playlist[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDiscover = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const [fetchedTracks, fetchedCategories, fetchedPlaylists] = await Promise.all([
        musicService.getDiscoverTracks(),
        musicService.getCategories(),
        musicService.getPlaylists()
      ]);

      setTracks(fetchedTracks);
      setCategories(fetchedCategories);
      setPlaylists(fetchedPlaylists);
    } catch (err: any) {
      logger.error('Failed to load discover data', { error: err });
      setError(err.message || 'Failed to load discover data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDiscover();
  }, []);

  return { tracks, categories, playlists, loading, error, retry: fetchDiscover };
};
