import React from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

interface PodcastResultCardProps {
  podcastResult: any;
  prompt: string;
  handleAction: (action: 'like' | 'share', targetId?: string) => void;
  setCurrentTrack: (track: any) => void;
  setIsPlaying: (playing: boolean) => void;
}

export const PodcastResultCard: React.FC<PodcastResultCardProps> = ({
  podcastResult,
  prompt,
  handleAction,
  setCurrentTrack,
  setIsPlaying
}) => {
  return (
    <div className="p-5 bg-surface-container border border-primary/40 rounded-2xl shadow-2xl flex flex-col gap-4 text-white">
      <div className="flex items-center gap-4">
        <div className="relative w-24 h-24 rounded-xl overflow-hidden shadow-md flex-shrink-0 group">
          {podcastResult.coverUrl ? (
            <img
              src={podcastResult.coverUrl}
              alt="Podcast Cover"
              loading="lazy"
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="w-full h-full bg-surface flex items-center justify-center">
              <Icon name="podcasts" size="lg" />
            </div>
          )}
          {/* Overlay Actions */}
          <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex flex-col justify-end p-1">
            <div className="flex justify-between items-center">
              <button onClick={() => handleAction('like', podcastResult.id || prompt)} className="p-1 hover:text-primary transition-colors text-white" title="Like">
                <Icon name="favorite_border" size="sm" />
              </button>
              <button 
                  onClick={(e) => {
                    e.stopPropagation();
                    handleAction('share', podcastResult.id || prompt);
                  }} 
                  className="p-2 hover:bg-white/10 rounded-full transition-colors text-white"
                  title="Share"
                >
                  <Icon name="share" size="sm" />
                </button>
            </div>
          </div>
        </div>
        <div className="flex-1 overflow-hidden">
          <Typography variant="title-md" className="font-bold truncate">{podcastResult.title}</Typography>
          <Typography variant="label-sm" color="secondary">Voice: {podcastResult.voice} • Duration: {podcastResult.duration}</Typography>
        </div>
      </div>

      <div className="bg-surface/80 p-3.5 rounded-xl border border-outline/20">
        <Typography variant="label-sm" color="primary" className="font-bold mb-1">EPISODE TRANSCRIPT</Typography>
        <p className="text-xs text-text-primary italic leading-relaxed font-serif">
          "{podcastResult.script}"
        </p>
      </div>

      <div className="flex items-center justify-between pt-1">
        <span className="text-[10px] text-text-secondary font-semibold">GOOGLE CLOUD</span>
        <button
          onClick={() => {
            if (typeof navigator !== 'undefined' && navigator.vibrate) {
              navigator.vibrate([50, 50, 50]);
            }
            setCurrentTrack({
              id: podcastResult.id || Date.now().toString(),
              title: podcastResult.title,
              artist: `Voice: ${podcastResult.voice || 'AOEDE'}`,
              albumArtUrl: podcastResult.coverUrl,
            });
            setIsPlaying(true);
          }}
          className="px-4 py-2 bg-primary text-black rounded-full text-xs font-bold flex items-center gap-1.5 hover:brightness-110 active:scale-95 transition-all shadow"
          title="Listen"
        >
          <Icon name="play_arrow" size="sm" />
        </button>
      </div>
    </div>
  );
};
