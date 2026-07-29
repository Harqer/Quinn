import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { audioService, PlayerState, RepeatMode } from '../services/AudioService';
import { Track } from '../services/MusicService';

interface PlayerContextType {
  currentTrack: Track | null;
  playerState: PlayerState;
  currentTime: number;
  duration: number;
  queue: Track[];
  queueIndex: number;
  shuffle: boolean;
  repeat: RepeatMode;
  playTrack: (track: Track) => void;
  playQueue: (tracks: Track[], startIndex?: number) => void;
  skipNext: () => void;
  skipPrevious: () => void;
  toggleShuffle: () => void;
  toggleRepeat: () => void;
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
  const [queue, setQueue] = useState<Track[]>(audioService.queue);
  const [queueIndex, setQueueIndex] = useState<number>(audioService.queueIndex);
  const [shuffle, setShuffle] = useState<boolean>(audioService.shuffle);
  const [repeat, setRepeat] = useState<RepeatMode>(audioService.repeat);

  useEffect(() => {
    const unsubState = audioService.onStateChange(setPlayerState);
    const unsubTrack = audioService.onTrackChange(setCurrentTrack);
    const unsubProgress = audioService.onProgress((cur, dur) => {
      setCurrentTime(cur);
      setDuration(dur);
    });
    const unsubQueue = audioService.onQueueChange((q, i) => {
      setQueue(q);
      setQueueIndex(i);
    });
    const unsubMode = audioService.onModeChange((s, r) => {
      setShuffle(s);
      setRepeat(r);
    });

    return () => {
      unsubState();
      unsubTrack();
      unsubProgress();
      unsubQueue();
      unsubMode();
    };
  }, []);

  const playTrack = (track: Track) => audioService.playTrack(track);
  const playQueue = (tracks: Track[], startIndex: number = 0) => audioService.playQueue(tracks, startIndex);
  const skipNext = () => audioService.skipNext();
  const skipPrevious = () => audioService.skipPrevious();
  const toggleShuffle = () => audioService.toggleShuffle();
  const toggleRepeat = () => audioService.toggleRepeat();
  const togglePlayPause = () => audioService.togglePlayPause();
  const seek = (time: number) => audioService.seek(time);
  const setVolume = (volume: number) => audioService.setVolume(volume);

  return (
    <PlayerContext.Provider value={{
      currentTrack,
      playerState,
      currentTime,
      duration,
      queue,
      queueIndex,
      shuffle,
      repeat,
      playTrack,
      playQueue,
      skipNext,
      skipPrevious,
      toggleShuffle,
      toggleRepeat,
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
