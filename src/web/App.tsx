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
const ChatScreen = lazy(() => import('./features/chat/ChatScreen').then(m => ({ default: m.ChatScreen })));
const ProfileScreen = lazy(() => import('./features/profile/ProfileScreen').then(m => ({ default: m.ProfileScreen })));
const SettingsScreen = lazy(() => import('./features/settings/SettingsScreen').then(m => ({ default: m.SettingsScreen })));
const PremiumPlansScreen = lazy(() => import('./features/settings/PremiumPlansScreen').then(m => ({ default: m.PremiumPlansScreen })));
const DiscoverScreen = lazy(() => import('./features/discover/DiscoverScreen').then(m => ({ default: m.DiscoverScreen })));
const LiveSessionScreen = lazy(() => import('./features/live/LiveSessionScreen').then(m => ({ default: m.LiveSessionScreen })));
const CategoryViewScreen = lazy(() => import('./features/discover/CategoryViewScreen').then(m => ({ default: m.CategoryViewScreen })));
const PlaylistViewScreen = lazy(() => import('./features/library/PlaylistViewScreen').then(m => ({ default: m.PlaylistViewScreen })));

import { BottomNav } from './components/organisms/BottomNav';
import { PlayerBar } from './components/organisms/PlayerBar';
import { MobilePlayerScreen } from './components/organisms/MobilePlayerScreen';
import { ProfileSettingsButton } from './components/molecules/ProfileSettingsButton';
import { useAppContext } from './contexts/AppContext';
import { PlayerProvider, usePlayerContext } from './contexts/PlayerContext';

type Route = 'welcome' | 'login' | 'home' | 'search' | 'library' | 'album' | 'podcast' | 'devices' | 'delete-account' | 'chat' | 'profile' | 'settings' | 'premium' | 'discover' | 'live' | 'category' | 'playlist';

export const NavigationContext = createContext<(route: Route) => void>(() => {});
export const useNavigate = () => useContext(NavigationContext);

interface SidebarContextType {
  isSidebarOpen: boolean;
  toggleSidebar: () => void;
}
export const SidebarContext = createContext<SidebarContextType>({ isSidebarOpen: true, toggleSidebar: () => {} });
export const useSidebar = () => useContext(SidebarContext);

export const App: React.FC = () => {
  const [route, setRoute] = useState<Route>('welcome');
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

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

  const showBottomNav = ['home', 'discover', 'live', 'search', 'library', 'podcast', 'devices', 'chat', 'profile', 'settings', 'premium'].includes(route);
  const showPlayerBar = ['home', 'discover', 'live', 'category', 'playlist', 'search', 'library', 'album', 'podcast', 'devices', 'chat', 'profile', 'settings', 'premium'].includes(route);

  return (
    <SidebarContext.Provider value={{ isSidebarOpen, toggleSidebar: () => setIsSidebarOpen(prev => !prev) }}>
      <NavigationContext.Provider value={setRoute}>
        <div className="flex h-full w-full p-0 md:p-6 md:gap-6 gap-2 text-text-primary font-sans overflow-hidden bg-black pb-0 md:pb-[92px]">
          
          {showBottomNav && (
            <aside className={`hidden md:flex flex-col gap-6 z-40 transition-all duration-300 ${isSidebarOpen ? 'w-[280px] lg:w-[420px] opacity-100' : 'w-0 opacity-0 overflow-hidden'}`}>
              <BottomNav currentRoute={route} onNavigate={(r: any) => setRoute(r)} />
            </aside>
          )}

        <main className="flex-1 bg-[#121212] md:rounded-xl flex flex-col overflow-hidden relative group">
          <div className="flex-1 overflow-y-auto custom-scrollbar w-full relative pb-[160px] md:pb-0">
            <Suspense fallback={
              <div className="flex flex-col items-center justify-center h-full w-full bg-[#121414] text-[#1db954] gap-3">
                <div className="w-8 h-8 border-3 border-primary border-t-transparent rounded-full animate-spin"></div>
                <span className="text-xs font-bold text-text-secondary">Loading Mave...</span>
              </div>
            }>
              {route === 'welcome' && <LoginScreen onLogin={handleLogin} initialMode="signup" />}
              {route === 'login' && <LoginScreen onLogin={handleLogin} initialMode="login" />}
              {route === 'home' && <HomeScreen />}
              {route === 'discover' && <DiscoverScreen />}
              {route === 'live' && <LiveSessionScreen />}
              {route === 'category' && <CategoryViewScreen onBack={() => setRoute('discover')} />}
              {route === 'playlist' && <PlaylistViewScreen onBack={() => setRoute('library')} />}
              {route === 'search' && <SearchScreen />}
              {route === 'library' && <LibraryScreen />}
              {route === 'podcast' && <PodcastGeneratorScreen />}
              {route === 'devices' && <DevicesScreen onBack={() => setRoute('home')} />}
              {route === 'album' && <AlbumView onBack={() => setRoute('home')} />}
              {route === 'delete-account' && <DeleteAccountScreen />}
              {route === 'chat' && <ChatScreen />}
              {route === 'profile' && <ProfileScreen />}
              {route === 'settings' && <SettingsScreen />}
              {route === 'premium' && <PremiumPlansScreen />}
            </Suspense>
          </div>

          {/* Top Right Profile/Settings for Web */}
          {currentUser && showBottomNav && (
            <div className="hidden md:block absolute top-6 right-6 z-50">
              <ProfileSettingsButton />
            </div>
          )}
        </main>

        {showBottomNav && (
          <>
            {/* Bottom Left Profile/Settings for Mobile (above BottomNav) */}
            {currentUser && (
              <div className="md:hidden fixed top-6 right-4 z-50">
                <ProfileSettingsButton />
              </div>
            )}
            <div className="md:hidden fixed bottom-0 left-0 right-0 z-50 border-t border-outline/30 bg-background pb-safe">
              <BottomNav currentRoute={route} onNavigate={(r: any) => setRoute(r)} />
            </div>
          </>
        )}
        
        {showPlayerBar && (
          <PlayerBarWrapper onAlbumClick={() => setRoute('album')} />
        )}
      </div>
    </NavigationContext.Provider>
    </SidebarContext.Provider>
  );
};

