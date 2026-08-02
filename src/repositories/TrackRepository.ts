import { getRedis } from "../config/redis.js";
import logger from "../config/logger.js";
import crypto from "crypto";

export interface RepositoryTrack {
  id: string;
  title: string;
  artistName?: string;
  albumTitle?: string;
  audioUrl: string;
  coverUrl?: string;
  durationMs: number;
  prompt?: string;
  visibility: string;
  isCommunity: boolean;
  ownerUid?: string;
  createdAt: string;
}

export class TrackRepository {
  /**
   * Generates a short code and maps it to a trackId in Redis.
   * This provides fast shortlink resolution without database contention.
   */
  async createShortLink(trackId: string): Promise<string> {
    const shortCode = crypto.randomUUID().split("-")[0];
    
    const redis = getRedis();
    if (redis) {
      // Set to expire after 30 days (2592000 seconds)
      await redis.set(`shortlink:${shortCode}`, trackId, "EX", 2592000);
      logger.info("[TrackRepository] Created shortlink in Redis", { shortCode, trackId });
    } else {
      logger.warn("[TrackRepository] Redis is not configured, shortlink will not be persistent.");
    }
    
    return shortCode;
  }

  async resolveShortLink(shortCode: string): Promise<string | null> {
    const redis = getRedis();
    if (redis) {
      const trackId = await redis.get(`shortlink:${shortCode}`);
      return trackId;
    }
    return null;
  }
}

export const trackRepository = new TrackRepository();
