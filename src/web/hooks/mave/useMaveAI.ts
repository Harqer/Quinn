import { useRef, useState, useCallback, useEffect } from 'react';
import { GoogleGenAI, Modality } from '@google/genai';
import { MaveMessage } from './types';
import { functionDeclarations } from './core';
import { logger } from "../../lib/logger";
import { getFunctions, httpsCallable } from 'firebase/functions';
import { useMaveTools } from './useMaveTools';

export function useMaveAI(
  setMessages: React.Dispatch<React.SetStateAction<MaveMessage[]>>,
  setCoverArtUrl: React.Dispatch<React.SetStateAction<string | null>>,
  setVideoMotionUrl: React.Dispatch<React.SetStateAction<string | null>>,
  setIsGenerating: React.Dispatch<React.SetStateAction<boolean>>,
  pushAudio: (blob: Blob) => void,
  clearAudioQueue: () => void
) {
  const [isConnected, setIsConnected] = useState(false);
  const aiRef = useRef<GoogleGenAI | null>(null);
  const aiRestRef = useRef<GoogleGenAI | null>(null);
  const sessionRef = useRef<any>(null);
  const { handleToolCall } = useMaveTools(setMessages, setCoverArtUrl, setVideoMotionUrl);

  const initAI = useCallback(async () => {
    if (!aiRef.current) {
      try {
        const functions = getFunctions();
        const getLiveToken = httpsCallable(functions, 'getLiveToken');
        const res = await getLiveToken();
        const data = res.data as { token: string };
        if (!data.token) throw new Error('No token returned from server');
        aiRef.current = new GoogleGenAI({ apiKey: data.token });
      } catch (e) {
        throw new Error(`Failed to initialize AI: ${e instanceof Error ? e.message : String(e)}`);
      }
    }
    return aiRef.current;
  }, []);

  const initAIRest = useCallback(async () => {
    if (!aiRestRef.current) {
      try {
        const functions = getFunctions();
        const getLiveToken = httpsCallable(functions, 'getLiveToken');
        const res = await getLiveToken();
        const data = res.data as { token: string };
        if (!data.token) throw new Error('No rest token returned from server');
        aiRestRef.current = new GoogleGenAI({ apiKey: data.token });
      } catch (e) {
        throw new Error(`Failed to initialize AI Rest: ${e instanceof Error ? e.message : String(e)}`);
      }
    }
    return aiRestRef.current;
  }, []);

  const connectLiveSession = useCallback(async () => {
    const ai = await initAI();
    try {
      const session = await ai.live.connect({
        model: 'gemini-3.1-flash-live-preview',
        config: {
          responseModalities: [Modality.AUDIO],
          systemInstruction: {
            parts: [{ text: "You are Mave, the Executive Creative Director and Master Musical Orchestrator. Help the user create and tweak music. Respond naturally, conversationally. Do NOT use markdown formatting." }]
          },
          tools: [{ functionDeclarations: functionDeclarations as any }]
        },
        callbacks: {
          onopen: () => {
            setIsConnected(true);
            logger.info('Live API Connected');
          },
          onmessage: (response: any) => {
            const content = response.serverContent;
            if (content?.modelTurn?.parts) {
              for (const part of content.modelTurn.parts) {
                if (part.inlineData) {
                  const audioBytes = Uint8Array.from(atob(part.inlineData.data), c => c.charCodeAt(0));
                  pushAudio(new Blob([audioBytes], { type: 'audio/pcm;rate=24000' }));
                }
                if (part.functionCall) {
                  handleToolCall(part.functionCall);
                }
              }
            }
            if (content?.outputTranscription) {
              setMessages(prev => [{ id: Date.now().toString(), text: content.outputTranscription.text, sender: 'mave' as const }, ...prev].slice(0, 15));
            }
            if (content?.interrupted) {
              clearAudioQueue();
            }
          },
          onerror: (error: any) => logger.error('Live API Error:', error),
          onclose: () => {
            setIsConnected(false);
            logger.info('Live API Closed');
          }
        }
      });
      sessionRef.current = session;
    } catch (err: any) {
      logger.error('Failed to connect to Live API', err);
      const errorMessage = err?.message || 'An unknown error occurred.';
      setMessages(prev => [{ id: Date.now().toString(), text: `Error connecting live session: ${errorMessage}`, sender: 'mave' as const }, ...prev].slice(0, 15));
    }
  }, [initAI, handleToolCall, pushAudio, clearAudioQueue, setMessages]);

  useEffect(() => {
    return () => {
      sessionRef.current?.close();
    };
  }, []);

  const sendText = async (text: string) => {
    setIsGenerating(true);
    const userMsgId = Date.now().toString();
    setMessages(prev => [{ id: userMsgId, text, sender: 'user' as const }, ...prev].slice(0, 15));

    if (sessionRef.current) {
      sessionRef.current.sendRealtimeInput({ text });
      return;
    }

    const responseId = (Date.now() + 1).toString();
    try {
      const ai = await initAIRest();
      let fullText = '';
      let addedMessage = false;
      let responseStream;
      
      const createStream = async (modelName: string) => {
        return await ai.models.generateContentStream({
          model: modelName,
          contents: text,
          config: {
            systemInstruction: 'You are Mave, the Executive Creative Director and Master Musical Orchestrator. First, you MUST provide a raw, unstructured, stream-of-consciousness thinking process inside <think>...</think> XML tags. Do NOT use numbered lists or formal steps in your thinking; just think aloud naturally. After the closing </think> tag, generate your final conversational response. Do NOT use any markdown formatting in your final response. Speak as a natural voice assistant.',
            tools: [{ functionDeclarations: functionDeclarations as any }]
          }
        });
      };

      try {
        responseStream = await createStream('gemini-3.6-flash');
        const iterator = responseStream[Symbol.asyncIterator]();
        const first = await iterator.next();
        
        const processChunk = async (chunk: any) => {
          if (chunk.functionCalls && chunk.functionCalls.length > 0) {
            for (const call of chunk.functionCalls) {
              await handleToolCall(call);
            }
          }
          let chunkText = '';
          try { chunkText = chunk.text || ''; } catch { /* ignore */ }
          if (chunkText) {
            fullText += chunkText;
            let reasoning = '';
            let parsedText = '';
            let isReasoningComplete = false;
            
            const thinkStart = fullText.indexOf('<think>');
            const thinkEnd = fullText.indexOf('</think>');
            
            if (thinkStart !== -1) {
              if (thinkEnd !== -1) {
                reasoning = fullText.substring(thinkStart + 7, thinkEnd).trim();
                parsedText = fullText.substring(thinkEnd + 8).trim();
                isReasoningComplete = true;
              } else {
                reasoning = fullText.substring(thinkStart + 7).trim();
              }
            } else {
              parsedText = fullText.trim();
              isReasoningComplete = true;
            }

            if (!addedMessage) {
              if (parsedText || reasoning) {
                setMessages(prev => [{ id: responseId, text: parsedText, reasoning, isReasoningComplete, sender: 'mave' as const }, ...prev].slice(0, 15));
                addedMessage = true;
              }
            } else {
              setMessages(prev => prev.map(m => m.id === responseId ? { ...m, text: parsedText, reasoning, isReasoningComplete } : m));
            }
          }
        };

        if (!first.done) {
          await processChunk(first.value);
          for await (const chunk of iterator) {
            await processChunk(chunk);
          }
        }
      } catch (e: any) {
        console.warn('Mave orchestration on 3.6-flash failed, falling back to 3.5-flash-lite', e);
        responseStream = await createStream('gemini-3.5-flash-lite');
        for await (const chunk of responseStream) {
          if (chunk.functionCalls && chunk.functionCalls.length > 0) {
            for (const call of chunk.functionCalls) {
              await handleToolCall(call);
            }
          }
          let chunkText = '';
          try { chunkText = chunk.text || ''; } catch {}
          if (chunkText) {
            fullText += chunkText;
            setMessages(prev => {
              if (!addedMessage) { addedMessage = true; return [{ id: responseId, text: fullText, sender: 'mave' as const }, ...prev].slice(0, 15); }
              return prev.map(m => m.id === responseId ? { ...m, text: fullText } : m);
            });
          }
        }
      }
    } catch (err: any) {
      console.error('Failed to send text to Mave:', err);
      setMessages(prev => [{ id: responseId, text: `Error: An unexpected error occurred.`, sender: 'mave', isError: true }, ...prev]);
    } finally {
      setIsGenerating(false);
    }
  };

  const warp = (params: { bpm?: number; density?: number }) => {
    if (sessionRef.current) {
      sessionRef.current.sendRealtimeInput({ text: `Tweak the track: BPM ${params.bpm}, Density ${params.density}` });
    }
  };

  const requestCoverArt = async (prompt: string, hq = false) => {
    await handleToolCall({ name: 'generate_cover_art', args: { prompt, hq } });
  };

  const requestVideo = async (prompt: string) => {
    await handleToolCall({ name: 'generate_video', args: { prompt } });
  };

  return { isConnected, sessionRef, connectLiveSession, sendText, warp, requestCoverArt, requestVideo };
}
