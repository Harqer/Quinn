import os

ui_dir = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui"

for root, _, files in os.walk(ui_dir):
    for f in files:
        if f.endswith(".kt"):
            filepath = os.path.join(root, f)
            with open(filepath, 'r') as f_in:
                content = f_in.read()
                
            if content.startswith("import androidx.compose.material3.MaterialTheme\npackage com."):
                # Find the package line
                lines = content.split('\n')
                pkg_line = lines[1]
                lines[0] = pkg_line
                lines[1] = "import androidx.compose.material3.MaterialTheme"
                new_content = '\n'.join(lines)
                with open(filepath, 'w') as f_out:
                    f_out.write(new_content)
                print(f"Fixed imports in {filepath}")

