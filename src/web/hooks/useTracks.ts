import { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";

import { musicService, Track } from '../services/MusicService';
import { spotifyService } from '../services/SpotifyService';
export type { Track };

export function useTracks() {
  const [userTracks, setUserTracks] = useState<Track[]>([]);
  const [communityTracks, setCommunityTracks] = useState<Track[]>([]);
  const [spotifyTracks, setSpotifyTracks] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchTracks() {
      try {
        const discover = await musicService.getDiscoverTracks();
        const library = await musicService.getLibraryTracks();
        const spotify = await spotifyService.getLibraryTracks();
        
        setCommunityTracks(discover);
        setUserTracks(library);
        setSpotifyTracks(spotify);
      } catch (err) {
        logger.error('Failed to fetch tracks:', err);
      } finally {
        setLoading(false);
      }
    }

    fetchTracks();
  }, []);

  return { userTracks, communityTracks, spotifyTracks, loading };
}
