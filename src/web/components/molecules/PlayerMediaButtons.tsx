import React from 'react';

export interface PlayerMediaButtonsProps {
  isEmpty?: boolean;
  isPlaying?: boolean;
  isShuffleEnabled?: boolean;
  repeatMode?: 'none' | 'all' | 'one';
  onPlayPause?: (e: React.MouseEvent) => void;
  onShuffle?: (e: React.MouseEvent) => void;
  onSkipPrevious?: (e: React.MouseEvent) => void;
  onSkipNext?: (e: React.MouseEvent) => void;
  onRepeat?: (e: React.MouseEvent) => void;
}

export const PlayerMediaButtons: React.FC<PlayerMediaButtonsProps> = ({
  isEmpty = false,
  isPlaying = false,
  isShuffleEnabled = false,
  repeatMode = 'none',
  onPlayPause,
  onShuffle,
  onSkipPrevious,
  onSkipNext,
  onRepeat
}) => {
  return (
    <div className="flex items-center gap-4">
      <button 
        className={`transition-colors ${isShuffleEnabled ? 'text-primary' : 'text-text-secondary hover:text-white'}`} 
        disabled={isEmpty}
        onClick={(e) => { e.stopPropagation(); onShuffle?.(e); }}
      >
        <span className="material-symbols-outlined text-[20px]">shuffle</span>
      </button>
      <button 
        className="text-text-secondary hover:text-white transition-colors" 
        disabled={isEmpty}
        onClick={(e) => { e.stopPropagation(); onSkipPrevious?.(e); }}
      >
        <span className="material-symbols-outlined text-[24px]">skip_previous</span>
      </button>
      <button 
        className="w-8 h-8 rounded-full bg-white text-black flex items-center justify-center hover:scale-105 active:scale-95 transition-transform"
        disabled={isEmpty}
        onClick={(e) => { e.stopPropagation(); onPlayPause?.(e); }}
      >
        <span className="material-symbols-outlined text-[24px]" style={{ fontVariationSettings: "'FILL' 1" }}>
          {isPlaying ? "pause" : "play_arrow"}
        </span>
      </button>
      <button 
        className="text-text-secondary hover:text-white transition-colors" 
        disabled={isEmpty}
        onClick={(e) => { e.stopPropagation(); onSkipNext?.(e); }}
      >
        <span className="material-symbols-outlined text-[24px]">skip_next</span>
      </button>
      <button 
        className={`transition-colors ${repeatMode !== 'none' ? 'text-primary' : 'text-text-secondary hover:text-white'}`} 
        disabled={isEmpty}
        onClick={(e) => { e.stopPropagation(); onRepeat?.(e); }}
      >
        <span className="material-symbols-outlined text-[20px] relative">
          {repeatMode === 'one' ? 'repeat_one' : 'repeat'}
        </span>
      </button>
    </div>
  );
};