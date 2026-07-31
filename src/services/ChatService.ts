import { getRedis } from "../config/redis.js";
import logger from "../config/logger.js";

export class ChatService {
  async getChatHistory(sessionId: string): Promise<any[]> {
    try {
      const redis = getRedis();
      if (!redis) {
        logger.warn("Redis is not available. Chat history cannot be loaded.");
        throw new Error("Chat history storage is unavailable");
      }
      
      const data = await redis.get(`chat_history:${sessionId}`);
      if (data) {
        return JSON.parse(data);
      }
      return [];
    } catch (err) {
      logger.error("Failed to load chat history from storage", { error: err });
      throw new Error("Failed to load chat history");
    }
  }

  async saveChatHistory(sessionId: string, messages: any[]): Promise<void> {
    try {
      const redis = getRedis();
      if (!redis) {
        logger.warn("Redis is not available. Chat history cannot be saved.");
        throw new Error("Chat history storage is unavailable");
      }
      
      await redis.set(`chat_history:${sessionId}`, JSON.stringify(messages), "EX", 86400 * 30); // 30 days
    } catch (err) {
      logger.error("Failed to save chat history to storage", { error: err });
      throw new Error("Failed to save chat history");
    }
  }
}

export const chatService = new ChatService();
