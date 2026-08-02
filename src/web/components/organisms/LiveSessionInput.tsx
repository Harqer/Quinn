import React from 'react';
import { Icon } from '../atoms/Icon';

interface LiveSessionInputProps {
  input: string;
  setInput: (value: string) => void;
  isGenerating: boolean;
  showCamera: boolean;
  isRecording: boolean;
  onSend: () => void;
  onToggleCamera: () => void;
  onMicToggle: () => void;
}

export const LiveSessionInput: React.FC<LiveSessionInputProps> = ({
  input,
  setInput,
  isGenerating,
  showCamera,
  isRecording,
  onSend,
  onToggleCamera,
  onMicToggle
}) => {
  return (
    <div className="bg-surface-container border-t border-white/5 p-3 px-4 flex items-center gap-2 shrink-0">
      <button 
        onClick={onToggleCamera}
        className={`w-10 h-10 flex items-center justify-center transition-colors ${showCamera ? 'text-primary' : 'text-secondary hover:text-white'}`}
      >
        <Icon name="camera_alt" size="xl" />
      </button>
      <div className="flex-1 bg-surface rounded-full flex items-center px-4 py-2 border border-white/10 focus-within:border-primary transition-colors">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && onSend()}
          placeholder="Describe a vibe..."
          disabled={isGenerating}
          className="bg-transparent w-full outline-none text-white placeholder-white/40 disabled:opacity-50"
        />
      </div>
      {input.trim() ? (
        <button 
          onClick={onSend} 
          disabled={isGenerating}
          className="w-10 h-10 flex items-center justify-center bg-primary text-black rounded-full shadow-md hover:scale-105 transition-transform disabled:opacity-50 disabled:hover:scale-100"
        >
          <Icon name="send" size="md" />
        </button>
      ) : (
        <button 
          onClick={onMicToggle} 
          disabled={isGenerating && !isRecording}
          className={`w-10 h-10 flex items-center justify-center rounded-full shadow-md transition-all ${isRecording ? 'bg-error text-white animate-pulse' : 'bg-primary text-black hover:scale-105'} disabled:opacity-50 disabled:hover:scale-100`}
        >
          <Icon name={isRecording ? 'stop' : 'mic'} size="md" />
        </button>
      )}
    </div>
  );
};
