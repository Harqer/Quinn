/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: UI Component for orchestrator-deck.ts
 */

import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { orchestratorDeckStyles } from './orchestrator-deck.styles';

@customElement('orchestrator-deck')
export class OrchestratorDeck extends LitElement {
  static styles = orchestratorDeckStyles;

  @property() isHapticsEnabled = false;
  @property() showGestureTutorial = false;
  @property() currentInstrument = "piano";
  @property() isGestureActive = false;
  @property() gestureX = 0.5;
  @property() gestureY = 0.5;
  @property() currentPlayingPitch = "";
  @property() harmonicVoicing = 1.0;

  handleGestureStartWrapper(e: Event) { this.dispatchEvent(new CustomEvent("gesture-start", {detail: e, bubbles: true, composed: true})); }
  handleGestureMoveWrapper(e: Event) { this.dispatchEvent(new CustomEvent("gesture-move", {detail: e, bubbles: true, composed: true})); }
  handleGestureEndWrapper(e: Event) { this.dispatchEvent(new CustomEvent("gesture-end", {detail: e, bubbles: true, composed: true})); }
  handleGestureWheelWrapper(e: Event) { this.dispatchEvent(new CustomEvent("gesture-wheel", {detail: e, bubbles: true, composed: true})); }

  render() {
    return html`
      <div class="orchestrator-card">
          <div class="orchestrator-header">
            <h4 class="orchestrator-title">
              <span class="material-icons-round text-pink-400">music_note</span>
              Audience Orchestrator
            </h4>
            <div class="orchestrator-actions">
              <button 
                class="orchestrator-help-btn ${this.isHapticsEnabled ? "text-pink-400" : ""}"
                @click=${() => {
                  this.dispatchEvent(new CustomEvent("toggle-haptics", {bubbles: true, composed: true}));
                  if (this.isHapticsEnabled && navigator.vibrate) {
                    navigator.vibrate(20);
                  }
                }}
                title="${this.isHapticsEnabled ? "Disable Haptic Immersion" : "Enable Haptic Immersion"}"
                style="${this.isHapticsEnabled ? "color: #f472b6;" : ""}"
              >
                <span class="material-icons-round">${this.isHapticsEnabled ? "vibration" : "mobile_off"}</span>
              </button>
              <button 
                class="orchestrator-help-btn"
                @click=${() => this.dispatchEvent(new CustomEvent("toggle-tutorial", {bubbles: true, composed: true}))}
                title="Toggle Gesture Tutorial Guide"
              >
                <span class="material-icons-round">help_outline</span>
              </button>
              <span class="orchestrator-badge">Live Synth</span>
            </div>
          </div>

          <p class="orchestrator-description">
            Be the orchestrator like Jacob Collier. Tap and drag inside the coordinate grid to play real-time chords with hands. Adjust the harmonic voicing to stack octaves!
          </p>

          <!-- Instrument Selection Tabs -->
          <div class="orchestrator-sections">
            <button 
              class="orchestrator-section-btn ${this.currentInstrument === "piano" ? "active piano" : ""}" 
              @click=${() => this.dispatchEvent(new CustomEvent("switch-instrument", {detail: "piano", bubbles: true, composed: true}))}
              title="Acoustic Grand Piano"
            >
              <span class="material-icons-round section-icon">piano</span>
              <span class="section-label">Piano</span>
            </button>
            <button 
              class="orchestrator-section-btn ${this.currentInstrument === "clarinet" ? "active" : ""}" 
              @click=${() => this.dispatchEvent(new CustomEvent("switch-instrument", {detail: "clarinet", bubbles: true, composed: true}))}
              title="Concert Clarinet woodwind"
            >
              <span class="material-icons-round section-icon">air</span>
              <span class="section-label">Clarinet</span>
            </button>
            <button 
              class="orchestrator-section-btn ${this.currentInstrument === "violin" ? "active" : ""}" 
              @click=${() => this.dispatchEvent(new CustomEvent("switch-instrument", {detail: "violin", bubbles: true, composed: true}))}
              title="Orchestral Saw Strings"
            >
              <span class="material-icons-round section-icon">auto_stories</span>
              <span class="section-label">Strings</span>
            </button>
            <button 
              class="orchestrator-section-btn ${this.currentInstrument === "chimes" ? "active" : ""}" 
              @click=${() => this.dispatchEvent(new CustomEvent("switch-instrument", {detail: "chimes", bubbles: true, composed: true}))}
              title="Celestial Chimes"
            >
              <span class="material-icons-round section-icon">filter_vintage</span>
              <span class="section-label">Chimes</span>
            </button>
          </div>

          <!-- The Coordinate Gesture Interaction Pad -->
          <div 
            class="orchestrator-gesture-pad ${this.isGestureActive ? "active" : ""}"
            @mousedown=${this.handleGestureStartWrapper}
            @mousemove=${this.handleGestureMoveWrapper}
            @mouseup=${this.handleGestureEndWrapper}
            @mouseleave=${this.handleGestureEndWrapper}
            @touchstart=${this.handleGestureStartWrapper}
            @touchmove=${this.handleGestureMoveWrapper}
            @touchend=${this.handleGestureEndWrapper}
            @wheel=${this.handleGestureWheelWrapper}
          >
            <div class="gesture-pad-grid"></div>
            <canvas class="gesture-pad-canvas"></canvas>
            
            <!-- Crosshair Target -->
            <div 
              class="gesture-pad-target" 
              style="left: ${this.gestureX * 100}%; top: ${this.gestureY * 100}%;"
            ></div>

            <!-- Labels -->
            <div class="gesture-pad-label-x">Pitch / Frequency (C3 - C6)</div>
            <div class="gesture-pad-label-y">Timbre / Tone</div>
            <div class="gesture-pad-status">
              ${this.isGestureActive ? `PITCH: ${this.currentPlayingPitch}` : "TAP & DRAG TO PLAY"}
            </div>

            <!-- Swipe/Pinch Tutorial Interactive Overlay -->
            ${this.showGestureTutorial ? html`
              <div class="gesture-tutorial-overlay" @mousedown=${(e: Event) => e.stopPropagation()} @touchstart=${(e: Event) => e.stopPropagation()}>
                <div class="tutorial-path-animation">
                  <div class="tutorial-path-point start"></div>
                  <div class="tutorial-path-point end"></div>
                  <div class="tutorial-path-hand">
                    <span class="material-icons-round">touch_app</span>
                  </div>
                </div>
                <div class="gesture-tutorial-content">
                  <div class="tutorial-guide-title">
                    <span class="material-icons-round">school</span>
                    <span>Interactive Gesture Guide</span>
                  </div>
                  <ul class="tutorial-instructions">
                    <li>
                      <span class="instruction-bullet x">↔</span>
                      <span>Drag <strong>Left / Right</strong> to shift <strong>Pitch</strong></span>
                    </li>
                    <li>
                      <span class="instruction-bullet y">↕</span>
                      <span>Drag <strong>Up / Down</strong> to filter <strong>Tone/Timbre</strong></span>
                    </li>
                    <li>
                      <span class="instruction-bullet scroll">⤢</span>
                      <span><strong>Pinch</strong> or <strong>Scroll</strong> to adjust chord width</span>
                    </li>
                  </ul>
                  <button class="tutorial-got-it-btn" @click=${(e: Event) => { e.stopPropagation(); this.showGestureTutorial = false; }}>
                    Got it, play!
                  </button>
                </div>
              </div>
            ` : ""}
          </div>

          <!-- Harmonic Chord Sliders -->
          <div class="orchestrator-sliders">
            <div class="orchestrator-slider-row">
              <div class="orchestrator-slider-header">
                <span class="orchestrator-slider-label">Harmonic Chord Voicing Span</span>
                <span class="orchestrator-slider-value">x${this.harmonicVoicing.toFixed(1)}</span>
              </div>
              <input 
                type="range" 
                min="1.0" 
                max="5.0" 
                step="0.1" 
                class="orchestrator-slider-input" 
                .value=${this.harmonicVoicing}
                @input=${(e: any) => {
                  this.dispatchEvent(new CustomEvent("update-voicing", {detail: parseFloat((e.target as HTMLInputElement).value), bubbles: true, composed: true}))
                }}
              />
            </div>
          </div>

          <div class="orchestrator-footer-tip">
            <span class="material-icons-round text-pink-400 animate-pulse">pinch</span>
            <span>Tip: Scroll inside pad or pinch to change chord spacing!</span>
          </div>
        </div>
    `;
  }
}
