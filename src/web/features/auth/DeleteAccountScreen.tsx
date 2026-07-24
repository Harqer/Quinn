import React, { useState } from 'react';
import { getAuth, deleteUser } from "firebase/auth";

import { Icon } from '../../components/atoms/Icon';

export const DeleteAccountScreen: React.FC = () => {
  const [status, setStatus] = useState<'idle' | 'loading' | 'success' | 'error'>('idle');
  const [errorMessage, setErrorMessage] = useState('');

  const handleDelete = async () => {
    const auth = getAuth();
    const user = auth.currentUser;
    
    if (!user) {
      setStatus('error');
      setErrorMessage('You must be signed in to delete your account.');
      return;
    }

    if (!window.confirm('Are you absolutely sure you want to delete your account? This action cannot be undone.')) {
      return;
    }

    setStatus('loading');
    try {
      await deleteUser(user);
      setStatus('success');
    } catch (error: any) {
      console.error('Failed to delete account:', error);
      setStatus('error');
      if (error.code === 'auth/requires-recent-login') {
        setErrorMessage('This operation is sensitive and requires recent authentication. Please log in again before retrying.');
      } else {
        setErrorMessage(error.message || 'An error occurred while deleting your account.');
      }
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-[#121414] text-white p-6 text-center">
      <h1 className="text-3xl font-bold mb-4">Delete Account</h1>
      
      {status === 'success' ? (
        <div className="bg-green-900/50 text-green-200 p-6 rounded-lg max-w-md">
          <p className="text-lg">Your account has been successfully deleted.</p>
          <p className="mt-2 opacity-80">We're sorry to see you go.</p>
        </div>
      ) : (
        <div className="max-w-md w-full bg-[#1e2020] p-8 rounded-2xl shadow-xl">
          <p className="mb-6 text-gray-300">
            Deleting your account will permanently erase all your data, tracks, and settings from Mave Studio. This action cannot be undone.
          </p>
          
          {status === 'error' && (
            <div className="bg-red-900/50 text-red-200 p-4 rounded-lg mb-6 text-sm">
              {errorMessage}
            </div>
          )}
          
          <button
            onClick={handleDelete}
            disabled={status === 'loading'}
            className="w-full py-4 rounded-full bg-red-600 hover:bg-red-700 text-white font-bold transition-colors disabled:opacity-50 flex items-center justify-center"
            title="Delete My Account"
          >
            {status === 'loading' ? <Icon name="hourglass_empty" size="md" /> : <Icon name="delete_forever" size="md" />}
          </button>
        </div>
      )}
    </div>
  );
};
