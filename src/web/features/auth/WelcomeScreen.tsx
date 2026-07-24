import React from 'react';
import { useTranslation } from 'react-i18next';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { Button } from '../../components/atoms/Button';
import maveLogoDark from '../../assets/mave_brand_dark.png';

export const WelcomeScreen: React.FC<{ onSignUp: () => void, onLogin: () => void }> = ({ onSignUp, onLogin }) => {
  const { t } = useTranslation();
  return (
    <div className="flex flex-col items-center justify-center h-full w-full bg-background px-6 pb-12 relative overflow-hidden">
      <div className="absolute inset-0 bg-[#121212] z-0">
        <img 
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuDkStbOBQ4sccmT9egT0jR8eujGoi4_MYyolBqw03Dwk6iIfxEc3a1iulaO4Jv9ApLoHaAhSBU9UnZKndnJAAHN0MKm5ywQhmRYX6K1IQYpHvg8_oXSA3-para9CAQjJy3_CEJs63DssaVOqzHnm2GmSeR-Kx8LqC_SIn4n_d_6LQY5b4FE5NgsLXmVR13UJ6z037OJ1nScbCWUcMuI3ySfq5qaciVP4h7gRIh-Z__r72VqTM51QFcyGDvI1MCPWL7LduI" 
          alt="Background Collage" 
          className="w-full h-full object-cover opacity-80"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-[#121212] via-[#121212]/50 to-transparent"></div>
      </div>
      
      <div className="z-10 flex flex-col items-center w-full mt-auto">
        <div className="w-56 h-auto flex items-center justify-center mb-6">
           <img src={maveLogoDark} alt="Mave Logo" className="w-full h-auto object-contain" />
        </div>
        
        <Typography variant="display" className="text-center mb-10 tracking-tight leading-tight font-bold text-white">
          {t('welcome.title')}
        </Typography>

        <div className="w-full space-y-3 flex flex-col items-center">
          <Button fullWidth onClick={onSignUp} icon={<Icon name="person_add" />} title={t('welcome.signUp')} />
          <Button variant="outlined" fullWidth onClick={onLogin} icon={<Icon name="login" />} title={t('welcome.logIn')} />
        </div>
        
        <button onClick={onLogin} className="mt-8 text-white font-bold text-base hover:scale-105 transition-transform active:scale-95" title={t('welcome.logIn')}>
          <Icon name="login" size="md" />
        </button>
      </div>
    </div>
  );
};
