import React from 'react';
import { Icon } from '../../../components/atoms/Icon';

export interface DeviceListItemProps {
  device: any;
  isConnecting: boolean;
  isConnected: boolean;
  onConnect: (device: any) => void;
}

export const DeviceListItem: React.FC<DeviceListItemProps> = ({
  device,
  isConnecting,
  isConnected,
  onConnect
}) => {
  const isExpanded = isConnecting || isConnected;

  return (
    <div 
      className={`w-full rounded-2xl p-5 transition-all ${
        isExpanded ? 'bg-surface-container-highest border-[1.5px] border-[#9333EA] shadow-lg shadow-primary/10' : 'bg-surface-container hover:bg-surface-variant/80 border border-transparent cursor-pointer'
      }`}
      onClick={() => !isExpanded && onConnect(device)}
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
              onConnect(device);
            }}
          >
            Connect
          </button>
        )}
      </div>
    </div>
  );
};