import re

with open('./components/organisms/renderAndroidEmail.txt', 'r') as f:
    email_html = f.read().replace('private renderAndroidEmail() {', '').rstrip()[:-1]
with open('./components/organisms/renderAndroidPassword.txt', 'r') as f:
    password_html = f.read().replace('private renderAndroidPassword() {', '').rstrip()[:-1]
with open('./components/organisms/renderAndroidName.txt', 'r') as f:
    name_html = f.read().replace('private renderAndroidName() {', '').rstrip()[:-1]

# Merge into one component
merged_html = f"""
  render() {{
    if (this.step === 'email') return this.renderEmail();
    if (this.step === 'password') return this.renderPassword();
    if (this.step === 'name') return this.renderName();
    return html``;
  }}

  private renderEmail() {{
    {email_html}
  }}

  private renderPassword() {{
    {password_html}
  }}

  private renderName() {{
    {name_html}
  }}
"""

# Replace variable bindings and event handlers
merged_html = merged_html.replace('this.androidFlowStep = "welcome"', 'this.dispatchEvent(new CustomEvent("step-change", { detail: "welcome", bubbles: true, composed: true }))')
merged_html = merged_html.replace('this.androidFlowStep = "email"', 'this.dispatchEvent(new CustomEvent("step-change", { detail: "email", bubbles: true, composed: true }))')
merged_html = merged_html.replace('this.androidFlowStep = "password"', 'this.dispatchEvent(new CustomEvent("step-change", { detail: "password", bubbles: true, composed: true }))')
merged_html = merged_html.replace('this.androidFlowStep = "name"', 'this.dispatchEvent(new CustomEvent("step-change", { detail: "name", bubbles: true, composed: true }))')
merged_html = merged_html.replace('this.androidFlowStep = "search_home";', 'this.dispatchEvent(new CustomEvent("step-change", { detail: "search_home", bubbles: true, composed: true }));')
merged_html = merged_html.replace('this.dispatchError(', 'this.dispatchEvent(new CustomEvent("error", { detail: ')
merged_html = re.sub(r'this\.dispatchEvent\(new CustomEvent\("error", \{ detail: (.*?)\)(.*?)', r'this.dispatchEvent(new CustomEvent("error", { detail: \1, bubbles: true, composed: true }))\2', merged_html)

ts_content = f"""import {{ LitElement, html }} from 'lit';
import {{ customElement, property }} from 'lit/decorators.js';
import {{ androidOnboardingFlowStyles }} from './android-onboarding-flow.styles';

@customElement('android-onboarding-flow')
export class AndroidOnboardingFlow extends LitElement {{
  static styles = androidOnboardingFlowStyles;

  @property({{ type: String }}) step = 'email';
  @property({{ type: String }}) androidEmail = '';
  @property({{ type: String }}) androidPassword = '';
  @property({{ type: String }}) androidName = '';
  @property({{ type: Boolean }}) androidOptInNews = false;
  @property({{ type: Boolean }}) androidOptInShare = false;

  private handleEmailInput(e: any) {{ this.androidEmail = e.target.value; this.dispatchEvent(new CustomEvent('update-email', {{ detail: this.androidEmail, bubbles: true, composed: true }})); }}
  private handlePasswordInput(e: any) {{ this.androidPassword = e.target.value; this.dispatchEvent(new CustomEvent('update-password', {{ detail: this.androidPassword, bubbles: true, composed: true }})); }}
  private handleNameInput(e: any) {{ this.androidName = e.target.value; this.dispatchEvent(new CustomEvent('update-name', {{ detail: this.androidName, bubbles: true, composed: true }})); }}
  private handleOptInNews(e: any) {{ this.androidOptInNews = e.target.checked; this.dispatchEvent(new CustomEvent('update-opt-in-news', {{ detail: this.androidOptInNews, bubbles: true, composed: true }})); }}
  private handleOptInShare(e: any) {{ this.androidOptInShare = e.target.checked; this.dispatchEvent(new CustomEvent('update-opt-in-share', {{ detail: this.androidOptInShare, bubbles: true, composed: true }})); }}

{merged_html.replace('@input=${(e: any) => this.androidEmail = e.target.value}', '@input=${this.handleEmailInput}')
            .replace('@input=${(e: any) => this.androidPassword = e.target.value}', '@input=${this.handlePasswordInput}')
            .replace('@input=${(e: any) => this.androidName = e.target.value}', '@input=${this.handleNameInput}')
            .replace('@change=${(e: any) => this.androidOptInNews = e.target.checked}', '@change=${this.handleOptInNews}')
            .replace('@change=${(e: any) => this.androidOptInShare = e.target.checked}', '@change=${this.handleOptInShare}')}
}}
"""

with open('./components/organisms/android-onboarding-flow.ts', 'w') as f:
    f.write(ts_content)

print("Generated android-onboarding-flow.ts")
