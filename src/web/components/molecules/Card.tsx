import React from 'react';
import { Typography } from '../atoms/Typography';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  title: string;
  subtitle?: string;
  imageUrl?: string;
  isCircle?: boolean;
  onClick?: () => void;
}

export const Card: React.FC<CardProps> = ({
  title,
  subtitle,
  imageUrl,
  isCircle = false,
  className = '',
  onClick,
  ...props
}) => {
  return (
    <div 
      className={`flex flex-col gap-2 w-[120px] flex-shrink-0 cursor-pointer group ${className}`} 
      onClick={onClick}
      {...props}
    >
      <div className={`w-full aspect-square bg-surface-container flex items-center justify-center shadow-lg overflow-hidden relative ${isCircle ? 'rounded-full' : 'rounded-md'}`}>
        {imageUrl ? (
          <img src={imageUrl} alt={title} className="w-full h-full object-cover" />
        ) : (
          <div className="w-full h-full bg-surface-container flex items-center justify-center">
             <Typography variant="display" color="secondary" className="opacity-50">
               {title.charAt(0)}
             </Typography>
          </div>
        )}
        <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors pointer-events-none" />
      </div>
      <div className="flex flex-col">
        <Typography variant="body-md" className="truncate font-bold">
          {title}
        </Typography>
        {subtitle && (
          <Typography variant="body-sm" color="secondary" className="truncate">
            {subtitle}
          </Typography>
        )}
      </div>
    </div>
  );
};
