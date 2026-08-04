import React, { useState, useEffect } from 'react';
import { AnimatedGradient } from '../../components/atoms/AnimatedGradient';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { musicService, Category } from '../../services/MusicService';

interface MoodAdScreenProps {
  onClose: () => void;
  onStartMix: (mood: string) => void;
  isStarting?: boolean;
  generationError?: string | null;
}

export const MoodAdScreen: React.FC<MoodAdScreenProps> = ({ onClose, onStartMix, isStarting, generationError }) => {
  const [selectedMood, setSelectedMood] = useState<string>('');
  const [customMood, setCustomMood] = useState<string>('');
  const [moods, setMoods] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    musicService.getCategories()
      .then(categories => {
        if (!mounted) return;
        // Filter or just take the first 3 for the UI
        const moodCategories = categories.filter(c => c.type === 'mood' || c.type === 'genre').slice(0, 3);
        setMoods(moodCategories);
        if (moodCategories.length > 0) setSelectedMood(moodCategories[0].title);
        setIsLoading(false);
      })
      .catch(err => {
        if (!mounted) return;
        console.error("Failed to fetch categories:", err);
        setError("Couldn't load moods.");
        setIsLoading(false);
      });
    return () => { mounted = false; };
  }, []);

  const handleStart = () => {
    if (isStarting) return;
    onStartMix(customMood.trim() || selectedMood);
  };

  return (
    <div className="fixed inset-0 z-[100] flex flex-col items-center pt-24 px-6 bg-gradient-to-b from-[#3a3541] to-[#1e1c24] text-white overflow-hidden animate-fade-in">
      {!isStarting && (
        <button 
          onClick={onClose} 
          className="absolute top-6 right-6 p-2 rounded-full hover:bg-white/10 transition-colors"
        >
          <Icon name="close" size="lg" />
        </button>
      )}

      <Typography variant="display" className="text-center font-bold mb-12 max-w-[320px] leading-tight">
        Start music to match your mood
      </Typography>

      {isLoading ? (
        <div className="flex gap-6 mb-12 justify-center">
          <div className="w-8 h-8 border-4 border-white/20 border-t-white rounded-full animate-spin"></div>
        </div>
      ) : error ? (
        <div className="mb-12 text-red-400 bg-red-400/10 px-4 py-2 rounded-lg">
          <Typography variant="body-md">{error}</Typography>
        </div>
      ) : (
        <div className="flex gap-6 mb-12 justify-center">
          {moods.map(m => {
            const isSelected = (!customMood && selectedMood === m.title);
            return (
              <div key={m.id} className={`flex flex-col items-center gap-4 cursor-pointer ${isStarting ? 'opacity-50 pointer-events-none' : ''}`} onClick={() => { setSelectedMood(m.title); setCustomMood(''); }}>
                <AnimatedGradient 
                  mood={m.title.toLowerCase()} 
                  className={`w-24 h-24 sm:w-28 sm:h-28 rounded-full transition-transform hover:scale-105 ${isSelected ? 'ring-2 ring-offset-2 ring-offset-[#3a3541] ring-white' : ''} ${isStarting && isSelected ? 'animate-pulse' : ''}`}
                />
                <Typography variant="label-lg" className={`font-medium text-lg ${isSelected ? 'text-white' : 'text-gray-400'}`}>
                  {m.title}
                </Typography>
              </div>
            );
          })}
        </div>
      )}

      <div className="w-full max-w-sm mb-auto">
        <div className="relative">
          <input 
            type="text" 
            placeholder="Or type a custom mood..." 
            value={customMood}
            onChange={(e) => setCustomMood(e.target.value)}
            disabled={isStarting}
            className="w-full bg-white/10 border border-white/20 rounded-full py-4 px-6 text-white placeholder-gray-400 focus:outline-none focus:border-white transition-colors text-center text-lg disabled:opacity-50"
            onFocus={() => setSelectedMood('')}
          />
        </div>
      </div>

      <div className="w-full max-w-sm pb-12 mt-6 flex flex-col gap-4">
        {generationError && (
          <div className="text-center p-3 bg-red-500/20 text-red-200 rounded-lg text-sm border border-red-500/30">
            {generationError}
          </div>
        )}
        <button 
          onClick={handleStart}
          disabled={isStarting || isLoading || (!selectedMood && !customMood.trim())}
          className="w-full bg-white text-black font-bold py-4 rounded-full text-xl hover:scale-[1.02] transition-transform active:scale-95 disabled:opacity-50 disabled:hover:scale-100 flex justify-center items-center gap-2"
        >
          {isStarting ? (
            <>
              <div className="w-5 h-5 border-2 border-black/20 border-t-black rounded-full animate-spin"></div>
              Generating...
            </>
          ) : (
            'Start Mix'
          )}
        </button>
      </div>
    </div>
  );
};
