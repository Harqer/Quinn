import { getAuth } from 'firebase/auth';
import { getCommunityTracks, getUserTracks, getCategories, getPlaylists, getAlbums, getPodcasts, getAudiobooks, addTrackToPlaylist, createTrack, createPlaylist, getPlaylistTracks, getCategoryTracks, searchTracks, getAlbumTracks } from "../../lib/dataconnect/esm/index.esm.js";

export interface Track {
  id: string;
  title: string;
  artist: string;
  albumArtUrl?: string;
  audioUrl?: string;
  videoUrl?: string;
  createdAt?: string;
  duration?: number;
}

export interface Category {
  id: string;
  title: string;
  imageUrl?: string;
  type: string;
}

export interface Album {
  id: string;
  title: string;
  artist: string;
  imageUrl?: string;
  releaseYear?: number;
}

export interface Playlist {
  id: string;
  name: string;
  creator: string;
  coverUrl?: string;
}

class MusicService {
  async getDiscoverTracks(): Promise<Track[]> {
    const res = await getCommunityTracks();
    return res.data.tracks.map((t: any) => ({
      id: t.id,
      title: t.title,
      artist: t.album.primaryArtist.name,
      albumArtUrl: t.coverUrl || undefined,
      audioUrl: undefined,
      createdAt: t.createdAt
    }));
  }

  async getLibraryTracks(): Promise<Track[]> {
    const res = await getUserTracks();
    return res.data.tracks.map((t: any) => ({
      id: t.id,
      title: t.title,
      artist: t.album.primaryArtist.name,
      albumArtUrl: t.coverUrl || undefined,
      createdAt: t.createdAt
    }));
  }

  async search(query: string): Promise<Track[]> {
    const response = await searchTracks({ query: query });
    return response.data.tracks.map((t: any) => ({
      id: t.id,
      title: t.title,
      artist: t.album.primaryArtist.name,
      albumArtUrl: t.coverUrl || undefined,
      audioUrl: t.audioUrl,
      createdAt: t.createdAt
    }));
  }
  
  async getCategories(): Promise<Category[]> {
    const res = await getCategories();
    return res.data.categories.map((c: any) => ({
      id: c.id,
      title: c.name,
      imageUrl: c.imageUrl || undefined,
      type: c.type
    }));
  }
  
  async getAlbumTracks(albumId: string, albumName: string): Promise<Track[]> {
    const response = await getAlbumTracks({ albumId: albumId });
    return response.data.tracks.map((t: any) => ({
      id: t.id,
      title: t.title,
      artist: t.album.primaryArtist.name,
      albumArtUrl: t.coverUrl || undefined,
      audioUrl: t.audioUrl,
      createdAt: t.createdAt
    }));
  }

  async getPlaylists(): Promise<Playlist[]> {
    const res = await getPlaylists();
    return res.data.playlists.map((p: any) => ({
      id: p.id,
      name: p.name,
      creator: p.creatorName || 'Mave Studio',
      coverUrl: p.imageUrl || undefined
    }));
  }

  /**
   * Fetch tracks belonging to a playlist.
   */
  async getPlaylistTracks(playlistId: string): Promise<Track[]> {
    const response = await getPlaylistTracks({ playlistId });
    return response.data.playlistEntries.map((entry: any) => ({
      id: entry.track.id,
      title: entry.track.title,
      artist: entry.track.album.primaryArtist.name,
      albumArtUrl: entry.track.coverUrl || '',
      audioUrl: entry.track.audioUrl,
    }));
  }

  async getCategoryTracks(categoryId: string): Promise<Track[]> {
    const response = await getCategoryTracks({ categoryId });
    return response.data.musicCategories.map((entry: any) => ({
      id: entry.track.id,
      title: entry.track.title,
      artist: entry.track.album.primaryArtist.name,
      albumArtUrl: entry.track.coverUrl || '',
      audioUrl: entry.track.audioUrl,
    }));
  }

  async getLikedTracks(): Promise<Track[]> {
    const user = getAuth().currentUser;
    if (!user) return [];
    
    const token = await user.getIdToken();
    const res = await fetch(`${window.location.origin}/api/interactions/liked`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!res.ok) {
      throw new Error(`Failed to fetch liked tracks: ${res.statusText}`);
    }

    return await res.json();
  }

  /**
   * Save (like) a track, album, or podcast to the user's library.
   * Routes through the centralized API instead of raw fetch at the call site.
   */
  async likeItem(id: string, type: 'track' | 'album' | 'podcast' = 'track'): Promise<void> {
    const user = getAuth().currentUser;
    const token = user ? await user.getIdToken() : '';
    const baseUrl = import.meta.env.VITE_API_URL || '';
    const endpoint = type === 'podcast'
      ? `${baseUrl}/api/spotify/podcast/save`
      : `${baseUrl}/api/spotify/music/save`;
    const res = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
      },
      body: JSON.stringify({ id, type })
    });
    if (!res.ok) {
      throw new Error(`Failed to save item: ${res.statusText}`);
    }
  }
}

export const musicService = new MusicService();
