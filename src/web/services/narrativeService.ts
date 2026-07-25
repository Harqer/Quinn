import { logger } from "../lib/logger";
export const narrativeService = {
  getVoices: async () => {
    try {
      const baseUrl = import.meta.env.VITE_API_URL || '';
      const res = await fetch(`${baseUrl}/api/music/podcast/voices`);
      if (!res.ok) throw new Error('Failed to fetch voices');
      return await res.json();
    } catch (e) {
      logger.error(e instanceof Error ? e.message : String(e));
      return [];
    }
  },
  generatePodcast: async (prompt: string, voice: string) => {
    const baseUrl = import.meta.env.VITE_API_URL || '';
    const res = await fetch(`${baseUrl}/api/music/podcast/generate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ prompt: prompt.trim(), voice })
    });
    if (!res.ok) throw new Error('Podcast generation request failed');
    return res;
  }
};
