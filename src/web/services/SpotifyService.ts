import { getAuth } from 'firebase/auth';

export const spotifyService = {
  get baseUrl() {
    return import.meta.env.VITE_API_URL || '';
  },

  async getHeaders(): Promise<HeadersInit> {
    const auth = getAuth();
    const user = auth.currentUser;
    const headers: Record<string, string> = {
      'Content-Type': 'application/json'
    };
    if (user) {
      try {
        const token = await user.getIdToken();
        headers['Authorization'] = `Bearer ${token}`;
      } catch (e) {
        console.warn('Failed to get auth token', e);
      }
    }
    return headers;
  },

  async getLibraryTracks(): Promise<any[]> {
    try {
      const headers = await this.getHeaders();
      const res = await fetch(`${this.baseUrl}/api/spotify/library`, { headers });
      if (!res.ok) throw new Error('Failed to fetch spotify library tracks');
      const data = await res.json();
      // Spotify returns an items array with tracks inside
      return data.items || [];
    } catch (err) {
      console.error(err);
      return [];
    }
  },

  async addTrackToPlaylist(trackUri: string, type: 'music' | 'podcast' | 'audiobook' = 'music'): Promise<boolean> {
    try {
      const headers = await this.getHeaders();
      const res = await fetch(`${this.baseUrl}/api/spotify/playlist/add`, {
        method: 'POST',
        headers,
        body: JSON.stringify({ trackUri, type })
      });
      return res.ok;
    } catch (err) {
      console.error(err);
      return false;
    }
  }
};
