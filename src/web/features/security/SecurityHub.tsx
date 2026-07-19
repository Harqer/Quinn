import React, { useState, useEffect } from 'react';
import { logger } from '@/web/lib/logger';

export const SecurityHub: React.FC = () => {
  const [alerts, setAlerts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('/api/vulnerability-alerts')
      .then(res => res.json())
      .then(data => {
        setAlerts(data.alerts || []);
        setLoading(false);
      })
      .catch(err => logger.error("Failed to fetch vulnerability alerts", err));
  }, []);

  return (
    <div className="h-full p-8 overflow-y-auto bg-gray-950">
      <div className="max-w-4xl mx-auto space-y-8">
        <header className="flex items-center justify-between">
          <div className="space-y-1">
            <h2 className="text-3xl font-bold text-white">Security Hub</h2>
            <p className="text-gray-500 font-medium italic italic">Real-time vulnerability monitoring and automated remediation.</p>
          </div>
          <div className="flex gap-2">
            <span className="px-4 py-2 rounded-xl bg-green-500/10 border border-green-500/20 text-green-500 text-xs font-bold uppercase tracking-widest">
              System Shield Enabled
            </span>
          </div>
        </header>

        <div className="grid gap-4">
          {loading ? (
            [...Array(3)].map((_, i) => (
              <div key={i} className="h-24 rounded-2xl bg-white/5 animate-pulse" />
            ))
          ) : alerts.length === 0 ? (
            <div className="p-12 text-center rounded-3xl bg-white/5 border border-white/5">
              <span className="material-icons-round text-5xl text-gray-700 mb-4">verified_user</span>
              <p className="text-gray-400 font-bold">No vulnerabilities detected.</p>
            </div>
          ) : (
            alerts.map(alert => (
              <AlertItem key={alert.id} alert={alert} />
            ))
          )}
        </div>
      </div>
    </div>
  );
};

const AlertItem: React.FC<{ alert: any }> = ({ alert }) => (
  <div className="p-6 rounded-3xl bg-white/5 border border-white/5 hover:border-red-500/30 transition-all group">
    <div className="flex items-start justify-between">
      <div className="flex gap-4">
        <div className={`w-12 h-12 rounded-2xl flex items-center justify-center ${
          alert.severity === 'critical' ? 'bg-red-500/20 text-red-500' : 'bg-orange-500/20 text-orange-500'
        }`}>
          <span className="material-icons-round">gpp_maybe</span>
        </div>
        <div className="space-y-1">
          <h4 className="font-bold text-white flex items-center gap-2">
            {alert.packageName}
            <span className="text-xs px-2 py-0.5 rounded-full bg-white/10 text-gray-400 font-mono">v{alert.firstPatchedVersion}</span>
          </h4>
          <p className="text-sm text-gray-500 line-clamp-1">{alert.summary}</p>
        </div>
      </div>
      <span className={`text-[10px] font-black uppercase tracking-tighter px-2 py-1 rounded ${
        alert.severity === 'critical' ? 'bg-red-500 text-white' : 'bg-orange-500 text-white'
      }`}>
        {alert.severity}
      </span>
    </div>

    <div className="mt-6 pt-6 border-t border-white/5 grid grid-cols-2 gap-4">
      <div className="space-y-2">
        <p className="text-[10px] text-gray-600 font-black uppercase tracking-widest">Remediation</p>
        <p className="text-xs text-gray-400 leading-relaxed font-medium">{alert.upgradePlan?.explanation}</p>
      </div>
      <div className="space-y-2">
        <p className="text-[10px] text-gray-600 font-black uppercase tracking-widest">Action Command</p>
        <code className="block p-3 rounded-xl bg-black/50 text-primary text-[10px] font-mono border border-white/5 break-all">
          {alert.upgradePlan?.command}
        </code>
      </div>
    </div>
  </div>
);
