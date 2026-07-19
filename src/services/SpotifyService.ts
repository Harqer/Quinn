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
}

export const spotifyService = new SpotifyService();
