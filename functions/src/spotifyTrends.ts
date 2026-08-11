import { onSchedule } from "firebase-functions/v2/scheduler";
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import * as logger from "firebase-functions/logger";
import { executeMutation } from "./dataconnect";

const SPOTIFY_CLIENT_ID = defineSecret("SPOTIFY_CLIENT_ID");
const SPOTIFY_CLIENT_SECRET = defineSecret("SPOTIFY_CLIENT_SECRET");

// The Spotify API base URL (overridable for testing)
const SPOTIFY_API_BASE_URL = process.env.SPOTIFY_API_BASE_URL || "https://api.spotify.com/v1";

// Spotify Token Endpoint
const SPOTIFY_TOKEN_URL = process.env.SPOTIFY_TOKEN_URL || "https://accounts.spotify.com/api/token";

async function getSpotifyAccessToken(clientIdOverride?: string, clientSecretOverride?: string): Promise<string> {
  const clientId = clientIdOverride || SPOTIFY_CLIENT_ID.value() || process.env.SPOTIFY_CLIENT_ID;
  const clientSecret = clientSecretOverride || SPOTIFY_CLIENT_SECRET.value() || process.env.SPOTIFY_CLIENT_SECRET;
  
  if (!clientId || !clientSecret) {
    throw new Error("Missing Spotify credentials (SPOTIFY_CLIENT_ID, SPOTIFY_CLIENT_SECRET)");
  }

  const credentials = Buffer.from(`${clientId}:${clientSecret}`).toString('base64');
  
  const response = await fetch(SPOTIFY_TOKEN_URL, {
    method: 'POST',
    headers: {
      'Authorization': `Basic ${credentials}`,
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: 'grant_type=client_credentials'
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Failed to get Spotify token: ${response.status} ${errorText}`);
  }

  const data = await response.json() as { access_token: string };
  return data.access_token;
}

export const fetchSpotifyTrends = onSchedule({
  schedule: "every week",
  secrets: [SPOTIFY_CLIENT_ID, SPOTIFY_CLIENT_SECRET]
}, async (event) => {
  try {
    logger.info("Starting Spotify Trends fetch");
    
    // 1. Get Access Token
    const accessToken = await getSpotifyAccessToken();

    // 2. Fetch Global Top 50 Playlist (Playlist ID: 37i9dQZEVXbMDoHDwVN2tF)
    const playlistId = "37i9dQZEVXbMDoHDwVN2tF";
    const playlistUrl = `${SPOTIFY_API_BASE_URL}/playlists/${playlistId}/tracks?limit=20`;
    
    const response = await fetch(playlistUrl, {
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Failed to fetch Spotify playlist: ${response.status} ${errorText}`);
    }

    const data = await response.json() as any;
    const tracks = data.items.map((item: any) => item.track);

    let addedCount = 0;

    // 3. Insert Tracks into DataConnect
    for (const track of tracks) {
      if (!track || !track.preview_url) {
        continue;
      }
      
      const title = track.name;
      const audioUrl = track.preview_url;
      const coverUrl = track.album?.images?.[0]?.url || "";
      const durationMs = track.duration_ms;
      const ownerUid = "system_spotify_trends";
      
      await executeMutation("SeedTrack", {
        title,
        audioUrl,
        coverUrl,
        durationMs,
        prompt: `Spotify Trending: ${track.artists?.map((a: any) => a.name).join(", ")}`,
        isCommunity: true,
        ownerUid
      });
      
      addedCount++;
    }
    
    logger.info(`Successfully added ${addedCount} trending tracks from Spotify.`);

  } catch (error) {
    logger.error("Error in fetchSpotifyTrends", error);
    throw error;
  }
});

export const fetchPersonalizedSpotifyVibe = onCall(
  {
    secrets: [SPOTIFY_CLIENT_ID, SPOTIFY_CLIENT_SECRET],
    cors: true
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "User must be authenticated.");
    }

    const { playlistId = "37i9dQZEVXbMDoHDwVN2tF", vibeQuery = "chill" } = request.data || {};

    try {
      const accessToken = await getSpotifyAccessToken();
      
      // Fetch playlist or search by vibe query
      const url = playlistId 
        ? `${SPOTIFY_API_BASE_URL}/playlists/${playlistId}/tracks?limit=15`
        : `${SPOTIFY_API_BASE_URL}/search?q=${encodeURIComponent(vibeQuery)}&type=track&limit=15`;

      const response = await fetch(url, {
        headers: { 'Authorization': `Bearer ${accessToken}` }
      });

      if (!response.ok) {
        throw new Error(`Spotify API response error: ${response.statusText}`);
      }

      const rawData = await response.json() as any;
      const rawTracks = playlistId ? rawData.items?.map((i: any) => i.track) : rawData.tracks?.items;

      const personalizedTracks = [];

      for (const track of rawTracks || []) {
        if (!track || !track.name) continue;

        const trackTitle = track.name;
        const artistName = track.artists?.map((a: any) => a.name).join(", ") || "Spotify Vibe Artist";
        const coverUrl = track.album?.images?.[0]?.url || "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4";
        const audioUrl = track.preview_url || "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
        const durationMs = track.duration_ms || 180000;

        await executeMutation("SeedTrack", {
          title: `${trackTitle} - ${artistName}`,
          audioUrl,
          coverUrl,
          durationMs,
          prompt: `Personalized Vibe [${vibeQuery}]: ${artistName}`,
          isCommunity: false,
          ownerUid: request.auth.uid
        });

        personalizedTracks.push({
          id: track.id || `sp_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`,
          title: trackTitle,
          artist: artistName,
          coverUrl,
          audioUrl,
          durationMs,
          vibe: vibeQuery
        });
      }

      return {
        vibe: vibeQuery,
        count: personalizedTracks.length,
        tracks: personalizedTracks
      };
    } catch (err: any) {
      logger.error("Failed to fetch personalized Spotify vibe:", err);
      throw new HttpsError("internal", `Failed to fetch personalized vibe: ${err.message || err}`);
    }
  }
);
