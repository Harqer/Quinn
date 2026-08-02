import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

# Add imports
imports = """import './organisms/top-nav';
import './organisms/bottom-nav';
"""
content = re.sub(r'(import \{ LitElement)', imports + r'\1', content, count=1)

# Replace top-nav-bar
content = re.sub(r'<div id="top-nav-bar">.*?(?=<div class="main-content-area">)', r'''<top-nav
          .wearableActive=${this.wearableActive}
          .page=${this.page}
          .securityAlerts=${this.securityAlerts}
          @nav-main=${() => { this.page = "main"; this.launchExperience(); }}
          @nav-community=${() => { this.page = "community"; void this.fetchCommunityTracks(); }}
          @nav-android=${() => { this.page = "android_flow"; }}
          @nav-security=${() => { this.page = "security"; void this.fetchSecurityAlerts(); }}
        ></top-nav>\n        ''', content, flags=re.DOTALL)

# Replace bottom-nav-bar-web
content = re.sub(r'<div id="bottom-nav-bar-web">.*?(?=</div>\s*</div>\s*`;)', r'''<bottom-nav
          .page=${this.page}
          .securityAlerts=${this.securityAlerts}
          @nav-main=${() => { this.page = "main"; this.launchExperience(); }}
          @nav-community=${() => { this.page = "community"; void this.fetchCommunityTracks(); }}
          @nav-android=${() => { this.page = "android_flow"; }}
          @nav-security=${() => { this.page = "security"; void this.fetchSecurityAlerts(); }}
        ></bottom-nav>\n      ''', content, flags=re.DOTALL)

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)
print("Updated lyria_camera.ts with nav components")
