
import logger from "../config/logger.js";
import { getRedis } from "../config/redis.js";

export class SpotifyService {
  private REDIRECT_URI = process.env.VITE_API_URL 
    ? `${process.env.VITE_API_URL}/api/spotify/callback` 
    : "http://localhost:8080/api/spotify/callback";

  async getToken(userId: string): Promise<string | null> {
    const redis = getRedis();
    if (!redis) {
      throw new Error("Redis connection is unavailable");
    }
    return await redis.get(`spotify_token:${userId}`);
  }

  getAuthUrl(userId: string): string {
    const clientId = process.env.SPOTIFY_CLIENT_ID;
    const scope = "user-library-read playlist-read-private streaming user-read-email user-read-private user-modify-playback-state";
    
    if (!clientId) {
      throw new Error("Missing SPOTIFY_CLIENT_ID");
    }

    return `https://accounts.spotify.com/authorize?response_type=code&client_id=${clientId}&scope=${encodeURIComponent(scope)}&redirect_uri=${encodeURIComponent(this.REDIRECT_URI)}&state=${encodeURIComponent(userId)}`;
  }

  async handleCallback(code: string, state: string): Promise<void> {
    const clientId = process.env.SPOTIFY_CLIENT_ID;
    const clientSecret = process.env.SPOTIFY_CLIENT_SECRET;

    if (!code || !clientId || !clientSecret) {
      throw new Error("Missing parameters or credentials");
    }

    const tokenRes = await fetch("https://accounts.spotify.com/api/token", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "Authorization": "Basic " + Buffer.from(`${clientId}:${clientSecret}`).toString("base64"),
      },
      body: new URLSearchParams({
        code,
        redirect_uri: this.REDIRECT_URI,
        grant_type: "authorization_code",
      }).toString(),
    });

    const data: any = await tokenRes.json();
    if (data.access_token) {
      if (state) {
        const redis = getRedis();
        if (redis) {
          await redis.set(`spotify_token:${state}`, data.access_token, "EX", data.expires_in);
          if (data.refresh_token) {
            await redis.set(`spotify_refresh:${state}`, data.refresh_token);
          }
        }
      }
    } else {
      logger.error("Spotify Auth Error Details", data);
      throw new Error(data.error_description || "Authentication failed");
    }
  }

  async fetchLibrary(token: string): Promise<any> {
    const tracksRes = await fetch("https://api.spotify.com/v1/me/tracks?limit=20", {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!tracksRes.ok) {
      throw new Error(`Failed to fetch library: ${tracksRes.statusText}`);
    }
    return await tracksRes.json();
  }
}

export const spotifyService = new SpotifyService();
