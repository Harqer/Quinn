import React, { createContext, useContext, useState, ReactNode } from 'react';
import { Track } from '../hooks/useTracks';

interface AppContextType {
  activeAlbumId: string | null;
  setActiveAlbumId: (id: string | null) => void;
  currentTrack: Track | null;
  setCurrentTrack: (track: Track | null) => void;
  isPlaying: boolean;
  setIsPlaying: (playing: boolean) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [activeAlbumId, setActiveAlbumId] = useState<string | null>(null);
  const [currentTrack, setCurrentTrack] = useState<Track | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);

  return (
    <AppContext.Provider value={{ activeAlbumId, setActiveAlbumId, currentTrack, setCurrentTrack, isPlaying, setIsPlaying }}>
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
