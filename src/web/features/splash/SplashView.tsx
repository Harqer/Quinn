import React from 'react';

interface Props {
  onLaunch: () => void;
}

export const SplashView: React.FC<Props> = ({ onLaunch }) => {
  return (
    <div className="flex flex-col items-center justify-center h-full px-6 text-center bg-gradient-to-br from-gray-900 to-black">
      <div className="w-24 h-24 mb-8 rounded-3xl bg-primary flex items-center justify-center shadow-2xl shadow-primary/40 animate-pulse">
        <span className="material-icons-round text-5xl">music_note</span>
      </div>

      <h1 className="text-4xl font-bold mb-4 tracking-tight">Musically Live</h1>
      <p className="text-gray-400 text-lg mb-12 max-w-md leading-relaxed">
        Turn your surroundings into a living symphony. Connect your Meta Wearables to begin your POV musical journey.
      </p>

      <button
        onClick={onLaunch}
        className="px-8 py-4 bg-primary hover:bg-primary/90 text-white rounded-full font-bold text-lg transition-all transform hover:scale-105 active:scale-95 shadow-xl shadow-primary/20"
      >
        Launch Experience
      </button>

      <div className="absolute bottom-12 flex gap-8 text-gray-500">
        <div className="flex flex-col items-center gap-2">
          <span className="material-icons-round">bluetooth</span>
          <span className="text-xs uppercase tracking-widest font-bold">Wearables</span>
        </div>
        <div className="flex flex-col items-center gap-2 text-primary">
          <span className="material-icons-round">psychology</span>
          <span className="text-xs uppercase tracking-widest font-bold">Gemini 1.5</span>
        </div>
        <div className="flex flex-col items-center gap-2">
          <span className="material-icons-round">graphic_eq</span>
          <span className="text-xs uppercase tracking-widest font-bold">Realtime</span>
        </div>
      </div>
    </div>
  );
};
