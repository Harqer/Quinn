import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { androidCompanionViewStyles } from './android-companion-view.styles';

import './android-search-home';
import './android-search-results';
import './android-album-details';
import './android-community';
import './android-go-live';
import './android-library';
import './android-options-menu';

@customElement('android-companion-view')
export class AndroidCompanionView extends LitElement {
  static styles = androidCompanionViewStyles;

  @property() wearableOnHead: any;
  @property() wearableBattery: any;
  @property() androidFlowStep: any;
  @property() androidEmail: any;
  @property() androidPassword: any;
  @property() androidName: any;
  @property() androidOptInNews: any;
  @property() androidOptInShare: any;
  @property() isAndroidCameraActive: any;
  @property() cameraStream: any;
  @property() isAndroidPlayerExpanded: any;
  @property() androidActiveSongImage: any;
  @property() androidActiveSongTitle: any;
  @property() androidActiveSongArtist: any;
  @property() playbackState: any;
  @property() isHapticsEnabled: any;
  @property() androidActiveSongCover: any;
  @property() androidSongProgress: any;
  @property() isAndroidPlaying: any;
  @property() androidLikedSongs: any;
  @property() isAndroidOptionsMenuOpen: any;

  render() {
    
  
    return html`
      <div class="android-simulation-container">
        <div class="android-phone-frame">
          <div class="android-status-bar">
            <span class="android-time">10:42</span>
            <div style="display: flex; align-items: center; gap: 4px; font-size: 10px; color: #a1a1aa; font-weight: 700;">
              ${this.wearableOnHead ? html`<span class="material-icons-round text-emerald-400" style="font-size: 11px;" title="Glasses On Head">face</span>` : html`<span class="material-icons-round text-zinc-500" style="font-size: 11px;" title="Glasses Off Head">person_off</span>`}
              <span>${this.wearableBattery}%</span>
            </div>
            <div class="android-status-icons">
              <span class="material-icons-round font-icon-small">bluetooth</span>
              <span class="material-icons-round font-icon-small">wifi</span>
              <span class="material-icons-round font-icon-small">battery_full</span>
            </div>
          </div>
          
          <div class="android-phone-screen">
            ${this.androidFlowStep === "welcome" ? html`<android-welcome @step-change=${(e: any) => this.androidFlowStep = e.detail}></android-welcome>` : ""}
            ${["email", "password", "name"].includes(this.androidFlowStep) ? html`<android-onboarding-flow
              .step=${this.androidFlowStep}
              .androidEmail=${this.androidEmail}
              .androidPassword=${this.androidPassword}
              .androidName=${this.androidName}
              .androidOptInNews=${this.androidOptInNews}
              .androidOptInShare=${this.androidOptInShare}
              @step-change=${(e: any) => this.androidFlowStep = e.detail}
              @update-email=${(e: any) => this.androidEmail = e.detail}
              @update-password=${(e: any) => this.androidPassword = e.detail}
              @update-name=${(e: any) => this.androidName = e.detail}
              @update-opt-in-news=${(e: any) => this.androidOptInNews = e.detail}
              @update-opt-in-share=${(e: any) => this.androidOptInShare = e.detail}
            ></android-onboarding-flow>` : ""}
            ${this.androidFlowStep === "search_home" ? html`<android-search-home></android-search-home>` : ""}
            ${this.androidFlowStep === "search_results" ? html`<android-search-results></android-search-results>` : ""}
            ${this.androidFlowStep === "album_details" ? html`<android-album-details></android-album-details>` : ""}
            ${this.androidFlowStep === "community" ? html`<android-community></android-community>` : ""}
            ${this.androidFlowStep === "go_live" ? html`<android-go-live></android-go-live>` : ""}
            ${this.androidFlowStep === "library" ? html`<android-library></android-library>` : ""}
          </div>
          
          <!-- Shared Bottom Navigation Bar (visible on home, search, album details, community, library) -->
          ${["search_home", "search_results", "album_details", "community", "library"].includes(this.androidFlowStep) ? html`
            <div class="android-mini-player-wrapper">
              ${this.isAndroidCameraActive ? html`
                <div class="android-glasses-preview animate-fade-in">
                  <div class="preview-badge">GLASSES POV LIVE STREAM</div>
                  <video .srcObject=${this.cameraStream} autoplay playsinline muted></video>
                </div>
              ` : ""}
              
              <div class="android-spotify-mini-player" @click=${() => this.isAndroidPlayerExpanded = true}>
                <div class="mini-player-left">
                  ${this.androidActiveSongImage === "default_album_cover" ? html`
                    <div style="background: #e3001a; width: 32px; height: 32px; border-radius: 4px; display: flex; justify-content: center; align-items: center; position: relative; flex-shrink: 0; box-shadow: 0 2px 4px rgba(0,0,0,0.3); margin-right: 8px;">
                      <div style="color: #fcd34d; font-size: 20px; font-weight: 950; line-height: 1;">1</div>
                    </div>
                  ` : html`
                    <img src="${this.androidActiveSongImage}" class="mini-player-art" />
                  `}
                  <div class="mini-player-info">
                    <span class="mini-player-title">${this.androidActiveSongTitle}</span>
                    <span class="mini-player-artist">
                      <span class="material-icons-round text-emerald-400 font-icon-tiny">bluetooth</span>
                      ${this.androidActiveSongArtist} • <span class="text-emerald-400 font-bold">RAYBAN META</span>
                    </span>
                  </div>
                </div>
                <div class="mini-player-right" @click=${(e: Event) => e.stopPropagation()}>
                  <button class="mini-player-action-btn" @click=${this.toggleAndroidCamera} title="Toggle POV Camera stream">
                    <span class="material-icons-round">${this.isAndroidCameraActive ? "videocam" : "videocam_off"}</span>
                  </button>
                  <button class="mini-player-action-btn play-pause-btn" @click=${this.togglePlayback}>
                    <span class="material-icons-round">${this.playbackState === "playing" ? "pause" : "play_arrow"}</span>
                  </button>
                </div>
              </div>
            </div>
            
            <div class="android-spotify-tabs">
              <button class="android-tab-btn ${this.androidFlowStep === 'search_home' || this.androidFlowStep === 'search_results' || this.androidFlowStep === 'album_details' ? 'active' : ''}" @click=${() => { this.androidFlowStep = "search_home"; this.isAndroidPlayerExpanded = false; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(6); }}>
                <span class="material-icons-round">search</span>
                <span>Search</span>
              </button>
              <button class="android-tab-btn ${this.androidFlowStep === 'community' || this.androidFlowStep === 'go_live' ? 'active' : ''}" @click=${() => { this.androidFlowStep = "community"; this.isAndroidPlayerExpanded = false; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(6); }}>
                <span class="material-icons-round">group</span>
                <span>Community</span>
              </button>
              <button class="android-tab-btn ${this.androidFlowStep === 'library' ? 'active' : ''}" @click=${() => { this.androidFlowStep = "library"; this.isAndroidPlayerExpanded = false; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(6); }}>
                <span class="material-icons-round">library_music</span>
                <span>Your Library</span>
              </button>
            </div>
          ` : ""}
          
          <!-- Expanded Player Overlay -->
          ${this.isAndroidPlayerExpanded ? html`
            <android-expanded-player
              .androidActiveSongTitle=${this.androidActiveSongTitle}
              .androidActiveSongArtist=${this.androidActiveSongArtist}
              .androidActiveSongCover=${this.androidActiveSongCover}
              .androidSongProgress=${this.androidSongProgress}
              .isAndroidPlaying=${this.isAndroidPlaying}
              .androidLikedSongs=${this.androidLikedSongs}
              @toggle-like=${(e: any) => this.toggleLikeSong(e.detail)}
              @play-previous=${() => this.playPreviousTrack()}
              @toggle-play=${() => this.toggleAndroidPlayPause()}
              @play-next=${() => this.playNextTrack()}
              @close-player=${() => this.isAndroidPlayerExpanded = false}
              @open-options=${() => this.isAndroidOptionsMenuOpen = true}
            ></android-expanded-player>
          ` : ""}

          <!-- Options Menu Bottom Sheet -->
          ${this.isAndroidOptionsMenuOpen ? html`<android-options-menu></android-options-menu>` : ""}

          <div class="android-navigation-bar">
            <span class="android-nav-dot" @click=${this.goBackAndroidStep}>◀</span>
            <span class="android-nav-dot" @click=${() => { this.androidFlowStep = "welcome"; this.isAndroidPlayerExpanded = false; }}>●</span>
            <span class="android-nav-dot" @click=${() => { this.androidFlowStep = "search_home"; this.isAndroidPlayerExpanded = false; }}>■</span>
          </div>
        </div>
        
        <!-- Interactive Control Deck explaining the integration -->
        <div class="android-explanation-deck">
          <h3><span class="material-icons-round">smartphone</span> Spotify-Style Android Wearables Companion</h3>
          <p>
            Experience the complete user lifecycle flow requested for the <strong>Ray-Ban Meta Wearables</strong> integration, matching the production Spotify UI styling.
          </p>
          
          <div class="integration-step-box">
            <div class="step-badge">FLOW STEP 1-3</div>
            <h4>Secure Authorization & Setup</h4>
            <p>Interactive welcome portal, credentials registration, and terms acceptance.</p>
          </div>
          
          <div class="integration-step-box">
            <div class="step-badge active">FLOW STEP 4-5</div>
            <h4>Orchestrated Search & Live POV Stream</h4>
            <p>Browse top genres or search for certified artists. Launching a track triggers the <strong>Gemini AI Orchestrator</strong> server-side route to compose live music on the fly!</p>
          </div>
          
          <div class="integration-step-box">
            <div class="step-badge haptic">HARDWARE TELEMETRY</div>
            <h4>Meta Wearables DAT Sync</h4>
            <p>
              Tapping the camera icon in Search displays the <strong>Glasses POV camera feed</strong> inside the mobile layout. Playback is fully integrated with on-head proximity haptics.
            </p>
          </div>
        </div>
      </div>
    `;
  
  }
}
