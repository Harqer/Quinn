import React, { useState, useEffect } from 'react';
import { MainDashboard } from '@/features/dashboard/MainDashboard';
import { SplashView } from '@/features/splash/SplashView';
import { CommunityStage } from '@/features/community/CommunityStage';
import { SecurityHub } from '@/features/security/SecurityHub';
import { PodcastView } from '@/features/podcast/PodcastView';
import { CompanionApp } from '@/features/android-flow/CompanionApp';
import { ToastMessage } from '@/ui/ToastMessage';
import { getAuth, onAuthStateChanged, User } from "firebase/auth";

type Page = 'splash' | 'main' | 'community' | 'podcast' | 'android_flow' | 'security';

export const App: React.FC = () => {
  const [page, setPage] = useState<Page>('splash');
  const [currentUser, setCurrentUser] = useState<User | null>(null);

  useEffect(() => {
    const auth = getAuth();
    return onAuthStateChanged(auth, (user) => {
      setCurrentUser(user);
    });
  }, []);

  return (
    <div className="flex flex-col h-screen bg-surface text-on-surface font-sans">
      <nav className="flex items-center justify-between p-4 border-b border-outline/10 bg-surface-container/50 backdrop-blur-md">
        <div className="flex items-center gap-2">
          <span className="text-primary material-icons-round">video_camera_front</span>
          <span className="text-xl font-bold tracking-tight">Musically</span>
        </div>

        <div className="flex gap-4">
          <NavTab active={page === 'splash'} onClick={() => setPage('splash')} icon="home" label="Welcome" />
          <NavTab active={page === 'main'} onClick={() => setPage('main')} icon="settings_overscan" label="Console" />
          <NavTab active={page === 'community'} onClick={() => setPage('community')} icon="public" label="Stage" />
          <NavTab active={page === 'podcast'} onClick={() => setPage('podcast')} icon="mic" label="Podcast" />
          <NavTab active={page === 'android_flow'} onClick={() => setPage('android_flow')} icon="phone_android" label="Companion" />
          <NavTab active={page === 'security'} onClick={() => setPage('security')} icon="security" label="Hub" />
        </div>
      </nav>

      <main className="flex-1 overflow-hidden relative">
        {page === 'splash' && <SplashView onLaunch={() => setPage('main')} />}
        {page === 'main' && <MainDashboard />}
        {page === 'community' && <CommunityStage />}
        {page === 'podcast' && <PodcastView />}
        {page === 'android_flow' && <CompanionApp />}
        {page === 'security' && <SecurityHub />}
      </main>

      <ToastMessage />
    </div>
  );
};

const NavTab: React.FC<{ active: boolean; onClick: () => void; icon: string; label: string }> = ({ active, onClick, icon, label }) => (
  <button
    onClick={onClick}
    className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-all ${
      active ? 'bg-primary text-on-primary shadow-lg shadow-primary/20' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high'
    }`}
  >
    <span className="material-icons-round text-sm">{icon}</span>
    <span className="text-sm font-medium">{label}</span>
  </button>
);
