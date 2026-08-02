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
    for block in blocks:
        if not block.strip() or '{' not in block: continue
        parts = block.split('{')
        if len(parts) < 2: continue
        selector = parts[0].strip()
        body = parts[1]
        
        keep = False
        for cls in classes:
            if f'.{cls}' in selector: keep = True; break
        if not keep:
            for ident in ids:
                if f'#{ident}' in selector: keep = True; break
                    
        if keep:
            extracted_css.append(f"{selector} {{{body}}}")
            
    return "\n".join(extracted_css)

with open('./components/organisms/android-companion-view.ts', 'r') as f:
    html = f.read()
    
classes, ids = extract_classes_and_ids(html)
css = extract_css(classes, ids)

with open('./components/organisms/android-companion-view.styles.ts', 'w') as f:
    f.write(f"import {{ css }} from 'lit';\n\nexport const androidCompanionViewStyles = css`\n{css}\n`;\n")

print("Extracted CSS for android-companion-view")
