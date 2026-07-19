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
      className="flex flex-col items-center justify-center gap-1 min-w-[64px] bg-transparent border-none outline-none cursor-pointer p-2 active:scale-95 transition-transform"
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
