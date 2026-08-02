import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { chatInterfaceStyles } from './chat-interface.styles';

@customElement('chat-interface')
export class ChatInterface extends LitElement {
  static styles = chatInterfaceStyles;

  @property() voiceStatus = "standby";
  @property() isRecordingVoice = false;
  @property() voiceTranscript = "";
  @property() geminiVoiceReply = "";

  render() {
    return html`
      <div class="voice-card">
          <div class="voice-card-header">
            <span class="material-icons-round voice-card-icon animate-pulse text-indigo-400">forum</span>
            <div class="voice-card-title-container">
              <h4 class="voice-card-title">Vocal & Text Director</h4>
              <p class="voice-card-subtitle">Natural language music control and custom vibes powered by Gemini Live TTS & Firestore bidirectional sync</p>
            </div>
            <div class="voice-status-badge status-${this.voiceStatus}">
              <span class="voice-status-dot"></span>
              <span class="voice-status-text">
                ${this.voiceStatus === "listening" ? "Listening..." :
                  this.voiceStatus === "processing" ? "Thinking..." :
                  this.voiceStatus === "speaking" ? "Speaking..." : "Standby"}
              </span>
            </div>
          </div>

          <div class="voice-card-body">
            <div class="mic-button-wrapper">
              <button 
                class="mic-button ${this.isRecordingVoice ? "recording" : ""} ${this.voiceStatus === "processing" ? "disabled" : ""}"
                ?disabled=${this.voiceStatus === "processing"}
                @click=${(e) => this.dispatchEvent(new CustomEvent("toggle-voice-recording", {bubbles: true, composed: true}))}
                title="${this.isRecordingVoice ? "Stop Recording" : "Start Recording Voice Command"}"
              >
                <span class="material-icons-round mic-icon">
                  ${this.isRecordingVoice ? "stop" : "mic"}
                </span>
              </button>
              <div class="mic-pulse-ring ring-1"></div>
              <div class="mic-pulse-ring ring-2"></div>
            </div>

            <div class="voice-transcript-area">
              ${this.voiceTranscript ? html`
                <div class="transcript-text">
                  <strong class="text-indigo-400">You:</strong> "${this.voiceTranscript}"
                </div>
              ` : html`
                <div class="transcript-placeholder">
                  Press the mic button and say, or type your vibe/command below:<br/>
                  <em class="text-indigo-300">"Play some relaxing lofi beats"</em> or <em class="text-indigo-300">"Stop the music"</em>
                </div>
              `}

              ${this.geminiVoiceReply ? html`
                <div class="ai-reply-text animate-fade-in">
                  <strong class="text-pink-400">Gemini:</strong> "${this.geminiVoiceReply}"
                </div>
              ` : ""}

              <div class="voice-text-input-row">
                <input 
                  type="text" 
                  id="text-vibe-input"
                  class="voice-text-field"
                  placeholder="Type custom vibe or command & press Enter..."
                  @keydown=${(e: KeyboardEvent) => {
                    if (e.key === "Enter") {
                      const inputVal = (this.shadowRoot?.querySelector("#text-vibe-input") as HTMLInputElement)?.value;
                      if (inputVal) {
                        this.dispatchEvent(new CustomEvent("submit-text-command", {detail: inputVal, bubbles: true, composed: true}));
                      }
                    }
                  }}
                  ?disabled=${this.voiceStatus === "processing"}
                />
                <button 
                  class="voice-text-submit-btn" 
                  @click=${() => {
                    const inputVal = (this.shadowRoot?.querySelector("#text-vibe-input") as HTMLInputElement)?.value;
                    if (inputVal) {
                      this.dispatchEvent(new CustomEvent("submit-text-command", {detail: inputVal, bubbles: true, composed: true}));
                    }
                  }}
                  ?disabled=${this.voiceStatus === "processing"}
                  title="Submit Vibe Command"
                >
                  <span class="material-icons-round">send</span>
                </button>
              </div>
            </div>
          </div>
        </div>
    `;
  }
}
