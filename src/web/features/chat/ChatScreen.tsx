import React, { useState, useRef, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';

interface Message {
  id: string;
  sender: 'user' | 'ai';
  text: string;
  tracks?: Array<{ title: string; artist: string; coverUrl?: string }>;
}

import { getAuth } from 'firebase/auth';

export const ChatScreen: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const endOfMessagesRef = useRef<HTMLDivElement>(null);
  const wsRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    let ws: WebSocket;
    
    const connectWs = async () => {
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const baseUrl = import.meta.env.VITE_WS_URL || `${protocol}//${window.location.host}`;
      const wsUrl = `${baseUrl.replace(/\/$/, '')}/api/music/ws`;
      
      const auth = getAuth();
      const user = auth.currentUser;
      let tokenParam = '';
      if (user) {
        try {
          const token = await user.getIdToken();
          tokenParam = `?token=${token}`;
        } catch(e) {}
      }
      
      ws = new WebSocket(`${wsUrl}${tokenParam}`);
      
      ws.onopen = () => {
        console.log("WebSocket connected to Mave Engine");
      };
      
      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'ai_response') {
            const aiResponse: Message = {
              id: Date.now().toString(),
              sender: 'ai',
              text: data.text || '',
              tracks: data.tracks
            };
            setMessages(prev => [...prev, aiResponse]);
          }
        } catch (err) {
          console.error("Error parsing WS message", err);
        }
      };

      ws.onclose = () => {
        console.log("WebSocket disconnected");
      };

      wsRef.current = ws;
    };

    connectWs();

    return () => {
      if (wsRef.current) {
        wsRef.current.close();
      }
    };
  }, []);

  useEffect(() => {
    endOfMessagesRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = () => {
    if (!inputValue.trim()) return;
    
    const text = inputValue.trim();
    const newUserMsg: Message = {
      id: Date.now().toString(),
      sender: 'user',
      text
    };
    
    setMessages(prev => [...prev, newUserMsg]);
    setInputValue('');
    
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({ type: 'user_message', text }));
    } else {
      console.error("WebSocket is not connected");
      // Could show an error toast here instead of injecting a fake message
    }
  };

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-hidden relative">
      {/* TopAppBar */}
      <header className="fixed top-0 w-full z-50 flex justify-between items-center px-4 h-[56px] backdrop-blur-xl bg-surface/80">
        <button className="text-primary hover:bg-surface-variant/50 p-2 rounded-full transition-colors">
          <Icon name="menu" />
        </button>
        <Typography variant="title-md" className="font-bold text-on-surface">Mave</Typography>
        <button className="text-primary hover:bg-surface-variant/50 p-2 rounded-full transition-colors">
          <Icon name="more_vert" />
        </button>
      </header>

      {/* Main Chat Canvas */}
      <main className="flex-1 overflow-y-auto chat-container pt-[64px] pb-[100px] px-4 flex flex-col space-y-4">
        {/* Welcome Message */}
        <div className="flex flex-col items-center justify-center py-8 text-center opacity-60">
          <Icon name="graphic_eq" className="text-primary text-[48px] mb-2" />
          <Typography variant="body-sm">Your personal audio curator. Ready to discover?</Typography>
        </div>

        {messages.map(msg => (
          msg.sender === 'ai' ? (
            <div key={msg.id} className="flex items-start gap-3">
              <div className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center shrink-0">
                <Icon name="auto_awesome" className="text-on-primary text-[18px]" />
              </div>
              <div className="message-bubble bg-surface-bright p-4 rounded-xl rounded-tl-none max-w-[85%]">
                <Typography variant="body-md" className="text-on-surface">{msg.text}</Typography>
                
                {msg.tracks && msg.tracks.length === 1 && (
                  <div className="mt-4 bg-surface-container-high rounded-xl overflow-hidden flex items-center p-3 border border-outline-variant/30 group cursor-pointer active:scale-95 transition-transform">
                    <div className="w-16 h-16 rounded-lg bg-surface-variant flex-shrink-0" />
                    <div className="ml-4 flex-1">
                      <Typography variant="body-md" className="font-bold text-on-surface">{msg.tracks[0].title}</Typography>
                      <Typography variant="body-sm" className="text-text-secondary">{msg.tracks[0].artist}</Typography>
                    </div>
                    <button className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-on-primary shadow-lg shadow-primary/20">
                      <Icon name="play_arrow" />
                    </button>
                  </div>
                )}

                {msg.tracks && msg.tracks.length > 1 && (
                  <div className="space-y-2 mt-4">
                    {msg.tracks.map((track, idx) => (
                      <div key={idx} className="flex items-center gap-3 p-2 hover:bg-surface-container-highest rounded-lg transition-colors">
                        <div className="w-10 h-10 bg-secondary-container rounded-lg flex items-center justify-center">
                          <Icon name="album" className="text-on-secondary-container" />
                        </div>
                        <div className="flex-1">
                          <Typography variant="body-sm" className="font-bold text-on-surface">{track.title}</Typography>
                          <Typography variant="label-sm" className="text-text-secondary">{track.artist}</Typography>
                        </div>
                        <Icon name="add_circle" className="text-text-secondary text-[20px]" />
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div key={msg.id} className="flex flex-col items-end w-full">
              <div className="message-bubble bg-primary-container p-4 rounded-xl rounded-tr-none text-on-primary-container shadow-lg shadow-primary-container/10 max-w-[85%]">
                <Typography variant="body-md">{msg.text}</Typography>
              </div>
              <span className="text-[10px] mt-1 text-text-secondary uppercase tracking-widest mr-1">Delivered</span>
            </div>
          )
        ))}
        <div ref={endOfMessagesRef} />
      </main>

      {/* Bottom Input Bar Section */}
      <div className="fixed bottom-0 left-0 w-full px-4 pb-8 pt-4 bg-gradient-to-t from-background via-background to-transparent z-50">
        <div className="max-w-4xl mx-auto flex items-end gap-2 bg-surface-container-high rounded-full p-2 shadow-2xl border border-outline-variant/20 backdrop-blur-xl">
          <button className="w-10 h-10 flex items-center justify-center text-primary hover:bg-surface-container-highest rounded-full transition-colors flex-shrink-0">
            <Icon name="add" />
          </button>
          
          <textarea 
            className="flex-1 bg-transparent border-none focus:ring-0 text-on-surface text-body-md py-2 resize-none placeholder:text-text-secondary max-h-32 min-h-[40px] outline-none" 
            placeholder="Ask Mave anything..." 
            rows={1}
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSend();
              }
            }}
          />
          
          {inputValue.trim() ? (
             <button onClick={handleSend} className="w-10 h-10 flex items-center justify-center bg-primary text-on-primary rounded-full transition-all shadow-lg shadow-primary/20 flex-shrink-0">
               <Icon name="send" />
             </button>
          ) : (
            <div className="flex items-center flex-shrink-0">
              <button className="w-10 h-10 flex items-center justify-center text-text-secondary hover:bg-surface-container-highest rounded-full transition-colors">
                <Icon name="mic" />
              </button>
              <button onClick={() => {
                const input = document.createElement('input');
                input.type = 'file';
                input.accept = 'image/*,video/*';
                input.onchange = async (e: any) => {
                  const file = e.target.files[0];
                  if (!file) return;
                  const reader = new FileReader();
                  reader.onload = (ev) => {
                    const base64 = ev.target?.result;
                    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
                      wsRef.current.send(JSON.stringify({ type: 'vision', image: base64 }));
                      setMessages(prev => [...prev, {
                        id: Date.now().toString(),
                        sender: 'user',
                        text: `[Sent media for analysis]`
                      }]);
                    }
                  };
                  reader.readAsDataURL(file);
                };
                input.click();
              }} className="w-10 h-10 flex items-center justify-center text-text-secondary hover:bg-surface-container-highest rounded-full transition-colors">
                <Icon name="photo_camera" />
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
