import { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";

export interface SpotifyPlaylist {
  id: string;
  name: string;
  images: { url: string }[];
  owner: { display_name: string };
}

export function useSpotify() {
  const [playlists, setPlaylists] = useState<SpotifyPlaylist[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(true);

  const checkStatus = async () => {
    try {
      const auth = getAuth();
      const user = auth.currentUser;
      if (!user) return;
      
      const token = await user.getIdToken();
      const baseUrl = import.meta.env.VITE_API_URL || '';
      const res = await fetch(`${baseUrl}/api/spotify/status`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setIsConnected(data.connected);
        if (data.connected) {
          fetchPlaylists(token);
        } else {
          setLoading(false);
        }
      }
    } catch (err) {
      logger.error('Failed to check Spotify status', err);
      setLoading(false);
    }
  };

  const fetchPlaylists = async (token: string) => {
    try {
      const baseUrl = import.meta.env.VITE_API_URL || '';
      const res = await fetch(`${baseUrl}/api/spotify/playlists`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
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
      const auth = getAuth();
      const user = auth.currentUser;
      if (!user) return;
      
      const token = await user.getIdToken();
      const baseUrl = import.meta.env.VITE_API_URL || '';
      const res = await fetch(`${baseUrl}/api/spotify/auth-url`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      
      if (res.ok) {
        const data = await res.json();
        window.open(data.url, 'Spotify Auth', 'width=500,height=600');
      }
    } catch (err) {
      logger.error('Failed to get Spotify auth url', err);
    }
  };

  useEffect(() => {
    checkStatus();

    const handleMessage = (event: MessageEvent) => {
      if (event.data?.type === 'OAUTH_AUTH_SUCCESS') {
        checkStatus();
      }
    };
    window.addEventListener('message', handleMessage);
    return () => window.removeEventListener('message', handleMessage);
  }, []);

  return { playlists, isConnected, loading, connectSpotify };
}
