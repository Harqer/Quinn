import React, { createContext, useContext, useState, ReactNode } from 'react';
import { Track } from '../hooks/useTracks';

interface AppContextType {
  activeAlbumId: string | null;
  setActiveAlbumId: (id: string | null) => void;
  currentTrack: Track | null;
  setCurrentTrack: (track: Track | null) => void;
  isPlaying: boolean;
  setIsPlaying: (playing: boolean) => void;
  sendPlaybackCommand: (commandType: string) => void;
  setPlaybackCommandSender: (sender: (cmd: string) => void) => void;
  isQueueVisible: boolean;
  setIsQueueVisible: (visible: boolean) => void;
  globalVolume: number;
  setGlobalVolume: (vol: number) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [activeAlbumId, setActiveAlbumId] = useState<string | null>(null);
  const [currentTrack, setCurrentTrack] = useState<Track | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [playbackSender, setPlaybackSender] = useState<(cmd: string) => void>(() => () => {});
  const [isQueueVisible, setIsQueueVisible] = useState(false);
  const [globalVolume, setGlobalVolume] = useState(1.0);

  const sendPlaybackCommand = (cmd: string) => {
    playbackSender(cmd);
  };

  return (
    <AppContext.Provider value={{ 
      activeAlbumId, setActiveAlbumId, 
      currentTrack, setCurrentTrack, 
      isPlaying, setIsPlaying,
      sendPlaybackCommand, setPlaybackCommandSender: setPlaybackSender,
      isQueueVisible, setIsQueueVisible,
      globalVolume, setGlobalVolume
    }}>
      {children}
    </AppContext.Provider>
  );
};

export const useAppContext = () => {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useAppContext must be used within an AppProvider');
  }
  return context;
};
