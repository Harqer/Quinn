import express from "express";
import helmet from "helmet";
import compression from "compression";
import path from "path";
import { fileURLToPath } from "url";
import * as Sentry from "@sentry/node";
import spotifyRouter from "./routes/spotify.js";
import musicRouter from "./routes/music.js";
import logsRouter from "./routes/logs.js";
import logger from "./config/logger.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();

app.use(express.json());
app.use(helmet({ contentSecurityPolicy: false, frameguard: false }));
app.use(compression());

app.use("/api/spotify", spotifyRouter);
app.use("/api/music", musicRouter);
app.use("/api/logs", logsRouter);

// Serve frontend
app.use(express.static(path.join(__dirname, "../../dist")));
app.get("*", (req, res) => {
  if (!req.path.startsWith("/api")) {
    res.sendFile(path.join(__dirname, "../../dist/index.html"));
  }
});

// Error handling
app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
  logger.error("Express Error Handler:", { error: err.message, stack: err.stack });
  const status = err.status || 500;
  res.status(status).json({ error: { message: "Internal Server Error" } });
});

export default app;
