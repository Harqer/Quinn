with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

import re

# Fix AudioSynthService bindings
for m in ['playInstrumentSynth', 'updateInstrumentSynth', 'triggerBeatHaptic']:
    content = re.sub(r'this\.service_for\(' + m + r'\)', 'this.audioSynthService', content)

# Fix CanvasRendererService bindings
for m in ['drawSimulationFrame', 'drawFluidWave', 'renderGesturePadCanvas', 'renderVisualizerCanvas', 'getDominantFrequencyColors']:
    content = re.sub(r'this\.service_for\(' + m + r'\)', 'this.canvasRendererService', content)

# Fix LyriaApiService bindings
for m in ['startVoiceRecording', 'submitAndroidVibeCommand', 'submitTextCommand', 'generateFromFrame', 'shareVibeToCommunity']:
    content = re.sub(r'this\.service_for\(' + m + r'\)', 'this.lyriaApiService', content)

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)

print("Fixed service bindings")
