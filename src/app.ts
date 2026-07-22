import express from "express";
import helmet from "helmet";
import compression from "compression";
import path from "path";
import { fileURLToPath } from "url";
import { rateLimit } from "express-rate-limit";
import { RedisStore } from "rate-limit-redis";
import Redis from "ioredis";
import { spotifyRouter, musicRouter, logsRouter, reportsRouter, authRouter } from "./routes/index.js";
import logger from "./config/logger.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();

// Rate Limiting Setup (Production Grade)
let limiterStore;

const redisUrl = process.env.REDIS_URL;
if (redisUrl) {
  try {
    const client = new Redis(redisUrl, { maxRetriesPerRequest: 3, enableOfflineQueue: false });
    client.on("error", (err) => logger.warn("[REDIS_STORE] Redis client error:", { error: err.message }));
    limiterStore = new RedisStore({
      // @ts-expect-error - ioredis type compatibility
      sendCommand: (...args: string[]) => client.call(...args),
    });
  } catch (err) {
    logger.warn("[REDIS_STORE] Failed to initialize Redis store for rate limiting, falling back to memory store.");
  }
}

const globalLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per window
  standardHeaders: true,
  legacyHeaders: false,
  store: limiterStore,
  message: { error: { message: "Too many requests, please try again later.", code: "RATE_LIMIT_EXCEEDED" } },
});

app.use(globalLimiter);
app.use(express.json());
app.use(helmet());
app.use(compression());

app.use("/api/spotify", spotifyRouter);
app.use("/api/music", musicRouter);
app.use("/api/logs", logsRouter);
app.use("/api/reports", reportsRouter);
app.use("/api/auth", authRouter);

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
