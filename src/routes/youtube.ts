import { Router, Response, NextFunction } from "express";
import logger from "../config/logger.js";
import { optionalFirebaseToken, AuthenticatedRequest } from "../middlewares/auth.js";
import { youtubeService } from "../services/YouTubeService.js";

const router = Router();

router.get("/status", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.json({ connected: false });
  }
  try {
    const token = await youtubeService.getToken(userId);
    return res.json({ connected: !!token });
  } catch (err) {
    logger.error("Error getting YouTube token status", { error: err });
    next(err);
  }
});

router.get("/token", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.status(401).json({ error: "Unauthorized" });
  }
  try {
    const token = await youtubeService.getToken(userId);
    if (!token) return res.status(401).json({ error: "No token found" });
    return res.json({ token });
  } catch (err) {
    logger.error("Error getting YouTube token", { error: err });
    next(err);
  }
});

router.get("/auth-url", optionalFirebaseToken, (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.status(401).json({ error: "Please log in to connect YouTube." });
  }

  try {
    const authUrl = youtubeService.getAuthUrl(userId);
    return res.json({ url: authUrl });
  } catch (err) {
    logger.error("Error getting YouTube auth URL", { error: err });
    next(err);
  }
});

router.get("/callback", async (req, res, next) => {
  const code = req.query.code as string;
  const state = req.query.state as string;

  try {
    await youtubeService.handleCallback(code, state);
    return res.send(`
      <script>
        window.opener.postMessage({ type: 'YOUTUBE_OAUTH_SUCCESS' }, '*');
        window.close();
      </script>
    `);
  } catch (err: any) {
    logger.error("YouTube Auth Error", err);
    next(err);
  }
});

router.get("/library", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.status(401).json({ error: "Unauthorized" });
  }
  
  try {
    const token = await youtubeService.getToken(userId);
    if (!token) return res.status(401).json({ error: "Not connected" });

    const data = await youtubeService.fetchLibrary(token);
    return res.json(data);
  } catch (err) {
    logger.error("Failed to fetch YouTube library", { error: err });
    next(err);
  }
});

router.get("/search", optionalFirebaseToken, async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const userId = req.user?.uid;
  if (!userId || req.user?.isGuest) {
    return res.status(401).json({ error: "Unauthorized" });
  }
  
  try {
    const token = await youtubeService.getToken(userId);
    if (!token) return res.status(401).json({ error: "Not connected to YouTube" });

    const query = req.query.q as string;
    if (!query) {
      return res.status(400).json({ error: "Missing search query parameter 'q'" });
    }

    const maxResults = (req.query.maxResults as string) || '25';
    const type = (req.query.type as string) || 'video,channel,playlist';

    const data = await youtubeService.search(token, query, maxResults, type);
    return res.json(data);
  } catch (err) {
    logger.error("YouTube Search Error", { error: err });
    next(err);
  }
});

export default router;
