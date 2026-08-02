import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

match = re.search(r'^\s*private\s+renderLayout\s*\(\)\s*\{', content, re.MULTILINE)
if match:
    start_idx = match.start()
    brace_count, in_method, end_idx = 0, False, -1
    for i in range(start_idx, len(content)):
        if content[i] == '{': brace_count += 1; in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0: end_idx = i + 1; break
    if end_idx != -1:
        print(content[start_idx:end_idx])
