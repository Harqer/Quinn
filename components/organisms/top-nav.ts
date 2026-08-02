import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { topNavStyles } from './top-nav.styles';

@customElement('top-nav')
export class TopNav extends LitElement {
  static styles = topNavStyles;

  @property() wearableActive: any;
  @property() page: any;
  @property() securityAlerts: any = [];

  render() {
    return html`
      <div id="top-nav-bar">
          <div class="brand">
            <span class="material-icons-round brand-icon text-indigo-400 animate-pulse">auto_awesome</span>
            <span class="brand-name">Mave Director</span>
            <div class="status-pill ${this.wearableActive ? "wearable" : "standard"}">
              <span class="status-dot"></span>
              <span class="status-text">
                ${this.wearableActive ? "Wearables Integrated" : "Browser Webcam Mode"}
              </span>
            </div>
          </div>
          <div class="nav-tabs">
            <button
              class="nav-tab ${this.page === "main" ? "active" : ""}"
              @click=${() => { this.dispatchEvent(new CustomEvent("nav-main", {bubbles: true, composed: true})) }}
            >
              <span class="material-icons-round">music_note</span>
              Music Studio
            </button>
            <button
              class="nav-tab ${this.page === "community" ? "active" : ""}"
              @click=${() => { this.dispatchEvent(new CustomEvent("nav-community", {bubbles: true, composed: true})) }}
            >
              <span class="material-icons-round">group</span>
              Community
            </button>
            <button
              class="nav-tab ${this.page === "android_flow" ? "active" : ""}"
              @click=${() => { this.dispatchEvent(new CustomEvent("nav-android", {bubbles: true, composed: true})) }}
            >
              <span class="material-icons-round">phone_android</span>
              Android Companion App
            </button>
            <button
              class="nav-tab ${this.page === "security" ? "active" : ""}"
              @click=${() => { this.dispatchEvent(new CustomEvent("nav-security", {bubbles: true, composed: true})) }}
            >
              <span class="material-icons-round">security</span>
              Security Hub
              ${this.securityAlerts.length > 0 ? html`<span class="badge-count">${this.securityAlerts.length}</span>` : ""}
            </button>
          </div>
        </div>
    `;
  }
}
