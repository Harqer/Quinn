import React, { useRef } from 'react';
import { useTranslation } from 'react-i18next';

interface DashboardBottomBarProps {
  mode: string;
  isRecording: boolean;
  inputText: string;
  setInputText: (text: string) => void;
  handleSend: () => void;
  toggleRecording: () => void;
  handleCameraSnapshot: () => void;
  handleFileUpload: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export const DashboardBottomBar: React.FC<DashboardBottomBarProps> = ({
  mode,
  isRecording,
  inputText,
  setInputText,
  handleSend,
  toggleRecording,
  handleCameraSnapshot,
  handleFileUpload
}) => {
  const { t } = useTranslation();
  const fileInputRef = useRef<HTMLInputElement>(null);

  return (
    <div className="p-8 bg-[#121212] border-t border-white/5 relative z-10">
      <div className="max-w-5xl mx-auto flex gap-6 items-center bg-[#282828] p-3 rounded-full shadow-2xl border border-white/10 hover:border-white/20 transition-all">
        <div className="flex gap-2 border-r border-white/10 pr-4 pl-2">
          <button
            onClick={() => fileInputRef.current?.click()}
            className="w-10 h-10 rounded-full flex items-center justify-center text-gray-400 hover:text-white hover:bg-white/5 transition-all"
          >
            <span className="material-icons-round text-xl">add</span>
          </button>
          <input 
            type="file" 
            ref={fileInputRef}
            className="hidden" 
            accept="image/*,video/*" 
            onChange={handleFileUpload} 
          />
          <button
            onClick={handleCameraSnapshot}
            className="w-10 h-10 rounded-full flex items-center justify-center text-gray-400 hover:text-white hover:bg-white/5 transition-all"
          >
            <span className="material-icons-round text-xl">photo_camera</span>
          </button>
        </div>
        
        <button
          onClick={toggleRecording}
          className={`w-12 h-12 rounded-full flex items-center justify-center transition-all ${
            isRecording ? 'bg-red-600 text-white scale-110 shadow-lg' : 'text-gray-400 hover:text-white hover:bg-white/5'
          }`}
        >
          <span className="material-icons-round text-2xl">mic</span>
        </button>
        
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSend()}
          placeholder={mode === 'music' ? (t('dashboard.instructMusic') as string) : (t('dashboard.instructNarrative') as string)}
          className="flex-1 bg-transparent border-none outline-none text-base font-bold placeholder:text-gray-600 text-white px-2"
        />
        <button
          onClick={handleSend}
          disabled={!inputText.trim()}
          className="w-12 h-12 bg-white text-black rounded-full flex items-center justify-center disabled:opacity-30 disabled:grayscale transition-all hover:scale-105 active:scale-95 shadow-xl"
        >
          <span className="material-icons-round text-2xl">arrow_upward</span>
        </button>
      </div>
    </div>
  );
};
