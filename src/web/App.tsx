import React, { useState, useEffect } from 'react';
import { getAuth, onAuthStateChanged, User } from "firebase/auth";

import { LoginScreen } from './features/auth/LoginScreen';
import { HomeScreen } from './features/home/HomeScreen';
import { SearchScreen } from './features/search/SearchScreen';
import { LibraryScreen } from './features/library/LibraryScreen';
import { AlbumView } from './features/album/AlbumView';

import { BottomNav } from './components/organisms/BottomNav';
import { PlayerBar } from './components/organisms/PlayerBar';

type Route = 'login' | 'home' | 'search' | 'library' | 'album';

export const App: React.FC = () => {
  const [route, setRoute] = useState<Route>('login');
  const [currentUser, setCurrentUser] = useState<User | null>(null);

  useEffect(() => {
    const auth = getAuth();
    return onAuthStateChanged(auth, (user) => {
      setCurrentUser(user);
      if (user && route === 'login') {
        setRoute('home');
      }
    });
  }, [route]);

  const handleLogin = () => {
    setRoute('home');
  };

  const showBottomNav = ['home', 'search', 'library'].includes(route);
  const showPlayerBar = ['home', 'search', 'library', 'album'].includes(route);

  return (
    <div className="flex flex-col h-screen w-full bg-background text-text-primary font-sans overflow-hidden select-none">
      <main className="flex-1 overflow-hidden relative w-full max-w-[600px] mx-auto border-x border-outline/30">
        {route === 'login' && <LoginScreen onLogin={handleLogin} />}
        {route === 'home' && <HomeScreen />}
        {route === 'search' && <SearchScreen />}
        {route === 'library' && <LibraryScreen />}
        {route === 'album' && <AlbumView onBack={() => setRoute('home')} />}
        
        {showPlayerBar && (
          <div onClick={() => setRoute('album')} className="w-full">
            <PlayerBar />
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
