import React from 'react';

export interface ProgressBarProps {
  currentTime: number;
  duration: number;
  isEmpty?: boolean;
  onSeek?: (time: number) => void;
}

export const ProgressBar: React.FC<ProgressBarProps> = ({
  currentTime,
  duration,
  isEmpty = false,
  onSeek
}) => {
  const progressPercent = duration > 0 ? (currentTime / duration) * 100 : 0;

  const formatTime = (secs: number) => {
    const mins = Math.floor(secs / 60);
    const remaining = Math.floor(secs % 60);
    return `${mins}:${remaining.toString().padStart(2, '0')}`;
  };

  return (
    <div className="flex items-center gap-2 w-full text-xs text-text-secondary font-medium">
      <span>{formatTime(currentTime)}</span>
      <div 
        className="h-1 flex-1 bg-surface-container rounded-full overflow-hidden group cursor-pointer"
        onClick={(e) => {
          e.stopPropagation();
          if (!isEmpty && onSeek && duration > 0) {
            const rect = e.currentTarget.getBoundingClientRect();
            const x = Math.max(0, Math.min(e.clientX - rect.left, rect.width));
            onSeek((x / rect.width) * duration);
          }
        }}
      >
        <div className="h-full bg-white group-hover:bg-primary transition-colors" style={{ width: `${progressPercent}%` }} />
      </div>
      <span>{formatTime(duration)}</span>
    </div>
  );
};