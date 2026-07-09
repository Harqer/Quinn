import express from "express";
import path from "path";
import { fileURLToPath } from "url";
import { GoogleGenAI, Type } from "@google/genai";
import * as Sentry from "@sentry/node";
import dotenv from "dotenv";
import { initializeApp, getApps } from "firebase-admin/app";
import { getFirestore, FieldValue } from "firebase-admin/firestore";
import { getAuth } from "firebase-admin/auth";
import helmet from "helmet";
import compression from "compression";
import { rateLimit } from "express-rate-limit";
import { SecretManagerServiceClient } from "@google-cloud/secret-manager";

import firebaseConfig from "./firebase-applet-config.json" with { type: "json" };

dotenv.config();

// Meticulous runtime resolution of production secrets from Google Secret Manager vault.
// Fallback is gracefully routed to local process environment variables to preserve seamless local development & offline previews.
const isGcpEnvironment = process.env.NODE_ENV === "production" || !!process.env.K_SERVICE || !!process.env.GOOGLE_APPLICATION_CREDENTIALS;

if (isGcpEnvironment) {
  const projectId = firebaseConfig.projectId;
  if (projectId) {
    try {
      console.log(`GCP environment detected (Project: ${projectId}). Dynamic Secret Manager loading initiated...`);
      const client = new SecretManagerServiceClient();

      // Resolve GEMINI_API_KEY from Secret Manager
      try {
        const name = `projects/${projectId}/secrets/GEMINI_API_KEY/versions/latest`;
        const [version] = await client.accessSecretVersion({ name });
        const payload = version.payload?.data?.toString()?.trim();
        if (payload) {
          process.env.GEMINI_API_KEY = payload;
          console.log("Successfully resolved and loaded GEMINI_API_KEY from Google Secret Manager.");
        }
      } catch (err: any) {
        console.warn(`GEMINI_API_KEY not found in Google Secret Manager (falling back to environment): ${err.message || err}`);
      }

      // Resolve SENTRY_DSN from Secret Manager
      try {
        const name = `projects/${projectId}/secrets/SENTRY_DSN/versions/latest`;
        const [version] = await client.accessSecretVersion({ name });
        const payload = version.payload?.data?.toString()?.trim();
        if (payload) {
          process.env.SENTRY_DSN = payload;
          console.log("Successfully resolved and loaded SENTRY_DSN from Google Secret Manager.");
        }
      } catch (err: any) {
        console.warn(`SENTRY_DSN not found in Google Secret Manager (falling back to environment): ${err.message || err}`);
      }
    } catch (err: any) {
      console.error(`Failed to initialize Google Secret Manager client: ${err.message || err}. Falling back to standard env.`);
    }
  } else {
    console.warn("No GCP projectId found in firebase-applet-config.json. Skipping Secret Manager resolution.");
  }
} else {
  console.log("Local development/preview environment detected. Bypassing Google Secret Manager, relying on local environment variables.");
}

// Initialize Sentry Node SDK for backend crash tracking
const sentryDsn = process.env.SENTRY_DSN;
if (sentryDsn) {
  Sentry.init({
    dsn: sentryDsn,
    tracesSampleRate: 1.0,
    beforeSend(event) {
      // Sanitize any PII (authorization headers, keys, cookies) from requests
      if (event.request && event.request.headers) {
        delete event.request.headers["Authorization"];
        delete event.request.headers["authorization"];
        delete event.request.headers["Cookie"];
        delete event.request.headers["cookie"];
      }
      const sanitizeStr = (str: string): string => {
        return str.replace(/AQ\.[A-Za-z0-9_\-]+/g, "[REDACTED_API_KEY]")
                  .replace(/AIzaSy[A-Za-z0-9_\-]+/g, "[REDACTED_API_KEY]");
      };
      if (event.exception && event.exception.values) {
        event.exception.values.forEach((val) => {
          if (val.value) val.value = sanitizeStr(val.value);
          if (val.stacktrace && val.stacktrace.frames) {
            val.stacktrace.frames.forEach((frame) => {
              if (frame.filename) frame.filename = sanitizeStr(frame.filename);
              if (frame.function) frame.function = sanitizeStr(frame.function);
            });
          }
        });
      }
      return event;
    }
  });
} else {
  console.warn("SENTRY_DSN is not configured. Backend crash tracking is inactive.");
}

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
app.use(express.json());

