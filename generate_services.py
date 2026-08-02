import re

def process_service(name, file_name, methods):
    with open(f'./services/{file_name}.txt', 'r') as f:
        content = f.read()
    
    # Replace `private/public name(` with `public name(component: any, `
    # But wait, it's easier to just replace `this.` with `component.` inside the function body
    # And define the methods.
    
    new_methods = []
    
    for method in methods:
        # Extract the method from content
        match = re.search(r'^\s*(?:private\s+)?(?:async\s+)?' + method + r'\s*\([^)]*\)\s*(?::\s*[a-zA-Z0-9_<>]+)?\s*\{', content, re.MULTILINE)
        if not match: continue
        start_idx = match.start()
        brace_count, in_method, end_idx = 0, False, -1
        for i in range(start_idx, len(content)):
            if content[i] == '{': brace_count += 1; in_method = True
            elif content[i] == '}':
                brace_count -= 1
                if in_method and brace_count == 0: end_idx = i + 1; break
                
        if end_idx != -1:
            code = content[start_idx:end_idx]
            
            # Replace signature
            # e.g. `private playInstrumentSynth() {` -> `public playInstrumentSynth(component: any) {`
            sig_match = re.search(r'^\s*(?:private\s+)?(async\s+)?(' + method + r')\s*\(([^)]*)\)\s*(?::\s*[a-zA-Z0-9_<>]+)?\s*\{', code)
            
            if sig_match:
                is_async = sig_match.group(1) or ""
                m_name = sig_match.group(2)
                args = sig_match.group(3)
                
                new_args = "component: any" if not args.strip() else f"component: any, {args}"
                new_sig = f"  public {is_async}{m_name}({new_args}) {{"
                
                body = code[sig_match.end():]
                # We need to replace `this.` with `component.` inside the body.
                # However, there might be nested functions (arrow functions) that use `this`.
                # In TS/JS, replacing `this.` with `component.` is perfectly fine.
                # But wait, we shouldn't replace `this.` if it refers to something else? In these arrow functions, `this` is the outer `this`.
                body = re.sub(r'\bthis\.', 'component.', body)
                
                new_methods.append(new_sig + body)

    ts_content = f"""export class {name} {{
{chr(10).join(new_methods)}
}}
"""
    with open(f'./services/{name}.ts', 'w') as f:
        f.write(ts_content)
    print(f"Generated {name}.ts")

process_service('AudioSynthService', 'audio_methods', ['playInstrumentSynth', 'updateInstrumentSynth', 'triggerBeatHaptic'])
process_service('CanvasRendererService', 'canvas_methods', ['drawSimulationFrame', 'drawFluidWave', 'renderGesturePadCanvas', 'renderVisualizerCanvas', 'getDominantFrequencyColors'])
process_service('LyriaApiService', 'api_methods', ['startVoiceRecording', 'submitAndroidVibeCommand', 'submitTextCommand', 'generateFromFrame', 'shareVibeToCommunity'])
