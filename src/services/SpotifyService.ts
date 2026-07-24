import { getSecret } from "../config/secrets.js";
import { db } from "../config/firebase.js";
import logger from "../config/logger.js";
import { getRedis } from "../config/redis.js";

export class SpotifyService {
  async getValidToken(uid: string): Promise<string | null> {
    try {
      const redis = getRedis();
      if (redis) {
        const cachedToken = await redis.get(`spotify_token:${uid}`);
        if (cachedToken) {
          return cachedToken as string;
        }
      }

      const doc = await db.collection("spotify_tokens").doc(uid).get();
      const data = doc.exists ? doc.data() as any : null;
      if (data && data.accessToken) {
        const now = Date.now();
        const expiresAt = data.expiresAt || 0;

        if (now < expiresAt) {
          if (redis) {
            const ttlSeconds = Math.max(1, Math.floor((expiresAt - now) / 1000));
            await redis.set(`spotify_token:${uid}`, data.accessToken, { ex: ttlSeconds });
          }
          return data.accessToken;
        }

        if (data.refreshToken && getSecret("SPOTIFY_CLIENT_ID") && getSecret("SPOTIFY_CLIENT_SECRET")) {
          logger.info(`[SPOTIFY] Access token expired. Initiating automatic token refresh...`, { uid });
          const refreshParams = new URLSearchParams({
            grant_type: "refresh_token",
            refresh_token: data.refreshToken,
          });
          const basicAuth = Buffer.from(`${getSecret("SPOTIFY_CLIENT_ID")}:${getSecret("SPOTIFY_CLIENT_SECRET")}`).toString("base64");

          const tokenRes = await fetch("https://accounts.spotify.com/api/token", {
            method: "POST",
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
              "Authorization": `Basic ${basicAuth}`,
            },
            body: refreshParams.toString(),
          });

          if (tokenRes.ok) {
            const tokenData: any = await tokenRes.json();
            const newAccessToken = tokenData.access_token;
            const newExpiresIn = tokenData.expires_in || 3600;
            const newExpiresAt = Date.now() + (newExpiresIn * 1000);

            const updatedData = {
              accessToken: newAccessToken,
              expiresAt: newExpiresAt,
              refreshToken: tokenData.refresh_token || data.refreshToken,
            };

            await db.collection("spotify_tokens").doc(uid).set(updatedData, { merge: true });

            if (redis) {
              const ttlSeconds = Math.max(1, Math.floor((newExpiresAt - Date.now()) / 1000));
              await redis.set(`spotify_token:${uid}`, newAccessToken, { ex: ttlSeconds });
            }

            logger.info(`[SPOTIFY] Dynamic token refreshed successfully.`, { uid });
            return newAccessToken;
          }
        }
      }
    } catch (err) {
      logger.error("[SPOTIFY] Error retrieving token from storage:", { error: err, uid });
    }

    return null;
  }

  async savePodcastToPlaylist(uid: string, trackUri: string): Promise<boolean> {
    return this._saveToPlaylist(uid, trackUri, "podcastPlaylistId", "Mave Studio Podcasts", "Your AI-powered narratives orchestrated by Mave Studio.");
  }

  async saveMusicToPlaylist(uid: string, trackUri: string): Promise<boolean> {
    return this._saveToPlaylist(uid, trackUri, "musicPlaylistId", "Mave Studio Music", "Your AI-powered music orchestrated by Mave Studio.");
  }

  async saveAudiobookToPlaylist(uid: string, trackUri: string): Promise<boolean> {
    return this._saveToPlaylist(uid, trackUri, "audiobookPlaylistId", "Mave Studio Audiobooks", "Your AI-powered audiobooks orchestrated by Mave Studio.");
  }

  private async _saveToPlaylist(
    uid: string, 
    trackUri: string, 
    dbKey: "podcastPlaylistId" | "musicPlaylistId" | "audiobookPlaylistId",
    playlistName: string,
    playlistDesc: string
  ): Promise<boolean> {
    const token = await this.getValidToken(uid);
    if (!token) return false;

    try {
      // Use Redis to cache the playlist ID as well to avoid Firestore read
      const redis = getRedis();
      let playlistId: string | null = null;
      if (redis) {
        playlistId = await redis.get(`spotify_playlist:${uid}:${dbKey}`) as string | null;
      }

      if (!playlistId) {
        const tokenDoc = await db.collection("spotify_tokens").doc(uid).get();
        const tokenData = tokenDoc.exists ? tokenDoc.data() : null;
        playlistId = tokenData?.[dbKey] || null;
      }

      if (!playlistId) {
        // Find or Create playlist
        const meRes = await fetch("https://api.spotify.com/v1/me", {
          headers: { "Authorization": `Bearer ${token}` }
        });
        const meData: any = await meRes.json();
        const userSpotifyId = meData.id;

        const playlistsRes = await fetch("https://api.spotify.com/v1/me/playlists", {
          headers: { "Authorization": `Bearer ${token}` }
        });
        const playlistsData: any = await playlistsRes.json();
        const existing = playlistsData.items.find((p: any) => p.name === playlistName);

        if (existing) {
          playlistId = existing.id;
        } else {
          const createRes = await fetch(`https://api.spotify.com/v1/users/${userSpotifyId}/playlists`, {
            method: "POST",
            headers: {
              "Authorization": `Bearer ${token}`,
              "Content-Type": "application/json"
            },
            body: JSON.stringify({
              name: playlistName,
              description: playlistDesc,
              public: false
            })
          });
          const newData: any = await createRes.json();
          playlistId = newData.id;
        }

        if (playlistId) {
          await db.collection("spotify_tokens").doc(uid).set({ [dbKey]: playlistId }, { merge: true });
        }
      }

      if (playlistId && redis) {
        await redis.set(`spotify_playlist:${uid}:${dbKey}`, playlistId, { ex: 86400 }); // Cache playlist ID for 24h
      }

      if (playlistId) {
        const addRes = await fetch(`https://api.spotify.com/v1/playlists/${playlistId}/tracks`, {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ uris: [trackUri] })
        });
        return addRes.ok;
      }
    } catch (err) {
      logger.error("[SPOTIFY] Failed to save to playlist", { error: err, uid, playlistName });
    }
    return false;
  }
}

export const spotifyService = new SpotifyService();
