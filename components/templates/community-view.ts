/**
 * @AtomicLevel: Template
 * @SemanticPurpose: UI Component for community-view.ts
 */

import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { css } from 'lit';
const communityViewStyles = css`
  .community-viewport { width: 100%; height: 100%; display: flex; flex-direction: column; }
`;

@customElement('community-view')
export class CommunityView extends LitElement {
  static styles = communityViewStyles;

  @property({ type: Boolean }) communityTracksLoading = false;
  @property({ type: Array }) communityTracks: any[] = [];
  @property({ type: String }) activeCommunityTrackId = '';
  @property({ type: String }) playbackState = 'stopped';
  @property({ type: String }) shareTitleInput = '';
  @property({ type: String }) shareVibeInput = '';
  @property({ type: String }) voiceTranscript = '';

  render() {
      
    return html`
      <div class="community-viewport animate-fade-in">
        <div class="community-hero">
          <h1 class="community-hero-title">Mave Community Stage</h1>
          <p class="community-hero-desc">
            Vibe and co-create live with other artists! Play shared generative tracks or publish your own custom AI orchestrations directly to the public feed.
          </p>
        </div>

        <div class="community-grid-layout">
          <div class="tracks-feed-section">
            <div class="section-heading-row">
              <h3 class="section-title">
                <span class="material-icons-round text-indigo-400">explore</span>
                Live Public Feed
              </h3>
              <button class="refresh-btn-secondary" @click=${() => this.dispatchEvent(new CustomEvent("fetch-tracks", { bubbles: true, composed: true }))} ?disabled=${this.communityTracksLoading}>
                <span class="material-icons-round ${this.communityTracksLoading ? "spin" : ""}">sync</span>
                <span>Refresh Feed</span>
              </button>
            </div>

            ${this.communityTracksLoading && this.communityTracks.length === 0 ? html`
              <div style="display: flex; flex-direction: column; align-items: center; padding: 3rem; gap: 1rem;">
                <div class="feedback-loading-spinner"></div>
                <div style="color: #a1a1aa; font-size: 14px;">Tuning into the Community frequency...</div>
              </div>
            ` : html`
              <div class="community-tracks-list">
                ${this.communityTracks.map(track => {
                  const isPlayingThis = this.activeCommunityTrackId === track.id && this.playbackState === "playing";
                  return html`
                    <div class="community-track-card ${isPlayingThis ? "active-playing" : ""}" id="track-${track.id}">
                      <div class="track-art-wrapper">
                        <img class="track-cover-img" src="${track.imageUrl}" alt="${track.title}" />
                        <div class="track-play-overlay">
                          <button class="play-trigger-pill" @click=${() => {
                            this.dispatchEvent(new CustomEvent("play-track", { detail: { id: track.id, vibe: track.vibe }, bubbles: true, composed: true }));
                          }}>
                            <span class="material-icons-round">${isPlayingThis ? "pause" : "play_arrow"}</span>
                          </button>
                        </div>
                      </div>
                      <div class="track-details-row">
                        <span class="track-meta-title">${track.title}</span>
                        <span class="track-meta-artist">
                          <span class="material-icons-round text-indigo-400" style="font-size:14px;">person</span>
                          <span>Shared by ${track.artist}</span>
                        </span>
                      </div>
                      <div class="track-vibe-prompt">
                        "${track.vibe}"
                      </div>
                      <div class="track-footer-row">
                        <span style="display:flex; align-items:center; gap:3px;">
                          <span class="material-icons-round" style="font-size:12px;">schedule</span>
                          <span>${track.sharedBy === "Admin" || track.sharedBy === "Admin2" || track.sharedBy === "Admin3" ? "Featured" : "Vibe Artist"}</span>
                        </span>
                        <span>Active Orchestration</span>
                      </div>
                    </div>
                  `;
                })}
              </div>
            `}
          </div>

          <div class="creator-panel-section">
            <h3 class="creator-form-title">Publish Your Vibe</h3>
            
            <div class="form-group-custom">
              <label class="form-label-custom">Vibe Title</label>
              <input 
                type="text" 
                class="form-input-custom" 
                placeholder="Give your track a name..."
                .value=${this.shareTitleInput}
                @input=${(e: Event) => { this.dispatchEvent(new CustomEvent("update-title", { detail: (e.target as HTMLInputElement).value, bubbles: true, composed: true })); }}
              />
            </div>

            <div class="form-group-custom">
              <label class="form-label-custom">Music Prompt / Vibe Description</label>
              <textarea 
                class="form-input-custom" 
                rows="4"
                placeholder="e.g. ambient sunset synth with slow cinematic strings..."
                style="resize: none; font-family: sans-serif;"
                .value=${this.shareVibeInput}
                @input=${(e: Event) => { this.dispatchEvent(new CustomEvent("update-vibe", { detail: (e.target as HTMLTextAreaElement).value, bubbles: true, composed: true })); }}
              ></textarea>
              
              ${this.voiceTranscript ? html`
                <button class="use-current-badge animate-fade-in" @click=${() => {
                  this.dispatchEvent(new CustomEvent("use-transcript", { bubbles: true, composed: true }));
                }}>
                  <span class="material-icons-round" style="font-size: 12px;">auto_awesome</span>
                  <span>Use current active: "${this.voiceTranscript.length > 25 ? this.voiceTranscript.slice(0, 25) + "..." : this.voiceTranscript}"</span>
                </button>
              ` : ""}
            </div>

            <button 
              class="publish-vibe-btn" 
              ?disabled=${!this.shareTitleInput.trim() || !this.shareVibeInput.trim()}
              @click=${() => {
                this.dispatchEvent(new CustomEvent("share-vibe", { bubbles: true, composed: true }));
              }}
            >
              <span class="material-icons-round">publish</span>
              <span>Publish Vibe to Stage</span>
            </button>
          </div>
        </div>
      </div>
    `;
  
  }
}
