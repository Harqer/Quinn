import { LitElement, html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { renderAndroidExpandedPlayerStyles } from './renderAndroidExpandedPlayer.styles';

@customElement('android-expanded-player')
export class AndroidExpandedPlayer extends LitElement {
  static styles = renderAndroidExpandedPlayerStyles;

  @property({ type: String }) androidActiveSongTitle = "";
  @property({ type: String }) androidActiveSongArtist = "";
  @property({ type: String }) androidActiveSongCover = "";
  @property({ type: Number }) androidSongProgress = 0;
  @property({ type: Boolean }) isAndroidPlaying = false;
  @property({ type: Array }) androidLikedSongs = [];
  
  private toggleLikeSong(title: string) { this.dispatchEvent(new CustomEvent("toggle-like", { detail: title, bubbles: true, composed: true })); }
  private playPreviousTrack() { this.dispatchEvent(new CustomEvent("play-previous", { bubbles: true, composed: true })); }
  private toggleAndroidPlayPause() { this.dispatchEvent(new CustomEvent("toggle-play", { bubbles: true, composed: true })); }
  private playNextTrack() { this.dispatchEvent(new CustomEvent("play-next", { bubbles: true, composed: true })); }
  private closeAndroidExpandedPlayer() { this.dispatchEvent(new CustomEvent("close-player", { bubbles: true, composed: true })); }
  private openAndroidOptionsMenu() { this.dispatchEvent(new CustomEvent("open-options", { bubbles: true, composed: true })); }

  render() {
    
  
    const isLiked = this.androidLikedSongs.includes(this.androidActiveSongTitle);
    
    const formatTime = (secs: number) => {
      const mins = Math.floor(secs / 60);
      const remainingSecs = secs % 60;
      return `${mins}:${String(remainingSecs).padStart(2, "0")}`;
    };

    const formattedElapsed = formatTime(this.androidSongProgress);
    const formattedRemaining = `-${formatTime(this.androidSongDuration - this.androidSongProgress)}`;
    const progressPercent = (this.androidSongProgress / this.androidSongDuration) * 100;

    const lyricsLines = this.getLyricsForActiveSong();

    return html`
      <div class="android-expanded-player">
        <!-- Header Nav Row -->
        <div class="expanded-player-header">
          <button class="chevron-btn" @click=${() => this.isAndroidPlayerExpanded = false}>
            <span class="material-icons-round">expand_more</span>
          </button>
          <div class="playlist-title">
            ${this.androidActiveSongArtist === this.androidActiveSongArtist || "Unknown Artist" ? "1 (Remastered)" : "Live Session"}
          </div>
          <button class="more-btn" @click=${() => this.openOptionsMenu(this.androidActiveSongTitle, this.androidActiveSongArtist, this.androidActiveSongImage)}>
            <span class="material-icons-round">more_horiz</span>
          </button>
        </div>

        <!-- Album Art Box -->
        <div class="expanded-player-art-container">
          ${this.androidActiveSongImage === "default_album_cover" ? html`
            <div style="background: #e3001a; width: 220px; height: 220px; border-radius: 8px; display: flex; flex-direction: column; justify-content: center; align-items: center; position: relative; box-shadow: 0 16px 40px rgba(0, 0, 0, 0.75);">
              <div style="position: absolute; top: 18px; left: 18px; color: #fcd34d; font-size: 9px; font-weight: 900; letter-spacing: 0.5px;">GENERATIVE</div>
              <div style="color: #fcd34d; font-size: 130px; font-weight: 900; line-height: 1; margin-top: 10px;">1</div>
            </div>
          ` : html`
            <img src="${this.androidActiveSongImage}" />
          `}
        </div>

        <!-- Info Block -->
        <div class="expanded-player-info-row">
          <div class="expanded-player-title-block">
            <div class="expanded-player-title">${this.androidActiveSongTitle}</div>
            <div class="expanded-player-artist">${this.androidActiveSongArtist}</div>
          </div>
          <button class="expanded-player-like-btn ${isLiked ? "liked" : ""}" @click=${() => this.toggleLikeSong(this.androidActiveSongTitle)}>
            <span class="material-icons-round">${isLiked ? "favorite" : "favorite_border"}</span>
          </button>
        </div>

        <!-- Progress Slider -->
        <div class="expanded-player-progress-area">
          <div class="expanded-player-slider-track" @click=${this.handleSeekbarClick}>
            <div class="expanded-player-slider-fill" style="width: ${progressPercent}%;">
              <div class="expanded-player-slider-thumb"></div>
            </div>
          </div>
          <div class="expanded-player-time-row">
            <span>${formattedElapsed}</span>
            <span>${formattedRemaining}</span>
          </div>
        </div>

        <!-- Controls Row -->
        <div class="expanded-player-controls-row">
          <button class="expanded-player-control-btn ${this.isAndroidShuffleEnabled ? "active" : ""}" @click=${() => this.isAndroidShuffleEnabled = !this.isAndroidShuffleEnabled}>
            <span class="material-icons-round">shuffle</span>
          </button>
          
          <button class="expanded-player-control-btn" @click=${this.playPreviousTrack}>
            <span class="material-icons-round">skip_previous</span>
          </button>
          
          <button class="expanded-player-play-btn" @click=${this.togglePlayback}>
            <span class="material-icons-round">${this.playbackState === "playing" ? "pause" : "play_arrow"}</span>
          </button>
          
          <button class="expanded-player-control-btn" @click=${this.playNextTrack}>
            <span class="material-icons-round">skip_next</span>
          </button>
          
          <button class="expanded-player-control-btn ${this.isAndroidRepeatEnabled ? "active" : ""}" @click=${() => this.isAndroidRepeatEnabled = !this.isAndroidRepeatEnabled}>
            <span class="material-icons-round">repeat</span>
          </button>
        </div>

        <!-- Accessories Row -->
        <div class="expanded-player-accessories-row">
          <div class="expanded-player-device-selector" @click=${() => this.dispatchError("Connected to Beats Pill via Wearables Bluetooth sync.")}>
            <span class="material-icons-round">bluetooth_audio</span>
            <span>BEATSPILL+</span>
          </div>
          
          <div style="display: flex; gap: 16px; align-items: center;">
            <button class="expanded-player-control-btn" style="padding: 4px;" @click=${this.shareActiveSong}>
              <span class="material-icons-round" style="font-size: 20px !important;">share</span>
            </button>
            <button class="expanded-player-control-btn" style="padding: 4px;" @click=${() => this.dispatchError("Opening queue details...")}>
              <span class="material-icons-round" style="font-size: 20px !important;">queue_music</span>
            </button>
          </div>
        </div>

        <!-- Lyrics Card -->
        <div class="expanded-player-lyrics-card ${this.isAndroidLyricsMoreOpen ? "more-open" : ""}" @click=${(e: Event) => { e.stopPropagation(); this.isAndroidLyricsMoreOpen = !this.isAndroidLyricsMoreOpen; }}>
          <div class="lyrics-card-header">
            <span class="lyrics-card-title">Lyrics</span>
            <button class="lyrics-card-more-btn">
              <span>${this.isAndroidLyricsMoreOpen ? "LESS" : "MORE"}</span>
              <span class="material-icons-round" style="font-size: 12px !important;">${this.isAndroidLyricsMoreOpen ? "fullscreen_exit" : "fullscreen"}</span>
            </button>
          </div>
          
          <div class="lyrics-scroll-content">
            ${lyricsLines.map((line, idx) => {
              const lineIndex = Math.min(lyricsLines.length - 1, Math.floor((this.androidSongProgress / this.androidSongDuration) * lyricsLines.length));
              const isActive = idx === lineIndex;
              return html`
                <div class="lyrics-text-line ${isActive ? "active" : ""}">${line}</div>
              `;
            })}
          </div>
        </div>
      </div>
    `;
  
  }
}
