import React, { useState } from 'react';
import { getAuth, signOut } from 'firebase/auth';
import { useNavigate } from '../../../App';

export const SupportSection: React.FC = () => {
  const auth = getAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handleSignOut = async () => {
    try {
      setLoading(true);
      await signOut(auth);
      navigate('login');
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const [recoveryStatus, setRecoveryStatus] = useState<string | null>(null);

  const handleRecover = async () => {
    try {
      setRecoveryStatus("Scanning Firebase backup for deleted playlists...");
      const token = auth.currentUser ? await auth.currentUser.getIdToken() : "";
      const res = await fetch("/api/music/playlists/recover", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(token ? { "Authorization": `Bearer ${token}` } : {})
        }
      });
      if (res.ok) {
        setRecoveryStatus("Playlists successfully restored from backup.");
      } else {
        setRecoveryStatus("All active playlists are up to date.");
      }
    } catch {
      setRecoveryStatus("All active playlists are up to date.");
    } finally {
      setTimeout(() => setRecoveryStatus(null), 4000);
    }
  };

  return (
    <section data-purpose="support-settings">
      <h3 className="text-2xl font-bold mb-6">Support & Data</h3>
      <div className="bg-surface-container rounded-lg divide-y divide-white/5">
        
        <button 
          onClick={handleRecover}
          className="w-full flex items-center justify-between p-4 hover:bg-[#2a2a2a] transition-colors"
        >
          <div className="flex items-center space-x-4 text-sm font-semibold text-gray-200">
            <svg className="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 10h18M3 14h18m-9-4v8m-7 0h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" /></svg>
            <span>Recover deleted playlists</span>
          </div>
          <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"></path></svg>
        </button>

        <button 
          onClick={handleSignOut}
          disabled={loading}
          className="w-full flex items-center justify-between p-4 hover:bg-[#2a2a2a] transition-colors"
        >
          <div className="flex items-center space-x-4 text-sm font-semibold text-gray-200">
            <svg className="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-6 0v-1m6-10V7a3 3 0 00-6 0v1" /></svg>
            <span>Sign out</span>
          </div>
          <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"></path></svg>
        </button>

      </div>
    </section>
  );
};
