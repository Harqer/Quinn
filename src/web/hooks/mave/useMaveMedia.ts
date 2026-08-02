import { useCallback } from 'react';
import { MaveMessage } from './types';
import { useMaveAuth } from './useMaveAuth';
import { logger } from "../../lib/logger";

export function useMaveMedia(
  setMessages: React.Dispatch<React.SetStateAction<MaveMessage[]>>,
  setIsGenerating: React.Dispatch<React.SetStateAction<boolean>>
) {
  const { getBaseUrl, getAuthToken } = useMaveAuth();

  const sendVisionFrame = useCallback(async (image: string) => {
    let actualMimeType = 'image/jpeg';
    const match = image.match(/^data:([^;]+);/);
    if (match) actualMimeType = match[1];

    const base64Data = image.includes(',') ? image.split(',')[1] : image;
    
    const responseId = Date.now().toString();
    setMessages(prev => [{ id: responseId, text: 'Analyzing media and composing track...', sender: 'mave' as const, isReasoningComplete: true }, ...prev].slice(0, 15));
    setIsGenerating(true);

    try {
      const baseUrl = getBaseUrl();
      const authToken = await getAuthToken();
      const headers: Record<string, string> = { 'Content-Type': 'application/json' };
      if (authToken) headers['Authorization'] = `Bearer ${authToken}`;

      const res = await fetch(`${baseUrl}/api/music/generate-from-media`, {
        method: 'POST',
        headers,
        body: JSON.stringify({ data: base64Data, mimeType: actualMimeType })
      });

      if (!res.ok) throw new Error('Failed to generate music from media');
      const data = await res.json();
      
      setMessages(prev => prev.map(m => m.id === responseId ? { 
        ...m, 
        text: 'Here is the track inspired by your media!',
        trackId: data.id || data.trackId || responseId,
        title: data.title || data.trackName || "Media Inspired Track",
        audioUrl: data.url || data.audioUrl,
        artist: data.artist || data.artistName || "Mave",
        coverUrl: data.coverUrl
      } : m));

    } catch (err) {
      logger.error('Media to Music Failed', err);
      setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: 'Failed to analyze media and generate music.' } : m));
    } finally {
      setIsGenerating(false);
    }
  }, [getBaseUrl, getAuthToken, setMessages, setIsGenerating]);

  return { sendVisionFrame };
}
