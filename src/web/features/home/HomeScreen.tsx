import React from 'react';
import { Carousel } from '../../components/organisms/Carousel';
import { Card } from '../../components/molecules/Card';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { useTracks } from '../../hooks/useTracks';

export const HomeScreen: React.FC = () => {
  const { communityTracks, userTracks, loading } = useTracks();

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="flex justify-between items-center px-4 pt-12 pb-4 sticky top-0 bg-background/90 backdrop-blur-md z-10">
        <Typography variant="headline" className="font-bold tracking-tight">Recently played</Typography>
        <div className="flex gap-4 text-white">
          <Icon name="notifications_none" />
          <Icon name="history" />
          <Icon name="settings" />
        </div>
      </div>
      
      {loading ? (
        <div className="flex-1 flex items-center justify-center">
           <Typography variant="body-lg">Loading Studio...</Typography>
        </div>
      ) : (
        <>
          <Carousel>
            <Card title="1(Remastered)" />
            <Card title="Lana Del Rey" isCircle />
            <Card title="Marvin Gaye" isCircle />
            <Card title="Indie Mix" />
          </Carousel>
          
          <div className="px-4 py-4">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-6 h-6 bg-white rounded-sm flex items-center justify-center text-[10px] font-bold text-black">#</div>
              <div className="flex flex-col">
                <Typography variant="label-sm" color="secondary" className="tracking-wider font-bold">
                  #MAVEPULSE
                </Typography>
                <Typography variant="title-lg" className="font-bold">
                  Your Studio highlights
                </Typography>
              </div>
            </div>
            
            <div className="grid grid-cols-2 gap-4">
              <div className="aspect-square bg-gradient-to-br from-green-300 to-yellow-200 rounded-md p-4 flex flex-col justify-between shadow-lg">
                 <div>
                   <Typography variant="title-lg" className="text-black font-bold leading-tight">Your Top<br/>Vibes</Typography>
                   <Typography variant="display" className="text-black font-bold">2026</Typography>
                 </div>
                 <Typography variant="label-md" className="text-black font-bold">Daily Mave Mix</Typography>
              </div>
              <div className="aspect-square bg-gradient-to-br from-purple-400 to-purple-600 rounded-md p-4 flex flex-col justify-between shadow-lg relative overflow-hidden">
                 <div>
                   <Typography variant="title-lg" className="font-bold leading-tight">Mave AI<br/>Insights</Typography>
                 </div>
                 <div className="absolute -bottom-4 -right-4 w-24 h-24 border-4 border-white/20 rounded-full"></div>
                 <Typography variant="label-md" className="font-bold z-10 relative">Personal Orchestra</Typography>
              </div>
            </div>
          </div>
          
          <Carousel title="Community Vibes">
            {communityTracks.map(track => (
              <Card 
                key={track.id} 
                title={track.title} 
                subtitle={track.artist} 
                imageUrl={track.albumArtUrl}
              />
            ))}
          </Carousel>

          {userTracks.length > 0 && (
            <Carousel title="Your Library">
              {userTracks.map(track => (
                <Card 
                  key={track.id} 
                  title={track.title} 
                  subtitle={track.artist} 
                  imageUrl={track.albumArtUrl}
                />
              ))}
            </Carousel>
          )}
        </>
      )}
    </div>
  );
};
