import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

match = re.search(r'^\s*private\s+renderMain\s*\(\)\s*\{', content, re.MULTILINE)
if not match:
    print("Could not find renderMain")
    exit(1)

start_idx = match.start()
brace_count, in_method, end_idx = 0, False, -1
for i in range(start_idx, len(content)):
    if content[i] == '{': brace_count += 1; in_method = True
    elif content[i] == '}':
        brace_count -= 1
        if in_method and brace_count == 0: end_idx = i + 1; break
        
main_code = content[start_idx:end_idx]

html_body = main_code.replace('private renderMain() {', '').rstrip()[:-1]

# Convert events
html_body = html_body.replace('this.switchFeed("webcam")', 'this.dispatchEvent(new CustomEvent("switch-feed", {detail: "webcam", bubbles: true, composed: true}))')
html_body = html_body.replace('this.switchFeed("simulation")', 'this.dispatchEvent(new CustomEvent("switch-feed", {detail: "simulation", bubbles: true, composed: true}))')
html_body = html_body.replace('this.setupCamera', '(e) => this.dispatchEvent(new CustomEvent("setup-camera", {bubbles: true, composed: true}))')
html_body = html_body.replace('this.togglePlayback', '(e) => this.dispatchEvent(new CustomEvent("toggle-playback", {bubbles: true, composed: true}))')

# Properties list
props = [
    'playbackState', 'feedType', 'cameraStream', 'videoTimeoutId',
    'videoLoading', 'videoLoadError', 'videoLoadErrorTitle', 'voiceStatus',
    'isRecordingVoice', 'voiceTranscript', 'geminiVoiceReply', 'isHapticsEnabled',
    'showGestureTutorial', 'currentInstrument', 'isGestureActive', 'gestureX',
    'gestureY', 'currentPlayingPitch', 'harmonicVoicing', 'prompts'
]

props_str = "\n".join([f"  @property() {p}: any;" for p in props])

ts_content = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ playerControlsStyles }} from './player-controls.styles';

import './chat-interface';
import './orchestrator-deck';

@customElement('player-controls')
export class PlayerControls extends LitElement {{
  static styles = playerControlsStyles;

{props_str}

  render() {{
    {html_body}
  }}
}}
"""

with open('./components/organisms/player-controls.ts', 'w') as f:
    f.write(ts_content)

# Extract CSS
with open('./components/lyria_camera_styles.ts', 'r') as f:
    css_content = f.read()

classes = set()
ids = set()
for match in re.findall(r'class="([^"]+)"', html_body):
    classes.update(match.split())
for match in re.findall(r'id="([^"]+)"', html_body):
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
with open('./components/organisms/player-controls.styles.ts', 'w') as f:
    f.write(f"import {{ css }} from 'lit';\n\nexport const playerControlsStyles = css`\n{css}\n`;\n")

print("Generated player-controls.ts")
