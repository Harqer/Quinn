import React from 'react';
import { Icon } from '../atoms/Icon';
import { ErrorAlert } from '../molecules/ErrorAlert';

interface PodcastInputBarProps {
  prompt: string;
  setPrompt: (value: string) => void;
  error: string | null;
  setError: (value: string | null) => void;
  loading: boolean;
  isRecording: boolean;
  toggleRecording: () => void;
  handleGenerate: () => void;
}

export const PodcastInputBar: React.FC<PodcastInputBarProps> = ({
  prompt,
  setPrompt,
  error,
  setError,
  loading,
  isRecording,
  toggleRecording,
  handleGenerate
}) => {
  return (
    <div className="absolute bottom-0 left-0 w-full px-4 pb-8 pt-4 bg-gradient-to-t from-background via-background to-transparent z-50">
      {error && <div className="max-w-4xl mx-auto mb-2"><ErrorAlert message={error} /></div>}
      <div className="max-w-4xl mx-auto flex items-end gap-2 bg-surface-container-high rounded-full p-2 shadow-2xl border border-outline-variant/20 backdrop-blur-xl">

        <textarea 
          className="flex-1 bg-transparent border-none focus:ring-0 text-on-surface text-body-md py-2 resize-none placeholder:text-text-secondary max-h-32 min-h-[40px] outline-none" 
          placeholder="What should the podcast be about?" 
          rows={1}
          value={prompt}
          onChange={(e) => {
            setPrompt(e.target.value);
            if (error) setError(null);
          }}
          onKeyDown={(e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
              e.preventDefault();
              handleGenerate();
            }
          }}
        />
        
        {prompt.trim() ? (
           <button onClick={handleGenerate} disabled={loading} className="w-10 h-10 flex items-center justify-center bg-primary text-on-primary rounded-full transition-all shadow-lg shadow-primary/20 flex-shrink-0 disabled:opacity-50">
             <Icon name={loading ? "hourglass_empty" : "send"} />
           </button>
        ) : (
          <div className="flex items-center flex-shrink-0">
            <button 
              onClick={toggleRecording} 
              disabled={loading}
              className={`w-10 h-10 flex items-center justify-center rounded-full transition-colors disabled:opacity-50 ${isRecording ? 'bg-red-500 text-white animate-pulse' : 'text-text-secondary hover:bg-surface-container-highest'}`}
            >
              <Icon name="mic" />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
