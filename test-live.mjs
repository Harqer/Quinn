import { GoogleGenAI } from '@google/genai';
const ai = new GoogleGenAI({ apiKey: 'dummy', httpOptions: { apiVersion: 'v1alpha' } });
ai.live.music.connect({
  model: 'models/lyria-realtime-exp',
  callbacks: { onmessage: () => {} }
}).then(() => console.log('success')).catch(e => console.error('error', e));
