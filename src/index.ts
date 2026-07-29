import "dotenv/config";
import { WebSocketServer } from "ws";
import { auth } from "./config/firebase.js";
import app from "./app.js";
import { initAi } from "./services/ai.js";
import logger from "./config/logger.js";

import { initSecrets } from "./config/secrets.js";

async function startServer() {
  await initSecrets();
  const portNum = Number(process.env.PORT) || 8080;
  const server = app.listen(portNum, () => {
    logger.info(`[SERVER] Listening on ${portNum}`);
  });

  try {
    await initAi();
  } catch (err) {
    logger.error("[SERVER] AI initialization failed. Ensure GEMINI_API_KEY is injected natively into the environment.", { error: err });
  }

  // WebSockets removed in favor of Firebase AI SDK
  setInterval(() => {
    // keepalive heartbeat
  }, 1000 * 60 * 60);
}

startServer().catch((err) => {
  logger.error("Server Initialization Failed:", { error: err });
  process.exit(1);
});
