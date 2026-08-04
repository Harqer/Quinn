export async function* readSSE(
  url: string,
  body: object,
  token?: string
): AsyncGenerator<{ type: string; [key: string]: any }> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  
  const res = await fetch(url, { method: 'POST', headers, body: JSON.stringify(body) });
  
  if (!res.ok || !res.body) {
    throw new Error(`SSE request failed: ${res.status}`);
  }
  
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
        try { 
          yield JSON.parse(line.slice(6)); 
        } catch { 
          /* skip malformed */ 
        }
      }
    }
  }
}
