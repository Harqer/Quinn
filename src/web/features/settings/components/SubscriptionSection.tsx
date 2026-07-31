import React, { useState, useEffect } from 'react';
import { User } from 'firebase/auth';
import { getUserSettings } from '../../../../lib/dataconnect';
import { useNavigate } from '../../../App';

interface Props {
  user: User | null;
}

export const SubscriptionSection: React.FC<Props> = ({ user }) => {
  const [loading, setLoading] = useState(false);
  const [isPremium, setIsPremium] = useState(false);
  const [tierName, setTierName] = useState("Free");
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) return;
    const fetchPlan = async () => {
      try {
        const res = await getUserSettings();
        if (res.data?.userSettings) {
          const premium = res.data.userSettings.isPremium || false;
          setIsPremium(premium);
          
          const rawTier = (res.data.userSettings as any).subscriptionTier || (res.data.userSettings as any).theme || "free";
          if (rawTier.includes("ultra")) setTierName("Ultra Unlimited");
          else if (rawTier.includes("pro")) setTierName("Pro Studio");
          else if (rawTier.includes("basic")) setTierName("Basic Creator");
          else setTierName(premium ? "Premium" : "Free");
        }
      } catch (err) {
        console.error("Error fetching user settings via dataconnect", err);
      }
    };
    fetchPlan();
  }, [user]);

  const handleManage = async () => {
    if (!user) return;
    try {
      setLoading(true);
      const token = await user.getIdToken();
      const response = await fetch('/api/stripe/portal-session', {
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
            Mave {tierName}
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
            onClick={() => navigate('premium')} 
            disabled={loading}
            className="font-bold text-sm bg-[#1db954] text-black px-6 py-2 rounded-full hover:scale-105 transition-transform disabled:opacity-50"
          >
            Upgrade Plan
          </button>
        )}
        
        {isPremium && (
          <div className="flex flex-col gap-2 items-center">
            <button 
              onClick={() => navigate('premium')} 
              className="text-xs text-white/80 hover:underline"
            >
              View All Plans
            </button>
            <button 
              onClick={handleManage} 
              disabled={loading}
              className="font-bold text-sm bg-white/20 text-white px-6 py-2 rounded-full hover:bg-white/30 transition-colors disabled:opacity-50"
            >
              {loading ? 'Processing...' : 'Manage Billing'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
