import re

with open('./components/organisms/renderCommunity.txt', 'r') as f:
    html_content = f.read().replace('private renderCommunity() {', '').rstrip()[:-1]

# Replacements
html_content = html_content.replace('this.fetchCommunityTracks', '() => this.dispatchEvent(new CustomEvent("fetch-tracks", { bubbles: true, composed: true }))')
html_content = html_content.replace('this.activeCommunityTrackId = track.id;\n                            void this.submitAndroidVibeCommand(track.vibe);', 'this.dispatchEvent(new CustomEvent("play-track", { detail: { id: track.id, vibe: track.vibe }, bubbles: true, composed: true }));')
html_content = html_content.replace('this.shareTitleInput = (e.target as HTMLInputElement).value;', 'this.dispatchEvent(new CustomEvent("update-title", { detail: (e.target as HTMLInputElement).value, bubbles: true, composed: true }));')
html_content = html_content.replace('this.shareVibeInput = (e.target as HTMLTextAreaElement).value;', 'this.dispatchEvent(new CustomEvent("update-vibe", { detail: (e.target as HTMLTextAreaElement).value, bubbles: true, composed: true }));')
html_content = html_content.replace('this.shareVibeInput = this.voiceTranscript;\n                  this.shareTitleInput = this.shareTitleInput || "My Custom Vibe";', 'this.dispatchEvent(new CustomEvent("use-transcript", { bubbles: true, composed: true }));')
html_content = html_content.replace('void this.shareVibeToCommunity(this.shareTitleInput, this.shareVibeInput);', 'this.dispatchEvent(new CustomEvent("share-vibe", { bubbles: true, composed: true }));')

ts_content = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ communityViewStyles }} from './community-view.styles';

@customElement('community-view')
export class CommunityView extends LitElement {{
  static styles = communityViewStyles;

  @property({{ type: Boolean }}) communityTracksLoading = false;
  @property({{ type: Array }}) communityTracks: any[] = [];
  @property({{ type: String }}) activeCommunityTrackId = '';
  @property({{ type: String }}) playbackState = 'stopped';
  @property({{ type: String }}) shareTitleInput = '';
  @property({{ type: String }}) shareVibeInput = '';
  @property({{ type: String }}) voiceTranscript = '';

  render() {{
    {html_content}
  }}
}}
"""

with open('./components/organisms/community-view.ts', 'w') as f:
    f.write(ts_content)

print("Generated community-view.ts")
