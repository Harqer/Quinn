import React, { useState, useEffect } from 'react';
import { Icon } from '../../components/atoms/Icon';

export interface DevicesScreenProps {
  onBack?: () => void;
}

export const DevicesScreen: React.FC<DevicesScreenProps> = ({ onBack }) => {
  const [isSupported, setIsSupported] = useState(true);
  const [bluetoothEnabled, setBluetoothEnabled] = useState(false);
  const [devices, setDevices] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [isScanning, setIsScanning] = useState(false);
  const [volume, setVolume] = useState(50);
  const [connectingId, setConnectingId] = useState<string | null>(null);
  const [connectedId, setConnectedId] = useState<string | null>(null);
  const [salonInvitesEnabled, setSalonInvitesEnabled] = useState(true);

  useEffect(() => {
    if (!(navigator as any).bluetooth) {
      setIsSupported(false);
    } else {
      // Check if we can get bluetooth availability
      if ((navigator as any).bluetooth.getAvailability) {
        (navigator as any).bluetooth.getAvailability().then((available: boolean) => {
          setBluetoothEnabled(available);
        });
      } else {
        setBluetoothEnabled(true); // Assume enabled if getAvailability is not supported but bluetooth is
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
      const device = await (navigator as any).bluetooth.requestDevice({
        acceptAllDevices: true
      });
      
      setDevices(prev => {
        if (!prev.find(d => d.id === device.id)) {
          return [...prev, device];
        }
        return prev;
      });
    } catch (err: any) {
      if (err.name !== 'NotFoundError') { 
        setError(err.message || 'Failed to scan for devices');
      }
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

      {/* Main Container */}
      <main className="w-full h-full flex flex-col relative md:px-8 lg:px-16 max-w-7xl mx-auto">
      
      {/* Header */}
      <header className="p-4 md:py-8 flex items-center justify-between sticky top-0 bg-background z-10">
        <div className="flex items-center gap-4">
          <button aria-label="Close" className="p-2 hover:bg-surface-variant rounded-full transition-colors text-on-surface" onClick={onBack}>
            <Icon name="close" />
          </button>
          <h1 className="text-2xl md:text-4xl font-bold tracking-tight text-on-background">Your devices</h1>
        </div>
      </header>

      {/* Scrollable Content */}
      <div className="flex-1 overflow-y-auto px-4 md:px-0 pb-32 custom-scrollbar text-on-surface">
          
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            
            <div className="flex flex-col gap-8">
              {/* Bluetooth Toggle */}
              {/* Bluetooth Status */}
              <section>
                <div 
                  className="bg-surface-container rounded-2xl p-6 flex items-center justify-between"
                >
                  <div className="flex items-center gap-4">
                    <div className={`p-3 rounded-full ${bluetoothEnabled ? 'bg-primary/20 text-primary' : 'bg-surface-variant text-text-secondary'}`}>
                      <Icon name="bluetooth" />
                    </div>
                    <div>
                      <p className="font-bold text-lg text-on-surface">Bluetooth</p>
                      <p className="text-sm text-text-secondary">{bluetoothEnabled ? 'On' : 'Off (Manage in System Settings)'}</p>
                    </div>
                  </div>
                  {/* Bluetooth state indicator icon */}
                  <button 
                    className="cursor-pointer hover:opacity-80 transition-opacity"
                    title="Toggle Bluetooth search capability"
                    onClick={() => setBluetoothEnabled(!bluetoothEnabled)}
                  >
                    {bluetoothEnabled ? (
                      <svg xmlns="http://www.w3.org/2000/svg" height="36px" viewBox="0 -960 960 960" width="36px" className="fill-primary"><path d="M280-240q-100 0-170-70T40-480q0-100 70-170t170-70h400q100 0 170 70t70 170q0 100-70 170t-170 70H280Zm0-80h400q66 0 113-47t47-113q0-66-47-113t-113-47H280q-66 0-113 47t-47 113q0 66 47 113t113 47Zm485-75q35-35 35-85t-35-85q-35-35-85-35t-85 35q-35 35-35 85t35 85q35 35 85 35t85-35Zm-285-85Z"/></svg>
                    ) : (
                      <svg xmlns="http://www.w3.org/2000/svg" height="36px" viewBox="0 -960 960 960" width="36px" className="fill-on-surface"><path d="M280-240q-100 0-170-70T40-480q0-100 70-170t170-70h400q100 0 170 70t70 170q0 100-70 170t-170 70H280Zm0-80h400q66 0 113-47t47-113q0-66-47-113t-113-47H280q-66 0-113 47t-47 113q0 66 47 113t113 47Zm85-75q35-35 35-85t-35-85q-35-35-85-35t-85 35q-35 35-35 85t35 85q35 35 85 35t85-35Zm115-85Z"/></svg>
                    )}
                  </button>
                </div>
              </section>

              {/* Current Device */}
              <section>
                <h2 className="text-xl font-bold mb-6 text-on-surface px-2">Current device</h2>
                <div className="bg-surface-container rounded-2xl p-6 flex items-center gap-6 border border-outline/10">
                  <div className="bg-surface-variant p-4 rounded-full text-on-surface">
                    <Icon name="smartphone" />
                  </div>
                  <div>
                    <p className="font-bold text-on-surface text-xl">This browser</p>
                    <div className="flex items-center gap-2 text-primary text-sm font-semibold mt-1">
                      <Icon name="volume_up" size="sm" />
                      <span>System default</span>
                    </div>
                  </div>
                </div>
              </section>
            </div>

            {/* Available Devices Column */}
            <div className="flex flex-col gap-6">
              <section>
                <div className="flex justify-between items-center mb-6 px-2">
                  <h2 className="text-xl font-bold text-on-surface">Select a device</h2>
                  {isSupported && bluetoothEnabled && (
                    <button 
                      onClick={scanForDevices}
                      disabled={isScanning}
                      className="text-sm bg-on-surface text-surface px-4 py-2 rounded-full font-bold hover:scale-105 active:scale-95 transition-transform disabled:opacity-50 flex items-center gap-2"
                    >
                      <Icon name={isScanning ? "sync" : "bluetooth_searching"} className={isScanning ? "animate-spin" : ""} size="sm" />
                      {isScanning ? 'Scanning...' : 'Find Devices'}
                    </button>
                  )}
                </div>
                
                {error && (
                  <div className="mb-6 p-4 bg-red-900/30 border border-red-500/50 rounded-xl text-sm text-red-200">
                    {error}
                  </div>
                )}

                {isSupported && !bluetoothEnabled && !error && (
                  <div className="mb-6 p-6 bg-surface-container rounded-2xl text-center text-text-secondary border border-outline/10">
                    <Icon name="bluetooth_disabled" className="mx-auto mb-2 opacity-50" />
                    Turn on Bluetooth to find nearby devices.
                  </div>
                )}
                
                {bluetoothEnabled && devices.length === 0 && !error && !isScanning ? (
                  <div className="mt-8 p-12 text-center text-text-secondary bg-surface-container/50 rounded-3xl border border-outline/5 border-dashed">
                    <Icon name="devices" className="mx-auto mb-4 opacity-30 text-4xl" />
                    <p className="text-base font-medium">No other devices found yet.</p>
                    <p className="text-sm mt-2 opacity-70">Make sure your device is in pairing mode.</p>
                  </div>
                ) : (
                  <div className="flex flex-col gap-3">
                    {devices.map((device, index) => {
                      const isConnecting = connectingId === device.id;
                      const isConnected = connectedId === device.id;
                      const isExpanded = isConnecting || isConnected;
                      
                      return (
                        <div 
                          key={device.id || index}
                          className={`w-full rounded-2xl p-5 transition-all ${
                            isExpanded ? 'bg-surface-container-highest border-[1.5px] border-[#9333EA] shadow-lg shadow-primary/10' : 'bg-surface-container hover:bg-surface-variant/80 border border-transparent cursor-pointer'
                          }`}
                          onClick={() => !isExpanded && connectToDevice(device)}
                        >
                          <div className="w-full flex items-center justify-between text-left group disabled:opacity-80">
                            <div className="flex items-center gap-5">
                              <div className={`p-3 rounded-full ${isExpanded ? 'bg-primary/20 text-primary' : 'bg-surface-variant text-text-secondary group-hover:text-on-surface transition-colors'}`}>
                                <Icon name="speaker" />
                              </div>
                              <div>
                                <p className="font-bold text-lg text-on-surface">{device.name || 'Unknown Device'}</p>
                                <div className={`flex items-center gap-1.5 text-sm font-medium mt-1 ${isExpanded ? 'text-primary' : 'text-text-secondary'}`}>
                                  <Icon name="cast" size="sm" />
                                  <span>
                                    {isConnecting ? 'Connecting...' : isConnected ? 'Connected' : 'Bluetooth Device'}
                                  </span>
                                </div>
                              </div>
                            </div>
                            
                            {!isExpanded && (
                              <button 
                                className="opacity-0 group-hover:opacity-100 bg-surface-container-highest hover:bg-surface-variant text-on-surface text-sm font-bold py-2 px-5 rounded-full transition-all"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  connectToDevice(device);
                                }}
                              >
                                Connect
                              </button>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </section>
            </div>
            
          </div>
        </div>

      </main>
    </div>
  );
};
