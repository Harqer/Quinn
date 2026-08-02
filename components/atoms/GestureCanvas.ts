import { LitElement, html, css } from 'lit';
import { customElement, property, query } from 'lit/decorators.js';

@customElement('gesture-canvas')
export class GestureCanvas extends LitElement {
  static override styles = css`
    :host {
      display: block;
      position: absolute;
      inset: 0;
      pointer-events: auto;
    }
    canvas {
      width: 100%;
      height: 100%;
      touch-action: none;
    }
  `;

  @property({ type: Array }) points: { x: number; y: number }[] = [];

  @query('canvas') canvasElement!: HTMLCanvasElement;
  private ctx: CanvasRenderingContext2D | null = null;

  override firstUpdated() {
    if (this.canvasElement) {
      this.ctx = this.canvasElement.getContext('2d');
      this.resizeCanvas();
      window.addEventListener('resize', () => this.resizeCanvas());
    }
  }

  private resizeCanvas() {
    if (this.canvasElement) {
      this.canvasElement.width = this.clientWidth;
      this.canvasElement.height = this.clientHeight;
      this.draw();
    }
  }

  override updated(changedProperties: Map<string, any>) {
    if (changedProperties.has('points')) {
      this.draw();
    }
  }

  private draw() {
    if (!this.ctx || !this.canvasElement) return;
    this.ctx.clearRect(0, 0, this.canvasElement.width, this.canvasElement.height);
    if (this.points.length < 2) return;

    this.ctx.beginPath();
    this.ctx.strokeStyle = '#00ff88';
    this.ctx.lineWidth = 4;
    this.ctx.lineCap = 'round';
    this.ctx.moveTo(this.points[0].x, this.points[0].y);
    for (let i = 1; i < this.points.length; i++) {
      this.ctx.lineTo(this.points[i].x, this.points[i].y);
    }
    this.ctx.stroke();
  }

  override render() {
    return html`<canvas></canvas>`;
  }
}