/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: UI Component for android-welcome.ts
 */

import { LitElement, html } from 'lit';
import { customElement } from 'lit/decorators.js';
import { renderAndroidWelcomeStyles } from './renderAndroidWelcome.styles';

@customElement('android-welcome')
export class AndroidWelcome extends LitElement {
  static styles = renderAndroidWelcomeStyles;

  render() {
    
  
    return html`
      <div class="android-flow-welcome">
        <div class="android-spotify-logo-container">
          <span class="material-icons-round logo-soundwave text-emerald-400 font-icon-large">room_service</span>
          <span class="spotify-logo-text">Mave</span>
        </div>
        
        <h1 class="welcome-heading">Millions of Songs.<br/>Free on Mave.</h1>
        
        <div class="welcome-buttons-container">
          <button class="welcome-btn-primary" @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "email", bubbles: true, composed: true }))}>
            Sign up free
          </button>
          
          <button class="welcome-btn-secondary" @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "search_home", bubbles: true, composed: true }))}>
            Continue with Google
          </button>
          
          <button class="welcome-btn-secondary">
            Continue with Facebook
          </button>
          
          <button class="welcome-btn-secondary">
            Continue with Apple
          </button>
          
          <button class="welcome-btn-link" @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "search_home", bubbles: true, composed: true }))}>
            Log in
          </button>
        </div>
      </div>
    `;
  
  }
}
