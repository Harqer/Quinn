import React, { useState } from 'react';
import { signInWithPopup } from 'firebase/auth';
import { auth, googleProvider } from '../../lib/firebase';
import maveLogoDark from '../../assets/mave_brand_dark.png';
import { logger } from "../../lib/logger";

export const LoginScreen: React.FC<{ onLogin: () => void, initialMode?: 'login' | 'signup' }> = ({ onLogin, initialMode = 'signup' }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [mode, setMode] = useState<'login' | 'signup'>(initialMode);

  const handleGoogleLogin = async () => {
    setLoading(true);
    setError('');
    try {
      await signInWithPopup(auth, googleProvider);
      onLogin();
    } catch (err: any) {
      logger.error(err);
      setError(err.message || 'Failed to authenticate');
    } finally {
      setLoading(false);
    }
  };

  const handleAppleLogin = () => {
    setError('Apple login is not configured yet. Please use Google.');
  };

  return (
    <div className="font-sans h-full flex flex-col items-center justify-start p-6 bg-[#121414] text-white w-full overflow-y-auto relative">
      <main className="w-full max-w-[450px] flex flex-col items-center text-center mt-12 pb-32">
        <header className="mb-10 w-full flex flex-col items-center">
          <img src={maveLogoDark} alt="Mave Logo" className="w-32 h-auto mb-8 object-contain" />
          <h1 className="text-[3rem] font-extrabold tracking-tighter leading-tight mb-8">
            {mode === 'signup' ? 'Sign up to start listening' : 'Log in to Mave'}
          </h1>
        </header>

        <section className="w-full max-w-[324px]">
          {error && <div className="mb-4 p-3 bg-red-500/20 border border-red-500/50 rounded-md text-red-400 text-sm font-bold">{error}</div>}
          




          <div className="space-y-3">
            <button 
              type="button"
              onClick={handleGoogleLogin}
              disabled={loading}
              className="w-full flex items-center justify-center gap-4 py-3 px-4 border border-white/40 rounded-full hover:border-white transition-colors disabled:opacity-50 bg-white text-black"
            >
              <svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48" className="w-5 h-5">
                <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"></path>
                <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"></path>
                <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"></path>
                <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"></path>
                <path fill="none" d="M0 0h48v48H0z"></path>
              </svg>
              <span className="text-sm font-bold">{mode === 'signup' ? 'Sign up with Google' : 'Continue with Google'}</span>
            </button>
            

          </div>

          <div className="mt-12 text-center">
            {mode === 'signup' ? (
              <p className="text-gray-400">
                Already have an account? 
                <button onClick={() => setMode('login')} className="text-white underline font-bold hover:text-[#1db954] ml-1">Log in</button>
              </p>
            ) : (
              <p className="text-gray-400">
                Don't have an account? 
                <button onClick={() => setMode('signup')} className="text-white underline font-bold hover:text-[#1db954] ml-1">Sign up for Mave</button>
              </p>
            )}
          </div>
        </section>
      </main>

      <footer className="absolute bottom-0 left-0 w-full px-6 py-6 text-center text-[11px] leading-relaxed text-gray-400 pointer-events-none">
        <p className="max-w-[400px] mx-auto pointer-events-auto">
          This site is protected by reCAPTCHA and the Google 
          <a className="underline ml-1 mr-1 hover:text-white" href="#">Privacy Policy</a> and 
          <a className="underline ml-1 hover:text-white" href="#">Terms of Service</a> apply.
        </p>
      </footer>
    </div>
  );
};
