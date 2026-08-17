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
        
        const audioContext = new AudioContext({ sampleRate: 16000 });
        const source = audioContext.createMediaStreamSource(stream);
        const processor = audioContext.createScriptProcessor(4096, 1, 1);
        
        processor.onaudioprocess = (e) => {
          if (!sessionRef.current || !isRecording) return;
          const inputData = e.inputBuffer.getChannelData(0);
          
          // Convert Float32Array to Int16Array
          const pcmData = new Int16Array(inputData.length);
          for (let i = 0; i < inputData.length; i++) {
            const s = Math.max(-1, Math.min(1, inputData[i]));
            pcmData[i] = s < 0 ? s * 0x8000 : s * 0x7FFF;
          }
          
          // Convert Int16Array to base64
          const buffer = new ArrayBuffer(pcmData.length * 2);
          const view = new DataView(buffer);
          for (let i = 0; i < pcmData.length; i++) {
            view.setInt16(i * 2, pcmData[i], true); // true for little-endian
          }
          
          const bytes = new Uint8Array(buffer);
          let binary = '';
          for (let i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i]);
          }
          const base64 = btoa(binary);

          // The Google Gen AI SDK expects [{ mimeType, data }] for sendRealtimeInput
          sessionRef.current.sendRealtimeInput([{
            mimeType: 'audio/pcm;rate=16000',
            data: base64
          }]);
        };

        source.connect(processor);
        processor.connect(audioContext.destination);
        recorderRef.current = { stop: () => {
          source.disconnect();
          processor.disconnect();
          audioContext.close();
        }} as any;
        
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
