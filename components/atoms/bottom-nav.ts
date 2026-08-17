/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: UI Component for bottom-nav.ts
 */

import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { bottomNavStyles } from './bottom-nav.styles';

@customElement('bottom-nav')
export class BottomNav extends LitElement {
  static styles = bottomNavStyles;

  @property() page: any;
  @property() securityAlerts: any = [];

  render() {
    return html`
      <div id="bottom-nav-bar-web" style="position: relative;">
          <div class="nav-indicator" style="
            position: absolute;
            top: 0;
            left: ${this.page === 'main' ? '0%' : this.page === 'community' ? '25%' : this.page === 'android_flow' ? '50%' : '75%'};
            width: 25%;
            height: 100%;
            background: rgba(29, 185, 84, 0.15);
            border-bottom: 3px solid #1db954;
            transition: left 0.4s var(--spring-bounce, cubic-bezier(0.175, 0.885, 0.32, 1.275));
            pointer-events: none;
            z-index: 0;
          "></div>
          <button
            class="bottom-nav-tab-web ${this.page === "main" ? "active" : ""}"
            @click=${() => { this.dispatchEvent(new CustomEvent("nav-main", {bubbles: true, composed: true})) }}
          >
            <span class="material-icons-round">music_note</span>
            <span>Music Studio</span>
          </button>
          <button
            class="bottom-nav-tab-web ${this.page === "community" ? "active" : ""}"
            @click=${() => { this.dispatchEvent(new CustomEvent("nav-community", {bubbles: true, composed: true})) }}
          >
            <span class="material-icons-round">group</span>
            <span>Community</span>
          </button>
          <button
            class="bottom-nav-tab-web ${this.page === "android_flow" ? "active" : ""}"
            @click=${() => { this.dispatchEvent(new CustomEvent("nav-android", {bubbles: true, composed: true})) }}
          >
            <span class="material-icons-round">phone_android</span>
            <span>Companion</span>
          </button>
          <button
            class="bottom-nav-tab-web ${this.page === "security" ? "active" : ""}"
            @click=${() => { this.dispatchEvent(new CustomEvent("nav-security", {bubbles: true, composed: true})) }}
          >
            <span class="material-icons-round">security</span>
            <span>Security Hub</span>
            ${this.securityAlerts.length > 0 ? html`<span class="badge-count">${this.securityAlerts.length}</span>` : ""}
          </button>
      </div>
    `;
  }
}
