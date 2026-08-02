import { useRef, useCallback } from 'react';
import { logger } from "../../lib/logger";

export function useMavePlayback() {
  const audioQueue = useRef<Blob[]>([]);
  const isPlaying = useRef(false);

  const playNextAudio = useCallback(async () => {
    if (isPlaying.current || audioQueue.current.length === 0) return;
    isPlaying.current = true;
    const blob = audioQueue.current.shift();
    if (!blob) return;
    try {
      const url = URL.createObjectURL(blob);
      const audio = new Audio(url);
      audio.onended = () => {
        URL.revokeObjectURL(url);
        isPlaying.current = false;
        playNextAudio();
      };
      await audio.play();
    } catch (e) {
      logger.error('Audio playback failed', e);
      isPlaying.current = false;
      playNextAudio();
    }
  }, []);

  const sendPlaybackCommand = useCallback((commandType: string) => {
    logger.info('Playback command', commandType);
  }, []);

  const pushAudio = useCallback((blob: Blob) => {
    audioQueue.current.push(blob);
    playNextAudio();
  }, [playNextAudio]);

  const clearAudioQueue = useCallback(() => {
    audioQueue.current = [];
  }, []);

  return { playNextAudio, sendPlaybackCommand, pushAudio, clearAudioQueue };
}
