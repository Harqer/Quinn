import React from 'react';

interface SettingsRowProps {
  icon: React.ReactNode;
  label: string;
  onClick?: () => void;
  className?: string;
  textColor?: string;
}

export const SettingsRow: React.FC<SettingsRowProps> = ({ 
  icon, 
  label, 
  onClick, 
  className = "",
  textColor = "text-on-surface"
}) => {
  return (
    <div 
      onClick={onClick}
      className={`flex items-center justify-between p-4 group transition-colors cursor-pointer hover:bg-surface-variant ${className}`}
    >
      <div className={`flex items-center space-x-4 text-sm font-semibold ${textColor}`}>
        <div className="text-secondary group-hover:text-on-surface transition-colors">
          {icon}
        </div>
        <span>{label}</span>
      </div>
      <svg className="h-5 w-5 text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path d="M9 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"></path>
      </svg>
    </div>
  );
};
