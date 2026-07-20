import { spotifyRepository } from "../repositories/SpotifyRepository.js";
import logger from "../config/logger.js";

export class SpotifyService {
  async getValidToken(uid: string): Promise<string | null> {
    try {
      const data = await spotifyRepository.getToken(uid);
      if (data && data.accessToken) {
        const now = Date.now();
        const expiresAt = data.expiresAt || 0;

        if (now < expiresAt) {
          return data.accessToken;
        }

        if (data.refreshToken && process.env.SPOTIFY_CLIENT_ID && process.env.SPOTIFY_CLIENT_SECRET) {
          logger.info(`[SPOTIFY] Access token expired. Initiating automatic token refresh...`, { uid });
          const refreshParams = new URLSearchParams({
            grant_type: "refresh_token",
            refresh_token: data.refreshToken,
          });
          const basicAuth = Buffer.from(`${process.env.SPOTIFY_CLIENT_ID}:${process.env.SPOTIFY_CLIENT_SECRET}`).toString("base64");

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

            await spotifyRepository.saveToken(uid, updatedData);

            logger.info(`[SPOTIFY] Dynamic token refreshed successfully.`, { uid });
            return newAccessToken;
          }
        }
      }
    } catch (err) {
      logger.error("[SPOTIFY] Error retrieving token from storage:", { error: err, uid });
    }

    const vaultToken = process.env.SPOTIFY_BEARER_TOKEN || process.env.SPOTIFY_ACCESS_TOKEN;
    if (vaultToken) {
      return vaultToken;
    }

    return null;
  }

  async savePodcastToPlaylist(uid: string, trackUri: string): Promise<boolean> {
    const token = await this.getValidToken(uid);
    if (!token) return false;

    try {
      let playlistId = (await spotifyRepository.getToken(uid))?.podcastPlaylistId;

      if (!playlistId) {
        // Find or Create "Mave Studio Podcasts" playlist
        const meRes = await fetch("https://api.spotify.com/v1/me", {
          headers: { "Authorization": `Bearer ${token}` }
        });
        const meData: any = await meRes.json();
        const userSpotifyId = meData.id;

        const playlistsRes = await fetch("https://api.spotify.com/v1/me/playlists", {
          headers: { "Authorization": `Bearer ${token}` }
        });
        const playlistsData: any = await playlistsRes.json();
        const existing = playlistsData.items.find((p: any) => p.name === "Mave Studio Podcasts");

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
              name: "Mave Studio Podcasts",
              description: "Your AI-powered narratives orchestrated by Mave Studio.",
              public: false
            })
          });
          const newData: any = await createRes.json();
          playlistId = newData.id;
        }

        if (playlistId) {
          await spotifyRepository.saveToken(uid, { podcastPlaylistId: playlistId } as any);
        }
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
      logger.error("[SPOTIFY] Failed to save podcast to playlist", { error: err, uid });
    }
    return false;
  }
}

export const spotifyService = new SpotifyService();
