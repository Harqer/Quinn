import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

# Extract top-nav-bar
top_nav_match = re.search(r'<div id="top-nav-bar">.*?(?=<div class="main-content-area">)', content, re.DOTALL)
if top_nav_match:
    top_nav_html = top_nav_match.group(0).strip()
    # Replace method calls
    top_nav_html = top_nav_html.replace('this.page = "main"; this.launchExperience();', 'this.dispatchEvent(new CustomEvent("nav-main", {bubbles: true, composed: true}))')
    top_nav_html = top_nav_html.replace('this.page = "community"; void this.fetchCommunityTracks();', 'this.dispatchEvent(new CustomEvent("nav-community", {bubbles: true, composed: true}))')
    top_nav_html = top_nav_html.replace('this.page = "android_flow";', 'this.dispatchEvent(new CustomEvent("nav-android", {bubbles: true, composed: true}))')
    top_nav_html = top_nav_html.replace('this.page = "security"; void this.fetchSecurityAlerts();', 'this.dispatchEvent(new CustomEvent("nav-security", {bubbles: true, composed: true}))')

    top_ts = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ topNavStyles }} from './top-nav.styles';

@customElement('top-nav')
export class TopNav extends LitElement {{
  static styles = topNavStyles;

  @property() wearableActive: any;
  @property() page: any;
  @property() securityAlerts: any = [];

  render() {{
    return html`
      {top_nav_html}
    `;
  }}
}}
"""
    with open('./components/organisms/top-nav.ts', 'w') as f:
        f.write(top_ts)

# Extract bottom-nav-bar-web
bottom_nav_match = re.search(r'<div id="bottom-nav-bar-web">.*?(?=</div>\s*</div>\s*`;)', content, re.DOTALL)
if bottom_nav_match:
    bottom_nav_html = bottom_nav_match.group(0).strip()
    # Replace method calls
    bottom_nav_html = bottom_nav_html.replace('this.page = "main"; this.launchExperience();', 'this.dispatchEvent(new CustomEvent("nav-main", {bubbles: true, composed: true}))')
    bottom_nav_html = bottom_nav_html.replace('this.page = "community"; void this.fetchCommunityTracks();', 'this.dispatchEvent(new CustomEvent("nav-community", {bubbles: true, composed: true}))')
    bottom_nav_html = bottom_nav_html.replace('this.page = "android_flow";', 'this.dispatchEvent(new CustomEvent("nav-android", {bubbles: true, composed: true}))')
    bottom_nav_html = bottom_nav_html.replace('this.page = "security"; void this.fetchSecurityAlerts();', 'this.dispatchEvent(new CustomEvent("nav-security", {bubbles: true, composed: true}))')

    bottom_ts = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ bottomNavStyles }} from './bottom-nav.styles';

@customElement('bottom-nav')
export class BottomNav extends LitElement {{
  static styles = bottomNavStyles;

  @property() page: any;
  @property() securityAlerts: any = [];

  render() {{
    return html`
      {bottom_nav_html}
    `;
  }}
}}
"""
    with open('./components/organisms/bottom-nav.ts', 'w') as f:
        f.write(bottom_ts)

print("Generated nav components")
