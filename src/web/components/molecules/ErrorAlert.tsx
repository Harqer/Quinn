import React from 'react';
import { Icon } from '../atoms/Icon';
import { Typography } from '../atoms/Typography';

interface ErrorAlertProps {
  message: string;
  details?: string;
  onRetry?: () => void;
}

export const ErrorAlert: React.FC<ErrorAlertProps> = ({ message, details, onRetry }) => {
  return (
    <div className="flex flex-col items-center justify-center p-6 m-4 bg-error/10 border border-error/30 rounded-2xl">
      <Icon name="error_outline" className="text-error mb-3" size="3xl" />
      <Typography variant="title-md" className="text-error font-bold text-center">{message}</Typography>
      {details && (
        <Typography variant="body-sm" className="text-error/70 mt-2 text-center">{details}</Typography>
      )}
      {onRetry && (
        <button 
          onClick={onRetry} 
          className="mt-6 px-6 py-2 bg-error text-onError rounded-full font-bold hover:opacity-90 transition-opacity flex items-center gap-2 shadow-lg"
        >
          <Icon name="refresh" />
          Retry
        </button>
      )}
    </div>
  );
};
