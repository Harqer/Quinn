import re

with open('./components/lyria_camera_styles.ts', 'r') as f:
    css_content = f.read()

def extract_classes_and_ids(html_content):
    classes = set()
    ids = set()
    for match in re.findall(r'class="([^"]+)"', html_content):
        classes.update(match.split())
    for match in re.findall(r'id="([^"]+)"', html_content):
        ids.add(match)
    return classes, ids

def extract_css(classes, ids):
    extracted_css = []
    blocks = css_content.split('}')
    
    # We want to keep some root elements explicitly
    keep_selectors = [':host', 'video', 'canvas', '@keyframes pulse']
    
    for block in blocks:
        if not block.strip() or '{' not in block: continue
        parts = block.split('{')
        if len(parts) < 2: continue
        selector = parts[0].strip()
        body = parts[1]
        
        keep = False
        
        # Check global root selectors
        if any(ks in selector for ks in keep_selectors):
            keep = True
            
        for cls in classes:
            if f'.{cls}' in selector: keep = True; break
        if not keep:
            for ident in ids:
                if f'#{ident}' in selector: keep = True; break
                    
        if keep:
            extracted_css.append(f"{selector} {{{body}}}")
            
    return "\n".join(extracted_css)

with open('./components/lyria_camera.ts', 'r') as f:
    classes, ids = extract_classes_and_ids(f.read())
    
new_css = extract_css(classes, ids)

with open('./components/lyria_camera_styles.ts', 'w') as f:
    f.write(f"import {{ css }} from 'lit';\n\nexport default css`\n{new_css}\n`;\n")

print(f"Reduced styles to {len(new_css.splitlines())} lines.")
