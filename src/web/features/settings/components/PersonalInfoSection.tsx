import React, { useState, useEffect } from 'react';
import { getAuth, updateProfile } from 'firebase/auth';
import { upsertUser } from '../../../../lib/dataconnect';

export const PersonalInfoSection: React.FC = () => {
  const auth = getAuth();
  const user = auth.currentUser;
  
  const [displayName, setDisplayName] = useState(user?.displayName || '');
  const [photoURL, setPhotoURL] = useState(user?.photoURL || '');
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (user) {
      setDisplayName(user.displayName || '');
      setPhotoURL(user.photoURL || '');
    }
  }, [user]);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;
    try {
      setSaving(true);
      await updateProfile(user, { displayName, photoURL });
      
      try {
        await upsertUser({ displayName, email: user.email });
      } catch (dcErr) {
        console.error("DataConnect sync failed:", dcErr);
      }
      
      setMessage('Profile updated successfully');
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      console.error(err);
      setMessage('Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  return (
    <section data-purpose="personal-info">
      <h3 className="text-2xl font-bold mb-6">Personal Info</h3>
      <form onSubmit={handleSave} className="bg-surface-container p-6 rounded-lg space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-2">Display Name</label>
          <input 
            type="text" 
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            className="w-full bg-[#2a2a2a] border border-gray-700 rounded-md py-2 px-3 focus:outline-none focus:ring-1 focus:ring-white" 
            placeholder="Your name"
          />
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-2">Avatar URL</label>
          <input 
            type="text" 
            value={photoURL}
            onChange={(e) => setPhotoURL(e.target.value)}
            className="w-full bg-[#2a2a2a] border border-gray-700 rounded-md py-2 px-3 focus:outline-none focus:ring-1 focus:ring-white" 
            placeholder="https://..."
          />
          {photoURL && (
            <div className="mt-4 flex items-center space-x-4">
              <img src={photoURL} alt="Avatar Preview" className="h-16 w-16 rounded-full object-cover" />
              <span className="text-sm text-gray-400">Avatar Preview</span>
            </div>
          )}
        </div>

        <div className="flex items-center space-x-4">
          <button 
            type="submit" 
            disabled={saving}
            className="bg-white text-black px-6 py-2 rounded-full font-bold hover:scale-105 transition-transform disabled:opacity-50"
          >
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
          {message && <span className="text-sm text-green-400">{message}</span>}
        </div>
      </form>
    </section>
  );
};
