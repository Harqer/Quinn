import re

with open('./components/organisms/renderAndroidCompanion.txt', 'r') as f:
    html = f.read().replace('private renderAndroidCompanion() {', '').rstrip()[:-1]

# Replace method calls with custom elements
html = html.replace('this.renderAndroidSearchHome()', "html`<android-search-home></android-search-home>`")
html = html.replace('this.renderAndroidSearchResults()', "html`<android-search-results></android-search-results>`")
html = html.replace('this.renderAndroidAlbumDetails()', "html`<android-album-details></android-album-details>`")
html = html.replace('this.renderAndroidCommunity()', "html`<android-community></android-community>`")
html = html.replace('this.renderAndroidGoLive()', "html`<android-go-live></android-go-live>`")
html = html.replace('this.renderAndroidLibrary()', "html`<android-library></android-library>`")
html = html.replace('this.renderAndroidOptionsMenu()', "html`<android-options-menu></android-options-menu>`")

ts_content = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ androidCompanionViewStyles }} from './android-companion-view.styles';

import './android-search-home';
import './android-search-results';
import './android-album-details';
import './android-community';
import './android-go-live';
import './android-library';
import './android-options-menu';

@customElement('android-companion-view')
export class AndroidCompanionView extends LitElement {{
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

  render() {{
    {html}
  }}
}}
"""

with open('./components/organisms/android-companion-view.ts', 'w') as f:
    f.write(ts_content)

print("Generated android-companion-view.ts")
