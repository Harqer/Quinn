import React, { useState, useEffect, lazy, Suspense } from 'react';
import { getAuth, onAuthStateChanged, User } from "firebase/auth";

const WelcomeScreen = lazy(() => import('./features/auth/WelcomeScreen').then(m => ({ default: m.WelcomeScreen })));
const LoginScreen = lazy(() => import('./features/auth/LoginScreen').then(m => ({ default: m.LoginScreen })));
const HomeScreen = lazy(() => import('./features/home/HomeScreen').then(m => ({ default: m.HomeScreen })));
const SearchScreen = lazy(() => import('./features/search/SearchScreen').then(m => ({ default: m.SearchScreen })));
const LibraryScreen = lazy(() => import('./features/library/LibraryScreen').then(m => ({ default: m.LibraryScreen })));
const AlbumView = lazy(() => import('./features/album/AlbumView').then(m => ({ default: m.AlbumView })));
const PodcastGeneratorScreen = lazy(() => import('./features/podcast/PodcastGeneratorScreen').then(m => ({ default: m.PodcastGeneratorScreen })));
const DevicesScreen = lazy(() => import('./features/devices/DevicesScreen').then(m => ({ default: m.DevicesScreen })));
const DeleteAccountScreen = lazy(() => import('./features/auth/DeleteAccountScreen').then(m => ({ default: m.DeleteAccountScreen })));

import { BottomNav } from './components/organisms/BottomNav';
import { PlayerBar } from './components/organisms/PlayerBar';
import { useAppContext } from './contexts/AppContext';

type Route = 'welcome' | 'login' | 'home' | 'search' | 'library' | 'album' | 'podcast' | 'devices' | 'delete-account';

export const App: React.FC = () => {
  const [route, setRoute] = useState<Route>('welcome');
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const { currentTrack, isPlaying, setIsPlaying } = useAppContext();

  useEffect(() => {
    const auth = getAuth();
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      setCurrentUser(user);
    });
    return () => unsubscribe();
  }, []);

  useEffect(() => {
    setRoute((currentRoute) => {
      const path = window.location.pathname;
      if (path === '/delete-account') {
        return 'delete-account';
      }
      
      if (currentUser && (currentRoute === 'welcome' || currentRoute === 'login')) {
        return 'home';
      } else if (!currentUser && currentRoute !== 'login' && currentRoute !== 'welcome' && currentRoute !== 'delete-account') {
        return 'welcome';
      }
      return currentRoute;
    });
  }, [currentUser]);

  const handleLogin = () => {
    setRoute('home');
  };

  const showBottomNav = ['home', 'search', 'library', 'podcast', 'devices'].includes(route);
  const showPlayerBar = ['home', 'search', 'library', 'album', 'podcast', 'devices'].includes(route);

  return (
    <div className="flex flex-col h-screen w-full bg-background text-text-primary font-sans overflow-hidden select-none">
      <main className="flex-1 overflow-hidden relative w-full max-w-[600px] mx-auto border-x border-outline/30">
        <Suspense fallback={
          <div className="flex flex-col items-center justify-center h-full w-full bg-[#121414] text-[#1db954] gap-3">
            <div className="w-8 h-8 border-3 border-primary border-t-transparent rounded-full animate-spin"></div>
            <span className="text-xs font-bold text-text-secondary">Loading Mave...</span>
          </div>
        }>
          {route === 'welcome' && <WelcomeScreen onSignUp={() => setRoute('login')} onLogin={() => setRoute('login')} />}
          {route === 'login' && <LoginScreen onLogin={handleLogin} />}
          {route === 'home' && <HomeScreen />}
          {route === 'search' && <SearchScreen />}
          {route === 'library' && <LibraryScreen />}
          {route === 'podcast' && <PodcastGeneratorScreen />}
          {route === 'devices' && <DevicesScreen onBack={() => setRoute('home')} />}
          {route === 'album' && <AlbumView onBack={() => setRoute('home')} />}
          {route === 'delete-account' && <DeleteAccountScreen />}
        </Suspense>
        
        {showPlayerBar && (
          <div onClick={() => setRoute('album')} className="w-full">
            <PlayerBar 
              trackName={currentTrack?.title}
              artistName={currentTrack?.artist}
              albumArtUrl={currentTrack?.albumArtUrl}
              isPlaying={isPlaying}
              onPlayPause={(e) => {
                e.stopPropagation();
                setIsPlaying(!isPlaying);
              }}
            />
          </div>
        )}
        
        {showBottomNav && (
          <div className="w-full">
            <BottomNav currentRoute={route} onNavigate={(r: any) => setRoute(r)} />
          </div>
        )}
      </main>
    </div>
  );
};
