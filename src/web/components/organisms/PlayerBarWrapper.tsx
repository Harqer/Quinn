import React from 'react';
import { PlayerBar } from './PlayerBar';
import { MobilePlayerScreen } from './MobilePlayerScreen';
import { usePlayerContext } from '../../contexts/PlayerContext';
import { useAppContext } from '../../contexts/AppContext';
import { useNavigate } from '../../App';

interface PlayerBarWrapperProps {
  onAlbumClick: () => void;
}

export const PlayerBarWrapper: React.FC<PlayerBarWrapperProps> = ({ onAlbumClick }) => {
  const { 
    currentTrack, 
    playerState, 
    currentTime, 
    duration, 
    shuffle,
    repeat,
    skipNext,
    skipPrevious,
    toggleShuffle,
    toggleRepeat,
    togglePlayPause, 
    seek 
  } = usePlayerContext();
  
  const { isMobilePlayerExpanded, setIsMobilePlayerExpanded } = useAppContext();
  const navigate = useNavigate();
  
  return (
    <>
      <footer 
        onClick={() => {
          if (window.innerWidth < 768) {
            setIsMobilePlayerExpanded(true);
          } else {
            onAlbumClick();
          }
        }} 
        className="fixed bottom-[72px] md:bottom-0 left-0 right-0 h-[72px] md:h-[92px] bg-black px-2 md:px-6 flex items-center justify-between z-40 cursor-pointer hover:bg-surface-container transition-colors border-t border-surface-container md:border-t-0"
      >
        <PlayerBar 
          trackName={currentTrack?.title}
          artistName={currentTrack?.artist}
          albumArtUrl={currentTrack?.albumArtUrl}
          isPlaying={playerState === 'playing'}
          currentTime={currentTime}
          duration={duration}
          isShuffleEnabled={shuffle}
          repeatMode={repeat}
          onSeek={seek}
          onPlayPause={(e) => {
            e.stopPropagation();
            togglePlayPause();
          }}
          onShuffle={(e) => { e.stopPropagation(); toggleShuffle(); }}
          onSkipPrevious={(e) => { e.stopPropagation(); skipPrevious(); }}
          onSkipNext={(e) => { e.stopPropagation(); skipNext(); }}
          onRepeat={(e) => { e.stopPropagation(); toggleRepeat(); }}
        />
      </footer>
      
      {isMobilePlayerExpanded && (
        <MobilePlayerScreen
          track={currentTrack}
          isPlaying={playerState === 'playing'}
          currentTime={currentTime}
          duration={duration}
          onPlayPause={(e) => {
            e.stopPropagation();
            togglePlayPause();
          }}
          onSeek={seek}
          onShuffle={() => toggleShuffle()}
          onSkipPrevious={() => skipPrevious()}
          onSkipNext={() => skipNext()}
          onRepeat={() => toggleRepeat()}
          onClose={() => setIsMobilePlayerExpanded(false)}
          onDevicesClick={() => {
            setIsMobilePlayerExpanded(false);
            navigate('devices');
          }}
        />
      )}
    </>
  );
};
