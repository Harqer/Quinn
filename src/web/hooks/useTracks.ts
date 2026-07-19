import { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';

export interface Track {
  id: string;
  title: string;
  artist: string;
  albumArtUrl?: string;
  isExplicit?: boolean;
}

export function useTracks() {
  const [userTracks, setUserTracks] = useState<Track[]>([]);
  const [communityTracks, setCommunityTracks] = useState<Track[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchTracks() {
      try {
        const auth = getAuth();
        const user = auth.currentUser;
        
        // Fetch Community Tracks (No auth required)
        const commRes = await fetch('/api/music/community/tracks');
        if (commRes.ok) {
          const data = await commRes.json();
          setCommunityTracks(data.tracks || []);
        }

        // Fetch User Tracks (Auth required)
        if (user) {
          const token = await user.getIdToken();
          const userRes = await fetch('/api/music/user/tracks', {
            headers: { 'Authorization': `Bearer ${token}` }
          });
          if (userRes.ok) {
            const data = await userRes.json();
            setUserTracks(data.tracks || []);
          }
        }
      } catch (err) {
        console.error('Failed to fetch tracks:', err);
      } finally {
        setLoading(false);
      }
    }

    fetchTracks();
  }, []);

  return { userTracks, communityTracks, loading };
}
