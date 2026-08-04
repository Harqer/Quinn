import React, { useState } from 'react';
import { MoodAdScreen } from './MoodAdScreen';
import { usePlayerContext } from '../../contexts/PlayerContext';
import { getAuth } from 'firebase/auth';
import { readSSE } from '../../utils/sse';
import { Track } from '../../services/MusicService';

interface MoodAdScreenWrapperProps {
  onClose: () => void;
}

export const MoodAdScreenWrapper: React.FC<MoodAdScreenWrapperProps> = ({ onClose }) => {
  const { addToQueue } = usePlayerContext();
  const [isStarting, setIsStarting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const getBaseUrl = () => (import.meta.env.VITE_API_URL as string) || 'http://127.0.0.1:8081';

  const generateTrack = async (mood: string, playImmediately: boolean) => {
    try {
      const baseUrl = getBaseUrl();
      const auth = getAuth();
      const token = await auth.currentUser?.getIdToken();

      for await (const event of readSSE(`${baseUrl}/api/music/lyria/full`, { prompt: mood }, token)) {
        if (event.type === 'error') {
          throw new Error(event.message || 'Generation failed');
        } else if (event.type === 'done' && event.audioUrl) {
          const newTrack: Track = {
            id: Date.now().toString() + Math.random().toString(36).substring(7),
            title: event.trackName || `${mood} Mix`,
            artist: event.artistName || 'Lyria AI',
            audioUrl: event.audioUrl,
            albumArtUrl: '', // Could be generated or left empty to use AnimatedGradient fallback
            duration: 180, // Default duration, audioService will update with actual
          };
          
          addToQueue(newTrack);
          
          if (playImmediately) {
            onClose(); // Close the screen once the first track is queued and playing
          }
          break; // Done with this stream
        }
      }
    } catch (e) {
      console.error('Error generating track:', e);
      if (playImmediately) {
        setError(e instanceof Error ? e.message : 'Failed to generate mix.');
        setIsStarting(false);
      }
    }
  };

  const handleStartMix = async (mood: string) => {
    setIsStarting(true);
    setError(null);
    
    // 1. Generate the first track and wait for it
    await generateTrack(mood, true);
    
    // 2. Generate 4 more tracks in the background
    // We don't await these so the UI can proceed
    for (let i = 0; i < 4; i++) {
      // Fire and forget, they will append to queue when done
      generateTrack(mood, false);
    }
  };

  return (
    <MoodAdScreen 
      onClose={onClose} 
      onStartMix={handleStartMix} 
      isStarting={isStarting}
      generationError={error}
    />
  );
};
