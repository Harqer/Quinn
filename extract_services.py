import re
import os

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

def extract_method(method_name):
    match = re.search(r'^\s*(?:private\s+)?(?:async\s+)?' + method_name + r'\s*\([^)]*\)\s*(?::\s*[a-zA-Z0-9_<>]+)?\s*\{', content, re.MULTILINE)
    if not match: return None
    start_idx = match.start()
    brace_count, in_method, end_idx = 0, False, -1
    for i in range(start_idx, len(content)):
        if content[i] == '{': brace_count += 1; in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0: end_idx = i + 1; break
    return content[start_idx:end_idx] if end_idx != -1 else None

audio_methods = ['playInstrumentSynth', 'updateInstrumentSynth', 'triggerBeatHaptic']
canvas_methods = ['drawSimulationFrame', 'drawFluidWave', 'renderGesturePadCanvas', 'renderVisualizerCanvas', 'getDominantFrequencyColors']
api_methods = ['startVoiceRecording', 'submitAndroidVibeCommand', 'submitTextCommand', 'generateFromFrame', 'shareVibeToCommunity']

os.makedirs('./services', exist_ok=True)

with open('./services/audio_methods.txt', 'w') as f:
    for m in audio_methods:
        code = extract_method(m)
        if code: f.write(code + '\n\n')

with open('./services/canvas_methods.txt', 'w') as f:
    for m in canvas_methods:
        code = extract_method(m)
        if code: f.write(code + '\n\n')

with open('./services/api_methods.txt', 'w') as f:
    for m in api_methods:
        code = extract_method(m)
        if code: f.write(code + '\n\n')

print("Extracted methods for services")
