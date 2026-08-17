import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

interface LiveSessionHeaderProps {
  isConnected: boolean;
  onClose: () => void;
  onMoreOptions: () => void;
  onNavigateHome?: () => void;
}

export const LiveSessionHeader: React.FC<LiveSessionHeaderProps> = ({ isConnected, onClose, onMoreOptions, onNavigateHome }) => (
  <div className="flex items-center gap-3 px-4 py-4 bg-surface-container sticky top-0 z-10 shadow-sm shrink-0">
    <button onClick={onClose} className="text-on-surface hover:opacity-80 transition-opacity">
      <Icon name="close" size="2xl" />
    </button>
    <Typography variant="title-md" className="font-bold flex-1">
      Live Session {isConnected ? '(Connected)' : ''}
    </Typography>
    <button 
      onClick={onMoreOptions}
      className="text-on-surface hover:opacity-80 transition-opacity"
    >
      <Icon name="more_vert" size="2xl" />
    </button>
  </div>
);
