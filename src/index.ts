import dotenv from "dotenv";
import { SecretManagerServiceClient } from "@google-cloud/secret-manager";
import { WebSocketServer } from "ws";
import { auth, firebaseConfig } from "./config/firebase.js";
import app from "./app.js";
import { initAi } from "./services/ai.js";
import { setupMusicWebSocket } from "./routes/index.js";
import { startBackupCron } from "./services/backup.js";
import logger from "./config/logger.js";
import cluster from "cluster";
import os from "os";

dotenv.config();

const PORT = process.env.PORT || 3000;
const isGcpEnvironment = process.env.NODE_ENV === "production" || !!process.env.K_SERVICE || !!process.env.GOOGLE_APPLICATION_CREDENTIALS;

async function resolveSecrets() {
  if (isGcpEnvironment) {
    const projectId = firebaseConfig.projectId;
    if (projectId) {
      try {
        const client = new SecretManagerServiceClient();
        const secrets = ["GEMINI_API_KEY", "SENTRY_DSN", "SPOTIFY_CLIENT_ID", "SPOTIFY_CLIENT_SECRET", "UPSTASH_REDIS_REST_URL", "UPSTASH_REDIS_REST_TOKEN"];

        for (const secret of secrets) {
          try {
            const [version] = await client.accessSecretVersion({
              name: `projects/${projectId}/secrets/${secret}/versions/latest`,
            });
            const payload = version.payload?.data?.toString()?.trim();
            if (payload) process.env[secret] = payload;
          } catch (e) {}
        }
      } catch (err) {
        logger.error("Failed to initialize Google Secret Manager:", { error: err });
      }
    }
  }
}

async function startWorker() {
  await resolveSecrets();
  initAi();

  const server = app.listen(PORT, () => {
    logger.info(`[WORKER ${process.pid}] Listening on port ${PORT}`);
  });

  const wss = new WebSocketServer({ noServer: true });
  setupMusicWebSocket(wss);

  server.on("upgrade", async (request, socket, head) => {
    const url = new URL(request.url!, `http://${request.headers.host}`);
    if (url.pathname === "/api/music/ws") {
      const token = url.searchParams.get("token");
      if (!token) {
        socket.write("HTTP/1.1 401 Unauthorized\r\n\r\n");
        socket.destroy();
        return;
      }

      try {
        await auth.verifyIdToken(token);
        wss.handleUpgrade(request, socket, head, (ws) => {
          wss.emit("connection", ws, request);
        });
      } catch (err) {
        socket.write("HTTP/1.1 401 Unauthorized\r\n\r\n");
        socket.destroy();
      }
    } else {
      socket.destroy();
    }
  });
}

if (cluster.isPrimary && process.env.NODE_ENV === "production") {
  const numCPUs = os.cpus().length;
  logger.info(`[PRIMARY] Forking ${numCPUs} workers...`);

  startBackupCron();

  for (let i = 0; i < numCPUs; i++) {
    cluster.fork();
  }

  cluster.on("exit", (worker, code, signal) => {
    logger.warn(`[PRIMARY] Worker ${worker.process.pid} died. Forking new worker...`);
    cluster.fork();
  });
} else {
  startWorker().catch((err) => logger.error("Worker Initialization Failed:", { error: err }));
}
