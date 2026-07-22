import React, { useState } from 'react';
import { getAuth, signInWithPopup, GoogleAuthProvider } from 'firebase/auth';
import { Typography } from '../../components/atoms/Typography';
import { Button } from '../../components/atoms/Button';
import { Icon } from '../../components/atoms/Icon';
import maveLogoDark from '../../assets/mave_brand_dark.png';

/**
 * Rebranded Login Screen for Mave Studio.
 */
export const LoginScreen: React.FC<{ onLogin: () => void }> = ({ onLogin }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

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
      
      {/* Background artwork placeholder */}
      <div className="absolute top-10 flex flex-wrap gap-4 opacity-50 pointer-events-none p-4 justify-center z-0">
        <div className="w-32 h-32 rounded-full bg-primary/20 blur-3xl"></div>
        <div className="w-40 h-40 rounded-full bg-secondary/20 blur-3xl ml-10"></div>
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
          <Button fullWidth onClick={handleGoogleLogin} disabled={loading}>
            Sign up free
          </Button>
          
          <Button 
            variant="outlined" 
            fullWidth 
            onClick={handleGoogleLogin}
            disabled={loading}
            icon={<Icon name="login" size="md" />}
          >
            {loading ? 'Connecting...' : 'Continue with Google'}
          </Button>
        </div>
        
        <button onClick={handleGoogleLogin} disabled={loading} className="mt-8 text-white font-bold text-base hover:scale-105 transition-transform active:scale-95 disabled:opacity-50">
          Log in
        </button>
      </div>
    </div>
  );
};
