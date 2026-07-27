import { Router } from "express";
import fetch from "node-fetch";
import logger from "../config/logger.js";
import { getRedis } from "../config/redis.js";

const router = Router();
const REDIRECT_URI = process.env.VITE_API_URL 
    ? `${process.env.VITE_API_URL}/api/spotify/callback` 
    : "http://localhost:8080/api/spotify/callback";

// Helper to get access token from Redis per-user
async function getSpotifyToken(userId: string): Promise<string | null> {
  const redis = getRedis();
  if (!redis) return null;
  return await redis.get(`spotify_token:${userId}`);
}

router.get("/status", async (req, res) => {
  const redis = getRedis();
  const token = redis ? await redis.get("spotify_token:global") : null;
  res.json({ connected: !!token });
});

router.get("/token", async (req, res) => {
  const redis = getRedis();
  const token = redis ? await redis.get("spotify_token:global") : null;
  if (!token) return res.status(401).json({ error: "No token found" });
  res.json({ token });
});

router.get("/auth-url", (req, res) => {
  const clientId = process.env.SPOTIFY_CLIENT_ID;
  const scope = "user-library-read playlist-read-private streaming user-read-email user-read-private user-modify-playback-state";
  
  if (!clientId) {
    return res.status(500).json({ error: "Missing SPOTIFY_CLIENT_ID" });
  }

  const authUrl = `https://accounts.spotify.com/authorize?response_type=code&client_id=${clientId}&scope=${encodeURIComponent(scope)}&redirect_uri=${encodeURIComponent(REDIRECT_URI)}`;
  res.json({ url: authUrl });
});

router.get("/callback", async (req, res) => {
  const code = req.query.code as string;
  const clientId = process.env.SPOTIFY_CLIENT_ID;
  const clientSecret = process.env.SPOTIFY_CLIENT_SECRET;

  if (!code || !clientId || !clientSecret) {
    return res.status(400).send("Missing parameters or credentials");
  }

  try {
    const tokenRes = await fetch("https://accounts.spotify.com/api/token", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "Authorization": "Basic " + Buffer.from(`${clientId}:${clientSecret}`).toString("base64"),
      },
      body: new URLSearchParams({
        code,
        redirect_uri: REDIRECT_URI,
        grant_type: "authorization_code",
      }).toString(),
    });

    const data: any = await tokenRes.json();
    if (data.access_token) {
      // Typically we'd associate this with the user ID from auth middleware.
      // For now, storing globally or in session
      const redis = getRedis();
      if (redis) {
        await redis.set("spotify_token:global", data.access_token, "EX", data.expires_in);
        await redis.set("spotify_refresh:global", data.refresh_token);
      }
      
      res.send(`
        <script>
          window.opener.postMessage({ type: 'OAUTH_AUTH_SUCCESS' }, '*');
          window.close();
        </script>
      `);
    } else {
      res.status(400).json(data);
    }
  } catch (err: any) {
    logger.error("Spotify Auth Error", err);
    res.status(500).send("Authentication failed");
  }
});

router.get("/library", async (req, res) => {
  const redis = getRedis();
  const token = redis ? await redis.get("spotify_token:global") : null;
  
  if (!token) return res.status(401).json({ error: "Not connected" });

  try {
    const tracksRes = await fetch("https://api.spotify.com/v1/me/tracks?limit=20", {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await tracksRes.json();
    res.json(data);
  } catch (err) {
    res.status(500).json({ error: "Failed to fetch library" });
  }
});

export default router;
