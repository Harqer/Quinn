export interface Track {
  id: string;
  title: string;
  artist: string;
  albumArtUrl?: string;
  audioUrl?: string;
  isExplicit?: boolean;
  duration?: number;
}

export interface Playlist {
  id: string;
  title: string;
  coverUrl?: string;
  tracks: Track[];
}

export interface Album {
  id: string;
  title: string;
  artist: string;
  coverUrl?: string;
  tracks: Track[];
}

export interface Category {
  id: string;
  title: string;
  colorHex: string;
}

class MusicService {
  private mockTracks: Track[] = [
    { id: 't1', title: 'Midnight City', artist: 'M83', albumArtUrl: 'https://picsum.photos/200?random=1', audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', duration: 250 },
    { id: 't2', title: 'Blinding Lights', artist: 'The Weeknd', albumArtUrl: 'https://picsum.photos/200?random=2', audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3', duration: 200 },
    { id: 't3', title: 'Levitating', artist: 'Dua Lipa', albumArtUrl: 'https://picsum.photos/200?random=3', audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3', duration: 203 },
    { id: 't4', title: 'Starboy', artist: 'The Weeknd', albumArtUrl: 'https://picsum.photos/200?random=4', audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3', duration: 230 }
  ];

  private mockCategories: Category[] = [
    { id: 'c1', title: 'Pop', colorHex: '#e1118c' },
    { id: 'c2', title: 'Rock', colorHex: '#e13300' },
    { id: 'c3', title: 'Hip-Hop', colorHex: '#1e3264' },
    { id: 'c4', title: 'Jazz', colorHex: '#777777' }
  ];

  async getDiscoverTracks(): Promise<Track[]> {
    return Promise.resolve(this.mockTracks);
  }

  async getLibraryTracks(): Promise<Track[]> {
    return Promise.resolve(this.mockTracks.slice(1, 4));
  }

  async search(query: string): Promise<Track[]> {
    const q = query.toLowerCase();
    return Promise.resolve(this.mockTracks.filter(t => t.title.toLowerCase().includes(q) || t.artist.toLowerCase().includes(q)));
  }
  
  async getCategories(): Promise<Category[]> {
    return Promise.resolve(this.mockCategories);
  }
  
  async getAlbumTracks(albumId: string): Promise<Track[]> {
    return Promise.resolve(this.mockTracks);
  }
}

export const musicService = new MusicService();
