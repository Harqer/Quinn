import React, { useState } from 'react';
import { getAuth, signInWithPopup, GoogleAuthProvider } from 'firebase/auth';
import { Turnstile } from '@marsidev/react-turnstile';
import { Typography } from '../../components/atoms/Typography';
import { Button } from '../../components/atoms/Button';
import { Icon } from '../../components/atoms/Icon';
import maveLogoDark from '../../assets/mave_brand_dark.png';
import { useTracks } from '../../hooks/useTracks';

/**
 * Rebranded Login Screen for Mave Studio.
 */
export const LoginScreen: React.FC<{ onLogin: () => void }> = ({ onLogin }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [turnstileToken, setTurnstileToken] = useState<string | null>(null);
  const { communityTracks } = useTracks();

  const handleGoogleLogin = async () => {
    setLoading(true);
    setError('');
    try {
      const auth = getAuth();
      const provider = new GoogleAuthProvider();
      await signInWithPopup(auth, provider);
      onLogin();
    } catch (err: any) {
      console.error(err);
      setError(err.message || 'Failed to sign in');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center h-full w-full bg-background px-6 pb-12 relative overflow-hidden">
      <div className="absolute inset-0 bg-gradient-to-b from-surface-container to-background z-0"></div>
      
      {/* Background artwork from context/hooks */}
      <div className="absolute inset-0 flex flex-wrap opacity-30 pointer-events-none justify-center items-center z-0 overflow-hidden">
        {communityTracks.filter(t => t.albumArtUrl).slice(0, 4).map((track, i) => (
          <img 
            key={track.id} 
            src={track.albumArtUrl} 
            alt="" 
            className={`w-64 h-64 rounded-full object-cover blur-3xl absolute ${i % 2 === 0 ? '-left-10' : '-right-10'} ${i < 2 ? 'top-10' : 'bottom-20'}`} 
          />
        ))}
      </div>

      <div className="z-10 flex flex-col items-center w-full mt-auto">
        <div className="w-56 h-auto flex items-center justify-center mb-6">
           <img
            src={maveLogoDark}
            alt="Mave Logo"
            className="w-full h-auto object-contain"
          />
        </div>
        
        <Typography variant="display" className="text-center mb-10 tracking-tight leading-tight font-bold">
          Millions of Vibes.<br />Orchestrated by Mave.
        </Typography>

        {error && (
          <div className="mb-4 text-red-500 text-sm">{error}</div>
        )}
        
        <div className="w-full space-y-3 flex flex-col items-center">
          <Turnstile siteKey={(window as any).VITE_TURNSTILE_SITE_KEY || "1x00000000000000000000AA"} onSuccess={setTurnstileToken} />
          <Button fullWidth onClick={handleGoogleLogin} disabled={loading || !turnstileToken} icon={<Icon name="person_add" />} title="Sign up free" />
          
          <Button 
            variant="outlined" 
            fullWidth 
            onClick={handleGoogleLogin}
            disabled={loading || !turnstileToken}
            icon={<Icon name="login" size="md" />}
            title="Continue with Google"
          />
        </div>
        
        <button onClick={handleGoogleLogin} disabled={loading || !turnstileToken} className="mt-8 text-white font-bold text-base hover:scale-105 transition-transform active:scale-95 disabled:opacity-50" title="Log in">
          <Icon name="login" size="md" />
        </button>
      </div>
    </div>
  );
};
