import React, { useRef, useState, useCallback } from 'react';

interface GesturePadProps {
  onWarp?: (params: { bpm?: number; density?: number }) => void;
}

/**
 * Production-Grade 2D Gesture Pad.
 * Maps touch/mouse coordinates to Mave Realtime BPM and Density.
 * Includes synthetic haptic detents for tactile feedback.
 */
export const GesturePad: React.FC<GesturePadProps> = ({ onWarp }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [isActive, setIsActive] = useState(false);
  const [coords, setCoords] = useState({ x: 0.5, y: 0.5 });
  const lastEmit = useRef<number>(0);

  const handleInteraction = useCallback((clientX: number, clientY: number) => {
    if (!containerRef.current) return;

    const rect = containerRef.current.getBoundingClientRect();
    const x = Math.max(0, Math.min(1, (clientX - rect.left) / rect.width));
    const y = Math.max(0, Math.min(1, 1 - (clientY - rect.top) / rect.height));

    setCoords({ x, y });

    // Performance: Throttle emission to backend to 10fps
    const now = Date.now();
    if (now - lastEmit.current > 100) {
      const bpm = Math.round(60 + y * 140);
      const density = parseFloat(x.toFixed(2));

      onWarp?.({ bpm, density });

      // Haptic Detent: 15ms pulse every 10% movement
      if ('vibrate' in navigator && (Math.abs(x - coords.x) > 0.1 || Math.abs(y - coords.y) > 0.1)) {
        navigator.vibrate(10);
      }

      lastEmit.current = now;
    }
  }, [coords, onWarp]);

  return (
    <div
      ref={containerRef}
      className="w-full h-full relative bg-[#121212]/50 cursor-none touch-none overflow-hidden"
      onPointerDown={(e) => {
        setIsActive(true);
        (e.target as HTMLElement).setPointerCapture(e.pointerId);
        handleInteraction(e.clientX, e.clientY);
      }}
      onPointerMove={(e) => isActive && handleInteraction(e.clientX, e.clientY)}
      onPointerUp={() => setIsActive(false)}
    >
      {/* Grid Underlay */}
      <div className="absolute inset-0 grid grid-cols-4 grid-rows-4 opacity-10 pointer-events-none">
        {Array.from({ length: 16 }).map((_, i) => (
          <div key={i} className="border border-white/20" />
        ))}
      </div>

      {/* Crosshair / Reticle */}
      <div
        className="absolute w-8 h-8 -ml-4 -mt-4 transition-transform duration-75 pointer-events-none"
        style={{
          left: `${coords.x * 100}%`,
          top: `${(1 - coords.y) * 100}%`,
          transform: `scale(${isActive ? 1.5 : 1})`
        }}
      >
        <div className="absolute inset-0 border-2 border-[#1DB954] rounded-full animate-pulse" />
        <div className="absolute top-1/2 left-0 right-0 h-0.5 bg-[#1DB954]/40" />
        <div className="absolute left-1/2 top-0 bottom-0 w-0.5 bg-[#1DB954]/40" />
      </div>

      {/* Legend */}
      <div className="absolute bottom-2 left-2 text-[8px] font-black text-white/20 uppercase tracking-widest">
        Density Shift
      </div>
      <div className="absolute top-2 left-2 text-[8px] font-black text-white/20 uppercase tracking-widest rotate-90 origin-top-left ml-4">
        Tempo Warp
      </div>

      {/* Active Values */}
      {isActive && (
        <div className="absolute top-2 right-2 bg-black/80 px-2 py-1 rounded border border-[#1DB954]/30">
          <span className="text-[10px] font-mono text-[#1DB954]">
            {Math.round(60 + coords.y * 140)}BPM • {Math.round(coords.x * 100)}%
          </span>
        </div>
      )}
    </div>
  );
};
