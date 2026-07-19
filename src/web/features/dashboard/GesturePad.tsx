import React, { useState, useRef } from 'react';

export const GesturePad: React.FC = () => {
  const [coords, setCoords] = useState({ x: 0.5, y: 0.5 });
  const [active, setActive] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const handleMove = (e: React.MouseEvent | React.TouchEvent) => {
    if (!active || !containerRef.current) return;

    const rect = containerRef.current.getBoundingClientRect();
    const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX;
    const clientY = 'touches' in e ? e.touches[0].clientY : e.clientY;

    const x = Math.max(0, Math.min(1, (clientX - rect.left) / rect.width));
    const y = Math.max(0, Math.min(1, (clientY - rect.top) / rect.height));

    setCoords({ x, y });
  };

  return (
    <div
      ref={containerRef}
      onMouseDown={() => setActive(true)}
      onMouseUp={() => setActive(false)}
      onMouseLeave={() => setActive(false)}
      onMouseMove={handleMove}
      onTouchStart={() => setActive(true)}
      onTouchEnd={() => setActive(false)}
      onTouchMove={handleMove}
      className="w-full h-full cursor-crosshair touch-none"
    >
      <div className="absolute inset-0 grid grid-cols-8 grid-rows-8 opacity-10">
        {[...Array(64)].map((_, i) => (
          <div key={i} className="border-[0.5px] border-white" />
        ))}
      </div>

      <div
        className={`absolute w-8 h-8 -ml-4 -mt-4 rounded-full border-2 border-primary transition-transform duration-75 ${
          active ? 'scale-150 bg-primary/20' : 'scale-100 bg-white/10'
        }`}
        style={{ left: `${coords.x * 100}%`, top: `${coords.y * 100}%` }}
      >
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="w-1 h-1 bg-white rounded-full animate-ping" />
        </div>
      </div>

      <div className="absolute bottom-2 right-2 text-[8px] font-mono text-gray-600">
        X: {Math.round(coords.x * 100)} Y: {Math.round(coords.y * 100)}
      </div>
    </div>
  );
};
