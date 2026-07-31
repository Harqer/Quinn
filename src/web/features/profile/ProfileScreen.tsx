import React from 'react';
import { useNavigate } from '../../App';
import { getAuth } from 'firebase/auth';
import { useTracks } from '../../hooks/useTracks';
import { useAppContext } from '../../contexts/AppContext';
import { Typography } from '../../components/atoms/Typography';
import { Shimmer } from '../../components/atoms/Shimmer';
import { Icon } from '../../components/atoms/Icon';
import { usePlayerContext } from '../../contexts/PlayerContext';

export const ProfileScreen: React.FC = () => {
  const navigate = useNavigate();
  const auth = getAuth();
  const user = auth.currentUser;
  const { userTracks } = useTracks();
  const { setActiveAlbumId } = useAppContext();
  const { playQueue } = usePlayerContext();

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      {/* Header Area */}
      <div className="relative pt-12 md:pt-16 pb-8 px-4 md:px-8 bg-gradient-to-b from-surface-container-high to-background">
        <button 
          onClick={() => navigate('home')}
          className="absolute top-4 left-4 p-2 rounded-full bg-black/40 text-on-surface hover:bg-black/60 transition-colors"
        >
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>

        <div className="flex flex-col md:flex-row items-center md:items-end gap-6 mt-4">
          <div className="w-32 h-32 md:w-48 md:h-48 rounded-full bg-primary flex items-center justify-center text-4xl md:text-7xl font-bold text-black overflow-hidden shadow-2xl flex-shrink-0">
            {user?.photoURL ? (
              <img src={user.photoURL} alt="Profile" className="w-full h-full object-cover" />
            ) : (
              <Icon name="account_circle" />
            )}
          </div>
          <div className="flex flex-col items-center md:items-start flex-1 text-center md:text-left">
            <Typography variant="label-sm" className="font-bold uppercase tracking-widest text-on-surface">Profile</Typography>
            <h1 className="text-4xl md:text-6xl font-extrabold text-on-surface mt-2 mb-4 tracking-tight">
              {user?.displayName || user?.email?.split('@')[0] || 'User'}
            </h1>
            <div className="flex items-center gap-4 text-sm font-bold text-secondary">
              <span>{userTracks.length} Tracks</span>
            </div>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="px-4 md:px-8 py-6 flex items-center gap-4">
        <button 
          onClick={() => navigate('settings')}
          className="bg-transparent border border-outline text-on-surface font-bold px-4 py-1.5 rounded-full hover:border-on-surface hover:scale-105 transition-all text-sm"
        >
          Settings
        </button>
      </div>

      {/* Recent Tracks List */}
      <div className="px-4 md:px-8 py-4">
        <h2 className="text-xl font-bold mb-4 text-on-surface">Top tracks this month</h2>
        {userTracks.length > 0 ? (
          <div className="flex flex-col">
            {userTracks.slice(0, 5).map((track, index) => (
              <div 
                key={track.id} 
                className="flex items-center gap-4 p-2 rounded-md hover:bg-surface-variant transition-colors group cursor-pointer"
                onClick={() => {
                  if (track.audioUrl) {
                    playQueue(userTracks, index);
                  } else {
                    setActiveAlbumId(track.id);
                    navigate('album');
                  }
                }}
              >
                <span className="text-secondary font-semibold w-4 text-right">{index + 1}</span>
                <div className="w-10 h-10 bg-surface-container rounded-sm overflow-hidden flex-shrink-0">
                  {track.albumArtUrl ? (
                    <img src={track.albumArtUrl} alt={track.title} className="w-full h-full object-cover" />
                  ) : (
                    <Shimmer className="w-full h-full" />
                  )}
                </div>
                <div className="flex flex-col flex-1 min-w-0">
                  <span className="font-bold text-on-surface truncate">{track.title}</span>
                  <span className="text-sm text-secondary truncate">{track.artist || 'Unknown'}</span>
                </div>
                <div className="text-secondary text-sm hidden sm:block w-32 truncate">
                  Track
                </div>
                <Icon name="play_arrow" size="sm" className="opacity-0 group-hover:opacity-60 transition-opacity" />
              </div>
            ))}
          </div>
        ) : (
          <div className="text-secondary">No tracks generated yet.</div>
        )}
      </div>
    </div>
  );
};
