import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

# Add import
content = re.sub(r'(import \{ LitElement)', "import './organisms/splash-screen';\n" + r'\1', content, count=1)

# Remove method
match = re.search(r'^\s*private\s+renderSplash\s*\(\)\s*\{', content, re.MULTILINE)
if match:
    start_idx = match.start()
    brace_count, in_method, end_idx = 0, False, -1
    for i in range(start_idx, len(content)):
        if content[i] == '{': brace_count += 1; in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0: end_idx = i + 1; break
    if end_idx != -1:
        content = content[:start_idx] + content[end_idx:]

content = content.replace('this.renderSplash()', 'html`<splash-screen .currentUser=${this.currentUser}></splash-screen>`')

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)
print("Updated splash in lyria_camera.ts")
