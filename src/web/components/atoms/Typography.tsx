import React from 'react';

type TypographyVariant = 
  | 'display'
  | 'headline'
  | 'title-lg' | 'title-md' | 'title-sm'
  | 'body-lg' | 'body-md' | 'body-sm'
  | 'label-md' | 'label-sm';

type TypographyColor = 'primary' | 'secondary' | 'error' | 'inherit';

export interface TypographyProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: TypographyVariant;
  color?: TypographyColor;
  as?: React.ElementType;
}

export const Typography: React.FC<TypographyProps> = ({
  variant = 'body-md',
  color = 'primary',
  as: Component = 'span',
  className = '',
  children,
  ...props
}) => {
  const variantClasses = {
    'display': 'text-display',
    'headline': 'text-headline',
    'title-lg': 'text-title-lg',
    'title-md': 'text-title-md',
    'title-sm': 'text-title-sm',
    'body-lg': 'text-body-lg',
    'body-md': 'text-body-md',
    'body-sm': 'text-body-sm',
    'label-md': 'text-label-md',
    'label-sm': 'text-label-sm',
  };

  const colorClasses = {
    'primary': 'text-text-primary',
    'secondary': 'text-text-secondary',
    'error': 'text-error',
    'inherit': 'text-inherit',
  };

  const combinedClasses = `${variantClasses[variant]} ${colorClasses[color]} ${className}`;

  return (
    <Component className={combinedClasses.trim()} {...props}>
      {children}
    </Component>
  );
};
