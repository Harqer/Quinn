import { WebSocketServer } from "ws";
import { auth } from "./config/firebase.js";
import app from "./app.js";
import { initAi } from "./services/ai.js";
import { setupMusicWebSocket } from "./routes/index.js";
import logger from "./config/logger.js";

async function startServer() {
  const portNum = Number(process.env.PORT) || 8080;
  const server = app.listen(portNum, "0.0.0.0", () => {
    logger.info(`[SERVER] Listening on 0.0.0.0:${portNum}`);
  });

  try {
    await initAi();
  } catch (err) {
    logger.error("[SERVER] AI initialization failed. Ensure GEMINI_API_KEY is injected natively into the environment.", { error: err });
  }

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

startServer().catch((err) => {
  logger.error("Server Initialization Failed:", { error: err });
  process.exit(1);
});
