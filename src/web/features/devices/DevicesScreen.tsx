import React, { useState, useEffect } from 'react';
import { Icon } from '../../components/atoms/Icon';
import { DeviceStatusCard } from './components/DeviceStatusCard';
import { DeviceListItem } from './components/DeviceListItem';

export interface DevicesScreenProps {
  onBack?: () => void;
}

export const DevicesScreen: React.FC<DevicesScreenProps> = ({ onBack }) => {
  const [isSupported, setIsSupported] = useState(true);
  const [bluetoothEnabled, setBluetoothEnabled] = useState(false);
  const [devices, setDevices] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [isScanning, setIsScanning] = useState(false);
  const [connectingId, setConnectingId] = useState<string | null>(null);
  const [connectedId, setConnectedId] = useState<string | null>(null);

  useEffect(() => {
    if (!(navigator as any).bluetooth) {
      setIsSupported(false);
    } else {
      if ((navigator as any).bluetooth.getAvailability) {
        (navigator as any).bluetooth.getAvailability().then((available: boolean) => {
          setBluetoothEnabled(available);
        });
      } else {
        setBluetoothEnabled(true);
      }
    }
  }, []);

  const scanForDevices = async () => {
    if (!isSupported) {
      window.alert("Web Bluetooth is not supported in this browser.");
      return;
    }
    if (!bluetoothEnabled) {
      setError("Please enable Bluetooth first.");
      return;
    }
    setError(null);
    setIsScanning(true);
    try {
      const device = await (navigator as any).bluetooth.requestDevice({ acceptAllDevices: true });
      setDevices(prev => (!prev.find(d => d.id === device.id) ? [...prev, device] : prev));
    } catch (err: any) {
      if (err.name !== 'NotFoundError') setError(err.message || 'Failed to scan for devices');
    } finally {
      setIsScanning(false);
    }
  };

  const connectToDevice = async (device: any) => {
    if (!device.gatt) {
      setError("Device does not support GATT connections.");
      return;
    }
    setConnectingId(device.id);
    setError(null);
    try {
      await device.gatt.connect();
      setConnectedId(device.id);
    } catch (err: any) {
      setError(`Failed to connect to ${device.name || 'device'}: ${err.message}`);
    } finally {
      setConnectingId(null);
    }
  };

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-hidden">
      <main className="w-full h-full flex flex-col relative md:px-8 lg:px-16 max-w-7xl mx-auto">
        <header className="p-4 md:py-8 flex items-center justify-between sticky top-0 bg-background z-10">
          <div className="flex items-center gap-4">
            <button aria-label="Close" className="p-2 hover:bg-surface-variant rounded-full transition-colors text-on-surface" onClick={onBack}>
              <Icon name="close" />
            </button>
            <h1 className="text-2xl md:text-4xl font-bold tracking-tight text-on-background">Your devices</h1>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto px-4 md:px-0 pb-32 custom-scrollbar text-on-surface">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="flex flex-col gap-8">
              <DeviceStatusCard bluetoothEnabled={bluetoothEnabled} onToggleBluetooth={() => setBluetoothEnabled(!bluetoothEnabled)} />
              <section>
                <h2 className="text-xl font-bold mb-6 text-on-surface px-2">Current device</h2>
                <div className="bg-surface-container rounded-2xl p-6 flex items-center gap-6 border border-outline/10">
                  <div className="bg-surface-variant p-4 rounded-full text-on-surface"><Icon name="smartphone" /></div>
                  <div>
                    <p className="font-bold text-on-surface text-xl">This browser</p>
                    <div className="flex items-center gap-2 text-primary text-sm font-semibold mt-1"><Icon name="volume_up" size="sm" /><span>System default</span></div>
                  </div>
                </div>
              </section>
            </div>

            <div className="flex flex-col gap-6">
              <section>
                <div className="flex justify-between items-center mb-6 px-2">
                  <h2 className="text-xl font-bold text-on-surface">Select a device</h2>
                  {isSupported && bluetoothEnabled && (
                    <button onClick={scanForDevices} disabled={isScanning} className="text-sm bg-on-surface text-surface px-4 py-2 rounded-full font-bold hover:scale-105 active:scale-95 transition-transform disabled:opacity-50 flex items-center gap-2">
                      <Icon name={isScanning ? "sync" : "bluetooth_searching"} className={isScanning ? "animate-spin" : ""} size="sm" />
                      {isScanning ? 'Scanning...' : 'Find Devices'}
                    </button>
                  )}
                </div>
                {error && <div className="mb-6 p-4 bg-red-900/30 border border-red-500/50 rounded-xl text-sm text-red-200">{error}</div>}
                <div className="flex flex-col gap-3">
                  {devices.map((device, index) => (
                    <DeviceListItem key={device.id || index} device={device} isConnecting={connectingId === device.id} isConnected={connectedId === device.id} onConnect={connectToDevice} />
                  ))}
                </div>
              </section>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};
