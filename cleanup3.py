import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

# Add imports
imports = """import './organisms/community-view';
import './organisms/android-companion-view';
"""
content = re.sub(r'(import \{ LitElement)', imports + r'\1', content, count=1)

def remove_method(method_name, content):
    match = re.search(r'^\s*private\s+' + method_name + r'\s*\(\)\s*\{', content, re.MULTILINE)
    if not match: return content
    start_idx = match.start()
    brace_count, in_method, end_idx = 0, False, -1
    for i in range(start_idx, len(content)):
        if content[i] == '{': brace_count += 1; in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0: end_idx = i + 1; break
    return content[:start_idx] + content[end_idx:] if end_idx != -1 else content

methods = [
    'renderCommunity', 'renderAndroidCompanion',
    'renderAndroidSearchHome', 'renderAndroidSearchResults', 'renderAndroidAlbumDetails',
    'renderAndroidCommunity', 'renderAndroidGoLive', 'renderAndroidLibrary', 'renderAndroidOptionsMenu'
]

for m in methods:
    content = remove_method(m, content)

# Update render method in lyria_camera.ts
content = content.replace('this.renderCommunity()', "html`<community-view></community-view>`")
content = content.replace('this.renderAndroidCompanion()', "html`<android-companion-view></android-companion-view>`")

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)

