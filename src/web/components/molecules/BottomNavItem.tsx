import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

export interface BottomNavItemProps {
  icon: string;
  label: string;
  isActive?: boolean;
  onClick?: () => void;
}

export const BottomNavItem: React.FC<BottomNavItemProps> = ({
  icon,
  label,
  isActive = false,
  onClick,
}) => {
  return (
    <button 
      onClick={onClick}
      className={`flex flex-col md:flex-row items-center md:justify-start gap-1 md:gap-4 min-w-[64px] bg-transparent border-none outline-none cursor-pointer p-2 md:p-3 md:rounded-lg md:hover:bg-surface-container active:scale-95 md:active:scale-100 transition-all ${isActive ? 'md:bg-surface-container' : ''}`}
    >
      <Icon 
        name={icon} 
        size="2xl" 
        color={isActive ? 'white' : 'secondary'} 
      />
      <Typography 
        variant="label-sm" 
        color={isActive ? 'inherit' : 'secondary'}
        className={isActive ? 'text-white' : ''}
      >
        {label}
      </Typography>
    </button>
  );
};
