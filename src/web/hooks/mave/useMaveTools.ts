import { useCallback } from 'react';
import { MaveMessage } from './types';
import { readSSE } from './core';
import { logger } from "../../lib/logger";
import { useMaveAuth } from './useMaveAuth';

export function useMaveTools(
  setMessages: React.Dispatch<React.SetStateAction<MaveMessage[]>>,
  setCoverArtUrl: React.Dispatch<React.SetStateAction<string | null>>,
  setVideoMotionUrl: React.Dispatch<React.SetStateAction<string | null>>
) {
  const { getBaseUrl, getAuthToken } = useMaveAuth();

  const handleToolCall = useCallback(async (toolCall: { name: string; args?: any; id?: string }) => {
    logger.info("Tool Call Received", toolCall);
    const baseUrl = getBaseUrl();
    const authToken = await getAuthToken();
    const headers: Record<string, string> = { 'Content-Type': 'application/json' };
    if (authToken) headers['Authorization'] = `Bearer ${authToken}`;

    try {
      switch (toolCall.name) {
        case 'generate_full_track': {
          const responseId = Date.now().toString();
          let reasoningText = '';
          setMessages(prev => [{ id: responseId, text: '', sender: 'mave' as const }, ...prev].slice(0, 15));

          for await (const event of readSSE(`${baseUrl}/api/music/lyria/full`, { prompt: toolCall.args?.prompt }, authToken)) {
            if (event.type === 'reasoning' && event.text) {
              reasoningText += event.text;
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, reasoning: reasoningText } : m));
            } else if (event.type === 'status' || event.type === 'audio_chunk') {
              if (event.type === 'status') {
                 setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: event.message, isReasoningComplete: true } : m));
              }
            } else if (event.type === 'done' && event.audioUrl) {
              setMessages(prev => prev.map(m => m.id === responseId ? {
                ...m,
                text: reasoningText || 'Here is your track!',
                audioUrl: event.audioUrl,
                title: event.trackName,
                voice: event.artistName,
                type: 'track'
              } : m));
              const audio = new Audio(event.audioUrl);
              audio.play().catch(e => logger.error('Autoplay failed', e));
            } else if (event.type === 'error') {
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: `Error: ${event.message}` } : m));
            }
          }
          break;
        }

        case 'tweak_instrumentation': {
          const responseId = Date.now().toString();
          let reasoningText = '';
          setMessages(prev => [{ id: responseId, text: '', sender: 'mave' as const }, ...prev].slice(0, 15));

          for await (const event of readSSE(`${baseUrl}/api/music/lyria/steer`, {
            prompt: toolCall.args?.prompt,
            bpm: toolCall.args?.bpm,
            density: toolCall.args?.density,
            brightness: toolCall.args?.brightness
          }, authToken)) {
            if (event.type === 'reasoning' && event.text) {
              reasoningText += event.text;
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, reasoning: reasoningText } : m));
            } else if (event.type === 'status' || event.type === 'audio_chunk') {
              if (event.type === 'status') {
                 setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: event.message, isReasoningComplete: true } : m));
              }
            } else if (event.type === 'done' && event.audioUrl) {
              setMessages(prev => prev.map(m => m.id === responseId ? {
                ...m,
                text: reasoningText || 'Instrumentation updated.',
                audioUrl: event.audioUrl,
                type: 'track'
              } : m));
              const audio = new Audio(event.audioUrl);
              audio.play().catch(e => logger.error('Autoplay failed', e));
            } else if (event.type === 'error') {
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: `Error: ${event.message}` } : m));
            }
          }
          break;
        }

        case 'generate_cover_art': {
          const res = await fetch(`${baseUrl}/api/music/cover`, {
            method: 'POST', headers,
            body: JSON.stringify({ prompt: toolCall.args?.prompt, hq: toolCall.args?.hq })
          });
          const data = await res.json();
          if (data.url) {
            setCoverArtUrl(data.url);
            setMessages(prev => [{ id: Date.now().toString(), text: 'Cover art updated!', sender: 'mave' as const, coverUrl: data.url, type: 'cover_art' }, ...prev].slice(0, 15));
          } else {
            throw new Error(data.error || 'Cover art generation failed');
          }
          break;
        }

        case 'generate_video': {
          const responseId = Date.now().toString();
          setMessages(prev => [{ id: responseId, text: 'Generating your music video...', sender: 'mave' as const }, ...prev].slice(0, 15));
          const res = await fetch(`${baseUrl}/api/music/video`, {
            method: 'POST', headers,
            body: JSON.stringify({ prompt: toolCall.args?.prompt })
          });
          const data = await res.json();
          if (data.url) {
            setVideoMotionUrl(data.url);
            setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: 'Your music video is ready!', videoUrl: data.url, type: 'video' } : m));
          } else {
            throw new Error(data.error || 'Video generation failed');
          }
          break;
        }

        default:
          logger.warn('Unknown tool call', toolCall.name);
      }
    } catch (err: any) {
      logger.error('Failed to execute tool', err);
      setMessages(prev => [{ id: Date.now().toString(), text: `Error: Tool execution failed: ${err?.message || 'Unknown error'}`, sender: 'mave' as const }, ...prev].slice(0, 15));
    }
  }, [getBaseUrl, getAuthToken, setMessages, setCoverArtUrl, setVideoMotionUrl]);

  return { handleToolCall };
}
