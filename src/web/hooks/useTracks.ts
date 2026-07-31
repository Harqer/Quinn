import { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";

import { musicService, Track } from '../services/MusicService';
import { useSpotify } from './useSpotify';
export type { Track };

export function useTracks() {
  const [userTracks, setUserTracks] = useState<Track[]>([]);
  const [communityTracks, setCommunityTracks] = useState<Track[]>([]);
  const [spotifyTracks, setSpotifyTracks] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { getLibraryTracks } = useSpotify();

  useEffect(() => {
    async function fetchTracks(retryCount = 0) {
      try {
        if (retryCount === 0) setLoading(true);
        setError(null);
        
        const discover = await musicService.getDiscoverTracks();
        const library = await musicService.getLibraryTracks();
        const spotify = await getLibraryTracks();
        
        setCommunityTracks(discover);
        setUserTracks(library);
        setSpotifyTracks(spotify);
        setLoading(false);
      } catch (err) {
        logger.error('Failed to fetch tracks:', err);
        if (retryCount < 3) {
          const backoffMs = Math.pow(2, retryCount) * 1000;
          setTimeout(() => fetchTracks(retryCount + 1), backoffMs);
        } else {
          setError('Unable to load your tracks right now. Please check your connection.');
          setLoading(false);
        }
      }
    }

    fetchTracks();
  }, []); // Note: getLibraryTracks not in dep array intentionally to avoid loop on remount, it's stable enough.

  const retry = () => {
    setLoading(true);
    setError(null);
    // Kick off another effect-like run
    async function fetchTracks(retryCount = 0) {
      try {
        const discover = await musicService.getDiscoverTracks();
        const library = await musicService.getLibraryTracks();
        const spotify = await getLibraryTracks();
        
        setCommunityTracks(discover);
        setUserTracks(library);
        setSpotifyTracks(spotify);
        setLoading(false);
      } catch (err) {
        logger.error('Failed to fetch tracks on retry:', err);
        if (retryCount < 3) {
          const backoffMs = Math.pow(2, retryCount) * 1000;
          setTimeout(() => fetchTracks(retryCount + 1), backoffMs);
        } else {
          setError('Unable to load your tracks right now. Please check your connection.');
          setLoading(false);
        }
      }
    }
    fetchTracks(0);
  };

  return { userTracks, communityTracks, spotifyTracks, loading, error, retry };
}
