import express from "express";
import helmet from "helmet";
import compression from "compression";
import path from "path";
import { fileURLToPath } from "url";
import { rateLimit } from "express-rate-limit";
import { RedisStore } from "rate-limit-redis";
import Redis from "ioredis";
import { musicRouter } from "./routes/index.js";
import { getRedis } from "./config/redis.js";
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

// CORS Configuration
app.use((req, res, next) => {
  const allowedOrigins = (process.env.ALLOWED_ORIGINS || "*").split(",");
  const origin = req.headers.origin;
  if (allowedOrigins.includes("*") || (origin && allowedOrigins.includes(origin))) {
    res.setHeader("Access-Control-Allow-Origin", origin || "*");
  }
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Firebase-AppCheck");
  res.setHeader("Access-Control-Allow-Credentials", "true");
  if (req.method === "OPTIONS") {
    res.sendStatus(204);
    return;
  }
  next();
});

app.use("/api/music", musicRouter);

// URL Redirect Service
app.get("/s/:shortCode", async (req, res) => {
  res.redirect(302, "/");
});

// Serve frontend
app.use(express.static(path.join(__dirname, "../../dist")));

// Serve frontend fallback
app.use((req, res) => {
  if (req.method === "GET" && !req.path.startsWith("/api")) {
    res.sendFile(path.join(__dirname, "../../dist/index.html"));
  } else {
    res.status(404).json({ error: "Not Found" });
  }
});

// Error handling
app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
  logger.error("Express Error Handler:", { error: err.message, stack: err.stack });
  const status = err.status || 500;
  res.status(status).json({ error: { message: "Internal Server Error" } });
});

export default app;
