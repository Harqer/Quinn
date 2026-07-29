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
      }
    } catch (err) {
      logger.error('Failed to check Spotify status', err);
    } finally {
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

  // Add Spotify Web Playback SDK Initialization
  const [player, setPlayer] = useState<any>(null);
  const [deviceId, setDeviceId] = useState<string | null>(null);

  useEffect(() => {
    if (!isConnected) return;
    
    const script = document.createElement("script");
    script.src = "https://sdk.scdn.co/spotify-player.js";
    script.async = true;

    document.body.appendChild(script);

    (window as any).onSpotifyWebPlaybackSDKReady = async () => {
      // Get token from backend
      const res = await apiFetch('/api/spotify/token');
      const data = await res.json();
      const token = data.token;
      
      const spotifyPlayer = new (window as any).Spotify.Player({
        name: 'Mave Studio Web Player',
        getOAuthToken: (cb: any) => { cb(token); }, // Need actual token here for Premium playback
        volume: 0.5
      });

      setPlayer(spotifyPlayer);

      spotifyPlayer.addListener('ready', ({ device_id }: { device_id: string }) => {
        logger.info('Ready with Device ID', device_id);
        setDeviceId(device_id);
      });

      spotifyPlayer.connect();
    };

    return () => {
      script.remove();
      if (player) player.disconnect();
    };
  }, [isConnected]);

  useEffect(() => {
    checkStatus();
    const handleMessage = (event: MessageEvent) => {
      if (event.data?.type === 'OAUTH_AUTH_SUCCESS') checkStatus();
    };
    window.addEventListener('message', handleMessage);
    return () => window.removeEventListener('message', handleMessage);
  }, []);

  return { playlists, isConnected, loading, connectSpotify, getLibraryTracks, addTrackToPlaylist, player, deviceId };
}
