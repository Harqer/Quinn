import React from 'react';
import { useTranslation } from 'react-i18next';
import { MaveLogo } from '../atoms/MaveLogo';

interface MaveHeaderHubProps {
  mode: string;
  switchMode: (mode: string) => void;
  videoActive: boolean;
  setVideoActive: (active: boolean) => void;
}

export const MaveHeaderHub: React.FC<MaveHeaderHubProps> = ({
  mode,
  switchMode,
  videoActive,
  setVideoActive
}) => {
  const { t } = useTranslation();

  return (
    <div className="absolute top-6 left-8 right-8 flex justify-between items-center z-10">
      <div className="flex items-center gap-6">
        <MaveLogo variant="dark" size={140} />
        <div className="flex bg-[#282828] p-1 rounded-full border border-white/5">
          <button
            onClick={() => switchMode('music')}
            className={`px-6 py-1.5 rounded-full text-xs font-bold transition-all ${mode === 'music' ? 'bg-[#1DB954] text-black shadow-lg' : 'text-gray-400 hover:text-white'}`}
          >
            <span className="material-icons-round text-lg leading-none">music_note</span>
          </button>
          <button
            onClick={() => switchMode('podcast')}
            className={`px-6 py-1.5 rounded-full text-xs font-bold transition-all ${mode === 'podcast' ? 'bg-[#1DB954] text-black shadow-lg' : 'text-gray-400 hover:text-white'}`}
          >
            <span className="material-icons-round text-lg leading-none">podcasts</span>
          </button>
          <button
            onClick={() => switchMode('audiobook')}
            className={`px-6 py-1.5 rounded-full text-xs font-bold transition-all ${mode === 'audiobook' ? 'bg-[#1DB954] text-black shadow-lg' : 'text-gray-400 hover:text-white'}`}
          >
            <span className="material-icons-round text-lg leading-none">menu_book</span>
          </button>
        </div>
      </div>

      <div className="flex gap-3">
        <button
          onClick={() => setVideoActive(!videoActive)}
          className={`p-3 rounded-full transition-all ${videoActive ? 'bg-[#1DB954] text-black' : 'bg-[#282828] text-white hover:bg-[#333333]'}`}
        >
          <span className="material-icons-round text-xl">videocam</span>
        </button>
        <div className="px-4 py-2 bg-red-600 rounded-full flex items-center gap-2 shadow-lg">
          <span className="w-2 h-2 rounded-full bg-white animate-pulse" />
          <span className="text-[10px] font-black uppercase tracking-widest">{t('dashboard.livePov')}</span>
        </div>
      </div>
    </div>
  );
};
