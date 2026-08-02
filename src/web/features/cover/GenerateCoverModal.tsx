import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { logger } from "../../lib/logger";

const TypewriterText: React.FC<{ text: string, speed?: number }> = ({ text, speed = 40 }) => {
  const [displayed, setDisplayed] = useState('');
  
  useEffect(() => {
    setDisplayed('');
    let i = 0;
    const interval = setInterval(() => {
      if (i < text.length) {
        setDisplayed(text.substring(0, i + 1));
        i++;
      } else {
        clearInterval(interval);
      }
    }, speed);
    return () => clearInterval(interval);
  }, [text, speed]);

  return <span>{displayed}<span className="animate-pulse">|</span></span>;
};

export interface GenerateCoverModalProps {
  isOpen: boolean;
  onClose: () => void;
  trackTitle?: string;
  onCoverGenerated?: (coverUrl: string, type: 'image' | 'video') => void;
}

export const GenerateCoverModal: React.FC<GenerateCoverModalProps> = ({
  isOpen,
  onClose,
  trackTitle = "Untitled Track",
  onCoverGenerated
}) => {
  const [coverType, setCoverType] = useState<'image' | 'video'>('image');
  const [prompt, setPrompt] = useState('');
  const [selectedPreset, setSelectedPreset] = useState('Vibrant Synthwave');
  const [isGenerating, setIsGenerating] = useState(false);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const { t } = useTranslation();

  if (!isOpen) return null;

  const imagePresets = ['Vibrant Synthwave', 'Minimalist Neon', 'Abstract Cyberpunk', 'Retro Vinyl'];
  const videoPresets = ['Neon Audio Visualizer', 'Glitch Wave', 'Fluid Ambient', 'Reactive Particles'];

  const presets = coverType === 'image' ? imagePresets : videoPresets;

  const handleGenerate = async () => {
    setIsGenerating(true);
    try {
      const finalPrompt = prompt.trim() || `${selectedPreset} aesthetic for track ${trackTitle}`;
      const baseUrl = import.meta.env.VITE_API_URL || '';
      const response = await fetch(`${baseUrl}/api/music/generate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          image: finalPrompt,
          type: coverType === 'image' ? 'cover_art' : 'video_motion'
        })
      });

      const data = await response.json();
      const generatedUrl = data.audioUrl || data.url;
      if (!generatedUrl) {
        throw new Error('No URL returned from server');
      }

      setPreviewUrl(generatedUrl);
      if (onCoverGenerated) {
        onCoverGenerated(generatedUrl, coverType);
      }
    } catch (err) {
      logger.error('Failed to generate cover', err);
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-md p-4">
      <div className="w-full max-w-md bg-[#121414] border border-surface-container rounded-3xl p-6 shadow-2xl flex flex-col gap-5 text-white">
        {/* Header */}
        <div className="flex items-center justify-between">
          <Typography variant="title-lg" className="font-bold flex items-center gap-2">
            <Icon name="auto_awesome" color="primary" />
            {t('cover.generate')} {coverType === 'image' ? t('cover.imageCover') : t('cover.videoCover')}
          </Typography>
          <button onClick={onClose} className="p-1 text-text-secondary hover:text-white">
            <Icon name="close" size="xl" />
          </button>
        </div>

        {/* Mode Switcher */}
        <div className="flex bg-[#1e2020] rounded-full p-1 border border-surface-container">
          <button
            className={`flex-1 py-2 text-xs font-bold rounded-full transition-all flex items-center justify-center gap-1.5 ${
              coverType === 'image' ? 'bg-[#1db954] text-black shadow-md' : 'text-text-secondary hover:text-white'
            }`}
            onClick={() => setCoverType('image')}
            title="Image Cover"
          >
            <Icon name="image" size="sm" />
          </button>
          <button
            className={`flex-1 py-2 text-xs font-bold rounded-full transition-all flex items-center justify-center gap-1.5 ${
              coverType === 'video' ? 'bg-[#1db954] text-black shadow-md' : 'text-text-secondary hover:text-white'
            }`}
            onClick={() => setCoverType('video')}
            title="Video Cover"
          >
            <Icon name="videocam" size="sm" />
          </button>
        </div>

        {/* Preview Canvas */}
        <div className="w-full aspect-square bg-[#1a1c1c] rounded-2xl overflow-hidden border border-surface-container relative flex items-center justify-center group shadow-inner">
          {previewUrl ? (
            coverType === 'video' ? (
              <video src={previewUrl} autoPlay loop muted className="w-full h-full object-cover" />
            ) : (
              <img src={previewUrl} alt="Cover Preview" className="w-full h-full object-cover" />
            )
          ) : (
            <div className="flex flex-col items-center justify-center gap-2 p-6 text-center text-text-secondary">
              {coverType === 'image' ? (
                <Icon name="brush" size="5xl" className="opacity-40 animate-pulse" />
              ) : (
                <svg xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" width="48px" fill="currentColor" className="opacity-40 animate-pulse"><path d="m480-420 240-160-240-160v320Zm28 220h224q-7 26-24 42t-44 20L228-85q-33 5-59.5-15.5T138-154L85-591q-4-33 16-59t53-30l46-6v80l-36 5 54 437 290-36Zm-148-80q-33 0-56.5-23.5T280-360v-440q0-33 23.5-56.5T360-880h440q33 0 56.5 23.5T880-800v440q0 33-23.5 56.5T800-280H360Zm0-80h440v-440H360v440Zm220-220ZM218-164Z"/></svg>
              )}
              <Typography variant="body-md" className="font-medium text-text-secondary">
                {t('cover.describeMood')}
              </Typography>
            </div>
          )}
          {isGenerating && (
            <div className="absolute inset-0 bg-black/70 backdrop-blur-sm flex flex-col items-center justify-center gap-3">
              <div className="w-10 h-10 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
              <Typography variant="body-md" className="font-bold text-primary">
                <TypewriterText text={coverType === 'image' ? t('cover.generatingImage') : t('cover.generatingVideo')} />
              </Typography>
            </div>
          )}
        </div>

        {/* Style Preset Pills */}
        <div className="flex flex-col gap-2">
          <Typography variant="label-sm" className="text-text-secondary text-xs uppercase tracking-wider font-bold">
            {t('cover.stylePresets')}
          </Typography>
          <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-none">
            {presets.map((preset) => {
              const iconName = preset.includes('Synthwave') || preset.includes('Glitch') ? 'waves' :
                               preset.includes('Neon') || preset.includes('Fluid') ? 'lens_blur' :
                               preset.includes('Cyberpunk') || preset.includes('Particles') ? 'memory' : 'album';
              return (
              <button
                key={preset}
                onClick={() => setSelectedPreset(preset)}
                className={`px-3.5 py-1.5 rounded-full flex items-center justify-center gap-1.5 transition-all ${
                  selectedPreset === preset
                    ? 'bg-[#1db954] text-black shadow-sm font-bold'
                    : 'bg-[#282a2b] text-text-secondary hover:bg-surface-container hover:text-white'
                }`}
                title={preset}
              >
                <Icon name={iconName} size="sm" />
                <span className="text-xs font-semibold">{preset}</span>
              </button>
              );
            })}
          </div>
        </div>

        {/* Prompt Input */}
        <div className="flex flex-col gap-1.5">
          <Typography variant="label-sm" className="text-text-secondary text-xs uppercase tracking-wider font-bold">
            {t('cover.customPrompt')}
          </Typography>
          <input
            type="text"
            placeholder={t('cover.promptPlaceholder')}
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            className="w-full bg-[#1e2020] border border-surface-container rounded-xl px-4 py-3 text-sm text-white outline-none focus:border-[#1db954] transition-colors"
          />
        </div>

        {/* Action Controls */}
        <div className="flex flex-col gap-2 mt-1">
          <button
            onClick={handleGenerate}
            disabled={isGenerating}
            className="w-full py-3.5 rounded-full bg-[#1db954] text-black font-bold text-sm hover:bg-[#1ed760] active:scale-98 transition-all flex items-center justify-center gap-2 shadow-lg disabled:opacity-50"
            title={`Create ${coverType === 'image' ? 'Image' : 'Video'} Cover`}
          >
            <Icon name="auto_awesome" size="lg" />
            <span>{isGenerating ? "Synthesizing Cover..." : `Create ${coverType === 'image' ? 'Image' : 'Video'} Cover`}</span>
          </button>
          <button
            onClick={() => setCoverType(coverType === 'image' ? 'video' : 'image')}
            className="text-xs text-text-secondary hover:text-white py-1 transition-colors flex items-center justify-center gap-1"
            title={coverType === 'image' ? t('cover.switchToVideo') : t('cover.switchToImage')}
          >
            <Icon name="swap_horiz" size="md" />
            <span>{coverType === 'image' ? t('cover.switchToVideo') : t('cover.switchToImage')}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
