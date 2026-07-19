import React from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { TrackListItem } from '../../components/molecules/TrackListItem';

export const LibraryScreen: React.FC = () => {
  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="flex items-center gap-4 px-4 pt-12 pb-4 sticky top-0 bg-background/90 backdrop-blur-md z-10 border-b border-surface-container">
        <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-xs font-bold text-black overflow-hidden shadow-inner">
           S
        </div>
        <Typography variant="headline" className="font-bold tracking-tight flex-1">Your Library</Typography>
        <div className="flex gap-4 text-white">
          <Icon name="search" size="xl" />
          <Icon name="add" size="xl" />
        </div>
      </div>
      
      <div className="flex gap-2 px-4 py-4 overflow-x-auto scrollbar-hide">
        <FilterPill label="Playlists" />
        <FilterPill label="Artists" />
        <FilterPill label="Albums" />
        <FilterPill label="Podcasts & Shows" />
      </div>

      <div className="px-4 py-2 flex items-center justify-between text-white mb-2">
        <div className="flex items-center gap-2">
           <Icon name="swap_vert" size="md" />
           <Typography variant="label-md" className="font-bold">Recently played</Typography>
        </div>
        <Icon name="format_list_bulleted" size="lg" color="secondary" />
      </div>

      <div className="flex flex-col px-0 gap-0">
        <TrackListItem 
          title="Liked Songs" 
          artist="Playlist • 58 songs" 
          rightElement={<span />}
        />
        <TrackListItem 
          title="New Episodes" 
          artist="Updated 2 days ago" 
          rightElement={<span />}
        />
        <TrackListItem 
          title="Lolo Zouaï" 
          artist="Artist" 
          rightElement={<span />}
        />
        <TrackListItem 
          title="Lana Del Rey" 
          artist="Artist" 
          rightElement={<span />}
        />
        <TrackListItem 
          title="Front Left" 
          artist="Playlist • Spotify" 
          rightElement={<span />}
        />
      </div>
    </div>
  );
};

const FilterPill = ({ label }: { label: string }) => (
  <button className="border border-outline rounded-full px-4 py-1.5 text-text-primary text-[11px] tracking-wide font-medium whitespace-nowrap hover:bg-surface-container transition-colors">
    {label}
  </button>
);
