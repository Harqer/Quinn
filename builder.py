import re

# Android Welcome
with open('./components/organisms/renderAndroidWelcome.txt', 'r') as f:
    welcome_html = f.read()

# Replace local assignments with event dispatches
welcome_html = re.sub(r'this\.androidFlowStep = "([^"]+)"', r'this.dispatchEvent(new CustomEvent("step-change", { detail: "\1", bubbles: true, composed: true }))', welcome_html)

welcome_ts = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement }} from 'lit/decorators.js';
import {{ renderAndroidWelcomeStyles }} from './renderAndroidWelcome.styles';

@customElement('android-welcome')
export class AndroidWelcome extends LitElement {{
  static styles = renderAndroidWelcomeStyles;

  render() {{
    {welcome_html.replace('private renderAndroidWelcome() {', '').rstrip()[:-1]}
  }}
}}
"""

with open('./components/organisms/android-welcome.ts', 'w') as f:
    f.write(welcome_ts)

# Security Hub
with open('./components/organisms/renderSecurity.txt', 'r') as f:
    security_html = f.read()

# Make security self-contained or define missing properties
# For now, just define them as @property
security_ts = f"""import {{ LitElement, html, nothing }} from 'lit';
import {{ customElement, property, state }} from 'lit/decorators.js';
import {{ renderSecurityStyles }} from './renderSecurity.styles';

@customElement('security-hub')
export class SecurityHub extends LitElement {{
  static styles = renderSecurityStyles;

  @property({{ type: Boolean }}) mockAlertLoading = false;
  @property({{ type: Boolean }}) webhookCopied = false;
  @property({{ type: Boolean }}) spotifyLoading = false;
  @property({{ type: Boolean }}) spotifyConnected = false;
  @property({{ type: Boolean }}) spotifyTokenCopied = false;
  @property({{ type: String }}) spotifyAccessToken = "";
  @property({{ type: Array }}) securityAlerts = [];
  @property({{ type: Boolean }}) securityAlertsLoading = false;
  @property({{ type: String }}) expandedAlertId = "";

  private triggerMockAlert() {{ this.dispatchEvent(new CustomEvent("mock-alert", {{ bubbles: true, composed: true }})); }}
  private copyWebhookUrl() {{ this.dispatchEvent(new CustomEvent("copy-webhook", {{ bubbles: true, composed: true }})); }}
  private copySpotifyAccessToken() {{ this.dispatchEvent(new CustomEvent("copy-spotify-token", {{ bubbles: true, composed: true }})); }}
  private disconnectSpotify() {{ this.dispatchEvent(new CustomEvent("disconnect-spotify", {{ bubbles: true, composed: true }})); }}
  private connectSpotify() {{ this.dispatchEvent(new CustomEvent("connect-spotify", {{ bubbles: true, composed: true }})); }}
  private fetchSecurityAlerts() {{ this.dispatchEvent(new CustomEvent("fetch-alerts", {{ bubbles: true, composed: true }})); }}
  private toggleAlertDetails(id: string) {{ this.dispatchEvent(new CustomEvent("toggle-alert", {{ detail: id, bubbles: true, composed: true }})); }}
  private dispatchError(msg: string) {{ this.dispatchEvent(new CustomEvent("error", {{ detail: msg, bubbles: true, composed: true }})); }}

  render() {{
    {security_html.replace('private renderSecurity() {', '').rstrip()[:-1]}
  }}
}}
"""
with open('./components/organisms/security-hub.ts', 'w') as f:
    f.write(security_ts)

# Expanded Player
with open('./components/organisms/renderAndroidExpandedPlayer.txt', 'r') as f:
    player_html = f.read()

player_ts = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ renderAndroidExpandedPlayerStyles }} from './renderAndroidExpandedPlayer.styles';

@customElement('android-expanded-player')
export class AndroidExpandedPlayer extends LitElement {{
  static styles = renderAndroidExpandedPlayerStyles;

  @property({{ type: String }}) androidActiveSongTitle = "";
  @property({{ type: String }}) androidActiveSongArtist = "";
  @property({{ type: String }}) androidActiveSongCover = "";
  @property({{ type: Number }}) androidSongProgress = 0;
  @property({{ type: Boolean }}) isAndroidPlaying = false;
  @property({{ type: Array }}) androidLikedSongs = [];
  
  private toggleLikeSong(title: string) {{ this.dispatchEvent(new CustomEvent("toggle-like", {{ detail: title, bubbles: true, composed: true }})); }}
  private playPreviousTrack() {{ this.dispatchEvent(new CustomEvent("play-previous", {{ bubbles: true, composed: true }})); }}
  private toggleAndroidPlayPause() {{ this.dispatchEvent(new CustomEvent("toggle-play", {{ bubbles: true, composed: true }})); }}
  private playNextTrack() {{ this.dispatchEvent(new CustomEvent("play-next", {{ bubbles: true, composed: true }})); }}
  private closeAndroidExpandedPlayer() {{ this.dispatchEvent(new CustomEvent("close-player", {{ bubbles: true, composed: true }})); }}
  private openAndroidOptionsMenu() {{ this.dispatchEvent(new CustomEvent("open-options", {{ bubbles: true, composed: true }})); }}

  render() {{
    {player_html.replace('private renderAndroidExpandedPlayer() {', '').rstrip()[:-1]}
  }}
}}
"""
with open('./components/organisms/android-expanded-player.ts', 'w') as f:
    f.write(player_ts)
    
print("Created LitElements")
