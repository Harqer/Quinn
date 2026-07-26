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
  const { getLibraryTracks } = useSpotify();

  useEffect(() => {
    async function fetchTracks() {
      try {
        const discover = await musicService.getDiscoverTracks();
        const library = await musicService.getLibraryTracks();
        const spotify = await getLibraryTracks();
        
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
  }, []); // Note: getLibraryTracks not in dep array intentionally to avoid loop on remount, it's stable enough.

  return { userTracks, communityTracks, spotifyTracks, loading };
}
