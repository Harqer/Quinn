import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

export interface EmptyStateProps {
  icon: string;
  title: string;
  description: string;
  action?: {
    label: string;
    onClick: () => void;
  };
}

export const EmptyState: React.FC<EmptyStateProps> = ({ icon, title, description, action }) => {
  return (
    <div className="flex flex-col items-center justify-center p-8 text-center h-full w-full">
      <div className="w-16 h-16 rounded-full bg-surface-container flex items-center justify-center mb-4 text-text-secondary">
        <Icon name={icon} size="3xl" />
      </div>
      <Typography variant="title-lg" className="font-bold mb-2 text-white">
        {title}
      </Typography>
      <Typography variant="body-md" color="secondary" className="mb-6 max-w-[250px]">
        {description}
      </Typography>
      {action && (
        <button 
          onClick={action.onClick}
          className="bg-primary text-black font-bold px-6 py-3 rounded-full hover:scale-105 active:scale-95 transition-transform shadow-lg"
        >
          {action.label}
        </button>
      )}
    </div>
  );
};
