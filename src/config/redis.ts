import { Redis } from "@upstash/redis";
import logger from "./logger.js";

let redis: Redis;

export const initRedis = () => {
  if (!process.env.UPSTASH_REDIS_REST_URL || !process.env.UPSTASH_REDIS_REST_TOKEN) {
    logger.warn("[REDIS] Missing credentials. Redis caching will be disabled.");
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
  return await client.get(`vision:${imageHash}`);
};
