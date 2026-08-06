import os

ui_dir = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui"

def refactor_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    original = content
    
    # Glassmorphic / overlays
    content = content.replace(".background(Color.White.copy", ".background(MaterialTheme.colorScheme.surface.copy")
    content = content.replace(".background(Color.White,", ".background(MaterialTheme.colorScheme.surface,")
    content = content.replace(".background(Color.White)", ".background(MaterialTheme.colorScheme.surface)")
    
    # Scrims and black backgrounds
    content = content.replace(".background(Color.Black.copy(alpha = 0.2f)", ".background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)")
    content = content.replace(".background(Color.Black.copy(alpha = 0.3f)", ".background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)")
    content = content.replace(".background(Color.Black.copy(alpha = 0.5f)", ".background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)")
    content = content.replace(".background(Color.Black.copy(alpha = 0.8f)", ".background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)")
    content = content.replace(".background(Color.Black.copy(alpha = 0.7f)", ".background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)")
    content = content.replace(".background(Color.Black)", ".background(MaterialTheme.colorScheme.background)")
    
    if content != original:
        # Check imports
        if "MaterialTheme.colorScheme" in content and "import androidx.compose.material3.MaterialTheme" not in content:
            lines = content.split('\n')
            for i, line in enumerate(lines):
                if line.startswith("package com."):
                    lines.insert(i + 1, "import androidx.compose.material3.MaterialTheme")
                    break
            content = '\n'.join(lines)
            
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Refactored {filepath}")

for root, _, files in os.walk(ui_dir):
    for f in files:
        if f.endswith(".kt"):
            refactor_file(os.path.join(root, f))
