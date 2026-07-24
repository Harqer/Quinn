import React, { useState, useEffect } from 'react';
import { getAuth } from 'firebase/auth';
import { Icon } from '../../components/atoms/Icon';

export interface DevicesScreenProps {
  onBack?: () => void;
}

export const DevicesScreen: React.FC<DevicesScreenProps> = ({ onBack }) => {
  const [isMetaConnected, setIsMetaConnected] = useState<boolean>(false);
  const [batteryLevel, setBatteryLevel] = useState<number>(85);
  const [hudProjectionMode, setHudProjectionMode] = useState<boolean>(() => {
    return localStorage.getItem('mave_hud_mode') === 'true';
  });
  const [isScanning, setIsScanning] = useState<boolean>(false);
  const [activeCameraId, setActiveCameraId] = useState<string>('');
  const [videoDevices, setVideoDevices] = useState<MediaDeviceInfo[]>([]);
  const [statusMessage, setStatusMessage] = useState<string>('');

  useEffect(() => {
    if (navigator.mediaDevices && navigator.mediaDevices.enumerateDevices) {
      navigator.mediaDevices.enumerateDevices().then((devices) => {
        const cams = devices.filter((d) => d.kind === 'videoinput');
        setVideoDevices(cams);
        if (cams.length > 0) setActiveCameraId(cams[0].deviceId);
      }).catch((err) => {
        console.warn('[WEARABLES] MediaDevices enumeration error:', err);
      });
    }
  }, []);

  const toggleMetaConnection = async () => {
    setIsScanning(true);
    setStatusMessage(isMetaConnected ? 'Disconnecting...' : 'Connecting to Meta Wearables...');

    try {
      const auth = getAuth();
      const user = auth.currentUser;
      const token = user ? await user.getIdToken() : '';

      const targetStatus = isMetaConnected ? 'disconnected' : 'connected';

      const response = await fetch('/api/devices/connect', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          ...(token ? { 'Authorization': `Bearer ${token}` } : {})
        },
        body: JSON.stringify({ deviceId: 'meta_glasses', status: targetStatus })
      });

      if (!response.ok) {
        throw new Error('Device connection failed');
      }

      const data = await response.json();
      
      setIsMetaConnected(data.status === 'connected');
      if (data.status === 'connected') {
        setBatteryLevel(data.batteryLevel);
        setStatusMessage('Ray-Ban Meta Smart Glasses Connected Successfully!');
      } else {
        setStatusMessage('Meta Wearables Disconnected');
      }
    } catch (err: any) {
      console.error('[WEARABLES] Connection canceled or failed:', err);
      setStatusMessage('Failed to connect to device. Please try again.');
    } finally {
      setIsScanning(false);
    }
  };

  const toggleHudProjection = () => {
    const nextVal = !hudProjectionMode;
    setHudProjectionMode(nextVal);
    localStorage.setItem('mave_hud_mode', String(nextVal));
  };

  return (
    <div className={`flex flex-col h-full w-full ${hudProjectionMode ? 'bg-[#000000] text-[#9bbfff]' : 'bg-background text-text-primary'} p-6 overflow-y-auto`}>
      {/* Top Header */}
      <div className="flex items-center justify-between pb-6 border-b border-outline/20">
        <div className="flex items-center gap-3">
          {onBack && (
            <button
              onClick={onBack}
              className="p-2 rounded-full hover:bg-surface-container/60 transition-colors"
              aria-label="Go back"
            >
              <Icon name="arrow_back" />
            </button>
          )}
          <div>
            <h1 className="text-xl font-bold tracking-tight">Devices & Meta Wearables</h1>
            <p className="text-xs text-text-secondary">Ray-Ban Meta Smart Glasses & Intelligent Eyewear</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <span className={`w-2.5 h-2.5 rounded-full ${isMetaConnected ? 'bg-primary' : 'bg-text-secondary/40'}`}></span>
          <span className="text-xs font-semibold uppercase tracking-wider">{isMetaConnected ? 'Connected' : 'Disconnected'}</span>
        </div>
      </div>

      {/* Main Connection Card */}
      <div className="mt-6 space-y-4">
        <div className="text-xs font-bold uppercase tracking-wider text-text-secondary">Current Device</div>
        
        <div 
          onClick={toggleMetaConnection}
          className={`p-5 rounded-2xl border transition-all cursor-pointer flex items-center justify-between ${
            isMetaConnected 
              ? 'bg-surface-container border-primary/40 shadow-lg shadow-primary/10' 
              : 'bg-surface border-outline/30 hover:border-outline/60'
          }`}
        >
          <div className="flex items-center gap-4">
            <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${isMetaConnected ? 'bg-primary/20 text-primary' : 'bg-surface-container text-text-secondary'}`}>
              <Icon name="glasses" size="xl" />
            </div>
            <div>
              <h2 className="text-base font-bold">Ray-Ban Meta Smart Glasses</h2>
              <p className="text-xs text-text-secondary">
                {isMetaConnected ? `Connected • Battery ${batteryLevel}%` : 'Tap to scan and pair via Bluetooth / WebRTC'}
              </p>
            </div>
          </div>
          
          <button 
            disabled={isScanning}
            className={`p-2.5 rounded-xl transition-colors flex items-center justify-center ${
              isMetaConnected 
                ? 'bg-outline/30 hover:bg-outline/50 text-text-primary' 
                : 'bg-primary text-background hover:bg-primary-hover'
            }`}
            title={isScanning ? 'Scanning...' : isMetaConnected ? 'Disconnect' : 'Connect'}
          >
            {isScanning ? <Icon name="search" size="md" /> : isMetaConnected ? <Icon name="bluetooth_disabled" size="md" /> : <Icon name="bluetooth_connected" size="md" />}
          </button>
        </div>

        {statusMessage && (
          <p className="text-xs font-medium text-primary px-1">{statusMessage}</p>
        )}
      </div>

      {/* HUD Projection & Jetpack Glimmer Additive Theme Settings */}
      <div className="mt-8 space-y-4">
        <div className="text-xs font-bold uppercase tracking-wider text-text-secondary">Glasses Projection & Display</div>

        <div className="p-5 rounded-2xl bg-surface border border-outline/30 flex items-center justify-between">
          <div className="space-y-1">
            <div className="text-sm font-bold flex items-center gap-2">
              <span className="material-symbols-outlined text-primary text-base">visibility</span>
              Glimmer HUD Projection Mode
            </div>
            <p className="text-xs text-text-secondary max-w-[280px]">
              Sets pure black additive background (#000000) optimized for display glasses HUD projection.
            </p>
          </div>
          <button
            onClick={toggleHudProjection}
            className={`w-12 h-6 rounded-full transition-colors relative p-1 ${hudProjectionMode ? 'bg-primary' : 'bg-outline/40'}`}
          >
            <div className={`w-4 h-4 rounded-full bg-white transition-transform ${hudProjectionMode ? 'translate-x-6' : 'translate-x-0'}`}></div>
          </button>
        </div>
      </div>

      {/* Camera & Audio Hardware Streams */}
      <div className="mt-8 space-y-4">
        <div className="text-xs font-bold uppercase tracking-wider text-text-secondary">Hardware Camera & Sensors</div>

        {videoDevices.length > 0 ? (
          <div className="p-4 rounded-2xl bg-surface border border-outline/30 space-y-3">
            <label className="text-xs font-bold text-text-secondary">Active Smart Glasses Video Source:</label>
            <select
              value={activeCameraId}
              onChange={(e) => setActiveCameraId(e.target.value)}
              className="w-full p-2.5 text-xs bg-surface-container border border-outline/40 rounded-xl text-text-primary focus:outline-none focus:border-primary"
            >
              {videoDevices.map((cam, idx) => (
                <option key={cam.deviceId || idx} value={cam.deviceId}>
                  {cam.label || `Smart Glasses Camera ${idx + 1}`}
                </option>
              ))}
            </select>
          </div>
        ) : (
          <div className="p-4 rounded-2xl bg-surface border border-outline/30 text-xs text-text-secondary flex items-center gap-3">
            <span className="material-symbols-outlined text-primary">videocam</span>
            <span>Camera permissions active. Connect Ray-Ban Meta glasses to stream live video feeds into Mave Lyria.</span>
          </div>
        )}
      </div>
    </div>
  );
};
