import { LitElement, html, nothing } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { AndroidCommunityStyles } from './android-community.styles';

@customElement('android-community')
export class AndroidCommunity extends LitElement {
  static styles = AndroidCommunityStyles;

  @property() dispatchError: any;
  @property() isHapticsEnabled: any;
  @property() liveStreamStats: any;
  @property() communityComments: any;

  render() {
      
    return html`
      <div class="android-flow-community animate-fade-in">
        <div class="community-header-row">
          <h1 class="community-title-large">Creator Stage</h1>
          <button class="go-live-pulsing-btn" @click=${() => { this.dispatchEvent(new CustomEvent("step-change", { detail: "go_live", bubbles: true, composed: true })); if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate([25, 25, 60]); }}>
            <span class="pulsing-dot-red"></span>
            <span>Go Live</span>
          </button>
        </div>

        <div class="live-metrics-bento">
          <div class="metric-mini-card">
            <span class="metric-mini-val">${this.liveStreamStats.listeners}</span>
            <span class="metric-mini-lbl">Live Listeners</span>
          </div>
          <div class="metric-mini-card">
            <span class="metric-mini-val">${this.liveStreamStats.likes}</span>
            <span class="metric-mini-lbl">Fan Cheers</span>
          </div>
        </div>

        <div class="community-chat-container">
          <div class="chat-feed-title-row">
            <span class="chat-feed-title">Direct Fan Requests</span>
            <span style="font-size: 9px; color: #1db954; font-weight: bold; display: flex; align-items: center; gap: 3px;">
              <span class="pulsing-dot-red" style="background:#1db954;"></span> LIVE STREAM CHAT
            </span>
          </div>

          <div class="chat-bubbles-scroll">
            ${this.communityComments.map(comment => html`
              <div class="chat-bubble-card" @click=${() => {
                this.dispatchEvent(new CustomEvent("play-song", { detail: [comment.user + "'s Choice", "Live Request", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=100&auto=format&fit=crop&q=60", "futuristic cyberpunk techno synthwave"], bubbles: true, composed: true }));
                this.dispatchError(`Co-creating with ${comment.user}: Playing selected request vibe!`);
              }}>
                <div class="chat-user-row">
                  <span class="chat-username">${comment.user}</span>
                  <span class="chat-time">${comment.time}</span>
                </div>
                <div class="chat-text">${comment.text}</div>
                <div class="chat-suggestion-indicator">
                  <span class="material-icons-round" style="font-size:10px;">music_note</span>
                  <span>Tap to match this fan request</span>
                </div>
              </div>
            `)}
          </div>
        </div>
      </div>
    `;
  
  }
}
