import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('instrument-toolbar')
export class InstrumentToolbar extends LitElement {
  static override styles = css`
    :host {
      display: flex;
      gap: 8px;
      padding: 8px 12px;
      background: rgba(20, 20, 30, 0.85);
      backdrop-filter: blur(12px);
      border-radius: 20px;
      border: 1px solid rgba(255, 255, 255, 0.1);
    }
    button {
      background: transparent;
      border: none;
      color: #a0a0b0;
      padding: 6px 12px;
      border-radius: 12px;
      cursor: pointer;
      font-weight: 600;
      transition: all 0.2s ease;
    }
    button.active {
      background: #00ff88;
      color: #000;
    }
    button:hover:not(.active) {
      color: #fff;
      background: rgba(255, 255, 255, 0.05);
    }
  `;

  @property({ type: String }) selectedInstrument = 'synth';
  @property({ type: Array }) instruments = ['synth', 'bass', 'drums', 'lead'];

  private selectInstrument(inst: string) {
    this.selectedInstrument = inst;
    this.dispatchEvent(new CustomEvent('instrument-change', { detail: { instrument: inst }, bubbles: true, composed: true }));
  }

  override render() {
    return html`
      ${this.instruments.map(
        inst => html`
          <button
            class=${this.selectedInstrument === inst ? 'active' : ''}
            @click=${() => this.selectInstrument(inst)}
          >
            ${inst.toUpperCase()}
          </button>
        `
      )}
    `;
  }
}