// Enable secure HTTP headers using Helmet
// Note: We customize CSP and frameguard settings to allow rendering within the Google AI Studio iframe sandbox
app.use(
  helmet({
    contentSecurityPolicy: false,
    crossOriginEmbedderPolicy: false,
    crossOriginOpenerPolicy: false,
    crossOriginResourcePolicy: false,
    frameguard: false, // Allows the application to be embedded in iframes gracefully
  })
);

// Enable response compression for optimized performance and lower egress latency
app.use(compression());

// Secure /api routes with a robust sliding window rate limiter to prevent API abuse
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  limit: 100, // limit each IP to 100 requests per windowMs
  standardHeaders: "draft-7", // Use modern RateLimit headers conforming to draft-7
  legacyHeaders: false, // Disable legacy X-RateLimit headers
  message: { error: "Too many requests from this IP, please try again after 15 minutes." },
});
app.use("/api", apiLimiter);

interface AuthenticatedRequest extends express.Request {
  user?: any;
}

// Zero-Trust Firebase Auth Token Validation Middleware
const verifyFirebaseToken = async (
  req: AuthenticatedRequest,
  res: express.Response,
  next: express.NextFunction
) => {
  // Gracefully bypass token validation in local/preview development to facilitate seamless prototyping & offline simulation
  if (!isGcpEnvironment) {
    req.user = { uid: "local-dev-user" };
    next();
    return;
  }

  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    res.status(401).json({ error: "Unauthorized. Missing or malformed Authorization header." });
    return;
  }

  const idToken = authHeader.split("Bearer ")[1];
  try {
    const decodedToken = await getAuth().verifyIdToken(idToken);
    req.user = decodedToken;
    next();
  } catch (err) {
    res.status(401).json({ error: "Unauthorized. Invalid or expired token." });
  }
};

// Initialize Gemini Client
const ai = new GoogleGenAI({
  apiKey: process.env.GEMINI_API_KEY,
  httpOptions: {
    headers: {
      "User-Agent": "aistudio-build",
    },
  },
});

// Initialize Firebase Admin SDK
if (!getApps().length) {
  initializeApp({
    projectId: firebaseConfig.projectId,
  });
}

const db = getFirestore();

// 1. Shifted Gemini requests server-side route
app.post("/api/generate", verifyFirebaseToken, async (req, res, next) => {
  const { image } = req.body;
  if (!image) {
    res.status(400).json({ error: "Missing required 'image' field in request body." });
    return;
  }

  try {
    const systemPrompt = "You are a creative music director. Analyze the vibe, objects, and emotions in this image. Generate 3 short, evocative phrases, 4 to 5 words maximum per phrase, that can be used as prompts for an AI music generator. The phrases should describe genres, moods, instruments, or sound textures.";

    const response = await ai.models.generateContent({
      model: "gemini-3.5-flash",
      contents: {
        parts: [
          {
            inlineData: {
              mimeType: "image/jpeg",
              data: image,
            },
          },
          {
            text: systemPrompt,
          },
        ],
      },
      config: {
        responseMimeType: "application/json",
        responseSchema: {
          type: Type.OBJECT,
          properties: {
            prompts: {
              type: Type.ARRAY,
              description: "A list of 3 creative music prompts.",
              items: {
                type: Type.STRING,
              },
            },
          },
        },
      },
    });

    const text = response.text;
    if (!text) {
      throw new Error("No response content generated by Gemini.");
    }

    const parsed = JSON.parse(text);
    res.json(parsed);
  } catch (err) {
    next(err);
  }
});

// Secure runtime configuration endpoint for client application (protected under Zero-Trust Auth policy)
app.get("/api/config", verifyFirebaseToken, (req, res) => {
  res.json({
    geminiApiKey: process.env.GEMINI_API_KEY || "",
    sentryDsn: process.env.SENTRY_DSN || "",
  });
});

// Database API routes to capture wearables telemetry and gesture logs using Firestore
app.post("/api/logs/gesture", verifyFirebaseToken, async (req, res, next) => {
  const { gesture } = req.body;
  if (!gesture) {
    res.status(400).json({ error: "Missing 'gesture' field." });
    return;
  }

  try {
    const docRef = await db.collection("gesture_logs").add({
      gesture,
      timestamp: FieldValue.serverTimestamp(),
    });
    res.status(201).json({ success: true, id: docRef.id });
  } catch (err) {
    next(err);
  }
});

