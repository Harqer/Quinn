import React, { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';
import { getUserSettings, upsertUserSettings } from '../../../../lib/dataconnect';

export const PreferencesSection: React.FC = () => {
  const auth = getAuth();
  const user = auth.currentUser;
  const [theme, setTheme] = useState<'dark' | 'light'>('dark');
  const [loading, setLoading] = useState(true);
  const [isPremium, setIsPremium] = useState(false);
  const [stripeCustomerId, setStripeCustomerId] = useState<string | null>(null);
  const [parentalControls, setParentalControls] = useState(false);

  useEffect(() => {
    if (!user) return;
    const fetchPrefs = async () => {
      try {
        const res = await getUserSettings();
        if (res.data?.userSettings) {
          const settings = res.data.userSettings;
          setParentalControls(settings.parentalControlsEnabled || false);
          setTheme(settings.theme === 'light' ? 'light' : 'dark');
          setIsPremium(settings.isPremium || false);
          setStripeCustomerId(settings.stripeCustomerId || null);
          
          if (settings.theme === 'light') {
            document.documentElement.classList.remove('dark');
          } else {
            document.documentElement.classList.add('dark');
          }
        }
      } catch (err) {
        console.error("Error fetching preferences via dataconnect:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchPrefs();
  }, [user]);

  const toggleParental = async () => {
    if (!user) return;
    const newVal = !parentalControls;
    setParentalControls(newVal);
    try {
      await upsertUserSettings({ 
        parentalControlsEnabled: newVal,
        theme: theme,
        stripeCustomerId: stripeCustomerId
      });
    } catch (err) {
      console.error("Error updating parental controls via dataconnect:", err);
    }
  };

  const toggleTheme = async () => {
    if (!user) return;
    const newVal = theme === 'dark' ? 'light' : 'dark';
    setTheme(newVal);
    
    if (newVal === 'light') {
      document.documentElement.classList.remove('dark');
    } else {
      document.documentElement.classList.add('dark');
    }
    
    try {
      await upsertUserSettings({ 
        theme: newVal,
        parentalControlsEnabled: parentalControls,
        stripeCustomerId: stripeCustomerId
      });
    } catch (err) {
      console.error("Error updating theme via dataconnect:", err);
    }
  };

  if (loading) {
    return <div className="text-gray-400 text-sm py-4">Loading preferences...</div>;
  }

  return (
    <section data-purpose="preferences-settings">
      <h3 className="text-2xl font-bold mb-6">Preferences</h3>
      <div className="bg-surface-container rounded-lg divide-y divide-white/5">
        
        {/* Theme Toggle */}
        <div className="flex items-center justify-between p-4 group">
          <div className="flex items-center space-x-4 text-sm font-semibold text-gray-200">
            <svg className="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
            <span>Appearance (Dark Mode)</span>
          </div>
          <button 
            onClick={toggleTheme}
            className={`w-12 h-6 rounded-full transition-colors ${theme === 'dark' ? 'bg-spotify' : 'bg-gray-600'} relative`}
          >
            <div className={`absolute top-1 w-4 h-4 rounded-full bg-white transition-transform ${theme === 'dark' ? 'left-7' : 'left-1'}`} />
          </button>
        </div>

        {/* Parental Controls */}
        <div className="flex items-center justify-between p-4 group">
          <div className="flex items-center space-x-4 text-sm font-semibold text-gray-200">
            <svg className="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>
            <div className="flex flex-col">
               <span>Parental Controls</span>
               <span className="text-xs text-gray-400 font-normal">Block explicit content</span>
            </div>
          </div>
          <button 
            onClick={toggleParental}
            className={`w-12 h-6 rounded-full transition-colors ${parentalControls ? 'bg-spotify' : 'bg-gray-600'} relative`}
          >
            <div className={`absolute top-1 w-4 h-4 rounded-full bg-white transition-transform ${parentalControls ? 'left-7' : 'left-1'}`} />
          </button>
        </div>

      </div>
    </section>
  );
};
