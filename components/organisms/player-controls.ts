/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: UI Component for player-controls.ts
 */

import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { playerControlsStyles } from './player-controls.styles';

import './chat-interface';
import './orchestrator-deck';

@customElement('player-controls')
export class PlayerControls extends LitElement {
  static styles = playerControlsStyles;

  @property() playbackState: any;
  @property() feedType: any;
  @property() cameraStream: any;
  @property() videoTimeoutId: any;
  @property() videoLoading: any;
  @property() videoLoadError: any;
  @property() videoLoadErrorTitle: any;
  @property() voiceStatus: any;
  @property() isRecordingVoice: any;
  @property() voiceTranscript: any;
  @property() geminiVoiceReply: any;
  @property() isHapticsEnabled: any;
  @property() showGestureTutorial: any;
  @property() currentInstrument: any;
  @property() isGestureActive: any;
  @property() gestureX: any;
  @property() gestureY: any;
  @property() currentPlayingPitch: any;
  @property() harmonicVoicing: any;
  @property() prompts: any;

  render() {
    

  
    const isPlaying = this.playbackState === "playing";

    return html`
      <div id="feed-switcher">
        ${isPlaying ? html`
          <div id="music-radar-badge">
            <span class="radar-dot animate-pulse"></span>
            <span class="radar-text">Camera Audio Active</span>
            <div class="audio-mini-waves">
              <span class="wave-bar w-1 animate-eq"></span>
              <span class="wave-bar w-2 animate-eq" style="animation-delay: 0.15s;"></span>
              <span class="wave-bar w-3 animate-eq" style="animation-delay: 0.3s;"></span>
              <span class="wave-bar w-4 animate-eq" style="animation-delay: 0.45s;"></span>
            </div>
          </div>
        ` : ""}
        <button
          class="switcher-btn ${this.feedType === "webcam" ? "active" : ""}"
          @click=${() => this.dispatchEvent(new CustomEvent("switch-feed", {detail: "webcam", bubbles: true, composed: true}))}
        >
          <span class="material-icons-round">videocam</span>
          Webcam
        </button>
        <button
          class="switcher-btn ${this.feedType === "simulation" ? "active" : ""}"
          @click=${() => this.dispatchEvent(new CustomEvent("switch-feed", {detail: "simulation", bubbles: true, composed: true}))}
        >
          <span class="material-icons-round">auto_awesome</span>
          Cosmic Feed
        </button>
      </div>

      <div id="video-container" class="${isPlaying ? "music-active" : ""}">
        <canvas id="music-visualizer-canvas"></canvas>
        ${this.feedType === "webcam"
          ? html`
              <video 
                .srcObject=${this.cameraStream} 
                autoplay 
                playsinline 
                muted 
                @loadedmetadata=${(e: Event) => {
                  console.log("[CAMERA] loadedmetadata event successfully triggered.");
                  if (this.videoTimeoutId) {
                    window.clearTimeout(this.videoTimeoutId);
                    this.videoTimeoutId = null;
                  }
                  this.videoLoading = false;
                  this.videoLoadError = null;
                  
                  const video = e.currentTarget as HTMLVideoElement;
                  video.play().catch(err => {
                    console.error("[CAMERA_DIAGNOSTICS] autoplay/play blocked by browser sandbox or permissions:", err);
                    this.videoLoadError = "Video playback was blocked by browser autoplay rules. Click Retry or tap to initiate stream play.";
                  });
                }}
                @error=${(e: Event) => {
                  const video = e.currentTarget as HTMLVideoElement;
                  console.error(`[CAMERA_DIAGNOSTICS] HTMLVideoElement error event triggered. Error code: ${video.error?.code ?? "unknown"}, Message: ${video.error?.message ?? "unknown"}`);
                  if (this.videoTimeoutId) {
                    window.clearTimeout(this.videoTimeoutId);
                    this.videoTimeoutId = null;
                  }
                  this.videoLoading = false;
                  this.videoLoadError = `The browser media engine encountered a critical error decoding/rendering the video feed (Code: ${video.error?.code ?? "unknown"}).`;
                }}
                style="transform: scaleX(-1);"
              ></video>

              ${this.videoLoading
                ? html`
                    <div class="video-feedback-overlay">
                      <div class="feedback-loading-spinner"></div>
                      <div class="feedback-title info">
                        <span class="material-icons-round feedback-btn-icon spin">sync</span>
                        Initializing Camera Stream
                      </div>
                      <div class="feedback-desc">Connecting stream and fetching metadata. Please allow camera access permissions if prompted...</div>
                    </div>
                  `
                : ""}

              ${this.videoLoadError
                ? html`
                    <div class="video-feedback-overlay">
                      <span class="material-icons-round feedback-icon">error_outline</span>
                      <div class="feedback-title">${this.videoLoadErrorTitle || "Camera Stream Failed"}</div>
                      <div class="feedback-desc">${this.videoLoadError}</div>
                      <div class="btn-group">
                        <button class="feedback-btn" @click=${(e) => this.dispatchEvent(new CustomEvent("setup-camera", {bubbles: true, composed: true}))}>
                          <span class="material-icons-round feedback-btn-icon">refresh</span>
                          Retry Setup
                        </button>
                        <button class="feedback-btn" @click=${() => this.dispatchEvent(new CustomEvent("switch-feed", {detail: "simulation", bubbles: true, composed: true}))}>
                          <span class="material-icons-round feedback-btn-icon">auto_awesome</span>
                          Use Cosmic Feed
                        </button>
                      </div>
                    </div>
                  `
                : ""}
            `
          : html`<canvas id="simulation-canvas"></canvas>`}
      </div>
      <div id="overlay">
        <!-- Voice & Text Communication & Control Panel -->
        <chat-interface
          .voiceStatus=${this.voiceStatus}
          .isRecordingVoice=${this.isRecordingVoice}
          .voiceTranscript=${this.voiceTranscript}
          .geminiVoiceReply=${this.geminiVoiceReply}
        ></chat-interface>
        <!-- Jacob Collier-style Interactive Orchestrator -->
        <orchestrator-deck
          .isHapticsEnabled=${this.isHapticsEnabled}
          .showGestureTutorial=${this.showGestureTutorial}
          .currentInstrument=${this.currentInstrument}
          .isGestureActive=${this.isGestureActive}
          .gestureX=${this.gestureX}
          .gestureY=${this.gestureY}
          .currentPlayingPitch=${this.currentPlayingPitch}
          .harmonicVoicing=${this.harmonicVoicing}
        ></orchestrator-deck>
        <div id="prompts-container">
          ${this.prompts.map(
            (p) => html`
              <div class="prompt-tag">
                ${p.text}
              </div>
            `
          )}
        </div>
        <div id="controls">
          <button
            class="action-btn ${isPlaying ? "stop" : "play"}"
            @click=${(e) => this.dispatchEvent(new CustomEvent("toggle-playback", {bubbles: true, composed: true}))}
          >
            <span class="material-icons-round">
              ${isPlaying ? "stop" : "play_arrow"}
            </span>
            ${isPlaying ? "Stop Music" : "Start Music"}
          </button>
        </div>
      </div>
    `;
  
  }
}
