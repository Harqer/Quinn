import { describe, it, expect } from 'vitest';
import { GenerateSchema, ShareVibeSchema } from '../schemas/api.js';

describe('API Schemas', () => {
  it('should validate valid generate request', () => {
    const data = { image: 'base64str' };
    const result = GenerateSchema.safeParse(data);
    expect(result.success).toBe(true);
  });

  it('should reject empty image in generate request', () => {
    const data = { image: '' };
    const result = GenerateSchema.safeParse(data);
    expect(result.success).toBe(false);
  });

  it('should validate valid share vibe request', () => {
    const data = {
      title: 'Summer Jazz',
      artist: 'Mave',
      vibe: 'Relaxing jazz in the park',
      imageUrl: 'https://example.com/image.jpg'
    };
    const result = ShareVibeSchema.safeParse(data);
    expect(result.success).toBe(true);
  });
});
