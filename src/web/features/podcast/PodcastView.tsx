import React, { useState, useEffect, useRef } from 'react';
import { getAuth } from "firebase/auth";
import { logger } from '@/web/lib/logger';

interface PodcastSegment {
  id: string;
  text: string;
  type: 'quinn' | 'user';
  trackUri?: string;
}

export const PodcastView: React.FC = () => {
  const [isLive, setIsLive] = useState(false);
  const [segments, setSegments] = useState<PodcastSegment[]>([]);
  const [inputText, setInputText] = useState("");
  const wsRef = useRef<WebSocket | null>(null);
  const audioCtxRef = useRef<AudioContext | null>(null);

  useEffect(() => {
    const connectWs = async () => {
      const user = getAuth().currentUser;
      if (!user) return;
      const token = await user.getIdToken();
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const ws = new WebSocket(`${protocol}//${window.location.host}/api/music/ws?token=${token}`);

      ws.onopen = () => {
        ws.send(JSON.stringify({ type: 'switch_mode', mode: 'podcast' }));
      };

      ws.onmessage = (event) => {
        const msg = JSON.parse(event.data);
        if (msg.type === 'podcast_update') {
          setSegments(prev => [...prev, {
            id: Date.now().toString(),
            text: msg.script,
            type: 'quinn',
            trackUri: msg.trackUri // Backend should provide this if saved
          }]);
        } else if (msg.type === 'podcast_chunk') {
          // Play audio chunk logic
        }
      };
      wsRef.current = ws;
    };

    connectWs();
    return () => wsRef.current?.close();
  }, []);

  const handleSend = () => {
    if (!inputText.trim()) return;
    setSegments(prev => [...prev, { id: Date.now().toString(), text: inputText, type: 'user' }]);
    wsRef.current?.send(JSON.stringify({ type: 'text_command', text: inputText }));
    setInputText("");
  };

  const saveToSpotify = async (segmentId: string, trackUri?: string) => {
    if (!trackUri) return;
    try {
      await fetch('/api/spotify/podcast/save', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ trackUri: trackUri || `spotify:track:quinn_${segmentId}` })
      });
      alert("Saved to your Musically Podcasts playlist!");
    } catch (err) {
      logger.error("Failed to save podcast to Spotify", err);
    }
  };

  return (
    <div className="flex flex-col h-full bg-surface text-on-surface relative">
      <div className="flex-1 p-8 overflow-y-auto pb-32">
        <div className="max-w-4xl mx-auto space-y-8">
          <header>
            <h2 className="text-4xl font-black italic">Musically Podcast</h2>
            <p className="text-on-surface-variant">Conversational narratives with Quinn</p>
          </header>

          <div className="space-y-6">
            {segments.length === 0 && (
              <div className="text-center py-20 text-on-surface-variant italic">
                Start a session and tell Quinn what's on your mind...
              </div>
            )}
            {segments.map(segment => (
              <div
                key={segment.id}
                className={`flex ${segment.type === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div className={`max-w-[80%] p-6 rounded-3xl shadow-sm border ${
                  segment.type === 'user'
                    ? 'bg-primary text-on-primary border-transparent'
                    : 'bg-surface-container border-outline/5'
                }`}>
                  <p className="leading-relaxed">{segment.text}</p>
                  {segment.type === 'quinn' && (
                    <div className="mt-4 flex gap-4 border-t border-outline/10 pt-4">
                      <button
                        onClick={() => saveToSpotify(segment.id, segment.trackUri)}
                        disabled={!segment.trackUri}
                        className="text-xs font-bold flex items-center gap-1 hover:text-primary transition-colors disabled:opacity-30"
                      >
                        <span className="material-icons-round text-sm">library_add</span>
                        Save to Spotify
                      </button>
                      <button className="text-xs font-bold flex items-center gap-1 hover:text-primary transition-colors">
                        <span className="material-icons-round text-sm">share</span>
                        Share
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom Chat Bar */}
      <div className="absolute bottom-0 left-0 right-0 p-6 bg-gradient-to-t from-surface via-surface to-transparent">
        <div className="max-w-4xl mx-auto flex gap-4 items-center bg-surface-container-high p-2 rounded-full shadow-2xl border border-outline/10">
          <button className="w-12 h-12 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-surface-container-highest transition-all">
            <span className="material-icons-round">mic</span>
          </button>
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Tell Quinn how to shape the story..."
            className="flex-1 bg-transparent border-none outline-none text-sm font-medium px-2"
          />
          <button
            onClick={handleSend}
            disabled={!inputText.trim()}
            className="w-12 h-12 bg-primary text-on-primary rounded-full flex items-center justify-center disabled:opacity-50 disabled:grayscale transition-all hover:brightness-110"
          >
            <span className="material-icons-round">send</span>
          </button>
        </div>
      </div>
    </div>
  );
};
