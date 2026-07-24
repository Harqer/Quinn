import dotenv from "dotenv";
import { SecretManagerServiceClient } from "@google-cloud/secret-manager";
import { WebSocketServer } from "ws";
import { auth, firebaseConfig } from "./config/firebase.js";
import app from "./app.js";
import { initAi } from "./services/ai.js";
import { setupMusicWebSocket } from "./routes/index.js";
import logger from "./config/logger.js";
import cluster from "cluster";
import os from "os";

dotenv.config();

const PORT = process.env.PORT || 3000;
const isGcpEnvironment = process.env.NODE_ENV === "production" || !!process.env.K_SERVICE || !!process.env.GOOGLE_APPLICATION_CREDENTIALS;

async function resolveSecrets() {
  if (isGcpEnvironment) {
    logger.info("[SECRET_MANAGER] Resolving secrets from Google Cloud Secret Manager for production...");
    try {
      const projectId = firebaseConfig.projectId || "musically-studio";
      const client = new SecretManagerServiceClient();
      const secrets = ["GEMINI_API_KEY", "SENTRY_DSN", "SPOTIFY_CLIENT_ID", "SPOTIFY_CLIENT_SECRET", "REDIS_URL", "VITE_APP_CHECK_KEY"];

      for (const secret of secrets) {
        try {
          const [version] = await Promise.race([
            client.accessSecretVersion({ name: `projects/${projectId}/secrets/${secret}/versions/latest` }),
            new Promise<never>((_, reject) => setTimeout(() => reject(new Error("Timeout")), 1500))
          ]);
          const payload = version.payload?.data?.toString()?.trim();
          if (payload) process.env[secret] = payload;
        } catch (e: any) {
          logger.debug(`[SECRET_MANAGER] Secret ${secret} skipped: ${e.message || e}`);
        }
      }
    } catch (err) {
      logger.warn("[SECRET_MANAGER] Secret resolution skipped:", { error: err });
    }
  }
}

async function startWorker() {
  const portNum = Number(process.env.PORT) || 8080;
  const server = app.listen(portNum, "0.0.0.0", () => {
    logger.info(`[WORKER ${process.pid}] Listening on 0.0.0.0:${portNum}`);
  });

  resolveSecrets().then(() => initAi()).catch((err) => {
    logger.warn("[WORKER] Async secrets/AI initialization warning:", { error: err });
  });

  const wss = new WebSocketServer({ noServer: true });
  setupMusicWebSocket(wss);

  server.on("upgrade", async (request, socket, head) => {
    const url = new URL(request.url!, `http://${request.headers.host}`);
    if (url.pathname === "/api/music/ws") {
      const token = url.searchParams.get("token");

      if (!token) {
        logger.info("[WS] Guest user connected (Audio First)");
        wss.handleUpgrade(request, socket, head, (ws) => {
          wss.emit("connection", ws, request);
        });
        return;
      }

      try {
        await auth.verifyIdToken(token);
        wss.handleUpgrade(request, socket, head, (ws) => {
          wss.emit("connection", ws, request);
        });
      } catch (err) {
        logger.warn("[WS] Invalid authentication token provided. Connection rejected.");
        socket.write("HTTP/1.1 401 Unauthorized\r\n\r\n");
        socket.destroy();
      }
    } else {
      socket.destroy();
    }
  });
}

if (cluster.isPrimary && process.env.NODE_ENV === "production" && !process.env.K_SERVICE) {
  const numCPUs = os.cpus().length;
  logger.info(`[PRIMARY] Forking ${numCPUs} workers...`);

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
