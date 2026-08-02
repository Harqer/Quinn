// Tool declarations: Lyria 3 = full songs, Lyria RealTime = instrument tweaking
export const functionDeclarations = [
  {
    name: 'generate_full_track',
    description: 'Generate a new, complete professional music track or song (Lyria 3). Use this when the user wants a full song created from scratch.',
    parameters: {
      type: 'OBJECT',
      properties: {
        prompt: { type: 'STRING', description: 'Musical style, genre, and description of the full song to create' }
      },
      required: ['prompt']
    }
  },
  {
    name: 'tweak_instrumentation',
    description: 'Modify or tweak the instruments, density, BPM, brightness, or style of the currently playing track in real-time (Lyria RealTime). Use when the user wants to change how the song sounds without regenerating from scratch.',
    parameters: {
      type: 'OBJECT',
      properties: {
        prompt: { type: 'STRING', description: 'What to tweak (e.g. add more bass, make it faster, add jazz piano)' },
        bpm: { type: 'NUMBER', description: 'Target beats per minute' },
        density: { type: 'NUMBER', description: 'Note density 0.0-1.0' },
        brightness: { type: 'NUMBER', description: 'Tonal brightness 0.0-1.0' }
      },
      required: ['prompt']
    }
  },
  {
    name: 'generate_cover_art',
    description: 'Generate or update the album cover art for the current track. Use when the user asks for cover art.',
    parameters: {
      type: 'OBJECT',
      properties: {
        prompt: { type: 'STRING', description: 'Visual description for the cover art' },
        hq: { type: 'BOOLEAN', description: 'Set true for high-quality Pro model, false for fast default' }
      },
      required: ['prompt']
    }
  },
  {
    name: 'generate_video',
    description: 'Generate a music video for the current track. Only use when the user explicitly asks for a video.',
    parameters: {
      type: 'OBJECT',
      properties: {
        prompt: { type: 'STRING', description: 'Visual and cinematic description for the music video' }
      },
      required: ['prompt']
    }
  }
];

// SSE helper: reads a server-sent events stream from a fetch POST
export async function* readSSE(
  url: string,
  body: object,
  token?: string
): AsyncGenerator<{ type: string; [key: string]: any }> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  const res = await fetch(url, { method: 'POST', headers, body: JSON.stringify(body) });
  if (!res.ok || !res.body) throw new Error(`SSE request failed: ${res.status}`);
  const reader = res.body.getReader();
  const decoder = new TextDecoder();
  let buffer = '';
  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    buffer += decoder.decode(value, { stream: true });
    const lines = buffer.split('\n');
    buffer = lines.pop() || '';
    for (const line of lines) {
      if (line.startsWith('data: ')) {
        try { yield JSON.parse(line.slice(6)); } catch { /* skip malformed */ }
      }
    }
  }
}
