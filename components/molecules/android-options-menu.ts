/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: UI Component for android-options-menu.ts
 */

import { LitElement, html, nothing } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { AndroidOptionsMenuStyles } from './android-options-menu.styles';

@customElement('android-options-menu')
export class AndroidOptionsMenu extends LitElement {
  static styles = AndroidOptionsMenuStyles;

  @property() androidLikedSongs: any;
  @property() addActiveTrackToSpotify: any;
  @property() toggleLikeSong: any;
  @property() dispatchError: any;
  @property() androidActiveSongImage: any;
  @property() selectedSpotifyPlaylistId: any;
  @property() shareActiveSong: any;
  @property() androidActiveSongTitle: any;
  @property() isAndroidOptionsMenuOpen: any;
  @property() spotifyConnected: any;
  @property() androidActiveSongArtist: any;

  render() {
    

  
    const isLiked = this.androidLikedSongs.includes(this.androidActiveSongTitle);

    return html`
      <div class="android-options-menu" @click=${() => this.isAndroidOptionsMenuOpen = false}>
        <div class="options-menu-sheet" @click=${(e: Event) => e.stopPropagation()}>
          <!-- Sheet Header -->
          <div class="options-menu-header">
            ${this.androidActiveSongImage === "default_album_cover" ? html`
              <div style="background: #e3001a; width: 100px; height: 100px; border-radius: 8px; display: flex; flex-direction: column; justify-content: center; align-items: center; position: relative; box-shadow: 0 8px 24px rgba(0,0,0,0.6); margin-bottom: 12px;">
                <div style="position: absolute; top: 10px; left: 10px; color: #fcd34d; font-size: 6px; font-weight: 900; letter-spacing: 0.3px;">GENERATIVE</div>
                <div style="color: #fcd34d; font-size: 60px; font-weight: 900; line-height: 1; margin-top: 5px;">1</div>
              </div>
            ` : html`
              <img src="${this.androidActiveSongImage}" />
            `}
            <div class="options-menu-song-title">${this.androidActiveSongTitle}</div>
            <div class="options-menu-song-artist">${this.androidActiveSongArtist}</div>
          </div>

          <!-- Options List -->
          <div class="options-menu-list">
            <button class="options-menu-item ${isLiked ? "liked" : ""}" @click=${() => { this.toggleLikeSong(this.androidActiveSongTitle); this.isAndroidOptionsMenuOpen = false; }}>
              <span class="material-icons-round">${isLiked ? "favorite" : "favorite_border"}</span>
              <span>${isLiked ? "Unlike song" : "Like song"}</span>
            </button>
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchEvent(new CustomEvent('view-artist', { detail: { artist: this.androidActiveSongArtist }, bubbles: true, composed: true })); }}>
              <span class="material-icons-round">person</span>
              <span>View artist</span>
            </button>
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.shareActiveSong(); }}>
              <span class="material-icons-round">share</span>
              <span>Share link</span>
            </button>
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchEvent(new CustomEvent('like-all-songs', { bubbles: true, composed: true })); }}>
              <span class="material-icons-round">favorite_all</span>
              <span>Like all songs</span>
            </button>
            
            ${this.spotifyConnected ? html`
              <button class="options-menu-item" style="color: #10b981;" @click=${() => { this.isAndroidOptionsMenuOpen = false; void this.addActiveTrackToSpotify(this.selectedSpotifyPlaylistId); }}>
                <span class="material-icons-round" style="color: #10b981;">queue_music</span>
                <span style="font-weight: bold;">Add to Spotify Playlist</span>
              </button>
            ` : html`
              <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchEvent(new CustomEvent('add-to-playlist', { detail: { track: this.androidActiveSongTitle }, bubbles: true, composed: true })); }}>
                <span class="material-icons-round">playlist_add</span>
                <span>Add to playlist</span>
              </button>
            `}
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchEvent(new CustomEvent('add-to-queue', { detail: { track: this.androidActiveSongTitle }, bubbles: true, composed: true })); }}>
              <span class="material-icons-round">queue</span>
              <span>Add to queue</span>
            </button>
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchEvent(new CustomEvent('go-to-radio', { detail: { track: this.androidActiveSongTitle }, bubbles: true, composed: true })); }}>
              <span class="material-icons-round">wifi_tethering</span>
              <span>Go to radio</span>
            </button>
          </div>

          <!-- Close Button -->
          <button class="options-menu-close-btn" @click=${() => this.isAndroidOptionsMenuOpen = false}>
            Close
          </button>
        </div>
      </div>
    `;
  
  }
}
