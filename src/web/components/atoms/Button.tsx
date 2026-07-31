import React from 'react';
import { Typography } from './Typography';

export type ButtonVariant = 'filled' | 'outlined' | 'text' | 'elevated' | 'tonal';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  fullWidth?: boolean;
  icon?: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  variant = 'filled',
  fullWidth = false,
  icon,
  className = '',
  children,
  ...props
}) => {
  const baseClasses = 'inline-flex items-center justify-center gap-2 px-6 py-3 rounded-full transition-all duration-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 focus-visible:ring-offset-background active:scale-95 disabled:opacity-50 disabled:pointer-events-none cursor-pointer';
  const widthClasses = fullWidth ? 'w-full' : '';

  const variantClasses = {
    filled: 'bg-primary text-black hover:bg-primary-variant hover:shadow-md',
    elevated: 'bg-surface-container text-primary shadow-sm hover:shadow-md hover:bg-surface-container/90',
    tonal: 'bg-surface-container text-text-primary hover:bg-surface-container/80',
    outlined: 'border-2 border-outline text-primary hover:bg-primary/10 hover:border-primary',
    text: 'text-primary hover:bg-primary/10 px-4',
  };

  const combinedClasses = `${baseClasses} ${widthClasses} ${variantClasses[variant]} ${className}`;

  return (
    <button className={combinedClasses.trim()} {...props}>
      {icon && <span className="flex items-center justify-center">{icon}</span>}
      <Typography variant="label-md" color="inherit">
        {children}
      </Typography>
    </button>
  );
};
