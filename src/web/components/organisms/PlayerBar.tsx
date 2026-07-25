import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';
import { logger } from '../../lib/logger';
import { useAppContext } from '../../contexts/AppContext';
import { useNavigate } from '../../App';

export interface PlayerBarProps {
  onClick?: () => void;
  trackName?: string;
  artistName?: string;
  albumArtUrl?: string;
  isPlaying?: boolean;
  onPlayPause?: (e: React.MouseEvent) => void;
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
  onPlayPause,
  onShuffle,
  onSkipPrevious,
  onSkipNext,
  onRepeat
}) => {
  const isEmpty = !trackName || trackName === "Not playing";
  const { isQueueVisible, setIsQueueVisible, globalVolume, setGlobalVolume } = useAppContext();
  const navigate = useNavigate();
  const [progress, setProgress] = React.useState(0);

  React.useEffect(() => {
    let interval: NodeJS.Timeout;
    if (isPlaying && !isEmpty) {
      interval = setInterval(() => {
        setProgress(p => p >= 100 ? 0 : p + 1);
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isPlaying, isEmpty]);

  return (
    <div 
      className={`absolute bottom-[72px] left-2 right-2 h-14 bg-surface-container/90 backdrop-blur-[20px] rounded-md flex items-center px-2 shadow-lg cursor-pointer z-50 overflow-hidden border border-white/10 md:relative md:bottom-0 md:left-0 md:right-0 md:h-full md:bg-transparent md:backdrop-blur-none md:border-0 md:shadow-none md:rounded-none md:px-0 md:flex md:justify-between md:w-full ${isEmpty ? 'opacity-50 pointer-events-none' : ''}`}
      onClick={onClick}
    >
      {/* Left: Track Info */}
      <div className="flex items-center gap-3 justify-start overflow-hidden flex-1 md:flex-none md:w-[30%]">
        <div className="w-10 h-10 md:w-14 md:h-14 rounded bg-surface overflow-hidden flex-shrink-0 flex items-center justify-center">
          {albumArtUrl ? (
            <img src={albumArtUrl} alt="Album Art" className="w-full h-full object-cover" />
          ) : (
            <Icon name="music_note" color="secondary" />
          )}
        </div>
        
        <div className="flex flex-col flex-1 overflow-hidden">
          <Typography variant="body-md" className="font-bold truncate text-white hover:underline cursor-pointer">
            {isEmpty ? "Nothing playing" : trackName}
          </Typography>
          {artistName && !isEmpty && (
            <Typography variant="body-sm" color="secondary" className="truncate hover:underline cursor-pointer hover:text-white">
              {artistName}
            </Typography>
          )}
        </div>


      </div>

      {/* Center: Playback Controls (Desktop Only) */}
      <div className="hidden md:flex flex-col items-center justify-center max-w-[40%] w-full gap-2">
        <div className="flex items-center gap-4">
          <button 
            className="text-text-secondary hover:text-white transition-colors" 
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
            onClick={(e) => {
              e.stopPropagation();
              onPlayPause?.(e);
            }}
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
            className="text-text-secondary hover:text-white transition-colors" 
            disabled={isEmpty}
            onClick={(e) => { e.stopPropagation(); onRepeat?.(e); }}
          >
            <span className="material-symbols-outlined text-[20px]">repeat</span>
          </button>
        </div>
        <div className="flex items-center gap-2 w-full text-xs text-text-secondary font-medium">
          <span>0:{(progress % 60).toString().padStart(2, '0')}</span>
          <div className="h-1 flex-1 bg-surface-container rounded-full overflow-hidden group cursor-pointer">
            <div className="h-full bg-white group-hover:bg-primary transition-colors" style={{ width: `${progress}%` }}></div>
          </div>
          <span>3:24</span>
        </div>
      </div>

      {/* Right: Extra Controls (Desktop) / Mobile controls (Mobile) */}
      <div className="flex items-center justify-end gap-2 md:gap-3 md:w-[30%] ml-auto md:ml-0">
        {/* Mobile-only controls */}
        <div className="flex md:hidden items-center gap-2">
          <button 
            className="text-text-primary p-2 hover:text-white transition-colors" 
            disabled={isEmpty}
            onClick={(e) => { e.stopPropagation(); logger.trackEvent("devices_click"); navigate('devices'); }}
          >
             <Icon name="devices" />
          </button>
          <button 
            className="text-white p-2 hover:scale-105 active:scale-95 transition-transform"
            disabled={isEmpty}
            onClick={(e) => {
              e.stopPropagation();
              onPlayPause?.(e);
            }}
          >
            <Icon name={isPlaying ? "pause" : "play_arrow"} size="xl" />
          </button>
        </div>

        {/* Desktop-only controls */}
        <div className="hidden md:flex items-center justify-end text-text-secondary gap-1">
          <button 
            className="p-2 hover:text-white transition-colors" 
            disabled={isEmpty}
            onClick={(e) => { 
              e.stopPropagation(); 
              logger.trackEvent("slideshow_click");
              if (!document.fullscreenElement) {
                document.documentElement.requestFullscreen().catch(err => {
                  logger.error(`Error attempting to enable fullscreen: ${err.message}`);
                });
              } else {
                document.exitFullscreen();
              }
            }}
          >
            <span className="material-symbols-outlined text-[20px]">slideshow</span>
          </button>
          <button 
            className={`p-2 transition-colors ${isQueueVisible ? 'text-primary' : 'hover:text-white'}`}
            disabled={isEmpty}
            onClick={(e) => { 
              e.stopPropagation(); 
              logger.trackEvent("queue_click");
              setIsQueueVisible(!isQueueVisible);
            }}
          >
            <span className="material-symbols-outlined text-[20px]">queue_music</span>
          </button>
          <button 
            className="p-2 hover:text-white transition-colors" 
            disabled={isEmpty}
            onClick={(e) => { e.stopPropagation(); logger.trackEvent("devices_click"); navigate('devices'); }}
          >
            <span className="material-symbols-outlined text-[20px]">devices</span>
          </button>
          <div className="flex items-center gap-2 w-24 mx-1">
            <button 
              className="hover:text-white transition-colors" 
              disabled={isEmpty}
              onClick={(e) => { 
                e.stopPropagation(); 
                logger.trackEvent("volume_click"); 
                setGlobalVolume(globalVolume === 0 ? 1 : 0);
              }}
            >
              <span className="material-symbols-outlined text-[20px]">
                {globalVolume === 0 ? "volume_off" : "volume_up"}
              </span>
            </button>
            <div 
              className="h-1 flex-1 bg-surface-container rounded-full overflow-hidden group cursor-pointer relative"
              onClick={(e) => { 
                e.stopPropagation(); 
                logger.trackEvent("volume_slider_click");
                const rect = e.currentTarget.getBoundingClientRect();
                const x = Math.max(0, Math.min(e.clientX - rect.left, rect.width));
                setGlobalVolume(x / rect.width);
              }}
            >
               <div 
                 className="h-full bg-white group-hover:bg-primary transition-colors absolute left-0 top-0" 
                 style={{ width: `${globalVolume * 100}%` }}
               ></div>
            </div>
          </div>
          <button 
            className="p-2 hover:text-white transition-colors" 
            disabled={isEmpty}
            onClick={(e) => { 
              e.stopPropagation(); 
              logger.trackEvent("fullscreen_click");
              if (!document.fullscreenElement) {
                document.documentElement.requestFullscreen().catch(err => {
                  logger.error(`Error attempting to enable fullscreen: ${err.message}`);
                });
              } else {
                document.exitFullscreen();
              }
            }}
          >
            <span className="material-symbols-outlined text-[20px]">fullscreen</span>
          </button>
        </div>
      </div>

      {/* Mobile Progress Bar (Absolute bottom) */}
      {!isEmpty && (
        <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-surface rounded-full overflow-hidden md:hidden">
           <div className="h-full bg-primary rounded-full shadow-[0_0_8px_rgba(29,185,84,0.5)] transition-all duration-1000" style={{ width: `${progress}%` }} />
        </div>
      )}
    </div>
  );
};