const PlayerBarWrapper: React.FC<{ onAlbumClick: () => void }> = ({ onAlbumClick }) => {
  const { 
    currentTrack, 
    playerState, 
    currentTime, 
    duration, 
    shuffle,
    repeat,
    skipNext,
    skipPrevious,
    toggleShuffle,
    toggleRepeat,
    togglePlayPause, 
    seek 
  } = usePlayerContext();
  
  const { isMobilePlayerExpanded, setIsMobilePlayerExpanded } = useAppContext();
  const navigate = useNavigate();
  
  return (
    <>
      <footer 
        onClick={() => {
          if (window.innerWidth < 768) {
            setIsMobilePlayerExpanded(true);
          } else {
            onAlbumClick();
          }
        }} 
        className="fixed bottom-[72px] md:bottom-0 left-0 right-0 h-[72px] md:h-[92px] bg-black px-2 md:px-6 flex items-center justify-between z-40 cursor-pointer hover:bg-surface-container transition-colors border-t border-surface-container md:border-t-0"
      >
        <PlayerBar 
          trackName={currentTrack?.title}
          artistName={currentTrack?.artist}
          albumArtUrl={currentTrack?.albumArtUrl}
          isPlaying={playerState === 'playing'}
          currentTime={currentTime}
          duration={duration}
          isShuffleEnabled={shuffle}
          repeatMode={repeat}
          onSeek={seek}
          onPlayPause={(e) => {
            e.stopPropagation();
            togglePlayPause();
          }}
          onShuffle={(e) => { e.stopPropagation(); toggleShuffle(); }}
          onSkipPrevious={(e) => { e.stopPropagation(); skipPrevious(); }}
          onSkipNext={(e) => { e.stopPropagation(); skipNext(); }}
          onRepeat={(e) => { e.stopPropagation(); toggleRepeat(); }}
        />
      </footer>
      
      {isMobilePlayerExpanded && (
        <MobilePlayerScreen
          track={currentTrack}
          isPlaying={playerState === 'playing'}
          currentTime={currentTime}
          duration={duration}
          onPlayPause={(e) => {
            e.stopPropagation();
            togglePlayPause();
          }}
          onSeek={seek}
          onShuffle={() => toggleShuffle()}
          onSkipPrevious={() => skipPrevious()}
          onSkipNext={() => skipNext()}
          onRepeat={() => toggleRepeat()}
          onClose={() => setIsMobilePlayerExpanded(false)}
          onDevicesClick={() => {
            setIsMobilePlayerExpanded(false);
            navigate('devices');
          }}
        />
      )}
    </>
  );
};
