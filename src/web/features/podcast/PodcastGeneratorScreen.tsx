import React, { useState, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { narrativeService } from '../../services/narrativeService';
import { useAppContext } from '../../contexts/AppContext';
import { getAuth } from 'firebase/auth';
import { logger } from "../../lib/logger";

export const PodcastGeneratorScreen: React.FC = () => {
  const [prompt, setPrompt] = useState('');
  const [selectedVoice, setSelectedVoice] = useState('AOEDE');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [podcastResult, setPodcastResult] = useState<any | null>(null);
  const [thinkingText, setThinkingText] = useState('');
  const [voices, setVoices] = useState<any[]>([]);
  const { setCurrentTrack, setIsPlaying } = useAppContext();

  useEffect(() => {
    const fetchVoices = async () => {
      const fetchedVoices = await narrativeService.getVoices();
      if (fetchedVoices.length > 0) {
        setVoices(fetchedVoices);
      }
    };
    fetchVoices();
  }, []);

  const handleAction = async (action: 'like' | 'share', targetId?: string) => {
    if (!targetId) return;
    if ('vibrate' in navigator) navigator.vibrate([50, 50, 50]);
    try {
      if (action === 'like') {
        const auth = getAuth();
        const user = auth.currentUser;
        const token = user ? await user.getIdToken() : '';
        const baseUrl = import.meta.env.VITE_API_URL || '';
        await fetch(`${baseUrl}/api/spotify/podcast/save`, {
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {})
          },
          body: JSON.stringify({ trackId: targetId })
        });
        window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Podcast saved to library!' }));
      } else if (action === 'share') {
        window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Share link copied!' }));
      }
    } catch (err) {
      window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Failed to complete action' }));
    }
  };

  const handleGenerate = async () => {
    if (!prompt.trim()) {
      setError('Please enter a topic or concept for your podcast.');
      return;
    }
    setError(null);
    setPodcastResult(null);
    setThinkingText('');
    setLoading(true);

    try {
      const response = await narrativeService.generatePodcast(prompt, selectedVoice);

      const reader = response.body?.getReader();
      const decoder = new TextDecoder();
      let done = false;
      let scriptBuffer = '';

      while (!done && reader) {
        const { value, done: doneReading } = await reader.read();
        done = doneReading;
        if (value) {
          const chunkStr = decoder.decode(value, { stream: true });
          const lines = chunkStr.split('\n\n');
          for (const line of lines) {
            if (line.startsWith('data: ')) {
              try {
                const data = JSON.parse(line.replace('data: ', ''));
                if (data.type === 'chunk') {
                  scriptBuffer += data.text;
                  setThinkingText(scriptBuffer);
                } else if (data.type === 'complete') {
                  setPodcastResult(data.track);
                  setThinkingText('');
                } else if (data.type === 'error') {
                  setError(data.error);
                  setThinkingText('');
                }
              } catch (e) {
                logger.error("Failed to parse SSE data", e);
              }
            }
          }
        }
      }
      
      // Fallback or disconnect recovery if streaming drops midway
      if (!done && scriptBuffer.length > 0 && !podcastResult) {
        setPodcastResult({
          title: prompt,
          voice: selectedVoice,
          script: scriptBuffer,
          duration: "Stream Interrupted",
          coverUrl: ""
        });
        setThinkingText('');
      }
    } catch (err: any) {
      logger.error('[PODCAST] Generation error:', err);
      setError('Unable to load podcast right now. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto p-4 pb-32 gap-6">
      <div className="flex items-center gap-3 pt-8 border-b border-surface-container pb-4 sticky top-0 bg-background/90 backdrop-blur-md z-10">
        <Icon name="podcasts" size="xl" color="primary" />
        <div>
          <Typography variant="headline" className="font-bold">Podcast Studio</Typography>
          <Typography variant="label-sm" color="secondary">Powered by Google Cloud</Typography>
        </div>
      </div>

      {/* Voice Selector */}
      <div className="flex flex-col gap-2">
        <Typography variant="title-md" className="font-bold">Select Voice Profile</Typography>
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-2">
          {voices.map((v) => (
            <button
              key={v.id}
              onClick={() => setSelectedVoice(v.id)}
              className={`p-3 rounded-xl border text-left transition-all ${
                selectedVoice === v.id
                  ? 'border-primary bg-primary/10 text-white shadow-lg'
                  : 'border-surface-container bg-surface hover:border-outline/50 text-text-secondary'
              }`}
            >
              <div className="text-xs font-bold text-white">{v.name}</div>
              <div className="text-[10px] text-text-secondary">{v.desc}</div>
            </button>
          ))}
          {voices.length === 0 && (
             <div className="text-sm text-text-secondary italic">Loading voice presets...</div>
          )}
        </div>
      </div>

      {/* Prompt Input */}
      <div className="flex flex-col gap-2">
        <Typography variant="title-md" className="font-bold">Podcast Topic / Concept</Typography>
        <textarea
          value={prompt}
          onChange={(e) => {
            setPrompt(e.target.value);
            if (error) setError(null);
          }}
          placeholder="e.g. The evolution of ambient synth music in 1980s Tokyo nightlife..."
          rows={4}
          className="w-full bg-surface border border-outline/30 rounded-xl p-3 text-sm text-white placeholder-text-secondary outline-none focus:border-primary transition-colors resize-none"
        />
        {error && <span className="text-xs text-red-400 font-medium">{error}</span>}
      </div>

      {/* Action Button */}
      <button
        onClick={handleGenerate}
        disabled={loading || !prompt.trim()}
        className="w-full py-3.5 bg-primary text-black rounded-full font-bold text-sm flex items-center justify-center gap-2 hover:brightness-110 active:scale-95 disabled:opacity-50 transition-all shadow-lg"
        title="Create Podcast"
      >
        {loading ? (
          <Icon name="hourglass_empty" />
        ) : (
          <Icon name="auto_awesome" />
        )}
      </button>

      {/* Streaming Thoughts */}
      {loading && thinkingText && (
        <div className="p-4 bg-surface-container rounded-xl border border-primary/20 flex flex-col gap-2">
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded-full border-2 border-primary border-t-transparent animate-spin" />
            <Typography variant="label-sm" color="primary" className="font-bold">Analyzing & Generating...</Typography>
          </div>
          <Typography variant="body-sm" color="secondary" className="italic whitespace-pre-wrap">
            {thinkingText}
          </Typography>
        </div>
      )}

      {/* Output Podcast Card */}
      {podcastResult && (
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
                        const url = `${window.location.origin}/podcast`;
                        if (navigator.share) {
                          navigator.share({
                            title: 'Mave Podcast',
                            text: 'Listen to this podcast generated by Mave!',
                            url: url
                          }).catch(console.error);
                        } else {
                          navigator.clipboard.writeText(url);
                          window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Share link copied to clipboard!' }));
                        }
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
      )}
    </div>
  );
};
