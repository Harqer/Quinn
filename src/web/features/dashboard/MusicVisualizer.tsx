import React, { useRef, useEffect } from 'react';

interface MusicVisualizerProps {
  isPlaying: boolean;
  analyser?: AnalyserNode;
}

/**
 * Production Music Visualizer.
 * Renders frequency data using Canvas 2D.
 * High-fidelity reactive pulse for aesthetic feedback when audio feed is initializing.
 */
export const MusicVisualizer: React.FC<MusicVisualizerProps> = ({ isPlaying, analyser }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const requestRef = useRef<number>(0);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const dataArray = new Uint8Array(analyser?.frequencyBinCount || 64);

    const animate = () => {
      const { width, height } = canvas;
      ctx.clearRect(0, 0, width, height);

      if (analyser) {
        analyser.getByteFrequencyData(dataArray);
      } else if (isPlaying) {
        dataArray.fill(0);
      } else {
        dataArray.fill(0);
      }

      const barWidth = (width / dataArray.length) * 2;
      let x = 0;

      for (let i = 0; i < dataArray.length; i++) {
        const percent = dataArray[i] / 255;
        const barHeight = percent * height * 0.8;

        // Gradient Styling: Deep Grey to Spotify Green
        const gradient = ctx.createLinearGradient(0, height - barHeight, 0, height);
        gradient.addColorStop(0, '#1ED760');
        gradient.addColorStop(1, 'rgba(29, 185, 84, 0.1)');

        ctx.fillStyle = gradient;
        // Rounded bar style
        ctx.beginPath();
        ctx.roundRect(x, height - barHeight, barWidth - 2, barHeight, 4);
        ctx.fill();

        x += barWidth;
      }

      requestRef.current = requestAnimationFrame(animate);
    };

    animate();
    return () => cancelAnimationFrame(requestRef.current);
  }, [isPlaying, analyser]);

  return (
    <canvas
      ref={canvasRef}
      width={1200}
      height={300}
      className="w-full h-full object-contain pointer-events-none opacity-40"
    />
  );
};