app.post("/api/logs/battery", verifyFirebaseToken, async (req, res, next) => {
  const { batteryLevel, isWearDetected } = req.body;
  if (batteryLevel === undefined || isWearDetected === undefined) {
    res.status(400).json({ error: "Missing 'batteryLevel' or 'isWearDetected' field." });
    return;
  }

  try {
    const docRef = await db.collection("battery_logs").add({
      batteryLevel: Number(batteryLevel),
      isWearDetected: String(isWearDetected),
      timestamp: FieldValue.serverTimestamp(),
    });
    res.status(201).json({ success: true, id: docRef.id });
  } catch (err) {
    next(err);
  }
});

app.post("/api/logs/prompt", verifyFirebaseToken, async (req, res, next) => {
  const { prompt, weight } = req.body;
  if (!prompt || weight === undefined) {
    res.status(400).json({ error: "Missing 'prompt' or 'weight' field." });
    return;
  }

  try {
    const docRef = await db.collection("prompt_logs").add({
      prompt,
      weight: String(weight),
      timestamp: FieldValue.serverTimestamp(),
    });
    res.status(201).json({ success: true, id: docRef.id });
  } catch (err) {
    next(err);
  }
});

// Retrieve logs for status verification
app.get("/api/logs", verifyFirebaseToken, async (req, res, next) => {
  try {
    const gesturesSnap = await db.collection("gesture_logs")
      .orderBy("timestamp", "desc")
      .limit(50)
      .get();
    
    const batteriesSnap = await db.collection("battery_logs")
      .orderBy("timestamp", "desc")
      .limit(50)
      .get();

    const gestures = gesturesSnap.docs.map(doc => {
      const data = doc.data();
      return {
        id: doc.id,
        gesture: data.gesture,
        timestamp: data.timestamp ? data.timestamp.toDate() : new Date(),
      };
    });

    const batteries = batteriesSnap.docs.map(doc => {
      const data = doc.data();
      return {
        id: doc.id,
        batteryLevel: data.batteryLevel,
        isWearDetected: data.isWearDetected,
        timestamp: data.timestamp ? data.timestamp.toDate() : new Date(),
      };
    });

    res.json({ gestures, batteries });
  } catch (err) {
    next(err);
  }
});

// Serve frontend static assets
app.use(express.static(path.join(__dirname, "dist")));

// Fallback to SPA routing for other frontend pages
app.get("*all", (req, res) => {
  res.sendFile(path.join(__dirname, "dist", "index.html"));
});

// Sentry error handler must be registered before custom error handlers
if (sentryDsn) {
  Sentry.setupExpressErrorHandler(app);
}

// Centralized JSON error handler to prevent internal stack trace leakage
app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
  console.error("EXPRESS ERROR:", err);
  
  // Track backend exceptions via Sentry
  if (sentryDsn) {
    Sentry.captureException(err);
  }

  const status = err.status || err.statusCode || 500;
  res.status(status).json({
    error: {
      message: process.env.NODE_ENV === "production" ? "Internal Server Error" : err.message || "An unexpected error occurred",
      ...(process.env.NODE_ENV !== "production" && { stack: err.stack }),
    }
  });
});

// Configure the default container PORT (3000)
const PORT = 3000;
const server = app.listen(PORT, async () => {
  console.log(`Server started successfully on port ${PORT}`);
});

// Configure robust graceful shutdown sequence for Cloud Run environment
const shutdownGracefully = (signal: string) => {
  console.log(`Received ${signal}. Starting production graceful shutdown sequence...`);
  
  // Stop receiving new connections and process outstanding requests
  server.close(() => {
    console.log("Http server closed successfully. Process exiting.");
    process.exit(0);
  });

  // Enforce absolute timeout (e.g. 10s) to forcefully shut down if active requests hang
  setTimeout(() => {
    console.error("Graceful shutdown timeout exceeded. Force shutting down.");
    process.exit(1);
  }, 10000);
};

process.on("SIGTERM", () => shutdownGracefully("SIGTERM"));
process.on("SIGINT", () => shutdownGracefully("SIGINT"));
