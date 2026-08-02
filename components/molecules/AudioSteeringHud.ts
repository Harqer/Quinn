import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('audio-steering-hud')
export class AudioSteeringHud extends LitElement {
  static override styles = css`
    :host {
      display: flex;
      flex-direction: column;
      gap: 6px;
      padding: 12px;
      background: rgba(10, 10, 20, 0.8);
      border-radius: 12px;
      border: 1px solid rgba(255, 255, 255, 0.1);
      color: #fff;
      font-family: monospace;
      font-size: 11px;
    }
    .meter-bar {
      height: 6px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 3px;
      overflow: hidden;
    }
    .meter-fill {
      height: 100%;
      background: linear-gradient(90deg, #00ff88, #00aaff);
      transition: width 0.1s linear;
    }
  `;

  @property({ type: Number }) bpm = 120;
  @property({ type: Number }) energy = 0.5;
  @property({ type: String }) currentKey = 'C Major';

  override render() {
    return html`
      <div>STEERING HUD</div>
      <div>BPM: ${this.bpm}</div>
      <div>KEY: ${this.currentKey}</div>
      <div>ENERGY: ${(this.energy * 100).toFixed(0)}%</div>
      <div class="meter-bar">
        <div class="meter-fill" style="width: ${this.energy * 100}%"></div>
      </div>
    `;
  }
}