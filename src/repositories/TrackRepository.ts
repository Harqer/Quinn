import { db, FieldValue } from "../config/firebase.js";

export interface Track {
  id: string;
  title: string;
  artist: string;
  vibe: string;
  type: "music" | "podcast";
  imageUrl?: string;
  userId?: string;
  createdAt: any;
}

export class TrackRepository {
  private collection = db.collection("tracks");

  async saveTrack(track: Omit<Track, "id" | "createdAt">): Promise<string> {
    const docRef = await this.collection.add({
      ...track,
      createdAt: FieldValue.serverTimestamp(),
    });
    return docRef.id;
  }

  async bookmarkTrack(uid: string, trackId: string): Promise<string> {
    const bookmarkRef = db.collection("bookmarks").doc(`${uid}_${trackId}`);
    await bookmarkRef.set({
      userId: uid,
      trackId: trackId,
      timestamp: FieldValue.serverTimestamp(),
    });
    return bookmarkRef.id;
  }

  async getTrackById(id: string): Promise<Track | null> {
    const doc = await this.collection.doc(id).get();
    if (!doc.exists) return null;
    return { id: doc.id, ...doc.data() } as Track;
  }

  async getCommunityTracks(limit: number = 20): Promise<Track[]> {
    const snapshot = await this.collection
      .orderBy("createdAt", "desc")
      .limit(limit)
      .get();

    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data(),
    })) as Track[];
  }

  async getUserTracks(uid: string): Promise<Track[]> {
    const snapshot = await this.collection
      .where("userId", "==", uid)
      .orderBy("createdAt", "desc")
      .get();

    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data(),
    })) as Track[];
  }
}

export const trackRepository = new TrackRepository();
