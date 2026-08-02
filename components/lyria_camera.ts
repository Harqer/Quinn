import { LitElement, html, css } from 'lit';
import { customElement, property, state } from 'lit/decorators.js';
import './atoms/CameraStreamPreview';
import './atoms/GestureCanvas';
import './molecules/InstrumentToolbar';

@customElement('lyria-camera')
export class LyriaCamera extends LitElement {
  static override styles = css`
    :host {
      display: block;
      position: relative;
      width: 100%;
      height: 100%;
      background: #000;
      overflow: hidden;
    }
    .toolbar-overlay {
      position: absolute;
      bottom: 24px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 10;
    }
  `;

  @property({ type: Boolean }) isStreaming = false;
  @state() private mediaStream: MediaStream | null = null;
  @state() private gesturePoints: { x: number; y: number }[] = [];
  @state() private selectedInstrument = 'synth';

  override connectedCallback() {
    super.connectedCallback();
    this.startCameraStream();
  }

  override disconnectedCallback() {
    super.disconnectedCallback();
    this.stopCameraStream();
  }

  private async startCameraStream() {
    try {
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        this.mediaStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        this.isStreaming = true;
      }
    } catch (err) {
      console.warn('Failed to access camera/mic stream:', err);
    }
  }

  private stopCameraStream() {
    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach(track => track.stop());
      this.mediaStream = null;
      this.isStreaming = false;
    }
  }

  private handleInstrumentChange(e: CustomEvent) {
    this.selectedInstrument = e.detail.instrument;
  }

  override render() {
    return html`
      <camera-stream-preview .mediaStream=${this.mediaStream} .isStreaming=${this.isStreaming}></camera-stream-preview>
      <gesture-canvas .points=${this.gesturePoints}></gesture-canvas>
      <div class="toolbar-overlay">
        <instrument-toolbar
          .selectedInstrument=${this.selectedInstrument}
          @instrument-change=${this.handleInstrumentChange}
        ></instrument-toolbar>
      </div>
    `;
  }
}
