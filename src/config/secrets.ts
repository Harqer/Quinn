import { SecretManagerServiceClient } from "@google-cloud/secret-manager";
import { logger } from "./logger";

const client = new SecretManagerServiceClient();

let cachedSecrets: Record<string, string> = {};
let isInitialized = false;

const projectId = process.env.GOOGLE_CLOUD_PROJECT || process.env.GCP_PROJECT;

export async function initSecrets() {
  if (isInitialized) return;
  if (!projectId) {
    logger.warn("No GOOGLE_CLOUD_PROJECT defined, skipping Secret Manager initialization.");
    return;
  }
  
  const secretsToFetch = [
    "GEMINI_API_KEY",
    "SPOTIFY_CLIENT_ID",
    "SPOTIFY_CLIENT_SECRET",
    "UPSTASH_REDIS_REST_URL",
    "UPSTASH_REDIS_REST_TOKEN"
  ];

  for (const secret of secretsToFetch) {
    try {
      const name = `projects/${projectId}/secrets/${secret}/versions/latest`;
      const [version] = await client.accessSecretVersion({ name });
      const payload = version.payload?.data?.toString();
      if (payload) {
        cachedSecrets[secret] = payload;
      }
    } catch (e) {
      logger.warn(`Could not load secret ${secret} from Secret Manager: ${(e as Error).message}`);
    }
  }
  
  isInitialized = true;
  logger.info("Secrets loaded from Google Cloud Secret Manager.");
}

export function getSecret(name: string): string | undefined {
  // Fallback to process.env if not initialized or not found in Secret Manager
  return cachedSecrets[name] || process.env[name];
}
