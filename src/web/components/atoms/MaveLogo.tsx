import React from 'react';
import maveLogoDark from '../../../assets/mave_brand_dark.png';
import maveLogoLight from '../../../assets/mave_brand_light.png';

export const MaveLogo: React.FC<{ variant?: 'light' | 'dark', size?: number }> = ({ variant = 'dark', size = 120 }) => {
  const logoSrc = variant === 'light' ? maveLogoLight : maveLogoDark;
  return (
    <img
      src={logoSrc}
      alt="Mave Logo"
      style={{ width: size, height: 'auto', display: 'block' }}
    />
  );
};
