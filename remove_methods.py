import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

def remove_method(method_name, content):
    match = re.search(r'^\s*(?:private\s+)?(?:async\s+)?' + method_name + r'\s*\([^)]*\)\s*(?::\s*[a-zA-Z0-9_<>]+)?\s*\{', content, re.MULTILINE)
    if not match: return content
    start_idx = match.start()
    brace_count, in_method, end_idx = 0, False, -1
    for i in range(start_idx, len(content)):
        if content[i] == '{': brace_count += 1; in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0: end_idx = i + 1; break
    return content[:start_idx] + content[end_idx:] if end_idx != -1 else content

methods = [
    'playInstrumentSynth', 'updateInstrumentSynth', 'triggerBeatHaptic',
    'drawSimulationFrame', 'drawFluidWave', 'renderGesturePadCanvas', 'renderVisualizerCanvas', 'getDominantFrequencyColors',
    'startVoiceRecording', 'submitAndroidVibeCommand', 'submitTextCommand', 'generateFromFrame', 'shareVibeToCommunity'
]

for m in methods:
    content = remove_method(m, content)

# Add imports
imports = """import { AudioSynthService } from '../services/AudioSynthService';
import { CanvasRendererService } from '../services/CanvasRendererService';
import { LyriaApiService } from '../services/LyriaApiService';
"""
content = re.sub(r'(import \{ LitElement)', imports + r'\1', content, count=1)

# Add service instantiations to class properties
class_start = content.find('export class LyriaCamera extends LitElement {')
class_start = content.find('{', class_start) + 1

services = """
  private audioSynthService = new AudioSynthService();
  private canvasRendererService = new CanvasRendererService();
  private lyriaApiService = new LyriaApiService();
"""
content = content[:class_start] + services + content[class_start:]

# Replace this.method() with this.service.method(this, ...)
# For AudioSynthService
for m in ['playInstrumentSynth', 'updateInstrumentSynth', 'triggerBeatHaptic']:
    content = re.sub(r'this\.' + m + r'\(', f'this.audioSynthService.{m}(this, ', content)
    
# For CanvasRendererService
for m in ['drawSimulationFrame', 'drawFluidWave', 'renderGesturePadCanvas', 'renderVisualizerCanvas', 'getDominantFrequencyColors']:
    content = re.sub(r'this\.' + m + r'\(', f'this.canvasRendererService.{m}(this, ', content)
    
# For LyriaApiService
for m in ['startVoiceRecording', 'submitAndroidVibeCommand', 'submitTextCommand', 'generateFromFrame', 'shareVibeToCommunity']:
    content = re.sub(r'this\.' + m + r'\(', f'this.lyriaApiService.{m}(this, ', content)

# Also fix the event listeners if they are bound like @click=${this.method}
# Wait, replacing `this.method` is tricky if it's not a function call.
# I will do a basic replacement for the exact string `this.method` -> `() => this.service.method(this)`
for m in ['startVoiceRecording', 'submitAndroidVibeCommand', 'submitTextCommand', 'generateFromFrame', 'shareVibeToCommunity', 'playInstrumentSynth', 'updateInstrumentSynth', 'triggerBeatHaptic', 'drawSimulationFrame', 'drawFluidWave', 'renderGesturePadCanvas', 'renderVisualizerCanvas', 'getDominantFrequencyColors']:
    content = re.sub(r'@([a-z-]+)=\$\{this\.' + m + r'\}', f'@\\1=${{() => this.service_for(m).{m}(this)}}', content)

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)
print("Updated lyria_camera.ts with services")
