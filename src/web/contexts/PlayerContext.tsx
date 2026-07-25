import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { audioService, PlayerState } from '../services/AudioService';
import { Track } from '../services/MusicService';

interface PlayerContextType {
  currentTrack: Track | null;
  playerState: PlayerState;
  currentTime: number;
  duration: number;
  playTrack: (track: Track) => void;
  togglePlayPause: () => void;
  seek: (time: number) => void;
  setVolume: (volume: number) => void;
}

const PlayerContext = createContext<PlayerContextType | undefined>(undefined);

export const PlayerProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [currentTrack, setCurrentTrack] = useState<Track | null>(audioService.currentTrack);
  const [playerState, setPlayerState] = useState<PlayerState>(audioService.state);
  const [currentTime, setCurrentTime] = useState<number>(0);
  const [duration, setDuration] = useState<number>(0);

  useEffect(() => {
    const unsubState = audioService.onStateChange(setPlayerState);
    const unsubTrack = audioService.onTrackChange(setCurrentTrack);
    const unsubProgress = audioService.onProgress((cur, dur) => {
      setCurrentTime(cur);
      setDuration(dur);
    });

    return () => {
      unsubState();
      unsubTrack();
      unsubProgress();
    };
  }, []);

  const playTrack = (track: Track) => audioService.playTrack(track);
  const togglePlayPause = () => audioService.togglePlayPause();
  const seek = (time: number) => audioService.seek(time);
  const setVolume = (volume: number) => audioService.setVolume(volume);

  return (
    <PlayerContext.Provider value={{
      currentTrack,
      playerState,
      currentTime,
      duration,
      playTrack,
      togglePlayPause,
      seek,
      setVolume
    }}>
      {children}
    </PlayerContext.Provider>
  );
};

export const usePlayerContext = () => {
  const context = useContext(PlayerContext);
  if (context === undefined) {
    throw new Error('usePlayerContext must be used within a PlayerProvider');
  }
  return context;
};
