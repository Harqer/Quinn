import { Router, Response, NextFunction } from "express";
import logger from "../config/logger.js";
import { optionalFirebaseToken, AuthenticatedRequest } from "../middlewares/auth.js";
import { spotifyService } from "../services/SpotifyService.js";

const router = Router();

router.get("/status", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.json({ connected: false });
  }
  try {
    const token = await spotifyService.getToken(userId);
    return res.json({ connected: !!token });
  } catch (err) {
    logger.error("Error getting Spotify token status", { error: err });
    next(err);
  }
});

router.get("/token", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.status(401).json({ error: "Unauthorized" });
  }
  try {
    const token = await spotifyService.getToken(userId);
    if (!token) return res.status(401).json({ error: "No token found" });
    return res.json({ token });
  } catch (err) {
    logger.error("Error getting Spotify token", { error: err });
    next(err);
  }
});

router.get("/auth-url", optionalFirebaseToken, (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.status(401).json({ error: "Please log in to connect Spotify." });
  }

  try {
    const authUrl = spotifyService.getAuthUrl(userId);
    return res.json({ url: authUrl });
  } catch (err) {
    logger.error("Error getting Spotify auth URL", { error: err });
    next(err);
  }
});

router.get("/callback", async (req, res, next) => {
  const code = req.query.code as string;
  const state = req.query.state as string;

  try {
    await spotifyService.handleCallback(code, state);
    return res.send(`
      <script>
        window.opener.postMessage({ type: 'OAUTH_AUTH_SUCCESS' }, '*');
        window.close();
      </script>
    `);
  } catch (err: any) {
    logger.error("Spotify Auth Error", err);
    next(err);
  }
});

router.get("/library", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.status(401).json({ error: "Unauthorized" });
  }
  
  try {
    const token = await spotifyService.getToken(userId);
    if (!token) return res.status(401).json({ error: "Not connected" });

    const data = await spotifyService.fetchLibrary(token);
    return res.json(data);
  } catch (err) {
    logger.error("Failed to fetch library", { error: err });
    next(err);
  }
});

export default router;
