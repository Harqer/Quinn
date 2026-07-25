import { db, FieldValue } from "../config/firebase.js";
import { getRedis } from "../config/redis.js";
import logger from "../config/logger.js";
import crypto from "crypto";

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

class BatchWriter {
  private pendingOperations: { type: 'set' | 'update' | 'delete', ref: any, data?: any }[] = [];
  private timer: NodeJS.Timeout | null = null;
  
  public addOperation(type: 'set' | 'update' | 'delete', ref: any, data?: any) {
    this.pendingOperations.push({ type, ref, data });
    // Firestore batch limit is 500
    if (this.pendingOperations.length >= 499) {
      this.flush();
    } else if (!this.timer) {
      this.timer = setTimeout(() => this.flush(), 500);
    }
  }
  
  private async flush() {
    if (this.pendingOperations.length === 0) return;
    
    const ops = this.pendingOperations;
    this.pendingOperations = [];
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = null;
    }
    
    try {
      const batch = db.batch();
      for (const op of ops) {
        if (op.type === 'set') batch.set(op.ref, op.data);
        else if (op.type === 'update') batch.update(op.ref, op.data);
        else if (op.type === 'delete') batch.delete(op.ref);
      }
      await batch.commit();
    } catch (e) {
      logger.error("[BatchWriter] Flush failed", e);
    }
  }
}

const globalBatchWriter = new BatchWriter();
export { globalBatchWriter };

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
    
    // Batch the write to avoid database contention on high-volume ops
    globalBatchWriter.addOperation('set', bookmarkRef, {
      userId: uid,
      trackId: trackId,
      timestamp: FieldValue.serverTimestamp(),
    });
    
    return bookmarkRef.id;
  }

  async createShortLink(trackId: string): Promise<string> {
    const shortCode = crypto.randomUUID().split("-")[0];
    const linkRef = db.collection("shortlinks").doc(shortCode);
    
    await linkRef.set({
      trackId,
      createdAt: FieldValue.serverTimestamp()
    });
    
    const redis = getRedis();
    if (redis) {
      await redis.set(`shortlink:${shortCode}`, trackId);
    }
    
    return shortCode;
  }

  async getTrackById(id: string): Promise<Track | null> {
    const doc = await this.collection.doc(id).get();
    if (!doc.exists) return null;
    return { id: doc.id, ...doc.data() } as Track;
  }

  async getCommunityTracks(limit: number = 20): Promise<Track[]> {
    const redis = getRedis();
    const cacheKey = `community_tracks:${limit}`;

    if (redis) {
      const cached = await redis.get(cacheKey);
      if (cached) {
        return cached as Track[];
      }
    }

    const snapshot = await this.collection
      .orderBy("createdAt", "desc")
      .limit(limit)
      .get();

    const tracks = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data(),
    })) as Track[];

    if (redis) {
      await redis.set(cacheKey, tracks, { ex: 300 }); // Cache for 5 minutes
    }

    return tracks;
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
