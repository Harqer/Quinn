export const narrativeService = {
  getVoices: async () => {
    try {
      const res = await fetch('/api/music/podcast/voices');
      if (!res.ok) throw new Error('Failed to fetch voices');
      return await res.json();
    } catch (e) {
      console.error(e);
      return [];
    }
  },
  generatePodcast: async (prompt: string, voice: string) => {
    const res = await fetch('/api/music/podcast/generate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ prompt: prompt.trim(), voice })
    });
    if (!res.ok) throw new Error('Podcast generation request failed');
    return res;
  }
};
