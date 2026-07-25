import React, { useState, useEffect, lazy, Suspense, createContext, useContext } from 'react';
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

export const NavigationContext = createContext<(route: Route) => void>(() => {});
export const useNavigate = () => useContext(NavigationContext);

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
    <NavigationContext.Provider value={setRoute}>
      <div className="flex h-full w-full p-0 md:p-6 md:gap-6 gap-2 text-text-primary font-sans overflow-hidden select-none bg-black pb-0 md:pb-[92px]">
        
        {showBottomNav && (
          <aside className="hidden md:flex w-[280px] lg:w-[420px] h-full flex-col gap-6 z-40">
            <BottomNav currentRoute={route} onNavigate={(r: any) => setRoute(r)} />
          </aside>
        )}

        <main className="flex-1 bg-[#121212] md:rounded-xl flex flex-col overflow-hidden relative group">
          <div className="flex-1 overflow-y-auto custom-scrollbar w-full relative pb-24 md:pb-0">
            <Suspense fallback={
              <div className="flex flex-col items-center justify-center h-full w-full bg-[#121414] text-[#1db954] gap-3">
                <div className="w-8 h-8 border-3 border-primary border-t-transparent rounded-full animate-spin"></div>
                <span className="text-xs font-bold text-text-secondary">Loading Mave...</span>
              </div>
            }>
              {route === 'welcome' && <LoginScreen onLogin={handleLogin} initialMode="signup" />}
              {route === 'login' && <LoginScreen onLogin={handleLogin} initialMode="login" />}
              {route === 'home' && <HomeScreen />}
              {route === 'search' && <SearchScreen />}
              {route === 'library' && <LibraryScreen />}
              {route === 'podcast' && <PodcastGeneratorScreen />}
              {route === 'devices' && <DevicesScreen onBack={() => setRoute('home')} />}
              {route === 'album' && <AlbumView onBack={() => setRoute('home')} />}
              {route === 'delete-account' && <DeleteAccountScreen />}
            </Suspense>
          </div>
        </main>

        {showBottomNav && (
          <div className="md:hidden fixed bottom-0 left-0 right-0 z-50 border-t border-outline/30 bg-background pb-safe">
            <BottomNav currentRoute={route} onNavigate={(r: any) => setRoute(r)} />
          </div>
        )}
        
        {showPlayerBar && (
          <footer 
            onClick={() => setRoute('album')} 
            className="fixed bottom-[60px] md:bottom-0 left-0 right-0 h-[72px] md:h-[92px] bg-black px-2 md:px-6 flex items-center justify-between z-40 cursor-pointer hover:bg-surface-container transition-colors border-t border-surface-container md:border-t-0"
          >
            <PlayerBar 
              trackName={currentTrack?.title}
              artistName={currentTrack?.artist}
              albumArtUrl={currentTrack?.albumArtUrl}
              isPlaying={isPlaying}
              onPlayPause={(e) => {
                e.stopPropagation();
                setIsPlaying(!isPlaying);
              }}
              onShuffle={() => useAppContext().sendPlaybackCommand('toggle_shuffle')}
              onSkipPrevious={() => useAppContext().sendPlaybackCommand('skip_previous')}
              onSkipNext={() => useAppContext().sendPlaybackCommand('skip_next')}
              onRepeat={() => useAppContext().sendPlaybackCommand('toggle_repeat')}
            />
          </footer>
        )}
      </div>
    </NavigationContext.Provider>
  );
};
