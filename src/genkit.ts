import { genkit } from 'genkit';
import { googleAI } from '@genkit-ai/google-genai';
import { getSecret } from './config/secrets.js';
import logger from './config/logger.js';

export const ai = genkit({
  plugins: [googleAI({ apiKey: (getSecret("GEMINI_API_KEY") as string) || process.env.GEMINI_API_KEY })],
  model: 'googleai/gemini-2.5-pro',
});

logger.info('[GENKIT] Initialized with Google AI plugin');

