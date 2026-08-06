import os
import re

ui_dir = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui"

for root, dirs, files in os.walk(ui_dir):
    for f in files:
        if f.endswith(".kt"):
            filepath = os.path.join(root, f)
            with open(filepath, 'r') as file:
                content = file.read()
            
            original_content = content
            
            # Replace .clickable { to .debouncedClickable {
            # But wait, clickable can be .clickable(onClick = ...) or .clickable(interactionSource = ..., onClick = ...)
            # Let's just replace all instances of ".clickable" with ".debouncedClickable"
            # except import androidx.compose.foundation.clickable
            content = re.sub(r'(?<!import androidx\.compose\.foundation)\.clickable', '.debouncedClickable', content)
            
            if content != original_content:
                # Add import if missing
                if "import com.musically.studio.ui.utils.debouncedClickable" not in content:
                    # Find last import
                    import_idx = content.rfind("import ")
                    if import_idx != -1:
                        end_of_line = content.find("\n", import_idx)
                        content = content[:end_of_line] + "\nimport com.musically.studio.ui.utils.debouncedClickable" + content[end_of_line:]
                    else:
                        content = "import com.musically.studio.ui.utils.debouncedClickable\n" + content
                
                with open(filepath, 'w') as file:
                    file.write(content)
