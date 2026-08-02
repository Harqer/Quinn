import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

interface ChatHeaderProps {
  toggleSidebar?: () => void;
  onToggleSidebar?: () => void;
}

export const ChatHeader: React.FC<ChatHeaderProps> = ({ toggleSidebar, onToggleSidebar }) => {
  const handleToggle = onToggleSidebar || toggleSidebar || (() => {});
  return (
    <header className="absolute top-0 w-full z-50 flex justify-between items-center px-4 h-[56px] backdrop-blur-xl bg-surface/80">
      <button onClick={handleToggle} className="text-primary hover:bg-surface-variant/50 p-2 rounded-full transition-colors hidden md:block">
        <Icon name="menu" />
      </button>
      <button onClick={handleToggle} className="text-primary hover:bg-surface-variant/50 p-2 rounded-full transition-colors md:hidden">
        <Icon name="menu" />
      </button>
      <Typography variant="title-md" className="font-bold text-on-surface">Mave</Typography>
      <button 
        onClick={() => window.dispatchEvent(new CustomEvent('show-options-menu', { detail: 'chat' }))}
        className="text-primary hover:bg-surface-variant/50 p-2 rounded-full transition-colors"
      >
        <Icon name="more_vert" />
      </button>
    </header>
  );
};
