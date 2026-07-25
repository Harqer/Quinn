import React from 'react';
import { BottomNavItem } from '../molecules/BottomNavItem';

export interface BottomNavProps {
  currentRoute: string;
  onNavigate: (route: string) => void;
}

export const BottomNav: React.FC<BottomNavProps> = ({ currentRoute, onNavigate }) => {
  return (
    <>
      <div className="md:hidden flex flex-row justify-around items-center h-[72px] w-full bg-background/90 backdrop-blur-[20px] relative z-50">
        <BottomNavItem icon="home" label="Home" isActive={currentRoute === 'home'} onClick={() => onNavigate('home')} />
        <BottomNavItem icon="search" label="Search" isActive={currentRoute === 'search'} onClick={() => onNavigate('search')} />
        <BottomNavItem icon="podcasts" label="Podcast" isActive={currentRoute === 'podcast'} onClick={() => onNavigate('podcast')} />
        <BottomNavItem icon="glasses" label="Devices" isActive={currentRoute === 'devices'} onClick={() => onNavigate('devices')} />
        <BottomNavItem icon="library_music" label="Library" isActive={currentRoute === 'library'} onClick={() => onNavigate('library')} />
      </div>

      <div className="hidden md:flex flex-col gap-2 w-full h-full text-text-secondary">
        <div className="bg-[#121212] rounded-xl p-4 flex flex-col gap-2">
          <BottomNavItem icon="home" label="Home" isActive={currentRoute === 'home'} onClick={() => onNavigate('home')} />
          <BottomNavItem icon="search" label="Search" isActive={currentRoute === 'search'} onClick={() => onNavigate('search')} />
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
