import React from 'react';
import { Typography } from '../atoms/Typography';

interface PodcastThinkingStateProps {
  thinkingText: string;
}

export const PodcastThinkingState: React.FC<PodcastThinkingStateProps> = ({ thinkingText }) => {
  return (
    <div className="p-4 bg-surface-container rounded-xl border border-primary/20 flex flex-col gap-2">
      <div className="flex items-center gap-2">
        <div className="w-4 h-4 rounded-full border-2 border-primary border-t-transparent animate-spin" />
        <Typography variant="label-sm" color="primary" className="font-bold">Analyzing & Generating...</Typography>
      </div>
      <Typography variant="body-sm" color="secondary" className="italic whitespace-pre-wrap">
        {thinkingText}
      </Typography>
    </div>
  );
};
