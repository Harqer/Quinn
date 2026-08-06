import os

ui_dir = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui"

for root, _, files in os.walk(ui_dir):
    for f in files:
        if f.endswith(".kt"):
            filepath = os.path.join(root, f)
            with open(filepath, 'r') as f_in:
                content = f_in.read()
                
            if "MaterialTheme.colorScheme" in content and "import androidx.compose.material3.MaterialTheme" not in content:
                lines = content.split('\n')
                for i, line in enumerate(lines):
                    if line.startswith("package "):
                        lines.insert(i + 1, "import androidx.compose.material3.MaterialTheme")
                        break
                new_content = '\n'.join(lines)
                with open(filepath, 'w') as f_out:
                    f_out.write(new_content)
                print(f"Added import in {filepath}")

