import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

# Add imports
imports = "import './organisms/android-onboarding-flow';\n"
content = re.sub(r'(import \{ LitElement)', imports + r'\1', content, count=1)

def remove_method(method_name, content):
    match = re.search(r'^\s*private\s+' + method_name + r'\s*\(\)\s*\{', content, re.MULTILINE)
    if not match:
        return content
    
    start_idx = match.start()
    brace_count = 0
    in_method = False
    end_idx = -1
    
    for i in range(start_idx, len(content)):
        if content[i] == '{':
            brace_count += 1
            in_method = True
        elif content[i] == '}':
            brace_count -= 1
            if in_method and brace_count == 0:
                end_idx = i + 1
                break
                
    if end_idx != -1:
        return content[:start_idx] + content[end_idx:]
    return content

content = remove_method('renderAndroidEmail', content)
content = remove_method('renderAndroidPassword', content)
content = remove_method('renderAndroidName', content)

# Replace rendering in renderAndroidCompanion
target = r'\$\{this\.androidFlowStep === "email" \? this\.renderAndroidEmail\(\) : ""\}\s*\$\{this\.androidFlowStep === "password" \? this\.renderAndroidPassword\(\) : ""\}\s*\$\{this\.androidFlowStep === "name" \? this\.renderAndroidName\(\) : ""\}'

replacement = r"""${["email", "password", "name"].includes(this.androidFlowStep) ? html`<android-onboarding-flow
              .step=${this.androidFlowStep}
              .androidEmail=${this.androidEmail}
              .androidPassword=${this.androidPassword}
              .androidName=${this.androidName}
              .androidOptInNews=${this.androidOptInNews}
              .androidOptInShare=${this.androidOptInShare}
              @step-change=${(e: any) => this.androidFlowStep = e.detail}
              @update-email=${(e: any) => this.androidEmail = e.detail}
              @update-password=${(e: any) => this.androidPassword = e.detail}
              @update-name=${(e: any) => this.androidName = e.detail}
              @update-opt-in-news=${(e: any) => this.androidOptInNews = e.detail}
              @update-opt-in-share=${(e: any) => this.androidOptInShare = e.detail}
            ></android-onboarding-flow>` : ""}"""

content = re.sub(target, replacement, content, count=1)

with open('./components/lyria_camera.ts', 'w') as f:
    f.write(content)

