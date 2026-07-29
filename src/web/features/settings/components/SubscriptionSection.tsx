import React, { useState, useEffect } from 'react';
import { User } from 'firebase/auth';
import { getUserSettings } from '../../../../lib/dataconnect';
import { collection, addDoc, onSnapshot, doc, getFirestore } from 'firebase/firestore';

interface Props {
  user: User | null;
}

export const SubscriptionSection: React.FC<Props> = ({ user }) => {
  const [loading, setLoading] = useState(false);
  const [isPremium, setIsPremium] = useState(false);

  useEffect(() => {
    if (!user) return;
    const fetchPlan = async () => {
      try {
        const res = await getUserSettings();
        if (res.data?.userSettings) {
          setIsPremium(res.data.userSettings.isPremium || false);
        }
      } catch (err) {
        console.error("Error fetching user settings via dataconnect", err);
      }
    };
    fetchPlan();
  }, [user]);

  const handleSubscribe = async () => {
    if (!user) return;
    try {
      setLoading(true);
      const db = getFirestore();
      const docRef = await addDoc(collection(db, 'customers', user.uid, 'checkout_sessions'), {
        price: import.meta.env.VITE_STRIPE_PRICE_ID || 'price_default',
        success_url: window.location.href,
        cancel_url: window.location.href,
      });

      // Listen to the document to get the url returned by the Stripe extension
      onSnapshot(docRef, (snap) => {
        const data = snap.data();
        if (data?.error) {
          console.error(`An error occurred: ${data.error.message}`);
          setLoading(false);
        }
        if (data?.url) {
          window.location.assign(data.url);
        }
      });
    } catch (e) {
      console.error(e);
      setLoading(false);
    }
  };

  const handleManage = async () => {
    if (!user) return;
    try {
      setLoading(true);
      // Wait for 200ms to allow UI to show loading state
      await new Promise(r => setTimeout(r, 200));
      // For managing, usually you use the Firebase extension's createPortalLink function
      // Assuming a Cloud Function `ext-firestore-stripe-payments-createPortalLink` is deployed
      const token = await user.getIdToken();
      const response = await fetch('/api/stripe/portal-session', { // Keep this for now if there isn't a direct subcollection pattern for portal in the extension
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          returnUrl: window.location.href
        })
      });
      const data = await response.json();
      if (data.url) {
        window.location.href = data.url;
      } else {
          setLoading(false);
      }
    } catch (e) {
      console.error(e);
      setLoading(false);
    }
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-12" data-purpose="hero-subscription">
      <div className="bg-surface-container p-8 rounded-lg flex flex-col justify-between min-h-[160px]">
        <div>
          <span className="text-[11px] font-bold uppercase text-gray-400 bg-black/40 px-2 py-1 rounded">Your plan</span>
          <h2 className="text-3xl font-extrabold mt-4 text-white">
            {isPremium ? 'Mave Premium' : 'Mave Free'}
          </h2>
          <p className="mt-2 text-sm text-gray-400">{user?.email || 'Not logged in'}</p>
        </div>
      </div>
      
      <div className="bg-gradient-to-br from-[#4b1a5e] to-[#2e113a] p-8 rounded-lg flex flex-col items-center justify-center text-center space-y-4">
        <div className="bg-white/10 p-3 rounded-full">
          <svg className="h-8 w-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5"></path>
          </svg>
        </div>
        
        {!isPremium && (
          <button 
            onClick={handleSubscribe} 
            disabled={loading}
            className="font-bold text-sm hover:underline disabled:opacity-50"
          >
            {loading ? 'Processing...' : 'Join Premium'}
          </button>
        )}
        
        {isPremium && (
          <button 
            onClick={handleManage} 
            disabled={loading}
            className="font-bold text-sm hover:underline disabled:opacity-50"
          >
            {loading ? 'Processing...' : 'Manage Subscription'}
          </button>
        )}
        
        {!isPremium && (
          <button 
            onClick={handleManage} 
            disabled={loading}
            className="text-xs text-gray-300 hover:underline disabled:opacity-50 mt-2"
          >
            Manage Subscription
          </button>
        )}
      </div>
    </div>
  );
};
