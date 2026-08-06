import os
import re

ui_dir = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui"

def refactor_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    original = content
    
    # 1. Replace Color.DarkGray backgrounds with MaterialTheme.colorScheme.surfaceVariant
    content = content.replace(".background(Color.DarkGray)", ".background(MaterialTheme.colorScheme.surfaceVariant)")
    content = content.replace(".background(Color.DarkGray,", ".background(MaterialTheme.colorScheme.surfaceVariant,")
    
    # 2. Replace Color(0xFF121212) container colors
    content = content.replace("containerColor = Color(0xFF121212)", "containerColor = MaterialTheme.colorScheme.surfaceContainerHigh")
    content = content.replace(".background(Color(0xFF121212))", ".background(MaterialTheme.colorScheme.surfaceContainerHigh)")
    
    # 3. Add necessary imports if we used MaterialTheme
    if "MaterialTheme.colorScheme" in content and "import androidx.compose.material3.MaterialTheme" not in content:
        # Find package declaration
        if "package com.musically.studio.ui" in content:
            content = content.replace("package com.", "import androidx.compose.material3.MaterialTheme\npackage com.", 1)
            # Wait, package declaration must be first. Better to put it after package.
            
    if content != original:
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Refactored {filepath}")

for root, _, files in os.walk(ui_dir):
    for f in files:
        if f.endswith(".kt"):
            refactor_file(os.path.join(root, f))
