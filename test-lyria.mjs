import { GoogleGenAI } from '@google/genai';
import { getSecret } from './src/config/secrets.js';
import dotenv from 'dotenv';
dotenv.config();

const apiKey = process.env.GEMINI_API_KEY || getSecret('GEMINI_API_KEY');
const ai = new GoogleGenAI({ apiKey, httpOptions: { apiVersion: 'v1alpha' } });
ai.models.generateContent({ model: 'lyria-3-pro-preview', contents: 'a test song' })
  .then(res => console.log('Success:', res.text))
  .catch(err => console.error('Error:', err));
