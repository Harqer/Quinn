import React, { useState } from 'react';
import { copyToClipboard } from '../../utils/clipboard';
import { Icon } from './Icon';

interface CopyButtonProps {
  text: string;
  showToast?: (msg: string) => void;
  variant?: 'dashboard' | 'chat';
}

export const CopyButton: React.FC<CopyButtonProps> = ({ text, showToast, variant = 'dashboard' }) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    const success = await copyToClipboard(text);
    if (success) {
      setCopied(true);
      if (showToast) showToast('Copied to clipboard');
      setTimeout(() => setCopied(false), 2000);
    } else {
      if (showToast) showToast('Failed to copy');
    }
  };

  if (variant === 'chat') {
    return (
      <button
        onClick={handleCopy}
        className="text-on-primary-container/70 hover:text-on-primary-container transition-colors p-1 -mt-1 rounded-full active:bg-black/10 flex-shrink-0"
        title="Copy text"
      >
        <Icon name={copied ? "check" : "content_copy"} className="text-[16px]" />
      </button>
    );
  }

  return (
    <button 
      onClick={handleCopy}
      className="p-2 text-gray-500 hover:text-white transition-colors mt-2 flex-shrink-0"
      title="Copy text"
    >
      <span className="material-icons-round text-sm">{copied ? "check" : "content_copy"}</span>
    </button>
  );
};
