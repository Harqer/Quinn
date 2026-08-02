import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

def count_lines(s): return len(s.split('\n'))

methods = re.finditer(r'^\s*(?:(?:public|private|protected)\s+)?(?:async\s+)?([a-zA-Z0-9_]+)\s*\([^)]*\)\s*(?::\s*[a-zA-Z0-9_<>]+)?\s*\{', content, re.MULTILINE)

method_sizes = []

for match in methods:
    start_idx = match.start()
    name = match.group(1)
    
    # We don't want to capture class or constructor if it's matching weirdly, but regex ensures it has ()
    
    brace_count, in_method, end_idx = 0, False, -1
    for i in range(start_idx, len(content)):
        if content[i] == '{': brace_count += 1; in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0: end_idx = i + 1; break
            
    if end_idx != -1:
        lines = count_lines(content[start_idx:end_idx])
        method_sizes.append((name, lines))

for name, size in sorted(method_sizes, key=lambda x: x[1], reverse=True)[:20]:
    print(f"{name}: {size} lines")
