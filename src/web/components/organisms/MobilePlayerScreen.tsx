import React, { useRef, useState, useEffect } from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';
import { Track } from '../../hooks/useTracks';
import { logger } from '../../lib/logger';
import { getAuth } from 'firebase/auth';

export interface MobilePlayerScreenProps {
  track: Track | null;
  isPlaying: boolean;
  currentTime: number;
  duration: number;
  onPlayPause: (e: React.MouseEvent) => void;
  onSeek: (time: number) => void;
  onShuffle: () => void;
  onSkipPrevious: () => void;
  onSkipNext: () => void;
  onRepeat: () => void;
  onClose: () => void;
  onDevicesClick: () => void;
}

export const MobilePlayerScreen: React.FC<MobilePlayerScreenProps> = ({
  track,
  isPlaying,
  currentTime,
  duration,
  onPlayPause,
  onSeek,
  onShuffle,
  onSkipPrevious,
  onSkipNext,
  onRepeat,
  onClose,
  onDevicesClick
}) => {
  const [startY, setStartY] = useState<number | null>(null);
  const [currentY, setCurrentY] = useState<number | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const progressPercent = duration > 0 ? (currentTime / duration) * 100 : 0;
  
  const formatTime = (secs: number) => {
    const mins = Math.floor(secs / 60);
    const remaining = Math.floor(secs % 60);
    return `${mins}:${remaining.toString().padStart(2, '0')}`;
  };

  const handleTouchStart = (e: React.TouchEvent) => {
    setStartY(e.touches[0].clientY);
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    if (startY !== null) {
      setCurrentY(e.touches[0].clientY);
    }
  };

  const handleTouchEnd = () => {
    if (startY !== null && currentY !== null) {
      const diff = currentY - startY;
      if (diff > 100) {
        // Swipe down detected
        onClose();
      }
    }
    setStartY(null);
    setCurrentY(null);
  };

  const translateY = currentY !== null && startY !== null && currentY > startY 
    ? currentY - startY 
    : 0;

  // Prevent background scrolling when open
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = 'auto';
    };
  }, []);

  const handleAction = async (action: 'like' | 'bookmark' | 'playlist_add') => {
    if (!track?.id) return;
    if ('vibrate' in navigator) navigator.vibrate([50, 50, 50]);
    try {
      const { likeTrack, bookmarkTrack } = await import('../../../lib/dataconnect');
      
      let actionName = '';
      if (action === 'like') {
        await likeTrack({ trackId: track.id });
        actionName = 'Saved to Liked Tracks';
      } else if (action === 'bookmark') {
        await bookmarkTrack({ trackId: track.id });
        actionName = 'Saved to Bookmarks';
      } else if (action === 'playlist_add') {
        // Fallback for playlist add if not in Data Connect yet
        const auth = getAuth();
        const user = auth.currentUser;
        const token = user ? await user.getIdToken() : '';
        const baseUrl = import.meta.env.VITE_API_URL || '';
        await fetch(`${baseUrl}/api/spotify/playlist/add`, {
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {})
          },
          body: JSON.stringify({ trackId: track.id })
        });
        actionName = 'Added to Playlist';
      }
      
      window.dispatchEvent(new CustomEvent('show-toast', { detail: actionName }));
    } catch (err) {
      window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Failed to complete action' }));
    }
  };

  const handleShare = () => {
    if (!track) return;
    const url = `${window.location.origin}/track/${track.id}`;
    if (navigator.share) {
      navigator.share({
        title: track.title,
        text: `Listen to ${track.title} by ${track.artist} on Lyria!`,
        url: url
      }).catch(console.error);
    } else {
      navigator.clipboard.writeText(url);
      window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Share link copied!' }));
    }
  };

  return (
    <div 
      ref={containerRef}
      className="fixed inset-0 z-[100] bg-[#121212] text-white flex flex-col pt-12 pb-8 px-6 transition-transform duration-300 ease-out md:hidden"
      style={{ transform: `translateY(${translateY}px)` }}
      onTouchStart={handleTouchStart}
      onTouchMove={handleTouchMove}
      onTouchEnd={handleTouchEnd}
    >
      {/* Top Header */}
      <div className="flex items-center justify-between mb-8">
        <button onClick={onClose} className="p-2 -ml-2 text-text-secondary hover:text-white transition-colors">
          <Icon name="keyboard_arrow_down" size="lg" />
        </button>
        <Typography variant="body-sm" className="font-bold tracking-widest uppercase text-text-secondary">
          Now Playing
        </Typography>
        <button className="p-2 -mr-2 text-text-secondary hover:text-white transition-colors">
          <Icon name="more_vert" />
        </button>
      </div>

      {/* Album Art */}
      <div className="flex-1 max-h-[400px] flex items-center justify-center mb-8 w-full">
        <div className="w-full aspect-square max-w-[350px] rounded-lg overflow-hidden bg-surface-container shadow-2xl relative">
          {track?.albumArtUrl ? (
            <img src={track.albumArtUrl} alt="Album Art" className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <Icon name="music_note" size="xl" className="text-surface-bright" style={{ fontSize: '100px' }} />
            </div>
          )}
        </div>
      </div>

      {/* Track Info & Like */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex flex-col flex-1 overflow-hidden pr-4">
          <Typography variant="headline" as="h2" className="font-bold truncate text-white mb-1">
            {track?.title || "Not playing"}
          </Typography>
          <Typography variant="body-md" className="text-text-secondary truncate">
            {track?.artist || ""}
          </Typography>
        </div>
        <button 
          className="text-text-secondary hover:text-primary transition-colors p-2"
          onClick={() => handleAction('like')}
        >
          <Icon name="favorite" />
        </button>
      </div>

      {/* Progress Bar */}
      <div className="mb-6 w-full">
        <div 
          className="h-1.5 w-full bg-surface-bright rounded-full overflow-hidden cursor-pointer relative"
          onClick={(e) => {
            if (onSeek && duration > 0) {
              const rect = e.currentTarget.getBoundingClientRect();
              const x = Math.max(0, Math.min(e.clientX - rect.left, rect.width));
              onSeek((x / rect.width) * duration);
            }
          }}
        >
          <div className="h-full bg-white transition-all absolute left-0 top-0" style={{ width: `${progressPercent}%` }} />
        </div>
        <div className="flex items-center justify-between mt-2 text-xs text-text-secondary font-medium tracking-wider w-full">
          <span>{formatTime(currentTime)}</span>
          <span>{formatTime(duration)}</span>
        </div>
      </div>

      {/* Playback Controls */}
      <div className="flex items-center justify-between mb-8 w-full px-2">
        <button onClick={onShuffle} className="text-text-secondary hover:text-white p-2 transition-colors">
          <Icon name="shuffle" />
        </button>
        <button onClick={onSkipPrevious} className="text-white p-2 hover:text-primary transition-colors">
          <Icon name="skip_previous" size="lg" style={{ fontSize: '42px' }} />
        </button>
        <button 
          onClick={onPlayPause} 
          className="w-16 h-16 rounded-full bg-white text-black flex items-center justify-center hover:scale-105 active:scale-95 transition-transform shadow-lg"
        >
          <Icon name={isPlaying ? "pause" : "play_arrow"} size="lg" style={{ fontVariationSettings: "'FILL' 1", fontSize: '38px' }} />
        </button>
        <button onClick={onSkipNext} className="text-white p-2 hover:text-primary transition-colors">
          <Icon name="skip_next" size="lg" style={{ fontSize: '42px' }} />
        </button>
        <button onClick={onRepeat} className="text-text-secondary hover:text-white p-2 transition-colors">
          <Icon name="repeat" />
        </button>
      </div>

      {/* Bottom Actions */}
      <div className="flex items-center justify-between mt-auto w-full">
        <button onClick={onDevicesClick} className="text-text-secondary hover:text-white p-2 flex flex-col items-center gap-1 transition-colors">
          <Icon name="devices" />
        </button>
        <button onClick={() => handleAction('playlist_add')} className="text-text-secondary hover:text-white p-2 flex flex-col items-center gap-1 transition-colors">
          <Icon name="playlist_add" />
        </button>
        <button onClick={handleShare} className="text-text-secondary hover:text-white p-2 flex flex-col items-center gap-1 transition-colors">
          <Icon name="share" />
        </button>
        <button onClick={() => handleAction('bookmark')} className="text-text-secondary hover:text-white p-2 flex flex-col items-center gap-1 transition-colors">
          <Icon name="bookmark" />
        </button>
      </div>
    </div>
  );
};
