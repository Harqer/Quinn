import React, { useState } from 'react';
import { useQuinn } from '../../hooks/useQuinn';
import { Typography } from '../atoms/Typography';
import { Icon } from '../atoms/Icon';
import { Button } from '../atoms/Button';

export const QuinnChat: React.FC = () => {
  const { messages, sendText, mode, switchMode, isConnected } = useQuinn();
  const [inputText, setInputText] = useState('');

  const handleSend = () => {
    if (inputText.trim()) {
      sendText(inputText.trim());
      setInputText('');
    }
  };

  return (
    <div className="flex flex-col h-full w-full bg-background relative pb-20">
      {/* Header */}
      <div className="flex items-center justify-between p-4 bg-background/90 backdrop-blur-md sticky top-0 z-10 border-b border-surface-container">
        <Typography variant="title-lg" className="font-bold flex items-center gap-2">
          <Icon name="graphic_eq" color="primary" /> Quinn {mode === 'music' ? 'Music' : 'Podcast'}
        </Typography>
        <div className="flex bg-surface-container rounded-full p-1">
          <button 
            className={`px-3 py-1 rounded-full text-xs font-bold transition-colors ${mode === 'music' ? 'bg-primary text-black' : 'text-text-secondary hover:text-white'}`}
            onClick={() => switchMode('music')}
          >
            Music
          </button>
          <button 
             className={`px-3 py-1 rounded-full text-xs font-bold transition-colors ${mode === 'podcast' ? 'bg-primary text-black' : 'text-text-secondary hover:text-white'}`}
             onClick={() => switchMode('podcast')}
          >
            Podcast
          </button>
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-4">
        {messages.length === 0 ? (
          <div className="flex-1 flex flex-col items-center justify-center text-center opacity-50">
            <Icon name="graphic_eq" size="6xl" className="mb-4" />
            <Typography variant="headline">What do you want to hear?</Typography>
            <Typography variant="body-md">Prompt Quinn to generate music or talk in a podcast.</Typography>
          </div>
        ) : (
          messages.map((msg) => (
            <div key={msg.id} className={`flex ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
              <div 
                className={`max-w-[80%] p-3 rounded-2xl ${msg.sender === 'user' ? 'bg-primary text-black rounded-br-sm' : 'bg-surface-container text-white rounded-bl-sm'}`}
              >
                <Typography variant="body-md" color="inherit">
                  {msg.text}
                </Typography>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Input Area */}
      <div className="p-4 bg-background border-t border-surface-container">
        <div className="flex items-center gap-2 bg-surface rounded-full p-2 pl-4">
          <button className="text-text-secondary hover:text-white p-1">
            <Icon name="add_photo_alternate" />
          </button>
          <input
            type="text"
            placeholder={isConnected ? "Message Quinn..." : "Connecting..."}
            className="flex-1 bg-transparent border-none outline-none text-white text-sm"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') handleSend();
            }}
            disabled={!isConnected}
          />
          <button 
            className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-black active:scale-95 transition-transform disabled:opacity-50"
            onClick={handleSend}
            disabled={!isConnected || !inputText.trim()}
          >
             <Icon name="arrow_upward" />
          </button>
        </div>
      </div>
    </div>
  );
};
