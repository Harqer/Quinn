import React from 'react';
import { Typography } from '../atoms/Typography';

export interface CarouselProps extends React.HTMLAttributes<HTMLDivElement> {
  title?: string;
  children: React.ReactNode;
}

export const Carousel: React.FC<CarouselProps> = ({
  title,
  children,
  className = '',
  ...props
}) => {
  return (
    <section className={`flex flex-col gap-4 py-4 ${className}`} {...props}>
      {title && (
        <div className="px-4 md:px-6">
          <Typography variant="headline" className="tracking-tight">
            {title}
          </Typography>
        </div>
      )}
      
      <div className="flex gap-4 md:gap-6 overflow-x-auto px-4 md:px-6 pb-4 md:pb-6 scrollbar-hide snap-x">
        {React.Children.map(children, (child) => (
          <div className="snap-start">
            {child}
          </div>
        ))}
      </div>
    </section>
  );
};
