import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

def extract_method(method_name):
    # Find start
    match = re.search(r'^\s*private\s+' + method_name + r'\s*\(\)\s*\{', content, re.MULTILINE)
    if not match:
        return None
    
    start_idx = match.start()
    
    # Simple brace counting
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
        return content[start_idx:end_idx]
    return None

methods = ['renderAndroidExpandedPlayer', 'renderSecurity', 'renderAndroidWelcome']

for m in methods:
    code = extract_method(m)
    if code:
        with open(f'./components/organisms/{m}.txt', 'w') as f:
            f.write(code)
        print(f"Extracted {m}")
    else:
        print(f"Could not extract {m}")
