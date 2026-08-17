/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: UI Component for android-search-home.ts
 */

import { LitElement, html, nothing } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { AndroidSearchHomeStyles } from './android-search-home.styles';

@customElement('android-search-home')
export class AndroidSearchHome extends LitElement {
  static styles = AndroidSearchHomeStyles;

  @property() toggleAndroidCamera: any;
  @property() isHapticsEnabled: any;
  @property() isAndroidCameraActive: any;

  render() {
    

  
    return html`
      <div class="android-flow-search">
        <div class="android-search-header-row">
          <h1 class="search-title-large">Search</h1>
          <div style="display: flex; gap: 8px; align-items: center;">
            <button class="go-live-pulsing-btn" @click=${() => { this.dispatchEvent(new CustomEvent("step-change", { detail: "go_live", bubbles: true, composed: true })); if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate([20, 30, 50]); }}>
              <span class="pulsing-dot-red"></span>
              <span>Go Live</span>
            </button>
            <button class="android-search-camera-btn" @click=${this.toggleAndroidCamera} title="Connect Glasses Camera stream">
              <span class="material-icons-round text-white font-icon-large">${this.isAndroidCameraActive ? "videocam" : "videocam_off"}</span>
            </button>
          </div>
        </div>
        
        <div class="search-input-mock-container" @click=${() => this.dispatchEvent(new CustomEvent("step-change", { detail: "search_results", bubbles: true, composed: true }))}>
          <span class="material-icons-round search-mock-icon">search</span>
          <input 
            type="text" 
            class="search-mock-field" 
            placeholder="Artists, songs, or podcasts" 
            readonly
          />
        </div>
        
        <div class="search-browse-section">
          <h3 class="browse-section-title">Your top genres</h3>
          <div class="browse-grid-2">
            <div class="genre-card pop bg-purple-600" @click=${() => this.dispatchEvent(new CustomEvent("play-song", { detail: ["Pop Mix", "Various Artists", "", "pop vibes"], bubbles: true, composed: true }))}>
              <span>Pop</span>
            </div>
            
            <div class="genre-card indie bg-emerald-600" @click=${() => this.dispatchEvent(new CustomEvent("play-song", { detail: ["Indie Mix", "Various Artists", "", "indie vibes"], bubbles: true, composed: true }))}>
              <span>Indie</span>
            </div>
          </div>
          
          <h3 class="browse-section-title mt-4">Popular podcast categories</h3>
          <div class="browse-grid-2">
            <div class="genre-card bg-blue-800" style="background-color: #1e326f;">
              <span>News & Politics</span>
            </div>
            <div class="genre-card bg-rose-600" style="background-color: #e81156;">
              <span>Comedy</span>
            </div>
          </div>
          
          <h3 class="browse-section-title mt-4">Browse all</h3>
          <div class="browse-grid-2">
            <div class="genre-card bg-teal-700" style="background-color: #1e326f;">
              <span>Made for you</span>
            </div>
            <div class="genre-card bg-pink-700" style="background-color: #8d1212;">
              <span>Charts</span>
            </div>
            <div class="genre-card bg-indigo-700" style="background-color: #e1118c;">
              <span>New Releases</span>
            </div>
            <div class="genre-card bg-amber-700" style="background-color: #503750;">
              <span>Discover</span>
            </div>
          </div>
        </div>
      </div>
    `;
  
  }
}
