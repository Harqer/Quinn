import re

with open('./components/lyria_camera_styles.ts', 'r') as f:
    css_content = f.read()

def extract_classes_and_ids(html_content):
    classes = set()
    ids = set()
    class_matches = re.findall(r'class="([^"]+)"', html_content)
    for match in class_matches:
        for cls in match.split():
            classes.add(cls)
            
    id_matches = re.findall(r'id="([^"]+)"', html_content)
    for match in id_matches:
        ids.add(match)
    return classes, ids

def extract_css(classes, ids):
    extracted_css = []
    # A simple regex to find CSS blocks
    # We will look for blocks like `.className { ... }` or `#idName { ... }`
    # Also handles nested or grouped selectors loosely.
    
    # We'll split by '}' and then find the corresponding '{'
    blocks = css_content.split('}')
    for block in blocks:
        if not block.strip(): continue
        if '{' not in block: continue
        
        parts = block.split('{')
        if len(parts) < 2: continue
        
        selector = parts[0].strip()
        body = parts[1]
        
        # Check if selector contains any of our classes or ids
        # Also need to handle pseudo-classes
        keep = False
        for cls in classes:
            if f'.{cls}' in selector:
                keep = True
                break
        if not keep:
            for ident in ids:
                if f'#{ident}' in selector:
                    keep = True
                    break
                    
        if keep:
            extracted_css.append(f"{selector} {{{body}}}")
            
    return "\n".join(extracted_css)

def process_file(name):
    with open(f'./components/organisms/{name}.txt', 'r') as f:
        html = f.read()
    classes, ids = extract_classes_and_ids(html)
    css = extract_css(classes, ids)
    with open(f'./components/organisms/{name}.styles.ts', 'w') as f:
        f.write(f"import {{ css }} from 'lit';\n\nexport const {name}Styles = css`\n{css}\n`;\n")
    print(f"Extracted CSS for {name}")

process_file('renderAndroidWelcome')
process_file('renderSecurity')
process_file('renderAndroidExpandedPlayer')

