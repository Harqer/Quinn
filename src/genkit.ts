import { genkit } from 'genkit';
import { googleAI } from '@genkit-ai/googleai';
import logger from './config/logger.js';

export const ai = genkit({
  plugins: [googleAI()],
  model: 'googleai/gemini-2.5-pro',
});

logger.info('[GENKIT] Initialized with Google AI plugin');
