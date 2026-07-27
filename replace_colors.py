import os
import re

color_map = {
    "0xFF06B6D4": "com.musically.studio.ui.theme.MaveCyan500",
    "0xFF0A0A0A": "com.musically.studio.ui.theme.MaveGray900",
    "0xFF0A0C0E": "com.musically.studio.ui.theme.MaveGray850",
    "0xFF0D0F10": "com.musically.studio.ui.theme.MaveGray800",
    "0xFF121212": "com.musically.studio.ui.theme.MaveBackground",
    "0xFF121414": "com.musically.studio.ui.theme.MaveBackgroundVariant",
    "0xFF161820": "com.musically.studio.ui.theme.MaveBackgroundVariant2",
    "0xFF1A1C1C": "com.musically.studio.ui.theme.MaveBackgroundVariant3",
    "0xFF1A1D23": "com.musically.studio.ui.theme.MaveBackgroundVariant4",
    "0xFF1DB954": "com.musically.studio.ui.theme.MaveBrand",
    "0xFF1E2020": "com.musically.studio.ui.theme.MaveDarkSurface",
    "0xFF1E2430": "com.musically.studio.ui.theme.MaveDarkSurfaceVariant",
    "0xFF1ED760": "com.musically.studio.ui.theme.MaveGreenLight",
    "0xFF2196F3": "com.musically.studio.ui.theme.MaveBlue500",
    "0xFF252525": "com.musically.studio.ui.theme.MaveSurfaceVariant2",
    "0xFF282828": "com.musically.studio.ui.theme.MaveSurfaceContainer",
    "0xFF282A2B": "com.musically.studio.ui.theme.MaveSurfaceVariant3",
    "0xFF2A2D35": "com.musically.studio.ui.theme.MaveSurfaceVariant4",
    "0xFF2E2E2E": "com.musically.studio.ui.theme.MaveSurfaceVariant5",
    "0xFF37393A": "com.musically.studio.ui.theme.MaveSurfaceVariant6",
    "0xFF3F51B5": "com.musically.studio.ui.theme.MaveIndigo500",
    "0xFF4A5260": "com.musically.studio.ui.theme.MaveGray600",
    "0xFF5A6270": "com.musically.studio.ui.theme.MaveGray500",
    "0xFF8B5CF6": "com.musically.studio.ui.theme.MavePurple500",
    "0xFF8BC34A": "com.musically.studio.ui.theme.MaveLightGreen500",
    "0xFF9C27B0": "com.musically.studio.ui.theme.MavePurple700",
    "0xFF9E9E9E": "com.musically.studio.ui.theme.MaveGray400",
    "0xFF9EAABF": "com.musically.studio.ui.theme.MaveBlueGray400",
    "0xFFA7A7A7": "com.musically.studio.ui.theme.MaveGray300",
    "0xFFCDD5E0": "com.musically.studio.ui.theme.MaveBlueGray200",
    "0xFFE53935": "com.musically.studio.ui.theme.MaveRed600",
    "0xFFE5E2E1": "com.musically.studio.ui.theme.MaveGray200",
    "0xFFFF9800": "com.musically.studio.ui.theme.MaveOrange500",
    "0xFF4CAF50": "com.musically.studio.ui.theme.MaveGreen500"
}

dir_path = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/screens"

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    modified = False
    for hex_code, token in color_map.items():
        pattern = f'Color\({hex_code}\)'
        if re.search(pattern, content):
            content = re.sub(pattern, token, content)
            modified = True
            
    if modified:
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Updated {filepath}")

for root, _, files in os.walk(dir_path):
    for file in files:
        if file.endswith('.kt'):
            process_file(os.path.join(root, file))
