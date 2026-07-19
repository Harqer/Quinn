import { db } from "../config/firebase.js";

export interface SpotifyTokenData {
  accessToken: string;
  refreshToken: string;
  expiresAt: number;
  podcastPlaylistId?: string;
}

export class SpotifyRepository {
  private collection = db.collection("spotify_tokens");

  async getToken(uid: string): Promise<SpotifyTokenData | null> {
    const doc = await this.collection.doc(uid).get();
    if (!doc.exists) return null;
    return doc.data() as SpotifyTokenData;
  }

  async saveToken(uid: string, data: SpotifyTokenData): Promise<void> {
    await this.collection.doc(uid).set(data, { merge: true });
  }
}

export const spotifyRepository = new SpotifyRepository();
