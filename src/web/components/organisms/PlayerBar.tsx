import React from 'react';
import { Icon } from '../atoms/Icon';
import { ProgressBar } from '../atoms/ProgressBar';
import { PlayerTrackCard } from '../molecules/PlayerTrackCard';
import { PlayerMediaButtons } from '../molecules/PlayerMediaButtons';
import { logger } from '../../lib/logger';
import { useAppContext } from '../../contexts/AppContext';
import { useNavigate } from '../../App';

export interface PlayerBarProps {
  onClick?: () => void;
  trackName?: string;
  artistName?: string;
  albumArtUrl?: string;
  isPlaying?: boolean;
  currentTime?: number;
  duration?: number;
  isShuffleEnabled?: boolean;
  repeatMode?: 'none' | 'all' | 'one';
  onPlayPause?: (e: React.MouseEvent) => void;
  onSeek?: (time: number) => void;
  onShuffle?: (e: React.MouseEvent) => void;
  onSkipPrevious?: (e: React.MouseEvent) => void;
  onSkipNext?: (e: React.MouseEvent) => void;
  onRepeat?: (e: React.MouseEvent) => void;
}

export const PlayerBar: React.FC<PlayerBarProps> = ({ 
  onClick,
  trackName,
  artistName = "",
  albumArtUrl,
  isPlaying = false,
  currentTime = 0,
  duration = 0,
  isShuffleEnabled = false,
  repeatMode = 'none',
  onPlayPause,
  onSeek,
  onShuffle,
  onSkipPrevious,
  onSkipNext,
  onRepeat
}) => {
  const isEmpty = !trackName || trackName === "Not playing";
  const { isQueueVisible, setIsQueueVisible, globalVolume, setGlobalVolume } = useAppContext();
  const navigate = useNavigate();
  const progressPercent = duration > 0 ? (currentTime / duration) * 100 : 0;

  return (
    <div 
      className={`absolute bottom-[72px] left-2 right-2 h-14 bg-surface-container/90 backdrop-blur-[20px] rounded-md flex items-center px-2 shadow-lg cursor-pointer z-50 overflow-hidden border border-white/10 md:relative md:bottom-0 md:left-0 md:right-0 md:h-full md:bg-transparent md:backdrop-blur-none md:border-0 md:shadow-none md:rounded-none md:px-0 md:flex md:justify-between md:w-full ${isEmpty ? 'opacity-50 pointer-events-none' : ''}`}
      onClick={onClick}
    >
      <PlayerTrackCard isEmpty={isEmpty} trackName={trackName} artistName={artistName} albumArtUrl={albumArtUrl} />

      <div className="hidden md:flex flex-col items-center justify-center max-w-[40%] w-full gap-2">
        <PlayerMediaButtons 
          isEmpty={isEmpty} isPlaying={isPlaying} isShuffleEnabled={isShuffleEnabled} repeatMode={repeatMode}
          onPlayPause={onPlayPause} onShuffle={onShuffle} onSkipPrevious={onSkipPrevious} onSkipNext={onSkipNext} onRepeat={onRepeat}
        />
        <ProgressBar currentTime={currentTime} duration={duration} isEmpty={isEmpty} onSeek={onSeek} />
      </div>

      <div className="flex items-center justify-end gap-2 md:gap-3 md:w-[30%] ml-auto md:ml-0">
        <div className="flex md:hidden items-center gap-2">
          <button className="text-text-primary p-2 hover:text-white transition-colors" disabled={isEmpty} onClick={(e) => { e.stopPropagation(); logger.trackEvent("devices_click"); navigate('devices'); }}>
             <Icon name="devices" />
          </button>
          <button className="text-white p-2 hover:scale-105 active:scale-95 transition-transform" disabled={isEmpty} onClick={(e) => { e.stopPropagation(); onPlayPause?.(e); }}>
            <Icon name={isPlaying ? "pause" : "play_arrow"} size="xl" />
          </button>
        </div>

        <div className="hidden md:flex items-center justify-end text-text-secondary gap-1">
          <button className={`p-2 transition-colors ${isQueueVisible ? 'text-primary' : 'hover:text-white'}`} disabled={isEmpty} onClick={(e) => { e.stopPropagation(); logger.trackEvent("queue_click"); setIsQueueVisible(!isQueueVisible); }}>
            <span className="material-symbols-outlined text-[20px]">queue_music</span>
          </button>
          <button className="p-2 hover:text-white transition-colors" disabled={isEmpty} onClick={(e) => { e.stopPropagation(); logger.trackEvent("devices_click"); navigate('devices'); }}>
            <span className="material-symbols-outlined text-[20px]">devices</span>
          </button>
          <div className="flex items-center gap-2 w-24 mx-1">
            <button className="hover:text-white transition-colors" disabled={isEmpty} onClick={(e) => { e.stopPropagation(); logger.trackEvent("volume_click"); setGlobalVolume(globalVolume === 0 ? 1 : 0); }}>
              <span className="material-symbols-outlined text-[20px]">{globalVolume === 0 ? "volume_off" : "volume_up"}</span>
            </button>
            <div className="h-1 flex-1 bg-surface-container rounded-full overflow-hidden group cursor-pointer relative" onClick={(e) => { e.stopPropagation(); logger.trackEvent("volume_slider_click"); const rect = e.currentTarget.getBoundingClientRect(); const x = Math.max(0, Math.min(e.clientX - rect.left, rect.width)); setGlobalVolume(x / rect.width); }}>
               <div className="h-full bg-white group-hover:bg-primary transition-colors absolute left-0 top-0" style={{ width: `${globalVolume * 100}%` }}></div>
            </div>
          </div>
        </div>
      </div>

      {!isEmpty && (
        <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-surface rounded-full overflow-hidden md:hidden">
           <div className="h-full bg-primary rounded-full shadow-[0_0_8px_rgba(29,185,84,0.5)] transition-all duration-1000" style={{ width: `${progressPercent}%` }} />
        </div>
      )}
    </div>
  );
};
