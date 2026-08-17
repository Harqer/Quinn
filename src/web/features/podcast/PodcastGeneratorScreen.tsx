import React, { useState, useEffect, useRef } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { narrativeService } from '../../services/narrativeService';
import { useAppContext } from '../../contexts/AppContext';
import { logger } from "../../lib/logger";
import { PodcastInputBar } from '../../components/organisms/PodcastInputBar';
import { PodcastResultCard } from '../../components/organisms/PodcastResultCard';
import { PodcastThinkingState } from '../../components/molecules/PodcastThinkingState';

export const PodcastGeneratorScreen: React.FC = () => {
  const [prompt, setPrompt] = useState('');
  const [selectedVoice] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [podcastResult, setPodcastResult] = useState<any | null>(null);
  const [thinkingText, setThinkingText] = useState('');
  const { setCurrentTrack, setIsPlaying } = useAppContext();

  const [isRecording, setIsRecording] = useState(false);
  const recognitionRef = useRef<any>(null);

  useEffect(() => {
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
    if (SpeechRecognition) {
      const recognition = new SpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = true;
      
      recognition.onresult = (event: any) => {
        const transcript = Array.from(event.results)
          .map((result: any) => result[0].transcript)
          .join('');
        setPrompt(transcript);
      };
      
      recognition.onend = () => {
        setIsRecording(false);
      };
      
      recognitionRef.current = recognition;
    }
  }, []);

  const toggleRecording = () => {
    if (isRecording) {
      recognitionRef.current?.stop();
      setIsRecording(false);
    } else {
      if (recognitionRef.current) {
        setPrompt('');
        recognitionRef.current.start();
        setIsRecording(true);
      } else {
        window.dispatchEvent(new CustomEvent('show-toast', { detail: 'Speech recognition not supported in this browser' }));
      }
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
      let streamBuffer = '';
      
      const { AudioStreamPlayer } = await import('../../utils/AudioStreamPlayer');
      const audioPlayer = new AudioStreamPlayer();
      await audioPlayer.init();

      while (!done && reader) {
        const { value, done: doneReading } = await reader.read();
        done = doneReading;
        if (value) {
          const chunkStr = decoder.decode(value, { stream: true });
          streamBuffer += chunkStr;
          
          let eolIndex;
          while ((eolIndex = streamBuffer.indexOf('\n\n')) >= 0) {
            const line = streamBuffer.slice(0, eolIndex).trim();
            streamBuffer = streamBuffer.slice(eolIndex + 2);
            
            if (line.startsWith('data: ')) {
              try {
                const data = JSON.parse(line.substring(6));
                if (data.type === 'chunk' || data.type === 'thought') {
                  scriptBuffer += data.text;
                  setThinkingText(scriptBuffer);
                } else if (data.type === 'audio_chunk') {
                  audioPlayer.queueAudioChunk(data.data);
                } else if (data.type === 'complete') {
                  setPodcastResult(data.track);
                  setCurrentTrack(data.track);
                  setIsPlaying(true);
                  setThinkingText('');
                } else if (data.type === 'error') {
                  setError(data.error);
                  setThinkingText('');
                  audioPlayer.stop();
                }
              } catch (e) {
                logger.error("Failed to parse SSE data", e);
              }
            }
          }
        }
      }
      
      if (!done && scriptBuffer.length > 0 && !podcastResult) {
        setError('Stream interrupted. Please try generating again.');
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
        </div>
      </div>

      <PodcastInputBar 
        prompt={prompt} 
        setPrompt={setPrompt} 
        error={error} 
        setError={setError} 
        loading={loading} 
        handleGenerate={handleGenerate} 
        toggleRecording={toggleRecording} 
        isRecording={isRecording} 
      />

      {loading && thinkingText && (
        <PodcastThinkingState thinkingText={thinkingText} />
      )}

      {podcastResult && (
        <PodcastResultCard 
          podcastResult={podcastResult} 
          prompt={prompt} 
          handleAction={(action, id) => console.log('Action:', action, id)}
          setCurrentTrack={setCurrentTrack}
          setIsPlaying={setIsPlaying}
        />
      )}
    </div>
  );
};
