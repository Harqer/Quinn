import { Redis } from "@upstash/redis";
import logger from "./logger.js";

let redis: Redis;

export const initRedis = () => {
  if (!process.env.UPSTASH_REDIS_REST_URL || !process.env.UPSTASH_REDIS_REST_TOKEN) {
    logger.warn("[REDIS] Missing credentials. Redis caching and session storage will be disabled.");
    return;
  }

  redis = new Redis({
    url: process.env.UPSTASH_REDIS_REST_URL,
    token: process.env.UPSTASH_REDIS_REST_TOKEN,
  });
};

export const getRedis = () => {
  if (!redis) initRedis();
  return redis;
};

/**
 * Saves Mave session state using Redis JSON.
 */
export const saveMaveSession = async (sessionId: string, state: any) => {
  const client = getRedis();
  if (!client) return;
  await client.set(`session:${sessionId}`, JSON.stringify(state), { ex: 86400 }); // 24h TTL
};

export const getMaveSession = async (sessionId: string): Promise<any | null> => {
  const client = getRedis();
  if (!client) return null;
  const data = await client.get(`session:${sessionId}`);
  return data ? JSON.parse(data as string) : null;
};

/**
 * Caches vision analysis results with a TTL of 1 hour.
 */
export const cacheVisionResult = async (imageHash: string, description: string) => {
  const client = getRedis();
  if (!client) return;
  await client.set(`vision:${imageHash}`, description, { ex: 3600 });
};

export const getCachedVisionResult = async (imageHash: string): Promise<string | null> => {
  const client = getRedis();
  if (!client) return null;
  const result = await client.get(`vision:${imageHash}`);
  return result as string | null;
};
