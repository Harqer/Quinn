import React, { useRef, useEffect } from 'react';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';

interface ChatInputBarProps {
  inputValue: string;
  setInputValue: (value: string) => void;
  promptMode?: 'chat' | 'cover' | 'video';
  setPromptMode?: (mode: 'chat' | 'cover' | 'video') => void;
  isAddMenuOpen?: boolean;
  setIsAddMenuOpen?: (open: boolean) => void;
  handleSend: () => void;
  handleFileUpload?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  toggleRecording?: () => void;
  isRecording?: boolean;
}

export const ChatInputBar: React.FC<ChatInputBarProps> = ({
  inputValue, setInputValue, promptMode = 'chat', setPromptMode = () => {},
  isAddMenuOpen: externalIsAddMenuOpen, setIsAddMenuOpen: externalSetIsAddMenuOpen,
  handleSend, handleFileUpload = () => {}, toggleRecording = () => {}, isRecording = false
}) => {
  const [internalIsAddMenuOpen, setInternalIsAddMenuOpen] = React.useState(false);
  const isAddMenuOpen = externalIsAddMenuOpen ?? internalIsAddMenuOpen;
  const setIsAddMenuOpen = externalSetIsAddMenuOpen ?? setInternalIsAddMenuOpen;
  const fileInputRef = useRef<HTMLInputElement>(null);
  const cameraInputRef = useRef<HTMLInputElement>(null);
  const addMenuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (addMenuRef.current && !addMenuRef.current.contains(event.target as Node)) {
        setIsAddMenuOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [setIsAddMenuOpen]);

  return (
    <div className="absolute bottom-0 left-0 w-full px-4 pb-8 pt-4 bg-gradient-to-t from-background via-background to-transparent z-50">
      <div className="max-w-4xl mx-auto flex items-end gap-2 bg-surface-container-high rounded-full p-2 shadow-2xl border border-outline-variant/20 backdrop-blur-xl">
        <input type="file" accept="image/*,video/*" ref={fileInputRef} onChange={handleFileUpload} className="hidden" />
        <input type="file" accept="image/*,video/*" capture="environment" ref={cameraInputRef} onChange={handleFileUpload} className="hidden" />
        
        <div className="relative flex-shrink-0 flex items-center gap-1" ref={addMenuRef}>
          <button onClick={() => setIsAddMenuOpen(!isAddMenuOpen)} className={`w-10 h-10 flex items-center justify-center rounded-full transition-colors ${isAddMenuOpen ? 'bg-surface-container-highest text-primary' : 'text-primary hover:bg-surface-container-highest'}`}>
            <Icon name="add" />
          </button>
          <button onClick={() => cameraInputRef.current?.click()} className="w-10 h-10 flex items-center justify-center rounded-full transition-colors text-primary hover:bg-surface-container-highest" title="Take photo or video">
            <Icon name="photo_camera" />
          </button>
          {isAddMenuOpen && (
            <div className="absolute bottom-full left-0 mb-4 w-56 bg-surface-container-high rounded-2xl shadow-[0_8px_30px_rgb(0,0,0,0.12)] border border-outline-variant/30 py-2 overflow-hidden z-50 flex flex-col animate-in fade-in zoom-in-95 duration-200">
              <button onClick={() => { fileInputRef.current?.click(); setIsAddMenuOpen(false); }} className="flex items-center gap-4 px-4 py-3 hover:bg-surface-container-highest transition-colors text-left text-on-surface">
                <Icon name="upload_file" className="text-text-secondary" />
                <Typography variant="body-md">Upload files</Typography>
              </button>
              <div className="h-[1px] bg-outline-variant/20 my-1 mx-4" />
              <button onClick={() => { setPromptMode('cover'); setIsAddMenuOpen(false); }} className="flex items-center gap-4 px-4 py-3 hover:bg-surface-container-highest transition-colors text-left text-on-surface">
                <Icon name="image" className="text-text-secondary" />
                <Typography variant="body-md">Create Image</Typography>
              </button>
              <button onClick={() => { setPromptMode('video'); setIsAddMenuOpen(false); }} className="flex items-center gap-4 px-4 py-3 hover:bg-surface-container-highest transition-colors text-left text-on-surface">
                <Icon name="movie" className="text-text-secondary" />
                <Typography variant="body-md">Create video</Typography>
              </button>
            </div>
          )}
        </div>
        
        <textarea 
          className="flex-1 bg-transparent border-none focus:ring-0 text-on-surface text-body-md py-2 resize-none placeholder:text-text-secondary max-h-32 min-h-[40px] outline-none" 
          placeholder={promptMode === 'cover' ? "Describe the cover art..." : promptMode === 'video' ? "Describe the music video scene..." : "Ask Mave anything..."} 
          rows={1} value={inputValue} onChange={(e) => setInputValue(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); handleSend(); } }}
        />
        
        {inputValue.trim() ? (
           <button onClick={handleSend} className="w-10 h-10 flex items-center justify-center bg-primary text-on-primary rounded-full transition-all shadow-lg shadow-primary/20 flex-shrink-0">
             <Icon name="send" />
           </button>
        ) : (
          <div className="flex items-center flex-shrink-0">
            <button onClick={toggleRecording} className={`w-10 h-10 flex items-center justify-center rounded-full transition-colors ${isRecording ? 'bg-red-500 text-white' : 'text-text-secondary hover:bg-surface-container-highest'}`}>
              <Icon name="mic" />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
