import { LitElement, html, nothing } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { AndroidLibraryStyles } from './android-library.styles';

@customElement('android-library')
export class AndroidLibrary extends LitElement {
  static styles = AndroidLibraryStyles;

  @property() generateFromSpotify: any;
  @property() connectSpotify: any;
  @property() disconnectSpotify: any;
  @property() spotifyTopTracks: any;
  @property() isHapticsEnabled: any;
  @property() selectedPresetId: any;
  @property() spotifyLoading: any;
  @property() spotifySource: any;
  @property() saveManualSpotifyToken: any;
  @property() spotifyPlaylists: any;
  @property() spotifyConnected: any;
  @property() selectPreset: any;
  @property() manualSpotifyToken: any;
  @property() selectedSpotifyPlaylistId: any;
  @property() showSpotifyManualInput: any;

  render() {
      
    return html`
      <div class="android-flow-library animate-fade-in">
        <h1 class="community-title-large" style="margin-bottom: 4px;">Your Library</h1>
        
        <h3 class="presets-section-title">Glasses Vibe Presets</h3>
        <p class="presets-intro-text">
          Select a functional preset to instantly configure glasses telemetry, deploy proximity rules, and load custom musical vibes:
        </p>

        <div class="preset-bento-grid">
          <div class="preset-card-item ${this.selectedPresetId === 'preset-rooftop' ? 'active' : ''}" @click=${() => this.selectPreset("preset-rooftop")}>
            <div class="preset-left">
              <div class="preset-name-row">
                <span class="preset-title-val">1. Indie Rooftop</span>
                <span class="preset-badge-tag">Acoustic</span>
              </div>
              <span class="preset-desc">Warm blues guitar vibes</span>
            </div>
            <div class="preset-right">
              <div class="preset-telemetry-status">
                <span class="preset-battery-lbl">
                  <span class="material-icons-round" style="font-size:11px; color:#1db954;">battery_charging_full</span>
                  95%
                </span>
                <span class="preset-wear-status">Worn (On-Head)</span>
              </div>
              <span class="material-icons-round preset-play-icon">play_circle_filled</span>
            </div>
          </div>

          <div class="preset-card-item ${this.selectedPresetId === 'preset-lofi' ? 'active' : ''}" @click=${() => this.selectPreset("preset-lofi")}>
            <div class="preset-left">
              <div class="preset-name-row">
                <span class="preset-title-val">2. Late Night Lofi</span>
                <span class="preset-badge-tag" style="background:#38bdf8;">Chill</span>
              </div>
              <span class="preset-desc">Relaxed strings and beats</span>
            </div>
            <div class="preset-right">
              <div class="preset-telemetry-status">
                <span class="preset-battery-lbl">
                  <span class="material-icons-round" style="font-size:11px; color:#38bdf8;">battery_full</span>
                  78%
                </span>
                <span class="preset-wear-status">Worn (On-Head)</span>
              </div>
              <span class="material-icons-round preset-play-icon" style="color:#38bdf8;">play_circle_filled</span>
            </div>
          </div>

          <div class="preset-card-item ${this.selectedPresetId === 'preset-mainstage' ? 'active' : ''}" @click=${() => this.selectPreset("preset-mainstage")}>
            <div class="preset-left">
              <div class="preset-name-row">
                <span class="preset-title-val">3. Cyber Mainstage</span>
                <span class="preset-badge-tag" style="background:#e1118c; color:white;">EDM</span>
              </div>
              <span class="preset-desc">Futuristic electronic lead synth</span>
            </div>
            <div class="preset-right">
              <div class="preset-telemetry-status">
                <span class="preset-battery-lbl">
                  <span class="material-icons-round" style="font-size:11px; color:#e1118c;">battery_full</span>
                  62%
                </span>
                <span class="preset-wear-status">Worn (On-Head)</span>
              </div>
              <span class="material-icons-round preset-play-icon" style="color:#e1118c;">play_circle_filled</span>
            </div>
          </div>

          <div class="preset-card-item ${this.selectedPresetId === 'preset-desk' ? 'active' : ''}" @click=${() => this.selectPreset("preset-desk")}>
            <div class="preset-left">
              <div class="preset-name-row">
                <span class="preset-title-val">4. Desktop Standby</span>
                <span class="preset-badge-tag" style="background:#71717a; color:white;">Off-Head</span>
              </div>
              <span class="preset-desc">Deep cosmic space ambience</span>
            </div>
            <div class="preset-right">
              <div class="preset-telemetry-status">
                <span class="preset-battery-lbl" style="color:#ef4444;">
                  <span class="material-icons-round" style="font-size:11px; color:#ef4444;">battery_alert</span>
                  15%
                </span>
                <span class="preset-wear-status" style="color:#ef4444; font-weight:bold;">Off (Safety Proximity)</span>
              </div>
              <span class="material-icons-round preset-play-icon" style="color:#ef4444;">pause_circle_filled</span>
            </div>
          </div>
        </div>

        <div style="border-top: 1px solid #27272a; margin: 20px 0; padding-top: 16px;"></div>

        <div class="spotify-integration-section" style="margin-bottom: 24px;">
          <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <svg style="width: 20px; height: 20px; fill: #1db954;" viewBox="0 0 24 24">
                <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm4.586 14.424c-.18.295-.565.387-.86.207-2.377-1.454-5.37-1.783-8.893-.982-.336.075-.668-.135-.744-.47-.077-.337.136-.669.47-.745 3.848-.874 7.14-.5 9.82 1.13.295.182.387.567.207.86zm1.224-2.72c-.227.367-.707.487-1.074.26-2.72-1.672-6.87-2.157-10.078-1.182-.413.125-.844-.107-.97-.52-.124-.413.108-.844.52-.97 3.673-1.115 8.236-.572 11.34 1.34.368.228.488.708.262 1.072zm.107-2.828C14.484 8.766 8.823 8.58 5.518 9.582c-.512.156-1.047-.137-1.202-.65a.947.947 0 01.65-1.202C8.747 6.596 14.98 6.81 19.33 9.395c.462.274.61.874.336 1.336-.273.46-.873.61-1.335.336z"/>
              </svg>
              <h3 style="font-size: 13px; font-weight: 700; color: white; margin: 0; text-transform: uppercase; letter-spacing: 0.05em;">Spotify Premium Sync</h3>
            </div>
            
            ${this.spotifyConnected ? html`
              <span class="spotify-status-badge">
                <span class="pulsing-dot-green"></span>
                <span style="font-size: 9px; text-transform: uppercase; letter-spacing: 0.05em; font-weight: bold; color: #1db954;">${this.spotifySource === 'vault' ? 'Developer Link' : 'OAuth Linked'}</span>
              </span>
            ` : html`
              <span class="spotify-status-badge-offline" style="font-size: 9px; text-transform: uppercase; letter-spacing: 0.05em; font-weight: bold; background: #27272a; padding: 2px 8px; border-radius: 99px; color: #a1a1aa;">Offline</span>
            `}
          </div>

          ${this.spotifyConnected ? html`
            <div class="spotify-connected-deck animate-fade-in" style="display: flex; flex-direction: column; gap: 12px;">
              <div style="display: flex; justify-content: space-between; align-items: center; background: #18181b; padding: 12px; border-radius: 8px; border: 1px solid #27272a;">
                <div>
                  <div style="font-size: 11px; font-weight: bold; color: white;">Active Creator Session</div>
                  <div style="font-size: 9px; color: #a1a1aa;">Ready to push custom synthesized music & vibes</div>
                </div>
                <button class="spotify-disconnect-btn" @click=${this.disconnectSpotify} style="background: #ef4444; color: white; border: none; font-size: 9px; font-weight: bold; padding: 4px 10px; border-radius: 4px; cursor: pointer;">Disconnect</button>
              </div>

              <!-- Spotify User Playlists -->
              <div class="spotify-sub-panel" style="background: #18181b; border: 1px solid #27272a; border-radius: 8px; padding: 12px; display: flex; flex-direction: column; gap: 8px;">
                <div class="sub-panel-title" style="font-size: 10px; font-weight: bold; text-transform: uppercase; color: #a1a1aa; letter-spacing: 0.03em;">Target Sync Playlist</div>
                <div style="font-size: 9px; color: #71717a; margin-bottom: 4px;">Select which playlist to direct your synthesized vibes to:</div>
                
                <div class="spotify-playlists-scroller" style="max-height: 160px; overflow-y: auto; display: flex; flex-direction: column; gap: 6px; padding-right: 4px;">
                  <!-- Default Automatic Playlist option -->
                  <div class="spotify-playlist-item ${this.selectedSpotifyPlaylistId === '' ? 'active' : ''}" @click=${() => { this.selectedSpotifyPlaylistId = ''; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }} style="display: flex; align-items: center; gap: 10px; padding: 8px; border-radius: 6px; background: ${this.selectedSpotifyPlaylistId === '' ? '#1f2937' : '#111827'}; border: 1px solid ${this.selectedSpotifyPlaylistId === '' ? '#3b82f6' : 'transparent'}; cursor: pointer;">
                    <div class="playlist-avatar-placeholder" style="background: linear-gradient(135deg, #1db954, #191414); width: 32px; height: 32px; border-radius: 4px; display: flex; align-items: center; justify-content: center;">
                      <span class="material-icons-round" style="font-size: 14px; color: white;">auto_awesome</span>
                    </div>
                    <div style="flex: 1; min-width: 0;">
                      <div class="playlist-name-val" style="font-size: 10px; font-weight: bold; color: white; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">Mave Wearables Vibes (Auto)</div>
                      <div class="playlist-tracks-count" style="font-size: 8px; color: #71717a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">Smart dynamic auto-generated playlist</div>
                    </div>
                    <span class="material-icons-round check-icon" style="font-size: 16px; color: ${this.selectedSpotifyPlaylistId === '' ? '#1db954' : '#4b5563'};">${this.selectedSpotifyPlaylistId === '' ? 'check_circle' : 'radio_button_unchecked'}</span>
                  </div>

                  ${this.spotifyPlaylists.map(playlist => html`
                    <div class="spotify-playlist-item ${this.selectedSpotifyPlaylistId === playlist.id ? 'active' : ''}" @click=${() => { this.selectedSpotifyPlaylistId = playlist.id; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }} style="display: flex; align-items: center; gap: 10px; padding: 8px; border-radius: 6px; background: ${this.selectedSpotifyPlaylistId === playlist.id ? '#1f2937' : '#111827'}; border: 1px solid ${this.selectedSpotifyPlaylistId === playlist.id ? '#3b82f6' : 'transparent'}; cursor: pointer;">
                      ${playlist.images?.[0]?.url ? html`
                        <img src=${playlist.images[0].url} class="playlist-avatar-img" style="width: 32px; height: 32px; border-radius: 4px; object-fit: cover;" />
                      ` : html`
                        <div class="playlist-avatar-placeholder" style="background: #374151; width: 32px; height: 32px; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #a1a1aa;">
                          <span class="material-icons-round" style="font-size: 14px;">queue_music</span>
                        </div>
                      `}
                      <div style="flex: 1; min-width: 0;">
                        <div class="playlist-name-val" style="font-size: 10px; font-weight: bold; color: white; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${playlist.name}</div>
                        <div class="playlist-tracks-count" style="font-size: 8px; color: #71717a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${playlist.tracks?.total || 0} tracks • By ${playlist.owner?.display_name || 'Spotify'}</div>
                      </div>
                      <button class="playlist-remix-btn" @click=${(e: Event) => { e.stopPropagation(); this.generateFromSpotify({ name: playlist.name, artists: [{ name: playlist.owner?.display_name || 'Spotify' }], album: { images: playlist.images } }); }} style="background: #10b981; color: white; border: none; font-size: 8px; font-weight: bold; padding: 4px 8px; border-radius: 4px; cursor: pointer; display: flex; align-items: center; gap: 2px;">
                        <span class="material-icons-round" style="font-size: 12px;">auto_awesome</span>
                        <span>Remix Playlist</span>
                      </button>
                    </div>
                  `)}
                </div>
              </div>

              <!-- User Top Tracks from Spotify -->
              ${this.spotifyTopTracks.length > 0 ? html`
                <div class="spotify-sub-panel" style="background: #18181b; border: 1px solid #27272a; border-radius: 8px; padding: 12px; display: flex; flex-direction: column; gap: 6px; margin-top: 4px;">
                  <div class="sub-panel-title" style="font-size: 10px; font-weight: bold; text-transform: uppercase; color: #a1a1aa; letter-spacing: 0.03em;">Your Top Played Spotify Tracks</div>
                  <div class="spotify-top-tracks-list" style="display: flex; flex-direction: column; gap: 6px; max-height: 180px; overflow-y: auto;">
                    ${this.spotifyTopTracks.map((track, idx) => html`
                      <div class="spotify-track-item" style="display: flex; align-items: center; gap: 8px; padding: 6px; background: #111827; border-radius: 6px;">
                        <span class="track-index-no" style="font-size: 9px; color: #71717a; font-weight: bold; width: 14px; text-align: center;">${idx + 1}</span>
                        ${track.album?.images?.[2]?.url ? html`
                          <img src=${track.album.images[2].url} class="track-avatar-img" style="width: 24px; height: 24px; border-radius: 4px; object-fit: cover;" />
                        ` : html`
                          <div class="track-avatar-placeholder" style="background: #374151; width: 24px; height: 24px; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #a1a1aa;">
                            <span class="material-icons-round" style="font-size: 11px;">music_note</span>
                          </div>
                        `}
                        <div style="flex: 1; min-width: 0;">
                          <div class="track-title-val" style="font-size: 9px; font-weight: bold; color: white; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${track.name}</div>
                          <div class="track-artist-val" style="font-size: 8px; color: #71717a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${track.artists?.map((a: any) => a.name).join(', ')}</div>
                        </div>
                        <button class="track-sync-btn" @click=${() => this.generateFromSpotify(track)} style="background: #10b981; color: white; border: none; font-size: 8px; font-weight: bold; padding: 2px 6px; border-radius: 4px; cursor: pointer; display: flex; align-items: center; gap: 2px;">
                          <span class="material-icons-round" style="font-size: 10px;">auto_awesome</span>
                          <span>Remix with AI</span>
                        </button>
                      </div>
                    `)}
                  </div>
                </div>
              ` : ""}
            </div>
          ` : html`
            <div class="spotify-disconnected-deck animate-fade-in" style="background: #18181b; border: 1px solid #27272a; border-radius: 8px; padding: 14px; display: flex; flex-direction: column; gap: 12px;">
              <p class="spotify-promo-desc" style="font-size: 9.5px; color: #a1a1aa; line-height: 1.5; margin: 0;">
                Connect your Spotify account to instantly sync custom synthesized tracks, access your actual personal playlists, and direct soundscapes from your glasses directly to your account.
              </p>

              <!-- Spotify Production Guide -->
              <div class="spotify-setup-guide" style="background: #111827; border: 1px solid #374151; border-radius: 6px; padding: 10px; display: flex; flex-direction: column; gap: 6px;">
                <div style="font-size: 9px; font-weight: bold; color: #1db954; text-transform: uppercase; letter-spacing: 0.05em; display: flex; align-items: center; gap: 4px;">
                  <span class="material-icons-round" style="font-size: 11px;">info</span>
                  <span>Production OAuth Setup Guide</span>
                </div>
                <p style="font-size: 8.5px; color: #9ca3af; line-height: 1.4; margin: 0;">
                  You do <strong>not</strong> need to manually hunt for a Spotify Bearer Token. Simply configure your Spotify App credentials (under environment variables/secrets), register the exact Redirect URIs below in your <strong>Spotify Developer Dashboard</strong>, and click the green button to authorize:
                </p>
                <div style="display: flex; flex-direction: column; gap: 4px; margin-top: 2px;">
                  <div style="display: flex; flex-direction: column; background: #1f2937; padding: 6px; border-radius: 4px; border: 1px solid #2d3748;">
                    <span style="font-size: 7.5px; font-weight: bold; color: #9ca3af; text-transform: uppercase;">Development Callback URI:</span>
                    <code style="font-size: 8px; color: #60a5fa; word-break: break-all; margin-top: 1px; font-family: monospace;">https://ais-dev-pgvcritokphyopw5nxth3h-123203256118.europe-west2.run.app/api/spotify/callback</code>
                  </div>
                  <div style="display: flex; flex-direction: column; background: #1f2937; padding: 6px; border-radius: 4px; border: 1px solid #2d3748;">
                    <span style="font-size: 7.5px; font-weight: bold; color: #9ca3af; text-transform: uppercase;">Shared/Production Callback URI:</span>
                    <code style="font-size: 8px; color: #34d399; word-break: break-all; margin-top: 1px; font-family: monospace;">https://ais-pre-pgvcritokphyopw5nxth3h-123203256118.europe-west2.run.app/api/spotify/callback</code>
                  </div>
                </div>
              </div>
              
              <div class="spotify-btn-actions" style="display: flex; flex-direction: column; gap: 8px;">
                <button class="spotify-connect-oauth-btn" @click=${this.connectSpotify} ?disabled=${this.spotifyLoading} style="background: #1db954; color: white; border: none; font-weight: bold; font-size: 10px; padding: 8px 12px; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 6px; transition: opacity 0.2s;">
                  <svg style="width: 14px; height: 14px; fill: white;" viewBox="0 0 24 24">
                    <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm4.586 14.424c-.18.295-.565.387-.86.207-2.377-1.454-5.37-1.783-8.893-.982-.336.075-.668-.135-.744-.47-.077-.337.136-.669.47-.745 3.848-.874 7.14-.5 9.82 1.13.295.182.387.567.207.86zm1.224-2.72c-.227.367-.707.487-1.074.26-2.72-1.672-6.87-2.157-10.078-1.182-.413.125-.844-.107-.97-.52-.124-.413.108-.844.52-.97 3.673-1.115 8.236-.572 11.34 1.34.368.228.488.708.262 1.072zm.107-2.828C14.484 8.766 8.823 8.58 5.518 9.582c-.512.156-1.047-.137-1.202-.65a.947.947 0 01.65-1.202C8.747 6.596 14.98 6.81 19.33 9.395c.462.274.61.874.336 1.336-.273.46-.873.61-1.335.336z"/>
                  </svg>
                  <span>Connect Spotify Account via OAuth</span>
                </button>
                
                <button class="spotify-manual-key-btn" @click=${() => { this.showSpotifyManualInput = !this.showSpotifyManualInput; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }} style="background: transparent; color: #a1a1aa; border: 1px solid #374151; font-size: 10px; padding: 6px 12px; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 4px;">
                  <span class="material-icons-round" style="font-size: 12px;">vpn_key</span>
                  <span>Enter Temporary Developer Token (Fallback)</span>
                </button>
              </div>

              ${this.showSpotifyManualInput ? html`
                <div class="spotify-manual-token-pane animate-fade-in" style="background: #111827; border: 1px solid #374151; border-radius: 6px; padding: 10px; display: flex; flex-direction: column; gap: 8px;">
                  <div class="pane-instruction" style="font-size: 8px; color: #71717a; line-height: 1.4;">
                    Paste a valid temporary developer access token (created from your Spotify dashboard or authentication flows) to sync instantly.
                  </div>
                  <div style="display: flex; gap: 8px;">
                    <input 
                      type="password" 
                      class="spotify-input-custom" 
                      placeholder="Paste BQAA... token here"
                      .value=${this.manualSpotifyToken}
                      @input=${(e: Event) => { this.manualSpotifyToken = (e.target as HTMLInputElement).value; }}
                      style="flex: 1; background: #1f2937; border: 1px solid #374151; color: white; border-radius: 4px; padding: 4px 8px; font-size: 9px; min-width: 0;"
                    />
                    <button class="spotify-save-token-btn" @click=${() => this.saveManualSpotifyToken(this.manualSpotifyToken)} style="background: #3b82f6; color: white; border: none; font-size: 9px; font-weight: bold; padding: 4px 10px; border-radius: 4px; cursor: pointer;">Save</button>
                  </div>
                </div>
              ` : ""}
            </div>
          `}
        </div>
      </div>
    `;
  
  }
}
