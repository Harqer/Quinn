import React, { useState } from 'react';

export const CompanionApp: React.FC = () => {
  const [activeTab, setActiveTab] = useState('home');

  return (
    <div className="h-full flex items-center justify-center bg-gray-900 p-8">
      {/* Phone Frame */}
      <div className="w-[320px] h-[640px] bg-black rounded-[3rem] border-8 border-gray-800 shadow-2xl overflow-hidden relative flex flex-col">
        {/* Status Bar */}
        <div className="h-12 flex items-center justify-between px-8 text-white/90">
          <span className="text-xs font-bold font-mono">04:04</span>
          <div className="flex gap-2">
            <span className="material-icons-round text-xs">signal_cellular_4_bar</span>
            <span className="material-icons-round text-xs">wifi</span>
            <span className="material-icons-round text-xs">battery_full</span>
          </div>
        </div>

        {/* Dynamic Content */}
        <div className="flex-1 overflow-y-auto px-6 py-4 custom-scrollbar">
          {activeTab === 'home' && <HomeView />}
          {activeTab === 'search' && <SearchView />}
          {activeTab === 'library' && <LibraryView />}
        </div>

        {/* Bottom Navigation */}
        <nav className="h-16 bg-gray-900/80 backdrop-blur-xl border-t border-white/5 flex items-center justify-around px-2">
          <NavButton active={activeTab === 'home'} icon="home" label="Home" onClick={() => setActiveTab('home')} />
          <NavButton active={activeTab === 'search'} icon="search" label="Search" onClick={() => setActiveTab('search')} />
          <NavButton active={activeTab === 'library'} icon="library_music" label="Library" onClick={() => setActiveTab('library')} />
        </nav>

        {/* Home Indicator */}
        <div className="h-1 w-24 bg-white/20 rounded-full mx-auto mb-2" />
      </div>
    </div>
  );
};

const NavButton: React.FC<{ active: boolean; icon: string; label: string; onClick: () => void }> = ({ active, icon, label, onClick }) => (
  <button onClick={onClick} className="flex flex-col items-center gap-1 group">
    <div className={`px-5 py-1 rounded-full transition-all ${active ? 'bg-primary/20 text-primary' : 'text-gray-500 group-hover:text-white'}`}>
      <span className="material-icons-round text-xl">{icon}</span>
    </div>
    <span className={`text-[10px] font-bold ${active ? 'text-primary' : 'text-gray-500'}`}>{label}</span>
  </button>
);

const HomeView: React.FC = () => (
  <div className="space-y-8 animate-in fade-in slide-in-from-bottom-2">
    <header className="space-y-1">
      <h2 className="text-2xl font-black text-white italic">Good Morning</h2>
      <p className="text-gray-500 text-xs font-bold uppercase tracking-widest">Musically Live Stage</p>
    </header>

    <div className="space-y-4">
      <h3 className="text-sm font-bold text-gray-300">Recently Played</h3>
      <div className="grid grid-cols-2 gap-4">
        {[1, 2, 3, 4].map(i => (
          <div key={i} className="aspect-square rounded-2xl bg-white/5 border border-white/5 overflow-hidden p-3 flex flex-col justify-end">
            <div className="w-8 h-8 rounded-lg bg-primary/20 mb-2 flex items-center justify-center">
              <span className="material-icons-round text-primary text-sm">music_note</span>
            </div>
            <p className="text-[10px] font-black text-white truncate">POV Vibe {i}</p>
            <p className="text-[8px] text-gray-500 font-bold">Studio Session</p>
          </div>
        ))}
      </div>
    </div>
  </div>
);

const SearchView: React.FC = () => (
  <div className="space-y-6 animate-in fade-in slide-in-from-bottom-2">
    <h2 className="text-2xl font-black text-white italic">Search</h2>
    <div className="relative">
      <input type="text" placeholder="Artists, songs, or vibes" className="w-full bg-white/10 rounded-xl py-3 pl-10 pr-4 text-xs font-bold text-white border border-white/5 focus:border-primary/50 outline-none transition-all" />
      <span className="material-icons-round absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm">search</span>
    </div>

    <div className="grid grid-cols-2 gap-3">
      {['Lofi', 'Synthwave', 'Ambient', 'Techno', 'Indie', 'Jazz'].map(genre => (
        <div key={genre} className="h-20 rounded-xl bg-gradient-to-br from-primary/20 to-secondary/20 p-3 relative overflow-hidden group cursor-pointer">
          <span className="font-black text-sm text-white italic italic">{genre}</span>
          <span className="material-icons-round absolute -right-2 -bottom-2 text-4xl text-white/5 transform -rotate-12 group-hover:scale-125 transition-transform">music_note</span>
        </div>
      ))}
    </div>
  </div>
);

const LibraryView: React.FC = () => (
  <div className="space-y-6 animate-in fade-in slide-in-from-bottom-2">
    <h2 className="text-2xl font-black text-white italic">Library</h2>
    <div className="space-y-2">
      {[1, 2, 3].map(i => (
        <div key={i} className="flex items-center gap-4 p-2 rounded-xl hover:bg-white/5 transition-all cursor-pointer group">
          <div className="w-12 h-12 rounded-lg bg-gray-800 flex items-center justify-center">
            <span className="material-icons-round text-gray-600">playlist_play</span>
          </div>
          <div className="flex-1">
            <p className="text-xs font-bold text-white">Your Stage Playlist {i}</p>
            <p className="text-[10px] text-gray-500">42 tracks • Creator</p>
          </div>
          <span className="material-icons-round text-gray-700 opacity-0 group-hover:opacity-100 transition-opacity">chevron_right</span>
        </div>
      ))}
    </div>
  </div>
);
