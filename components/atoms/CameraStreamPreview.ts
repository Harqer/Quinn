import { LitElement, html, css } from 'lit';
import { customElement, property, query } from 'lit/decorators.js';

@customElement('camera-stream-preview')
export class CameraStreamPreview extends LitElement {
  static override styles = css`
    :host {
      display: block;
      position: relative;
      width: 100%;
      height: 100%;
    }
    video {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 12px;
    }
  `;

  @property({ type: Object }) mediaStream: MediaStream | null = null;
  @property({ type: Boolean }) isStreaming = false;

  @query('video') videoElement!: HTMLVideoElement;

  override updated(changedProperties: Map<string, any>) {
    if (changedProperties.has('mediaStream') && this.videoElement) {
      if (this.mediaStream) {
        this.videoElement.srcObject = this.mediaStream;
        this.videoElement.play().catch(console.error);
      } else {
        this.videoElement.srcObject = null;
      }
    }
  }

  override render() {
    return html`
      <video autoplay playsinline muted></video>
    `;
  }
}