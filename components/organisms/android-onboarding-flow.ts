import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { androidOnboardingFlowStyles } from './android-onboarding-flow.styles';

@customElement('android-onboarding-flow')
export class AndroidOnboardingFlow extends LitElement {
  static styles = androidOnboardingFlowStyles;

  @property({ type: String }) step = 'email';
  @property({ type: String }) androidEmail = '';
  @property({ type: String }) androidPassword = '';
  @property({ type: String }) androidName = '';
  @property({ type: Boolean }) androidOptInNews = false;
  @property({ type: Boolean }) androidOptInShare = false;

  private handleEmailInput(e: any) { this.androidEmail = e.target.value; this.dispatchEvent(new CustomEvent('update-email', { detail: this.androidEmail, bubbles: true, composed: true })); }
  private handlePasswordInput(e: any) { this.androidPassword = e.target.value; this.dispatchEvent(new CustomEvent('update-password', { detail: this.androidPassword, bubbles: true, composed: true })); }
  private handleNameInput(e: any) { this.androidName = e.target.value; this.dispatchEvent(new CustomEvent('update-name', { detail: this.androidName, bubbles: true, composed: true })); }
  private handleOptInNews(e: any) { this.androidOptInNews = e.target.checked; this.dispatchEvent(new CustomEvent('update-opt-in-news', { detail: this.androidOptInNews, bubbles: true, composed: true })); }
  private handleOptInShare(e: any) { this.androidOptInShare = e.target.checked; this.dispatchEvent(new CustomEvent('update-opt-in-share', { detail: this.androidOptInShare, bubbles: true, composed: true })); }


  render() {
    if (this.step === 'email') return this.renderEmail();
    if (this.step === 'password') return this.renderPassword();
    if (this.step === 'name') return this.renderName();
    return html``;
  }

  private renderEmail() {
    

  
    return html`
      <div class="android-flow-inputs">
        <div class="android-flow-header">
          <button class="android-header-back" @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "welcome", bubbles: true, composed: true }))}>
            <span class="material-icons-round">arrow_back</span>
          </button>
          <span class="android-header-title">Create account</span>
        </div>
        
        <div class="input-form-container">
          <h2 class="form-title">What's your email?</h2>
          <input 
            type="email" 
            class="form-text-input" 
            placeholder="Email" 
            .value=${this.androidEmail}
            @input=${this.handleEmailInput}
          />
          <span class="form-subtext">You'll need to confirm this email later.</span>
          
          <button 
            class="form-next-btn ${this.androidEmail.includes("@") ? "active" : ""}"
            ?disabled=${!this.androidEmail.includes("@")}
            @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "password", bubbles: true, composed: true }))}
          >
            Next
          </button>
        </div>
      </div>
    `;
  
  }

  private renderPassword() {
    
  
    return html`
      <div class="android-flow-inputs">
        <div class="android-flow-header">
          <button class="android-header-back" @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "email", bubbles: true, composed: true }))}>
            <span class="material-icons-round">arrow_back</span>
          </button>
          <span class="android-header-title">Create account</span>
        </div>
        
        <div class="input-form-container">
          <h2 class="form-title">Create a password</h2>
          <input 
            type="password" 
            class="form-text-input" 
            placeholder="Password" 
            .value=${this.androidPassword}
            @input=${this.handlePasswordInput}
          />
          <span class="form-subtext">Use atleast 8 characters.</span>
          
          <button 
            class="form-next-btn ${this.androidPassword.length >= 8 ? "active" : ""}"
            ?disabled=${this.androidPassword.length < 8}
            @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "name", bubbles: true, composed: true }))}
          >
            Next
          </button>
        </div>
      </div>
    `;
  
  }

  private renderName() {
    
  
    return html`
      <div class="android-flow-inputs">
        <div class="android-flow-header">
          <button class="android-header-back" @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "password", bubbles: true, composed: true }))}>
            <span class="material-icons-round">arrow_back</span>
          </button>
          <span class="android-header-title">Create account</span>
        </div>
        
        <div class="input-form-container">
          <h2 class="form-title">What's your name?</h2>
          <div class="name-input-wrapper">
            <input 
              type="text" 
              class="form-text-input" 
              placeholder="Name" 
              .value=${this.androidName}
              @input=${this.handleNameInput}
            />
            ${this.androidName.length > 2 ? html`
              <span class="material-icons-round name-check-icon text-emerald-400">check_circle</span>
            ` : ""}
          </div>
          <span class="form-subtext">This appears on your Mave profile.</span>
          
          <div class="terms-agreement-block">
            <p class="terms-paragraph">
              By tapping on "Create account", you agree to the Mave <span class="text-emerald-400 font-bold underline cursor-pointer">Terms of Use</span>.
            </p>
            <p class="terms-paragraph mt-2">
              To learn more about how Mave collects, uses, shares and protects your personal data, please see the <span class="text-emerald-400 font-bold underline cursor-pointer">Privacy Policy</span>.
            </p>
          </div>
          
          <div class="checkbox-form-row">
            <input 
              type="checkbox" 
              id="opt-news" 
              .checked=${this.androidOptInNews}
              @change=${this.handleOptInNews}
            />
            <label for="opt-news">Please send me news and offers from Mave.</label>
          </div>
          
          <div class="checkbox-form-row">
            <input 
              type="checkbox" 
              id="opt-share" 
              .checked=${this.androidOptInShare}
              @change=${this.handleOptInShare}
            />
            <label for="opt-share">Share my registration data with Mave's content providers for marketing purposes.</label>
          </div>
          
          <button 
            class="form-next-btn active bg-white text-black font-bold mt-4" 
            @click=${() => {
              this.dispatchEvent(new CustomEvent("step-change", { detail: "search_home", bubbles: true, composed: true }));
              this.dispatchEvent(new CustomEvent("error", { detail: `Signed up successfully as ${this.androidName}!`, bubbles: true, composed: true }));
            }}
          >
            Create account
          </button>
        </div>
      </div>
    `;
  
  }

}
