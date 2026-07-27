import React from 'react';
import { Shimmer } from '../atoms/Shimmer';

export const TrackListSkeleton: React.FC<{ count?: number }> = ({ count = 5 }) => {
  return (
    <div className="w-full space-y-4">
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="flex items-center gap-4 w-full p-2">
          {/* Cover Art Skeleton */}
          <Shimmer className="w-14 h-14 rounded-lg shrink-0" />
          
          <div className="flex-1 space-y-2">
            {/* Title Skeleton */}
            <Shimmer className="w-3/4 h-4 rounded-md" />
            {/* Subtitle Skeleton */}
            <Shimmer className="w-1/2 h-3 rounded-md" />
          </div>
          
          {/* Action Icon Skeleton */}
          <Shimmer className="w-8 h-8 rounded-full shrink-0" />
        </div>
      ))}
    </div>
  );
};
