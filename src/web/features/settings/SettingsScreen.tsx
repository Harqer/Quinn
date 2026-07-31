import React, { useState, useEffect } from 'react';
import { useNavigate } from '../../App';
import { getAuth, onAuthStateChanged, signOut, User } from 'firebase/auth';
import { PersonalInfoSection } from './components/PersonalInfoSection';
import { SubscriptionSection } from './components/SubscriptionSection';
import { PreferencesSection } from './components/PreferencesSection';
import { SupportSection } from './components/SupportSection';

export const SettingsScreen: React.FC = () => {
  const navigate = useNavigate();
  const auth = getAuth();
  const [user, setUser] = useState<User | null>(auth.currentUser);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      setUser(currentUser);
    });
    return () => unsubscribe();
  }, [auth]);

  const handleSignOut = async () => {
    await signOut(auth);
    navigate('login');
  };

  return (
    <div className="min-h-full w-full bg-[#121212] flex flex-col pb-32">
      <header className="sticky top-0 z-50 bg-[#000000] border-b border-white/10">
        <div className="max-w-4xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2 text-white">
            <svg viewBox="0 0 24 24" className="w-8 h-8 fill-current" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.66 0 12 0zm5.521 17.34c-.24.359-.66.48-1.021.24-2.82-1.74-6.36-2.101-10.561-1.141-.418.122-.779-.179-.899-.539-.12-.421.18-.78.54-.9 4.56-1.021 8.52-.6 11.64 1.32.42.18.54.659.301 1.02zm1.44-3.3c-.301.42-.84.6-1.262.3-3.239-1.98-8.159-2.58-11.939-1.44-.539.12-1.02-.18-1.14-.72-.12-.54.18-1.02.72-1.14 4.32-1.32 9.72-.6 13.319 1.62.48.239.6.839.302 1.38zm.12-3.36C15.24 8.4 8.82 8.16 5.16 9.301c-.6.179-1.2-.181-1.38-.721-.18-.6.18-1.2.72-1.38 4.26-1.26 11.28-1.02 15.721 1.62.539.3.719 1.02.42 1.56-.299.48-1.02.659-1.56.3z"/>
            </svg>
            <span className="font-bold text-xl tracking-tight">Settings</span>
          </div>
          
          <nav className="hidden md:flex items-center space-x-6 text-[15px] font-bold text-white">
            <button className="flex items-center gap-2 group hover:text-[#1db954] transition-colors" onClick={() => navigate('profile')}>
              {user?.photoURL ? (
                <img src={user.photoURL} alt="Profile" className="w-8 h-8 rounded-full object-cover border-2 border-transparent group-hover:border-[#1db954] transition-colors" />
              ) : (
                <div className="w-8 h-8 rounded-full bg-gray-500 flex items-center justify-center text-white border-2 border-transparent group-hover:border-[#1db954] transition-colors">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                </div>
              )}
              <span>Profile</span>
            </button>
          </nav>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-4 sm:px-6 py-8 w-full space-y-12">
        <SubscriptionSection user={user} />
        <PersonalInfoSection />
        <PreferencesSection />
        <SupportSection />
        
        <section>
          <div className="bg-[#242424] rounded-lg overflow-hidden divide-y divide-white/5">
            <button 
              onClick={() => navigate('delete-account')}
              className="w-full flex items-center justify-between py-4 px-4 hover:bg-white/5 transition-colors text-left"
            >
              <div className="flex items-center gap-4">
                <div className="text-[#b3b3b3]">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                </div>
                <span className="text-[#e3e3e3] font-medium text-[15px]">Delete account</span>
              </div>
            </button>
            <button 
              onClick={handleSignOut}
              className="w-full flex items-center justify-between py-4 px-4 hover:bg-white/5 transition-colors text-left"
            >
              <div className="flex items-center gap-4">
                <div className="text-[#b3b3b3]">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-6 0v-1m6-10V7a3 3 0 00-6 0v1" /></svg>
                </div>
                <span className="text-[#e3e3e3] font-medium text-[15px]">Sign out</span>
              </div>
            </button>
          </div>
        </section>
      </main>
    </div>
  );
};
