import React, { RefObject } from 'react';
import { Icon } from '../atoms/Icon';

interface LiveCameraViewProps {
  videoRef: RefObject<HTMLVideoElement>;
  showCamera: boolean;
  isGenerating: boolean;
  onCapture: () => void;
  onToggleCamera: () => void;
}

export const LiveCameraView: React.FC<LiveCameraViewProps> = ({
  videoRef,
  showCamera,
  isGenerating,
  onCapture,
  onToggleCamera
}) => {
  if (!showCamera) return null;

  return (
    <div className="relative w-full h-64 bg-black flex-shrink-0">
      <video 
        ref={videoRef} 
        autoPlay 
        playsInline 
        muted 
        className="w-full h-full object-cover"
      />
      <button 
        onClick={onCapture}
        disabled={isGenerating}
        className="absolute bottom-4 left-1/2 -translate-x-1/2 w-14 h-14 bg-white rounded-full flex items-center justify-center border-4 border-primary/50 shadow-lg disabled:opacity-50"
      >
        <div className="w-10 h-10 bg-primary rounded-full"></div>
      </button>
      <button 
        onClick={onToggleCamera}
        className="absolute top-4 right-4 w-10 h-10 bg-black/50 text-white rounded-full flex items-center justify-center"
      >
        <Icon name="close" size="md" />
      </button>
    </div>
  );
};
