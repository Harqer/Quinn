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
        <div className="px-4">
          <Typography variant="headline" className="tracking-tight">
            {title}
          </Typography>
        </div>
      )}
      
      <div className="flex gap-4 overflow-x-auto px-4 pb-4 scrollbar-hide snap-x">
        {React.Children.map(children, (child) => (
          <div className="snap-start">
            {child}
          </div>
        ))}
      </div>
    </section>
  );
};
