import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

# Add imports
imports = """import './organisms/android-welcome';
import './organisms/security-hub';
import './organisms/android-expanded-player';
"""

content = re.sub(r'(import \{ LitElement)', imports + r'\1', content, count=1)

def remove_method(method_name, content):
    match = re.search(r'^\s*private\s+' + method_name + r'\s*\(\)\s*\{', content, re.MULTILINE)
    if not match:
        return content
    
    start_idx = match.start()
    brace_count = 0
    in_method = False
    end_idx = -1
    
    for i in range(start_idx, len(content)):
        if content[i] == '{':
            brace_count += 1
            in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0:
                end_idx = i + 1
                break
                
    if end_idx != -1:
        return content[:start_idx] + content[end_idx:]
    return content

content = remove_method('renderAndroidWelcome', content)
content = remove_method('renderSecurity', content)
content = remove_method('renderAndroidExpandedPlayer', content)

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)

