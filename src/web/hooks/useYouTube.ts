import { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';
import { logger } from "../lib/logger";

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

export function useYouTube() {
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(true);

  const checkStatus = async () => {
    try {
      const res = await apiFetch('/api/youtube/status');
      if (res.ok) {
        const data = await res.json();
        setIsConnected(data.connected);
      }
    } catch (err) {
      logger.error('Failed to check YouTube status', err);
    } finally {
      setLoading(false);
    }
  };

  const connectYouTube = async () => {
    try {
      const res = await apiFetch('/api/youtube/auth-url');
      if (res.ok) {
        const data = await res.json();
        window.open(data.url, 'YouTube Auth', 'width=500,height=600');
      }
    } catch (err) {
      logger.error('Failed to get YouTube auth url', err);
    }
  };

  useEffect(() => {
    checkStatus();
    const handleMessage = (event: MessageEvent) => {
      if (event.data?.type === 'YOUTUBE_OAUTH_SUCCESS') checkStatus();
    };
    window.addEventListener('message', handleMessage);
    return () => window.removeEventListener('message', handleMessage);
  }, []);

  return { isConnected, loading, connectYouTube };
}
