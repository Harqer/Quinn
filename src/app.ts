import express from "express";
import helmet from "helmet";
import compression from "compression";
import path from "path";
import { fileURLToPath } from "url";
import { rateLimit } from "express-rate-limit";
import { RedisStore } from "rate-limit-redis";
import Redis from "ioredis";
import spotifyRouter from "./routes/spotify.js";
import musicRouter from "./routes/music.js";
import logsRouter from "./routes/logs.js";
import reportsRouter from "./routes/reports.js";
import logger from "./config/logger.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();

// Rate Limiting Setup (Production Grade)
const isProduction = process.env.NODE_ENV === "production";
let limiterStore;

if (isProduction && process.env.UPSTASH_REDIS_REST_URL) {
  // Extract TCP URL from REST URL if possible or assume separate ENV
  // For Upstash, we can use the REST API via a custom store or just use ioredis with the rediss:// url
  const redisUrl = process.env.REDIS_URL || process.env.UPSTASH_REDIS_REST_URL.replace("https://", "rediss://");
  const client = new Redis(redisUrl);
  limiterStore = new RedisStore({
    // @ts-expect-error - ioredis type compatibility
    sendCommand: (...args: string[]) => client.call(...args),
  });
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
app.use(helmet({ contentSecurityPolicy: false, frameguard: false }));
app.use(compression());

app.use("/api/spotify", spotifyRouter);
app.use("/api/music", musicRouter);
app.use("/api/logs", logsRouter);
app.use("/api/reports", reportsRouter);

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
