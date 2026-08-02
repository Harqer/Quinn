import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

# Add imports
imports = """import './organisms/chat-interface';
import './organisms/orchestrator-deck';
"""
content = re.sub(r'(import \{ LitElement)', imports + r'\1', content, count=1)

# Replace voice-card
content = re.sub(r'<div class="voice-card">.*?(?=<!-- Jacob Collier-style Interactive Orchestrator -->)', r'''<chat-interface
          .voiceStatus=${this.voiceStatus}
          .isRecordingVoice=${this.isRecordingVoice}
          .voiceTranscript=${this.voiceTranscript}
          .geminiVoiceReply=${this.geminiVoiceReply}
        ></chat-interface>\n        ''', content, flags=re.DOTALL)

# Replace orchestrator-card
content = re.sub(r'<div class="orchestrator-card">.*?(?=<div id="prompts-container">)', r'''<orchestrator-deck
          .isHapticsEnabled=${this.isHapticsEnabled}
          .showGestureTutorial=${this.showGestureTutorial}
          .currentInstrument=${this.currentInstrument}
          .isGestureActive=${this.isGestureActive}
          .gestureX=${this.gestureX}
          .gestureY=${this.gestureY}
          .currentPlayingPitch=${this.currentPlayingPitch}
          .harmonicVoicing=${this.harmonicVoicing}
        ></orchestrator-deck>\n        ''', content, flags=re.DOTALL)

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)

print("Updated lyria_camera.ts with chat-interface and orchestrator-deck")
