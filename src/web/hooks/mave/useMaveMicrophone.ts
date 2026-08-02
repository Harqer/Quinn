import { useState, useRef, useCallback, useEffect } from 'react';
import { logger } from "../../lib/logger";
import { MaveMessage } from './types';

export function useMaveMicrophone(
  sessionRef: React.MutableRefObject<any>,
  connectLiveSession: () => Promise<void>,
  setMessages: React.Dispatch<React.SetStateAction<MaveMessage[]>>
) {
  const [isRecording, setIsRecording] = useState(false);
  const recorderRef = useRef<MediaRecorder | null>(null);
  const audioStreamRef = useRef<MediaStream | null>(null);

  const toggleRecording = useCallback(async () => {
    if (isRecording) {
      recorderRef.current?.stop();
      if (audioStreamRef.current) {
        audioStreamRef.current.getTracks().forEach(track => track.stop());
        audioStreamRef.current = null;
      }
      setIsRecording(false);
    } else {
      if (!sessionRef.current) {
        await connectLiveSession();
      }
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: { sampleRate: 16000, channelCount: 1 } });
        audioStreamRef.current = stream;
        const recorder = new MediaRecorder(stream, { mimeType: 'audio/webm' });
        recorderRef.current = recorder;
        recorder.ondataavailable = async (e) => {
          if (e.data.size > 0 && sessionRef.current) {
            const buffer = await e.data.arrayBuffer();
            const base64 = btoa(String.fromCharCode(...new Uint8Array(buffer)));
            sessionRef.current.sendRealtimeInput({
              audio: { data: base64, mimeType: 'audio/pcm;rate=16000' }
            });
          }
        };
        recorder.start(250);
        setIsRecording(true);
      } catch (err) {
        logger.error('Microphone access denied', err);
        setMessages(prev => [{ id: Date.now().toString(), text: 'Error: Microphone access denied. Please allow mic access and try again.', sender: 'mave' as const }, ...prev].slice(0, 15));
      }
    }
  }, [isRecording, sessionRef, connectLiveSession, setMessages]);

  useEffect(() => {
    return () => {
      if (audioStreamRef.current) {
        audioStreamRef.current.getTracks().forEach(track => track.stop());
      }
    };
  }, []);

  return { isRecording, toggleRecording };
}
