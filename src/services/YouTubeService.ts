
import logger from "../config/logger.js";
import { getRedis } from "../config/redis.js";

export class YouTubeService {
  private REDIRECT_URI = process.env.VITE_API_URL 
    ? `${process.env.VITE_API_URL}/api/youtube/callback` 
    : "http://localhost:8080/api/youtube/callback";

  async getToken(userId: string): Promise<string | null> {
    const redis = getRedis();
    if (!redis) {
      throw new Error("Redis connection is unavailable");
    }
    return await redis.get(`youtube_token:${userId}`);
  }

  getAuthUrl(userId: string): string {
    const clientId = process.env.YOUTUBE_CLIENT_ID;
    const scope = "https://www.googleapis.com/auth/youtube.readonly";
    
    if (!clientId) {
      throw new Error("Missing YouTube Client ID configuration.");
    }

    return `https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=${clientId}&scope=${encodeURIComponent(scope)}&redirect_uri=${encodeURIComponent(this.REDIRECT_URI)}&access_type=offline&prompt=consent&state=${encodeURIComponent(userId)}`;
  }

  async handleCallback(code: string, state: string): Promise<void> {
    const clientId = process.env.YOUTUBE_CLIENT_ID;
    const clientSecret = process.env.YOUTUBE_CLIENT_SECRET;

    if (!code || !clientId || !clientSecret) {
      throw new Error("Missing parameters or credentials");
    }

    const tokenRes = await fetch("https://oauth2.googleapis.com/token", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        code,
        client_id: clientId,
        client_secret: clientSecret,
        redirect_uri: this.REDIRECT_URI,
        grant_type: "authorization_code",
      }).toString(),
    });

    const data: any = await tokenRes.json();
    if (data.access_token) {
      if (state) {
        const redis = getRedis();
        if (redis) {
          await redis.set(`youtube_token:${state}`, data.access_token, "EX", data.expires_in);
          if (data.refresh_token) {
            await redis.set(`youtube_refresh:${state}`, data.refresh_token);
          }
        }
      }
    } else {
      logger.error("YouTube Auth Error Details", data);
      throw new Error(data.error_description || "Authentication failed");
    }
  }

  async fetchLibrary(token: string): Promise<any> {
    const tracksRes = await fetch("https://www.googleapis.com/youtube/v3/playlists?part=snippet&mine=true&maxResults=20", {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!tracksRes.ok) {
      throw new Error(`Failed to fetch library: ${tracksRes.statusText}`);
    }
    return await tracksRes.json();
  }

  async search(token: string, query: string, maxResults: string, type: string): Promise<any> {
    const searchUrl = new URL("https://www.googleapis.com/youtube/v3/search");
    searchUrl.searchParams.append("part", "snippet");
    searchUrl.searchParams.append("q", query);
    searchUrl.searchParams.append("maxResults", maxResults);
    searchUrl.searchParams.append("type", type);

    const searchRes = await fetch(searchUrl.toString(), {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await searchRes.json();
    
    if (!searchRes.ok) {
      logger.error("YouTube search API error", data);
      throw new Error("Failed to search YouTube");
    }
    
    return data;
  }
}

export const youtubeService = new YouTubeService();
