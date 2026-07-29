import React, { useRef, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';

interface ReasoningStreamProps {
  reasoning: string;
  isComplete?: boolean;
}

export const ReasoningStream: React.FC<ReasoningStreamProps> = ({ reasoning, isComplete }) => {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (containerRef.current && !isComplete) {
      containerRef.current.scrollLeft = containerRef.current.scrollWidth;
    }
  }, [reasoning, isComplete]);

  const cleanReasoning = reasoning
    .replace(/\n/g, ' ')
    .replace(/^\s*\d+\.\s*/g, '')
    .replace(/\s+\d+\.\s*/g, ' ')
    .replace(/\*\*/g, '')
    .replace(/Thinking Process:\s*/gi, '')
    .trim();

  return (
    <div className="mb-3 border-l-2 border-primary/30 pl-3">
      <div className="flex items-center w-full max-w-full overflow-hidden relative">
        <div 
          ref={containerRef}
          className="flex-1 overflow-hidden whitespace-nowrap text-text-secondary custom-scrollbar-hidden mask-linear-fade"
          style={{ 
            maskImage: 'linear-gradient(to right, transparent, black 10%, black 90%, transparent)',
            WebkitMaskImage: 'linear-gradient(to right, transparent 0%, black 15%, black 100%)',
            scrollBehavior: 'auto'
          }}
        >
          <Typography variant="body-sm" className="italic opacity-80 inline">
            {cleanReasoning}
          </Typography>
        </div>
        {!isComplete && (
          <div className="ml-2 flex-shrink-0 flex items-center">
            <Icon name="progress_activity" className="animate-spin text-primary text-[14px]" />
          </div>
        )}
      </div>
    </div>
  );
};
