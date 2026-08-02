import re

with open('./components/organisms/renderSplash.txt', 'r') as f:
    html = f.read().replace('private renderSplash() {', '').rstrip()[:-1]

html = html.replace('this.logout', '(e) => this.dispatchEvent(new CustomEvent("logout", {bubbles: true, composed: true}))')
html = html.replace('this.loginWithGoogle', '(e) => this.dispatchEvent(new CustomEvent("login-google", {bubbles: true, composed: true}))')
html = html.replace('this.launchExperience', '(e) => this.dispatchEvent(new CustomEvent("launch-experience", {bubbles: true, composed: true}))')

ts_content = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ splashScreenStyles }} from './splash-screen.styles';

@customElement('splash-screen')
export class SplashScreen extends LitElement {{
  static styles = splashScreenStyles;

  @property({{ type: Object }}) currentUser: any = null;

  render() {{
    {html}
  }}
}}
"""

with open('./components/organisms/splash-screen.ts', 'w') as f:
    f.write(ts_content)

# Extract CSS
with open('./components/lyria_camera_styles.ts', 'r') as f:
    css_content = f.read()

classes = set()
ids = set()
for match in re.findall(r'class="([^"]+)"', html):
    classes.update(match.split())
for match in re.findall(r'id="([^"]+)"', html):
    ids.add(match)

extracted_css = []
blocks = css_content.split('}')
for block in blocks:
    if not block.strip() or '{' not in block: continue
    parts = block.split('{')
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

css = "\n".join(extracted_css)
with open('./components/organisms/splash-screen.styles.ts', 'w') as f:
    f.write(f"import {{ css }} from 'lit';\n\nexport const splashScreenStyles = css`\n{css}\n`;\n")

print("Generated splash-screen.ts")
