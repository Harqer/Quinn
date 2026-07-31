import { z } from 'zod';

export const GenerateSchema = z.object({
  image: z.string().min(1),
  type: z.enum(['music', 'podcast']).default('music'),
});

export const ShareVibeSchema = z.object({
  title: z.string().min(1).max(100),
  artist: z.string().optional(),
  vibe: z.string().min(1).max(500),
  imageUrl: z.string().url().optional(),
  type: z.enum(['music', 'podcast']).default('music'),
});

export const CommandSchema = z.object({
  sessionId: z.string().min(1),
  text: z.string().min(1),
});

export const VoiceCommandSchema = z.object({
  sessionId: z.string().min(1),
  audio: z.string().min(1),
  mimeType: z.string().optional(),
});

export const LogGestureSchema = z.object({
  gesture: z.string().min(1),
});

export const LogBatterySchema = z.object({
  batteryLevel: z.number().min(0).max(100),
  isWearDetected: z.union([z.boolean(), z.string()]),
});
