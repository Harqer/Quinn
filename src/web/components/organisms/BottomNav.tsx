import React from 'react';
import { BottomNavItem } from '../molecules/BottomNavItem';
import { useSpotify } from '../../hooks/useSpotify';
import { useYouTube } from '../../hooks/useYouTube';

export interface BottomNavProps {
  currentRoute: string;
  onNavigate: (route: string) => void;
}

export const BottomNav: React.FC<BottomNavProps> = ({ currentRoute, onNavigate }) => {
  const spotify = useSpotify();
  const youtube = useYouTube();

  return (
    <>
      <div className="md:hidden flex flex-row justify-around items-center h-[72px] w-full bg-background/90 backdrop-blur-[20px] relative z-50">
        <BottomNavItem icon="home" label="Home" isActive={currentRoute === 'home'} onClick={() => onNavigate('home')} />
        <BottomNavItem icon="explore" label="Discover" isActive={currentRoute === 'discover'} onClick={() => onNavigate('discover')} />
        <BottomNavItem icon="search" label="Search" isActive={currentRoute === 'search'} onClick={() => onNavigate('search')} />
        <BottomNavItem icon="graphic_eq" label="Live" isActive={currentRoute === 'live'} onClick={() => onNavigate('live')} />
        <BottomNavItem icon="library_music" label="Library" isActive={currentRoute === 'library'} onClick={() => onNavigate('library')} />
      </div>

      <div className="hidden md:flex flex-col gap-2 w-full h-full text-text-secondary">
        <div className="bg-[#121212] rounded-xl p-4 flex flex-col gap-2">
          <BottomNavItem icon="home" label="Home" isActive={currentRoute === 'home'} onClick={() => onNavigate('home')} />
          <BottomNavItem icon="explore" label="Discover" isActive={currentRoute === 'discover'} onClick={() => onNavigate('discover')} />
          <BottomNavItem icon="search" label="Search" isActive={currentRoute === 'search'} onClick={() => onNavigate('search')} />
          <BottomNavItem icon="graphic_eq" label="Live" isActive={currentRoute === 'live'} onClick={() => onNavigate('live')} />
          <BottomNavItem icon="auto_awesome" label="Mave Chat" isActive={currentRoute === 'chat'} onClick={() => onNavigate('chat')} />
          <BottomNavItem icon="podcasts" label="Podcast" isActive={currentRoute === 'podcast'} onClick={() => onNavigate('podcast')} />
          <BottomNavItem icon="glasses" label="Devices" isActive={currentRoute === 'devices'} onClick={() => onNavigate('devices')} />
        </div>
        <div className="bg-[#121212] rounded-xl p-4 flex-1 flex flex-col gap-4 overflow-hidden">
          <div className="flex items-center justify-between px-2 cursor-pointer hover:text-white transition-colors" onClick={() => onNavigate('library')}>
            <div className="flex items-center gap-4">
              <span className="material-symbols-outlined font-light text-2xl">library_music</span>
              <span className="font-bold text-sm">Your Library</span>
            </div>
            <span className="material-symbols-outlined hover:bg-surface-container p-1 rounded-full text-sm">add</span>
          </div>
          <div className="flex-1 overflow-y-auto pr-2 flex flex-col gap-2">
            {/* Connection Integrations */}
            <div className="bg-surface-container p-4 rounded-lg flex flex-col gap-2 mb-2">
              <span className="font-bold text-sm text-text-primary">Connections</span>
              
              {/* Spotify */}
              <div 
                className={`flex items-center justify-between p-2 rounded-lg cursor-pointer transition-colors ${spotify.isConnected ? 'bg-[#1db954]/20' : 'hover:bg-white/5'}`}
                onClick={() => { if (!spotify.isConnected) spotify.connectSpotify(); }}
              >
                <div className="flex items-center gap-3">
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6 text-[#1db954]" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.66 0 12 0zm5.521 17.34c-.24.359-.66.48-1.021.24-2.82-1.74-6.36-2.101-10.561-1.141-.418.122-.779-.179-.899-.539-.12-.421.18-.78.54-.9 4.56-1.021 8.52-.6 11.64 1.32.42.18.479.659.301 1.02zm1.44-3.3c-.301.42-.84.6-1.262.3-3.239-1.98-8.159-2.58-11.939-1.38-.479.12-1.02-.12-1.14-.6-.12-.48.12-1.021.6-1.141C9.6 9.9 15 10.561 18.72 12.84c.361.181.54.84.241 1.2zM19.081 9.9c-3.96-2.34-10.44-2.58-14.28-1.44-.6.18-1.26-.18-1.44-.78-.18-.6.18-1.26.78-1.44 4.56-1.32 11.76-1.02 16.32 1.68.54.3.72 1.02.42 1.56-.3.54-1.02.72-1.56.42z"/>
                  </svg>
                  <span className={`text-sm ${spotify.isConnected ? 'text-white' : 'text-gray-400'}`}>
                    {spotify.isConnected ? 'Spotify Connected' : 'Connect Spotify'}
                  </span>
                </div>
                {!spotify.isConnected && (
                  <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#e3e3e3"><path d="M200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h280v80H200v560h560v-280h80v280q0 33-23.5 56.5T760-120H200Zm188-212-56-56 372-372H560v-80h280v280h-80v-144L388-332Z"/></svg>
                )}
              </div>

              {/* YouTube */}
              <div 
                className={`flex items-center justify-between p-2 rounded-lg cursor-pointer transition-colors ${youtube.isConnected ? 'bg-[#ff0000]/20' : 'hover:bg-white/5'}`}
                onClick={() => { if (!youtube.isConnected) youtube.connectYouTube(); }}
              >
                <div className="flex items-center gap-3">
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6 text-[#ff0000]" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/>
                  </svg>
                  <span className={`text-sm ${youtube.isConnected ? 'text-white' : 'text-gray-400'}`}>
                    {youtube.isConnected ? 'YouTube Connected' : 'Connect YouTube'}
                  </span>
                </div>
                {!youtube.isConnected && (
                  <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#e3e3e3"><path d="M200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h280v80H200v560h560v-280h80v280q0 33-23.5 56.5T760-120H200Zm188-212-56-56 372-372H560v-80h280v280h-80v-144L388-332Z"/></svg>
                )}
              </div>
            </div>

            {/* Future library items go here */}
            <div className="bg-surface-container p-4 rounded-lg flex flex-col gap-2">
              <span className="font-bold text-sm text-text-primary">Create your first playlist</span>
              <span className="text-xs">It's easy, we'll help you</span>
              <button className="bg-white text-black font-bold text-xs py-2 px-4 rounded-full w-fit mt-2 hover:scale-105 transition-transform" onClick={() => onNavigate('podcast')}>
                Create playlist
              </button>
            </div>
            <div className="bg-surface-container p-4 rounded-lg flex flex-col gap-2">
              <span className="font-bold text-sm text-text-primary">Let's generate some music</span>
              <span className="text-xs">We'll keep you updated on new tracks</span>
              <button className="bg-white text-black font-bold text-xs py-2 px-4 rounded-full w-fit mt-2 hover:scale-105 transition-transform" onClick={() => onNavigate('podcast')}>
                Browse podcasts
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
