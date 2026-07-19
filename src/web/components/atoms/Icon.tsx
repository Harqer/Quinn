import React from 'react';

export interface IconProps extends React.HTMLAttributes<HTMLSpanElement> {
  name: string;
  size?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '3xl' | '4xl' | '5xl' | '6xl';
  color?: 'primary' | 'secondary' | 'inherit' | 'white' | 'black';
}

export const Icon: React.FC<IconProps> = ({
  name,
  size = 'md',
  color = 'inherit',
  className = '',
  ...props
}) => {
  const sizeClasses = {
    'sm': 'text-sm',
    'md': 'text-base',
    'lg': 'text-lg',
    'xl': 'text-xl',
    '2xl': 'text-2xl',
    '3xl': 'text-3xl',
    '4xl': 'text-4xl',
    '5xl': 'text-5xl',
    '6xl': 'text-6xl',
  };

  const colorClasses = {
    'primary': 'text-primary',
    'secondary': 'text-text-secondary',
    'inherit': 'text-inherit',
    'white': 'text-white',
    'black': 'text-black',
  };

  const combinedClasses = `material-icons-round ${sizeClasses[size]} ${colorClasses[color]} ${className}`;

  return (
    <span className={combinedClasses.trim()} {...props}>
      {name}
    </span>
  );
};
