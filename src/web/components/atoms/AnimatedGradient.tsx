import React, { useMemo } from 'react';

type Mood = 'chill' | 'energize' | 'focus' | 'random' | string;

interface Props {
  mood?: Mood;
  className?: string;
  style?: React.CSSProperties;
}

const getRandomGradient = () => {
  const gradients = [
    'linear-gradient(45deg, #ff9a9e 0%, #fecfef 99%, #fecfef 100%)',
    'linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%)',
    'linear-gradient(120deg, #d4fc79 0%, #96e6a1 100%)',
    'linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%)',
    'linear-gradient(120deg, #fccb90 0%, #d57eeb 100%)',
    'linear-gradient(120deg, #e0c3fc 0%, #8ec5fc 100%)',
    'linear-gradient(45deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(45deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(45deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(45deg, #30cfd0 0%, #330867 100%)',
    'linear-gradient(45deg, #a8edea 0%, #fed6e3 100%)',
    'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  ];
  return gradients[Math.floor(Math.random() * gradients.length)];
}

export const AnimatedGradient: React.FC<Props> = ({ mood = 'random', className = '', style }) => {
  const background = useMemo(() => {
    switch (mood.toLowerCase()) {
      case 'chill':
        return 'linear-gradient(45deg, #a2d2d6, #e7c2a4, #f39768)';
      case 'energize':
        return 'linear-gradient(45deg, #2b7a28, #5a9634, #a38c35)';
      case 'focus':
        return 'linear-gradient(45deg, #19273f, #2d558b, #44808c)';
      default:
        return getRandomGradient();
    }
  }, [mood]);

  return (
    <div 
      className={`relative overflow-hidden ${className}`}
      style={{
        background,
        backgroundSize: '200% 200%',
        animation: 'gradientMove 5s ease infinite alternate',
        ...style
      }}
    >
      <style>{`
        @keyframes gradientMove {
          0% { background-position: 0% 50%; }
          50% { background-position: 100% 50%; }
          100% { background-position: 0% 50%; }
        }
      `}</style>
    </div>
  );
};
