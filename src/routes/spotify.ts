import { Router, Response, Request } from "express";
import { verifyFirebaseToken, AuthenticatedRequest } from "../middlewares/auth.js";
import { spotifyService } from "../services/SpotifyService.js";
import { spotifyRepository } from "../repositories/SpotifyRepository.js";
import xss from "xss";

const router = Router();

router.get("/status", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  const uid = req.user?.uid || "local-dev-user";
  const token = await spotifyService.getValidToken(uid);
  if (token) {
    const data = await spotifyRepository.getToken(uid);
    const source = (data && data.accessToken === token) ? "oauth" : "vault";
    res.json({ connected: true, source, token });
  } else {
    res.json({ connected: false });
  }
});

router.get("/auth-url", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  const clientId = process.env.SPOTIFY_CLIENT_ID;
  if (!clientId) return res.status(400).json({ error: "Spotify Client ID not configured." });

  const host = req.get("host") || "";
  const protocol = req.secure || req.header("x-forwarded-proto") === "https" ? "https" : "http";
  const redirectUri = process.env.APP_URL ? `${process.env.APP_URL}/api/spotify/callback` : `${protocol}://${host}/api/spotify/callback`;

  const params = new URLSearchParams({
    client_id: clientId,
    response_type: "code",
    redirect_uri: redirectUri,
    scope: "user-top-read playlist-read-private playlist-read-collaborative playlist-modify-public playlist-modify-private",
    state: req.user?.uid || "local-dev-user",
  });

  res.json({ url: `https://accounts.spotify.com/authorize?${params.toString()}` });
});

router.get("/callback", async (req: Request, res: Response) => {
  const { code, state, error } = req.query;

  if (error) {
    const sanitizedError = xss(error as string);
    return res.send(`
      <html>
        <body style="background: #121212; color: #ffffff; font-family: sans-serif; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; text-align: center;">
          <div style="background: #18181b; border: 1px solid #dc2626; padding: 2.5rem; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); max-width: 400px;">
            <h2 style="color: #ef4444;">Connection Failed</h2>
            <p style="color: #a1a1aa;">Spotify returned an error: ${sanitizedError}</p>
            <button onclick="window.close()" style="background: #dc2626; color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 8px; cursor: pointer; font-weight: bold; margin-top: 1rem;">Close Window</button>
          </div>
          <script>
            if (window.opener) {
              window.opener.postMessage({ type: 'OAUTH_AUTH_FAILURE', error: "${sanitizedError}" }, '*');
            }
          </script>
        </body>
      </html>
    `);
  }

  if (!code) return res.status(400).send("Authorization code is missing.");

  const uid = (state as string) || "local-dev-user";
  const clientId = process.env.SPOTIFY_CLIENT_ID;
  const clientSecret = process.env.SPOTIFY_CLIENT_SECRET;

  if (!clientId || !clientSecret) return res.status(500).send("Spotify credentials not configured.");

  const host = req.get("host") || "";
  const protocol = req.secure || req.header("x-forwarded-proto") === "https" ? "https" : "http";
  const redirectUri = process.env.APP_URL ? `${process.env.APP_URL}/api/spotify/callback` : `${protocol}://${host}/api/spotify/callback`;

  try {
    const tokenRes = await fetch("https://accounts.spotify.com/api/token", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "Authorization": `Basic ${Buffer.from(`${clientId}:${clientSecret}`).toString("base64")}`,
      },
      body: new URLSearchParams({ grant_type: "authorization_code", code: code as string, redirect_uri: redirectUri }).toString(),
    });

    if (!tokenRes.ok) throw new Error("Token exchange failed");

    const tokenData: any = await tokenRes.json();
    await spotifyRepository.saveToken(uid, {
      accessToken: tokenData.access_token,
      refreshToken: tokenData.refresh_token,
      expiresAt: Date.now() + (tokenData.expires_in * 1000),
    });

    res.send(`
      <html>
        <body style="background: #121212; color: #ffffff; font-family: sans-serif; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; text-align: center;">
          <div style="background: #18181b; border: 1px solid #27272a; padding: 2.5rem; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); max-width: 400px;">
            <h2 style="margin: 0 0 0.5rem; font-weight: 700;">Connected to Spotify!</h2>
            <script>
              if (window.opener) {
                window.opener.postMessage({ type: 'OAUTH_AUTH_SUCCESS' }, '*');
                setTimeout(() => { window.close(); }, 1500);
              } else {
                setTimeout(() => { window.location.href = '/'; }, 2000);
              }
            </script>
          </div>
        </body>
      </html>
    `);
  } catch (err) {
    res.status(500).send("Internal Server Error");
  }
});

router.get("/playlists", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  const uid = req.user?.uid || "local-dev-user";
  const token = await spotifyService.getValidToken(uid);
  if (!token) return res.status(401).json({ error: "Unauthorized" });

  const response = await fetch("https://api.spotify.com/v1/me/playlists?limit=50", {
    headers: { "Authorization": `Bearer ${token}` }
  });
  if (response.ok) res.json(await response.json());
  else res.status(response.status).json({ error: "Failed to fetch playlists" });
});

router.post("/podcast/save", verifyFirebaseToken, async (req: AuthenticatedRequest, res: Response) => {
  const uid = req.user?.uid || "local-dev-user";
  const { trackUri } = req.body;
  if (!trackUri) return res.status(400).json({ error: "trackUri is required" });

  const success = await spotifyService.savePodcastToPlaylist(uid, trackUri);
  if (success) res.json({ success: true });
  else res.status(500).json({ error: "Failed to save podcast" });
});

export default router;
