import React from 'react';
import { Carousel } from '../../components/organisms/Carousel';
import { Card } from '../../components/molecules/Card';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { useTracks } from '../../hooks/useTracks';
import { getAuth } from 'firebase/auth';
import { Shimmer } from '../../components/atoms/Shimmer';
import maveLogoDark from '../../assets/mave_brand_dark.png';

export const HomeScreen: React.FC = () => {
  const { communityTracks, userTracks, loading } = useTracks();
  const auth = getAuth();
  const user = auth.currentUser;
  const userInitial = user?.displayName ? user.displayName[0].toUpperCase() : (user?.email ? user.email[0].toUpperCase() : 'M');

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="flex flex-col gap-4 px-4 pt-12 pb-2 sticky top-0 bg-background/90 backdrop-blur-md z-10">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-xs font-bold text-black overflow-hidden shadow-inner flex-shrink-0">
            {userInitial}
          </div>
          <Typography variant="headline" className="font-bold tracking-tight flex-1">
            Good {new Date().getHours() < 12 ? 'morning' : new Date().getHours() < 18 ? 'afternoon' : 'evening'}, {user?.displayName?.split(' ')[0] || 'User'}
          </Typography>
          <div className="flex gap-2">
            <button className="bg-surface px-4 py-1.5 rounded-full text-sm font-medium text-text-primary flex items-center justify-center" title="All" onClick={() => console.log('Filter All')}><Icon name="all_inclusive" size="sm" /></button>
            <button className="bg-surface-container px-4 py-1.5 rounded-full text-sm font-medium text-text-primary flex items-center justify-center" title="Music" onClick={() => console.log('Filter Music')}><Icon name="music_note" size="sm" /></button>
            <button className="bg-surface-container px-4 py-1.5 rounded-full text-sm font-medium text-text-primary flex items-center justify-center" title="Podcasts" onClick={() => console.log('Filter Podcasts')}><Icon name="podcasts" size="sm" /></button>
          </div>
        </div>
      </div>
      
      {loading ? (
        <div className="flex-1 flex items-center justify-center">
           <Typography variant="body-lg">Loading Studio...</Typography>
        </div>
      ) : (
        <>
          <div className="px-4 py-2">
            <div className="grid grid-cols-2 gap-2">
              {(userTracks.length > 0 ? userTracks : communityTracks).slice(0, 6).map(track => (
                <div key={`recent-${track.id}`} className="bg-surface-container hover:bg-surface rounded-[4px] flex items-center gap-2 overflow-hidden cursor-pointer transition-colors" onClick={() => console.log('Recent track clicked', track.id)}>
                  {track.albumArtUrl ? (
                    <img src={track.albumArtUrl} alt={track.title} className="w-14 h-14 object-cover" />
                  ) : (
                    <Shimmer className="w-14 h-14" />
                  )}
                  <Typography variant="label-md" className="font-bold line-clamp-2 pr-2 leading-tight">
                    {track.title}
                  </Typography>
                </div>
              ))}
            </div>
          </div>
          
          <Carousel title="Made for you">
            {userTracks.length > 0 ? (
              userTracks.slice(0, 5).map(track => (
                <Card key={track.id} title={track.title} subtitle={track.artist} imageUrl={track.albumArtUrl} onClick={() => console.log('Card clicked', track.id)} />
              ))
            ) : (
              communityTracks.slice(0, 5).map(track => (
                <Card key={track.id} title={track.title} subtitle={track.artist} imageUrl={track.albumArtUrl} onClick={() => console.log('Card clicked', track.id)} />
              ))
            )}
          </Carousel>
          
          <Carousel title="Community Vibes">
            {communityTracks.map(track => (
              <Card 
                key={track.id} 
                title={track.title} 
                subtitle={track.artist} 
                imageUrl={track.albumArtUrl}
                onClick={() => console.log('Community Card clicked', track.id)}
              />
            ))}
          </Carousel>

          {userTracks.length > 0 && (
            <Carousel title="Recently played">
              {userTracks.map(track => (
                <Card 
                  key={track.id} 
                  title={track.title} 
                  subtitle={track.artist || 'Unknown Artist'} 
                  imageUrl={track.albumArtUrl}
                  onClick={() => console.log('Recent played Card clicked', track.id)}
                />
              ))}
            </Carousel>
          )}
        </>
      )}
    </div>
  );
};
