import React, { useState } from 'react';
import { useNavigate } from '../../App';
import { getAuth } from 'firebase/auth';

export const PremiumPlansScreen: React.FC = () => {
  const navigate = useNavigate();
  const auth = getAuth();
  const [loadingTier, setLoadingTier] = useState<string | null>(null);

  const handleSubscribe = async (tier: string) => {
    try {
      setLoadingTier(tier);
      const user = auth.currentUser;
      if (!user) {
        navigate('login');
        return;
      }
      const token = await user.getIdToken();
      
      const res = await fetch('/api/stripe/checkout-session', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({ 
          returnUrl: window.location.origin + '/settings',
          tier 
        })
      });
      
      const data = await res.json();
      if (data.url) {
        window.location.href = data.url;
      } else {
        alert("Failed to start checkout");
      }
    } catch (e) {
      console.error(e);
      alert("Error starting checkout");
    } finally {
      setLoadingTier(null);
    }
  };

  const Checkmark = ({ active = true }: { active?: boolean }) => (
    <svg className={`w-5 h-5 flex-shrink-0 mr-3 ${active ? 'text-[#1db954]' : 'text-gray-500'}`} fill="currentColor" viewBox="0 0 20 20">
      <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
    </svg>
  );

  return (
    <div className="min-h-full w-full bg-[#121212] flex flex-col overflow-y-auto">
      {/* Header */}
      <header className="sticky top-0 z-50 bg-[#000000] border-b border-white/10">
        <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-4 text-white">
            <button onClick={() => navigate('settings')} className="hover:bg-white/10 p-2 rounded-full transition-colors">
              <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" /></svg>
            </button>
            <span className="font-bold text-xl tracking-tight">Lyria Premium</span>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-12 w-full flex-1">
        <div className="text-center mb-12">
          <h1 className="text-4xl md:text-5xl font-extrabold text-white mb-4 tracking-tight">Choose your Lyria experience</h1>
          <p className="text-gray-400 text-lg">Unlock the full potential of AI-generated music, video, and imagery.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 lg:gap-8">
          
          {/* Basic Tier */}
          <div className="bg-[#242424] rounded-xl p-8 flex flex-col border border-transparent hover:border-[#333] transition-colors relative shadow-xl">
            <h3 className="text-[#1db954] font-bold text-lg mb-2">Basic Creator</h3>
            <div className="text-white text-4xl font-extrabold mb-1">$20<span className="text-lg font-normal text-gray-400">/mo</span></div>
            <p className="text-sm text-gray-400 mb-8 h-10">Perfect for individuals starting their AI creation journey.</p>
            
            <ul className="space-y-4 mb-8 flex-1">
              <li className="flex items-start text-white text-[15px]"><Checkmark /> 30 Full Songs / month</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> 60 mins Real-time steering</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> 50 Images / month</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> 10 Videos / month</li>
              <li className="flex items-start text-gray-500 text-[15px]"><Checkmark active={false} /> Commercial Use License</li>
              <li className="flex items-start text-gray-500 text-[15px]"><Checkmark active={false} /> Priority Queue Access</li>
            </ul>

            <button 
              onClick={() => handleSubscribe('basic')}
              disabled={loadingTier !== null}
              className="w-full bg-[#1db954] text-black font-bold py-3 rounded-full hover:scale-105 transition-transform disabled:opacity-50"
            >
              {loadingTier === 'basic' ? 'Processing...' : 'Get Basic'}
            </button>
          </div>

          {/* Pro Tier */}
          <div className="bg-[#242424] rounded-xl p-8 flex flex-col border border-[#1db954] relative shadow-2xl scale-100 md:scale-105 z-10">
            <div className="absolute -top-3 left-1/2 -translate-x-1/2 bg-[#1db954] text-black text-xs font-bold px-3 py-1 rounded-full uppercase tracking-wider">Most Popular</div>
            <h3 className="text-[#1db954] font-bold text-lg mb-2">Pro Studio</h3>
            <div className="text-white text-4xl font-extrabold mb-1">$50<span className="text-lg font-normal text-gray-400">/mo</span></div>
            <p className="text-sm text-gray-400 mb-8 h-10">For serious creators who need higher volume and priority.</p>
            
            <ul className="space-y-4 mb-8 flex-1">
              <li className="flex items-start text-white text-[15px]"><Checkmark /> 100 Full Songs / month</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> 150 mins Real-time steering</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> 200 Images / month</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> 40 Videos / month</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> Commercial Use License</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> Priority Queue Access</li>
            </ul>

            <button 
              onClick={() => handleSubscribe('pro')}
              disabled={loadingTier !== null}
              className="w-full bg-[#1db954] text-black font-bold py-3 rounded-full hover:scale-105 transition-transform disabled:opacity-50"
            >
              {loadingTier === 'pro' ? 'Processing...' : 'Get Pro'}
            </button>
          </div>

          {/* Ultra Tier */}
          <div className="bg-[#242424] rounded-xl p-8 flex flex-col border border-transparent hover:border-[#333] transition-colors relative shadow-xl">
            <h3 className="text-[#1db954] font-bold text-lg mb-2">Ultra Unlimited</h3>
            <div className="text-white text-4xl font-extrabold mb-1">$100<span className="text-lg font-normal text-gray-400">/mo</span></div>
            <p className="text-sm text-gray-400 mb-8 h-10">Uncapped access for power users and professionals.</p>
            
            <ul className="space-y-4 mb-8 flex-1">
              <li className="flex items-start text-white text-[15px]"><Checkmark /> Unlimited Songs</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> Highest Real-time credits</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> Unlimited Images</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> Unlimited Videos</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> Commercial Use License</li>
              <li className="flex items-start text-white text-[15px]"><Checkmark /> Highest Priority Queue</li>
            </ul>

            <button 
              onClick={() => handleSubscribe('ultra')}
              disabled={loadingTier !== null}
              className="w-full bg-white text-black font-bold py-3 rounded-full hover:scale-105 transition-transform disabled:opacity-50"
            >
              {loadingTier === 'ultra' ? 'Processing...' : 'Get Ultra'}
            </button>
          </div>

        </div>
      </main>
    </div>
  );
};
