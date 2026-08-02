import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

def extract_method(method_name):
    match = re.search(r'^\s*private\s+' + method_name + r'\s*\(\)\s*\{', content, re.MULTILINE)
    if not match: return None
    start_idx = match.start()
    brace_count, in_method, end_idx = 0, False, -1
    for i in range(start_idx, len(content)):
        if content[i] == '{':
            brace_count += 1
            in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0:
                end_idx = i + 1
                break
    return content[start_idx:end_idx] if end_idx != -1 else None

for m in ['renderAndroidEmail', 'renderAndroidPassword', 'renderAndroidName']:
    code = extract_method(m)
    if code:
        with open(f'./components/organisms/{m}.txt', 'w') as f: f.write(code)
        print(f"Extracted {m}")
    else:
        print(f"Could not extract {m}")
