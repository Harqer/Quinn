import re

with open('./components/organisms/renderMain.txt', 'r') as f:
    html = f.read()

# Extract voice-card
voice_match = re.search(r'<div class="voice-card">.*?(?=<!-- Jacob Collier-style Interactive Orchestrator -->)', html, re.DOTALL)
if voice_match:
    voice_html = voice_match.group(0).strip()
    
    # Process voice_html
    voice_html = voice_html.replace('this.toggleVoiceRecording', '(e) => this.dispatchEvent(new CustomEvent("toggle-voice-recording", {bubbles: true, composed: true}))')
    voice_html = voice_html.replace('this.submitTextCommand', '(e) => this.dispatchEvent(new CustomEvent("submit-text-command", {detail: (document.getElementById("text-vibe-input") as HTMLInputElement)?.value, bubbles: true, composed: true}))')
    
    voice_ts = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ chatInterfaceStyles }} from './chat-interface.styles';

@customElement('chat-interface')
export class ChatInterface extends LitElement {{
  static styles = chatInterfaceStyles;

  @property() voiceStatus = "standby";
  @property() isRecordingVoice = false;
  @property() voiceTranscript = "";
  @property() geminiVoiceReply = "";

  render() {{
    return html`
      {voice_html}
    `;
  }}
}}
"""
    with open('./components/organisms/chat-interface.ts', 'w') as f:
        f.write(voice_ts)

# Extract orchestrator-card
orch_match = re.search(r'<div class="orchestrator-card">.*?(?=<div id="prompts-container">)', html, re.DOTALL)
if orch_match:
    orch_html = orch_match.group(0).strip()
    
    # Process orch_html
    orch_html = orch_html.replace('this.isHapticsEnabled = !this.isHapticsEnabled;', 'this.dispatchEvent(new CustomEvent("toggle-haptics", {bubbles: true, composed: true}));')
    orch_html = orch_html.replace('this.showGestureTutorial = !this.showGestureTutorial', 'this.dispatchEvent(new CustomEvent("toggle-tutorial", {bubbles: true, composed: true}))')
    orch_html = orch_html.replace('this.switchInstrument("piano")', 'this.dispatchEvent(new CustomEvent("switch-instrument", {detail: "piano", bubbles: true, composed: true}))')
    orch_html = orch_html.replace('this.switchInstrument("clarinet")', 'this.dispatchEvent(new CustomEvent("switch-instrument", {detail: "clarinet", bubbles: true, composed: true}))')
    orch_html = orch_html.replace('this.switchInstrument("violin")', 'this.dispatchEvent(new CustomEvent("switch-instrument", {detail: "violin", bubbles: true, composed: true}))')
    orch_html = orch_html.replace('this.switchInstrument("chimes")', 'this.dispatchEvent(new CustomEvent("switch-instrument", {detail: "chimes", bubbles: true, composed: true}))')
    
    orch_html = orch_html.replace('this.handleGestureStart', 'this.handleGestureStartWrapper')
    orch_html = orch_html.replace('this.handleGestureMove', 'this.handleGestureMoveWrapper')
    orch_html = orch_html.replace('this.handleGestureEnd', 'this.handleGestureEndWrapper')
    orch_html = orch_html.replace('this.handleGestureWheel', 'this.handleGestureWheelWrapper')
    
    orch_html = orch_html.replace('this.harmonicVoicing = parseFloat(e.target.value);\n                  this.updateInstrumentSynth();', 'this.dispatchEvent(new CustomEvent("update-voicing", {detail: parseFloat((e.target as HTMLInputElement).value), bubbles: true, composed: true}))')
    
    orch_ts = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ orchestratorDeckStyles }} from './orchestrator-deck.styles';

@customElement('orchestrator-deck')
export class OrchestratorDeck extends LitElement {{
  static styles = orchestratorDeckStyles;

  @property() isHapticsEnabled = false;
  @property() showGestureTutorial = false;
  @property() currentInstrument = "piano";
  @property() isGestureActive = false;
  @property() gestureX = 0.5;
  @property() gestureY = 0.5;
  @property() currentPlayingPitch = "";
  @property() harmonicVoicing = 1.0;

  handleGestureStartWrapper(e: Event) {{ this.dispatchEvent(new CustomEvent("gesture-start", {{detail: e, bubbles: true, composed: true}})); }}
  handleGestureMoveWrapper(e: Event) {{ this.dispatchEvent(new CustomEvent("gesture-move", {{detail: e, bubbles: true, composed: true}})); }}
  handleGestureEndWrapper(e: Event) {{ this.dispatchEvent(new CustomEvent("gesture-end", {{detail: e, bubbles: true, composed: true}})); }}
  handleGestureWheelWrapper(e: Event) {{ this.dispatchEvent(new CustomEvent("gesture-wheel", {{detail: e, bubbles: true, composed: true}})); }}

  render() {{
    return html`
      {orch_html}
    `;
  }}
}}
"""
    with open('./components/organisms/orchestrator-deck.ts', 'w') as f:
        f.write(orch_ts)

print("Generated chat-interface.ts and orchestrator-deck.ts")
