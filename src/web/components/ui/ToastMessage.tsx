import React, { useState, useEffect, useCallback } from 'react';

export const ToastMessage: React.FC = () => {
  const [visible, setVisible] = useState(false);
  const [message, setMessage] = useState('');

  const show = useCallback((msg: string) => {
    setMessage(msg);
    setVisible(true);
  }, []);

  useEffect(() => {
    if (visible) {
      const timer = setTimeout(() => setVisible(false), 3000);
      return () => clearTimeout(timer);
    }
  }, [visible]);

  // Attach to window for global access (similar to the Lit custom element dispatch)
  useEffect(() => {
    (window as any).showToast = show;
  }, [show]);

  if (!visible) return null;

  return (
    <div className="fixed bottom-24 left-1/2 -translate-x-1/2 px-6 py-3 bg-gray-800/90 backdrop-blur-xl border border-white/10 rounded-full shadow-2xl z-50 animate-in fade-in slide-in-from-bottom-4">
      <span className="text-sm font-medium">{message}</span>
    </div>
  );
};
