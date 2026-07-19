import React, { useRef, useEffect } from 'react';

interface Props {
  isPlaying: boolean;
}

export const MusicVisualizer: React.FC<Props> = ({ isPlaying }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animationId: number;

    const render = () => {
      const w = canvas.width = canvas.offsetWidth;
      const h = canvas.height = canvas.offsetHeight;

      ctx.clearRect(0, 0, w, h);

      if (isPlaying) {
        // Simple fluid wave simulation
        ctx.beginPath();
        ctx.moveTo(0, h / 2);
        for (let x = 0; x < w; x++) {
          const y = h / 2 + Math.sin(x * 0.01 + Date.now() * 0.005) * 50;
          ctx.lineTo(x, y);
        }
        ctx.strokeStyle = 'rgba(102, 80, 164, 0.5)';
        ctx.lineWidth = 4;
        ctx.stroke();
      }

      animationId = requestAnimationFrame(render);
    };

    render();
    return () => cancelAnimationFrame(animationId);
  }, [isPlaying]);

  return <canvas ref={canvasRef} className="w-full h-full" />;
};
