export type MaveMode = 'music' | 'podcast' | 'audiobook';

export interface MaveMessage {
  id: string;
  sender: 'user' | 'mave';
  text: string;
  isAudio?: boolean;
  trackId?: string;
  type?: string;
  title?: string;
  voice?: string;
  coverUrl?: string;
  script?: string;
  audioUrl?: string;
  videoUrl?: string;
  reasoning?: string;
  isReasoningComplete?: boolean;
  isError?: boolean;
}
