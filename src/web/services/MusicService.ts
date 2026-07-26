import { getAuth } from 'firebase/auth';
import { getCommunityTracks, getUserTracks, getCategories, getPlaylists, getAlbums, getPodcasts, getAudiobooks, addTrackToPlaylist, createTrack, createPlaylist } from "../../lib/dataconnect/esm/index.esm.js";

export interface Track {
  id: string;
  title: string;
  artist: string;
  albumArtUrl?: string;
  audioUrl?: string;
  videoUrl?: string;
  createdAt?: string;
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

class MusicService {
  async getDiscoverTracks(): Promise<Track[]> {
    try {
      const res = await getCommunityTracks();
      return res.data.tracks.map((t: any) => ({
        id: t.id,
        title: t.name,
        artist: t.artistName,
        albumArtUrl: t.imageUrl || undefined,
        audioUrl: undefined, // Add audioUrl mapping if available in schema
        createdAt: t.createdAt
      }));
    } catch (err) {
      console.error(err);
      return [];
    }
  }

  async getLibraryTracks(): Promise<Track[]> {
    try {
      const res = await getUserTracks();
      return res.data.tracks.map((t: any) => ({
        id: t.id,
        title: t.name,
        artist: t.artistName,
        albumArtUrl: t.imageUrl || undefined,
        createdAt: t.createdAt
      }));
    } catch (err) {
      console.error(err);
      return [];
    }
  }

  async search(query: string): Promise<Track[]> {
    try {
      // Fetch all community tracks and client-side filter for now
      // A dedicated Data Connect search query could be added later
      const all = await this.getDiscoverTracks();
      const q = query.toLowerCase();
      return all.filter(t => t.title.toLowerCase().includes(q) || t.artist.toLowerCase().includes(q));
    } catch (err) {
      console.error(err);
      return [];
    }
  }
  
  async getCategories(): Promise<Category[]> {
    try {
      const res = await getCategories();
      return res.data.categories.map((c: any) => ({
        id: c.id,
        title: c.name,
        imageUrl: c.imageUrl || undefined,
        type: c.type
      }));
    } catch (err) {
      console.error(err);
      return [];
    }
  }
  
  async getAlbumTracks(albumId: string): Promise<Track[]> {
    return this.getDiscoverTracks();
  }
}

export const musicService = new MusicService();
