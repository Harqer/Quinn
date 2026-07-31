import React from 'react';
import { useNavigate } from '../../App';
import { getAuth } from 'firebase/auth';

interface ProfileSettingsButtonProps {
  className?: string;
}

export const ProfileSettingsButton: React.FC<ProfileSettingsButtonProps> = ({ className = '' }) => {
  const navigate = useNavigate();
  const auth = getAuth();
  const user = auth.currentUser;

  return (
    <button
      onClick={() => navigate('settings')}
      className={`flex items-center gap-2 bg-surface hover:bg-surface-container transition-colors rounded-full p-1 pr-3 border border-outline/30 ${className}`}
    >
      {user?.photoURL ? (
        <img src={user.photoURL} alt="Profile" className="w-8 h-8 rounded-full object-cover" />
      ) : (
        <div className="w-8 h-8 rounded-full bg-primary/20 text-primary flex items-center justify-center">
          <span className="material-symbols-outlined text-lg">account_circle</span>
        </div>
      )}
      <span className="material-symbols-outlined text-text-secondary text-sm">settings</span>
    </button>
  );
};
