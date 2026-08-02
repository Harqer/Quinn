import { useState, useEffect, useRef, useCallback } from 'react';
import { MaveMessage, MaveMode } from './types';
import { useMaveAuth } from './useMaveAuth';
import { logger } from "../../lib/logger";

export function useMaveState() {
  const [messages, setMessages] = useState<MaveMessage[]>([]);
  const [mode, setMode] = useState<MaveMode>('music');
  const [thinkingText, setThinkingText] = useState("");
  const [coverArtUrl, setCoverArtUrl] = useState<string | null>(null);
  const [videoMotionUrl, setVideoMotionUrl] = useState<string | null>(null);
  const [isGenerating, setIsGenerating] = useState(false);
  
  const { getBaseUrl, getAuthToken } = useMaveAuth();
  const initialLoadDone = useRef(false);

  // Load chat history on mount
  useEffect(() => {
    const loadHistory = async () => {
      try {
        const baseUrl = getBaseUrl();
        const authToken = await getAuthToken();
        const headers: Record<string, string> = { 'Content-Type': 'application/json' };
        if (authToken) headers['Authorization'] = `Bearer ${authToken}`;
        
        const res = await fetch(`${baseUrl}/api/chat/history`, { headers });
        if (res.ok) {
          const data = await res.json();
          if (data.messages && data.messages.length > 0) {
            setMessages(data.messages);
          }
        }
      } catch (err) {
        logger.warn("Failed to load chat history", err);
      } finally {
        initialLoadDone.current = true;
      }
    };
    loadHistory();
  }, [getBaseUrl, getAuthToken]);

  // Save chat history on change
  useEffect(() => {
    if (!initialLoadDone.current || messages.length === 0) return;
    const saveHistory = async () => {
      try {
        const baseUrl = getBaseUrl();
        const authToken = await getAuthToken();
        const headers: Record<string, string> = { 'Content-Type': 'application/json' };
        if (authToken) headers['Authorization'] = `Bearer ${authToken}`;
        
        await fetch(`${baseUrl}/api/chat/history`, {
          method: 'POST',
          headers,
          body: JSON.stringify({ messages })
        });
      } catch (err) {
        logger.warn("Failed to save chat history", err);
      }
    };
    const t = setTimeout(saveHistory, 1000);
    return () => clearTimeout(t);
  }, [messages, getBaseUrl, getAuthToken]);

  const switchMode = useCallback((newMode: MaveMode) => {
    if ('vibrate' in navigator) navigator.vibrate(10);
    setMode(newMode);
  }, []);

  return {
    messages, setMessages,
    mode, setMode, switchMode,
    thinkingText, setThinkingText,
    coverArtUrl, setCoverArtUrl,
    videoMotionUrl, setVideoMotionUrl,
    isGenerating, setIsGenerating
  };
}
