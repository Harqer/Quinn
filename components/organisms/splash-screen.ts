import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { splashScreenStyles } from './splash-screen.styles';

@customElement('splash-screen')
export class SplashScreen extends LitElement {
  static styles = splashScreenStyles;

  @property({ type: Object }) currentUser: any = null;

  render() {
    
  
    return html`
      <div id="splash">
        <span class="material-icons-round splash-icon">video_camera_front</span>
        <h1 class="splash-title">Mave Camera Director</h1>
        <p class="splash-desc">
          Turn your camera feed into live generative music. Experience beautiful soundscapes evolving synchronously with your visual surroundings.
        </p>

        <!-- Cross-platform Mode Assurance Pill -->
        <div class="platform-badge mb-6">
          <span class="material-icons-round font-icon-green text-emerald-400">check_circle</span>
          <span>Web & Mobile Cameras Supported — Glasses Companion Optional</span>
        </div>

        <!-- Authentic Firebase Authentication Area -->
        <div class="auth-section">
          <div class="auth-title">Secure Developer Auth</div>
          ${this.currentUser ? html`
            <div class="auth-user-info">
              ${this.currentUser.photoURL ? html`
                <img src="${this.currentUser.photoURL}" class="auth-avatar" alt="Avatar" />
              ` : html`
                <span class="material-icons-round auth-placeholder-avatar">account_circle</span>
              `}
              <div class="auth-details">
                <div class="auth-name">${this.currentUser.displayName || "Authorized Developer"}</div>
                <div class="auth-email">${this.currentUser.email || "Firebase Session Active"}</div>
              </div>
            </div>
            <button class="auth-btn-signout" @click=${(e) => this.dispatchEvent(new CustomEvent("logout", {bubbles: true, composed: true}))}>
              Sign Out
            </button>
          ` : html`
            <div class="auth-email" style="margin-bottom: 0.25rem;">Enterprise Role Authentication Required</div>
            <button class="auth-btn-google" @click=${(e) => this.dispatchEvent(new CustomEvent("login-google", {bubbles: true, composed: true}))}>
              <span class="material-icons-round">login</span>
              Sign In with Google
            </button>
          `}
        </div>

        <button class="splash-btn" @click=${(e) => this.dispatchEvent(new CustomEvent("launch-experience", {bubbles: true, composed: true}))}>
          Launch Experience
        </button>
      </div>
    `;
  
  }
}
