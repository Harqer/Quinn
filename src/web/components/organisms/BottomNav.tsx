import React from 'react';
import { BottomNavItem } from '../molecules/BottomNavItem';

export interface BottomNavProps {
  currentRoute: string;
  onNavigate: (route: string) => void;
}

export const BottomNav: React.FC<BottomNavProps> = ({ currentRoute, onNavigate }) => {
  return (
    <div className="flex justify-around items-center h-[72px] bg-background/90 backdrop-blur-lg border-t border-surface-container relative z-50">
      <BottomNavItem 
        icon="home" 
        label="Home" 
        isActive={currentRoute === 'home'} 
        onClick={() => onNavigate('home')} 
      />
      <BottomNavItem 
        icon="search" 
        label="Search" 
        isActive={currentRoute === 'search'} 
        onClick={() => onNavigate('search')} 
      />
      <BottomNavItem 
        icon="library_music" 
        label="Your Library" 
        isActive={currentRoute === 'library'} 
        onClick={() => onNavigate('library')} 
      />
    </div>
  );
};
