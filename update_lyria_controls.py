import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

# Add import
content = re.sub(r'(import \{ LitElement)', "import './organisms/player-controls';\n" + r'\1', content, count=1)

# Remove import of chat-interface and orchestrator-deck from lyria_camera.ts if present
content = re.sub(r"import './organisms/chat-interface';\n", "", content)
content = re.sub(r"import './organisms/orchestrator-deck';\n", "", content)

# Remove method renderMain
match = re.search(r'^\s*private\s+renderMain\s*\(\)\s*\{', content, re.MULTILINE)
if match:
    start_idx = match.start()
    brace_count, in_method, end_idx = 0, False, -1
    for i in range(start_idx, len(content)):
        if content[i] == '{': brace_count += 1; in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0: end_idx = i + 1; break
    if end_idx != -1:
        content = content[:start_idx] + content[end_idx:]

props_to_pass = [
    'playbackState', 'feedType', 'cameraStream', 'videoTimeoutId',
    'videoLoading', 'videoLoadError', 'videoLoadErrorTitle', 'voiceStatus',
    'isRecordingVoice', 'voiceTranscript', 'geminiVoiceReply', 'isHapticsEnabled',
    'showGestureTutorial', 'currentInstrument', 'isGestureActive', 'gestureX',
    'gestureY', 'currentPlayingPitch', 'harmonicVoicing', 'prompts'
]

props_str = "\n".join([f"          .{p}=${{this.{p}}}" for p in props_to_pass])

player_controls_tag = f"""<player-controls
{props_str}
        ></player-controls>"""

content = content.replace('this.renderMain()', player_controls_tag)

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)
print("Updated player-controls in lyria_camera.ts")
