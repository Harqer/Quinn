import { Router, Request, Response, NextFunction } from "express";
import { optionalFirebaseToken, verifyAppCheck } from "../middlewares/auth.js";
import logger from "../config/logger.js";
import { chatService } from "../services/ChatService.js";

const chatRouter = Router();

/**
 * GET /api/chat/history?sessionId=...
 * Load chat history
 */
chatRouter.get("/history", optionalFirebaseToken, verifyAppCheck, async (req: Request, res: Response, next: NextFunction) => {
  const sessionId = req.query.sessionId as string || (req as any).user?.uid || "anon";
  try {
    const messages = await chatService.getChatHistory(sessionId);
    return res.json({ messages });
  } catch (err) {
    logger.error("Failed to load chat history", { error: err });
    next(err);
  }
});

/**
 * POST /api/chat/history
 * Save chat history
 */
chatRouter.post("/history", optionalFirebaseToken, verifyAppCheck, async (req: Request, res: Response, next: NextFunction) => {
  const { sessionId, messages } = req.body;
  const targetSessionId = sessionId || (req as any).user?.uid || "anon";
  
  if (!messages || !Array.isArray(messages)) {
    return res.status(400).json({ error: "messages array is required" });
  }

  try {
    await chatService.saveChatHistory(targetSessionId, messages);
    return res.json({ success: true });
  } catch (err) {
    logger.error("Failed to save chat history", { error: err });
    next(err);
  }
});

export default chatRouter;
