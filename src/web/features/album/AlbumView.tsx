import React from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { TrackListItem } from '../../components/molecules/TrackListItem';

export const AlbumView: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="bg-gradient-to-b from-[#6b1e1e] to-background pt-12 pb-6 px-4 sticky top-0 z-10 -mb-10">
        <div className="flex justify-between items-center text-white mb-6">
          <button onClick={onBack} className="drop-shadow-md p-2 -ml-2">
            <Icon name="chevron_left" size="3xl" />
          </button>
        </div>
      </div>
      
      <div className="px-4 relative z-0">
        <div className="flex flex-col items-center justify-center mb-6 mt-4">
          <div className="w-[220px] h-[220px] bg-surface-container shadow-2xl mb-6 flex items-center justify-center overflow-hidden">
             <div className="w-full h-full bg-gradient-to-br from-red-600 to-yellow-500 flex items-center justify-center text-white text-8xl font-bold font-serif shadow-inner">
               1
             </div>
          </div>
          <div className="w-full text-left">
            <Typography variant="title-lg" className="font-bold tracking-tight">1 (Remastered)</Typography>
            <span className="text-text-secondary text-sm mt-1 flex items-center gap-1 font-medium">
              <div className="w-5 h-5 rounded-full bg-white/20"></div>
              The Beatles
            </span>
          </div>
        </div>
        
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-4 text-text-secondary">
            <button className="p-2"><Icon name="favorite_border" size="3xl" /></button>
            <button className="p-2"><Icon name="add_circle_outline" size="3xl" /></button>
            <button className="p-2"><Icon name="more_horiz" size="3xl" /></button>
          </div>
          <button className="w-14 h-14 rounded-full bg-primary hover:scale-105 hover:bg-primary-variant transition-all flex items-center justify-center shadow-lg active:scale-95">
            <Icon name="play_arrow" size="3xl" color="black" className="ml-1" />
          </button>
        </div>
      </div>
      
      <div className="flex flex-col px-0 gap-0 mt-2">
        <TrackListItem title="Love Me Do - Mono / Remastered" artist="The Beatles" />
        <TrackListItem title="From Me to You - Mono / Remastered" artist="The Beatles" isPlaying />
        <TrackListItem title="She Loves You - Mono / Remastered" artist="The Beatles" />
        <TrackListItem title="I Want to Hold Your Hand - Remastered" artist="The Beatles" />
        <TrackListItem title="Can't Buy Me Love - Remastered" artist="The Beatles" />
      </div>
    </div>
  );
};
