import Redis from "ioredis";
import logger from "./logger.js";
import { getSecret } from "./secrets.js";

let redis: Redis | null = null;

export const initRedis = () => {
  const host = getSecret("REDIS_HOST") || process.env.REDIS_HOST;
  const port = parseInt(getSecret("REDIS_PORT") || process.env.REDIS_PORT || "6379", 10);
  const password = getSecret("REDIS_PASSWORD") || process.env.REDIS_PASSWORD;
  
  if (!host) {
    logger.warn("[REDIS] Missing REDIS_HOST. Redis caching and session storage will be disabled.");
    return;
  }

  redis = new Redis({
    host,
    port,
    password,
    tls: password ? {} : undefined, // Often Memorystore uses TLS when auth is enabled
    retryStrategy: (times) => Math.min(times * 50, 2000)
  });

  redis.on("error", (err) => {
    logger.error("[REDIS] Connection error", { error: err.message });
  });
};

export const getRedis = () => {
  if (redis === undefined) initRedis(); // initialize on first use if not explicitly initialized
  return redis;
};

/**
 * Saves Mave session state using Redis.
 */
export const saveMaveSession = async (sessionId: string, state: any) => {
  const client = getRedis();
  if (!client) return;
  await client.set(`session:${sessionId}`, JSON.stringify(state), "EX", 86400); // 24h TTL
};

export const getMaveSession = async (sessionId: string): Promise<any | null> => {
  const client = getRedis();
  if (!client) return null;
  const data = await client.get(`session:${sessionId}`);
  return data ? JSON.parse(data) : null;
};

/**
 * Caches vision analysis results with a TTL of 1 hour.
 */
export const cacheVisionResult = async (imageHash: string, description: string) => {
  const client = getRedis();
  if (!client) return;
  await client.set(`vision:${imageHash}`, description, "EX", 3600);
};

export const getCachedVisionResult = async (imageHash: string): Promise<string | null> => {
  const client = getRedis();
  if (!client) return null;
  const result = await client.get(`vision:${imageHash}`);
  return result;
};
