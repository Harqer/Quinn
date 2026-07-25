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
            
            <div className="mt-8 text-center text-[#b3b3b3]">
              <p className="text-sm">No other devices found on your network.</p>
            </div>
          </section>

        </div>
      </main>
    </div>
  );
};
