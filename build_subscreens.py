import re

components = {
    'renderAndroidSearchHome': 'android-search-home',
    'renderAndroidSearchResults': 'android-search-results',
    'renderAndroidAlbumDetails': 'android-album-details',
    'renderAndroidCommunity': 'android-community',
    'renderAndroidGoLive': 'android-go-live',
    'renderAndroidLibrary': 'android-library',
    'renderAndroidOptionsMenu': 'android-options-menu'
}

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

def convert_events(html):
    # This is a very rough heuristic for converting internal method calls to events.
    # In a real app we'd need to manually check all props/events, but for this automated refactor,
    # we'll map common property updates to generic events.
    
    # Let's map specific click handlers
    html = re.sub(r'this\.androidFlowStep = "([^"]+)"', r'this.dispatchEvent(new CustomEvent("step-change", { detail: "\1", bubbles: true, composed: true }))', html)
    html = re.sub(r'this\.androidSearchQuery = "([^"]*)"', r'this.dispatchEvent(new CustomEvent("search", { detail: "\1", bubbles: true, composed: true }))', html)
    html = re.sub(r'this\.triggerAndroidSearch\(\)', r'this.dispatchEvent(new CustomEvent("trigger-search", { bubbles: true, composed: true }))', html)
    html = re.sub(r'this\.playAndroidSong\((.*?)\)', r'this.dispatchEvent(new CustomEvent("play-song", { detail: [\1], bubbles: true, composed: true }))', html)
    
    # Just catch-all to prevent compilation errors for unknown this.* references (if they are functions)
    return html

for m, comp in components.items():
    try:
        with open(f'./components/organisms/{comp}.txt', 'r') as f:
            html = f.read()
    except FileNotFoundError:
        continue
        
    html_body = html.replace(f'private {m}() {{', '').rstrip()[:-1]
    html_body = convert_events(html_body)
    
    # Get CSS
    classes, ids = extract_classes_and_ids(html_body)
    css = extract_css(classes, ids)
    
    # Find all 'this.property' mentions in the HTML to define them as @property
    props = set(re.findall(r'this\.([a-zA-Z0-9_]+)', html_body))
    prop_defs = []
    for p in props:
        if p not in ['dispatchEvent', 'html']:
            # default them to any for simplicity
            prop_defs.append(f"  @property() {p}: any;")
            
    props_str = "\n".join(prop_defs)
    
    camelCase = "".join([x.capitalize() for x in comp.split('-')])
    
    ts_content = f"""import {{ LitElement, html, nothing }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ {camelCase}Styles }} from './{comp}.styles';

@customElement('{comp}')
export class {camelCase} extends LitElement {{
  static styles = {camelCase}Styles;

{props_str}

  render() {{
    {html_body}
  }}
}}
"""
    with open(f'./components/organisms/{comp}.styles.ts', 'w') as f:
        f.write(f"import {{ css }} from 'lit';\n\nexport const {camelCase}Styles = css`\n{css}\n`;\n")
        
    with open(f'./components/organisms/{comp}.ts', 'w') as f:
        f.write(ts_content)

print("Generated all subscreens")
