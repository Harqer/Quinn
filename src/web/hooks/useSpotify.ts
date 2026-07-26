import { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";

export interface SpotifyPlaylist {
  id: string;
  name: string;
  images: { url: string }[];
  owner: { display_name: string };
}

const apiFetch = async (endpoint: string, options: RequestInit = {}) => {
  const user = getAuth().currentUser;
  if (!user) throw new Error('Unauthenticated');
  const token = await user.getIdToken();
  const baseUrl = import.meta.env.VITE_API_URL || '';
  return fetch(`${baseUrl}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...options.headers
    }
  });
};

export function useSpotify() {
  const [playlists, setPlaylists] = useState<SpotifyPlaylist[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(true);

  const checkStatus = async () => {
    try {
      const res = await apiFetch('/api/spotify/status');
      if (res.ok) {
        const data = await res.json();
        setIsConnected(data.connected);
        if (data.connected) fetchPlaylists();
        else setLoading(false);
      }
    } catch (err) {
      logger.error('Failed to check Spotify status', err);
      setLoading(false);
    }
  };

  const fetchPlaylists = async () => {
    try {
      const res = await apiFetch('/api/spotify/playlists');
      if (res.ok) {
        const data = await res.json();
        setPlaylists(data.items || []);
      }
    } catch (err) {
      logger.error('Failed to fetch Spotify playlists', err);
    } finally {
      setLoading(false);
    }
  };

  const connectSpotify = async () => {
    try {
      const res = await apiFetch('/api/spotify/auth-url');
      if (res.ok) {
        const data = await res.json();
        window.open(data.url, 'Spotify Auth', 'width=500,height=600');
      }
    } catch (err) {
      logger.error('Failed to get Spotify auth url', err);
    }
  };

  const getLibraryTracks = async () => {
    try {
      const res = await apiFetch('/api/spotify/library');
      if (!res.ok) throw new Error('Failed to fetch spotify library tracks');
      return (await res.json()).items || [];
    } catch (err) {
      console.error(err);
      return [];
    }
  };

  const addTrackToPlaylist = async (trackUri: string, type: 'music' | 'podcast' | 'audiobook' = 'music') => {
    try {
      const res = await apiFetch('/api/spotify/playlist/add', {
        method: 'POST',
        body: JSON.stringify({ trackUri, type })
      });
      return res.ok;
    } catch (err) {
      console.error(err);
      return false;
    }
  };

  useEffect(() => {
    checkStatus();
    const handleMessage = (event: MessageEvent) => {
      if (event.data?.type === 'OAUTH_AUTH_SUCCESS') checkStatus();
    };
    window.addEventListener('message', handleMessage);
    return () => window.removeEventListener('message', handleMessage);
  }, []);

  return { playlists, isConnected, loading, connectSpotify, getLibraryTracks, addTrackToPlaylist };
}
