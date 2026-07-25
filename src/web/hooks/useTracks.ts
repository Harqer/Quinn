import { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";

import { musicService, Track } from '../services/MusicService';
export type { Track };

export function useTracks() {
  const [userTracks, setUserTracks] = useState<Track[]>([]);
  const [communityTracks, setCommunityTracks] = useState<Track[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchTracks() {
      try {
        const discover = await musicService.getDiscoverTracks();
        const library = await musicService.getLibraryTracks();
        setCommunityTracks(discover);
        setUserTracks(library);
      } catch (err) {
        logger.error('Failed to fetch tracks:', err);
      } finally {
        setLoading(false);
      }
    }

    fetchTracks();
  }, []);

  return { userTracks, communityTracks, loading };
}
