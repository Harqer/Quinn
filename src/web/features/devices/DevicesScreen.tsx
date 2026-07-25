import React, { useState } from 'react';
import { Icon } from '../../components/atoms/Icon';

export interface DevicesScreenProps {
  onBack?: () => void;
}

export const DevicesScreen: React.FC<DevicesScreenProps> = ({ onBack }) => {
  const [salonInvitesEnabled, setSalonInvitesEnabled] = useState(true);
  const [showTooltip, setShowTooltip] = useState(true);

  return (
    <div className="flex flex-col items-center min-h-full p-4 bg-[#121212] text-white">
      {/* Page Title for Desktop/Tablet */}
      <section className="hidden md:block w-full max-w-md mt-10 mb-8">
        <h1 className="text-6xl font-bold tracking-tight">Devices</h1>
      </section>

      {/* Main Container */}
      <main className="w-full max-w-md bg-[#121212] md:rounded-3xl overflow-hidden md:shadow-2xl md:border border-zinc-800 flex flex-col h-full md:h-[850px]">
        
        {/* Header */}
        <header className="p-4 flex items-center justify-between sticky top-0 bg-[#121212] z-10">
          <button aria-label="Close" className="p-2 hover:bg-white/10 rounded-full transition-colors" onClick={onBack}>
            <Icon name="close" />
          </button>
          <span className="font-bold text-sm">Your devices</span>
          <div className="w-8"></div>
        </header>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto px-4 pb-20 custom-scrollbar">
          
          {/* Current Device */}
          <section className="mt-4">
            <h2 className="text-xl font-bold mb-4">Current device</h2>
            <div className="bg-[#282828] rounded-lg p-4 flex items-center gap-4 border border-zinc-700/50">
              <div className="text-primary">
                <Icon name="smartphone" />
              </div>
              <div>
                <p className="font-bold">This phone</p>
                <div className="flex items-center gap-1 text-primary text-xs font-semibold">
                  <Icon name="speaker" className="text-[14px]" />
                  <span>Speakers</span>
                </div>
              </div>
            </div>
          </section>

          {/* Available Devices */}
          <section className="mt-8">
            <h2 className="text-sm font-bold mb-4">Select a device</h2>
            
            {/* Laptop */}
            <div className="flex items-center gap-4 py-3 cursor-pointer hover:bg-white/5 rounded-lg px-2 transition-colors">
              <div className="text-[#b3b3b3]">
                <Icon name="laptop_mac" />
              </div>
              <p className="font-medium text-text-primary">Alexandra's Laptop</p>
            </div>

            {/* Bureau */}
            <div className="flex items-center gap-4 py-3 cursor-pointer hover:bg-white/5 rounded-lg px-2 transition-colors">
              <div className="text-[#b3b3b3]">
                <Icon name="speaker_group" />
              </div>
              <div>
                <p className="font-medium text-text-primary">Bureau</p>
                <div className="flex items-center gap-1 text-[#b3b3b3] text-xs">
                  <Icon name="cast" className="text-[12px]" />
                  <span>Google Cast</span>
                </div>
              </div>
            </div>

            {/* Salon Active Example */}
            <div className="mt-4 bg-[#282828] rounded-xl p-5 relative border border-purple-500/30">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="text-[#b3b3b3]">
                    <Icon name="speaker_group" />
                  </div>
                  <div>
                    <p className="font-medium text-white">Salon</p>
                    <div className="flex items-center gap-1 text-primary text-xs font-semibold">
                      <Icon name="cast" className="text-[12px]" />
                      <span>Connecting...</span>
                    </div>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full border border-zinc-600 bg-surface-variant flex items-center justify-center">
                    <Icon name="person" className="text-[16px] text-text-secondary" />
                  </div>
                  <Icon name="chevron_right" className="text-[#b3b3b3]" />
                </div>
              </div>
              
              <div className="mt-6 flex items-center justify-between">
                <p className="text-sm text-[#b3b3b3] max-w-[200px]">Multiple people can join and control this speaker</p>
                
                <label className="relative inline-flex items-center cursor-pointer">
                  <input 
                    type="checkbox" 
                    className="sr-only peer" 
                    checked={salonInvitesEnabled}
                    onChange={(e) => setSalonInvitesEnabled(e.target.checked)}
                  />
                  <div className="w-11 h-6 bg-zinc-600 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
                </label>
              </div>
              
              <button className="mt-4 bg-primary text-black font-bold py-2 px-6 rounded-full text-sm hover:scale-105 transition-transform">
                Invite
              </button>

              {/* Blue Tooltip */}
              {showTooltip && (
                <div className="mt-4 relative bg-[#2e77ed] text-white p-3 rounded-lg text-xs flex items-center justify-between">
                  <div className="absolute top-[-10px] left-[20px] w-0 h-0 border-l-[10px] border-l-transparent border-r-[10px] border-r-transparent border-b-[10px] border-b-[#2e77ed]"></div>
                  <p>Invite nearby friends to queue songs and control what's playing on this speaker</p>
                  <button className="ml-2 hover:bg-white/20 p-1 rounded-full" onClick={() => setShowTooltip(false)}>
                    <Icon name="close" className="text-[16px]" />
                  </button>
                </div>
              )}
            </div>

            {/* Commode */}
            <div className="flex items-center gap-4 py-3 mt-2 cursor-pointer hover:bg-white/5 rounded-lg px-2 transition-colors">
              <div className="text-[#b3b3b3]">
                <Icon name="speaker_group" />
              </div>
              <div>
                <p className="font-medium text-text-primary">Commode</p>
                <div className="flex items-center gap-1 text-[#b3b3b3] text-xs">
                  <Icon name="cast" className="text-[12px]" />
                  <span>Google Cast</span>
                </div>
              </div>
            </div>
            
            {/* Meta Wearables Device */}
            <div className="flex items-center gap-4 py-3 mt-2 cursor-pointer hover:bg-white/5 rounded-lg px-2 transition-colors">
              <div className="text-[#b3b3b3]">
                <Icon name="glasses" />
              </div>
              <div>
                <p className="font-medium text-text-primary">Ray-Ban Meta</p>
                <div className="flex items-center gap-1 text-[#b3b3b3] text-xs">
                  <Icon name="bluetooth" className="text-[12px]" />
                  <span>Bluetooth</span>
                </div>
              </div>
            </div>
          </section>

        </div>
      </main>
    </div>
  );
};
