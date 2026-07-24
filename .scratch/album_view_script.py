import re

with open("/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/screens/AlbumViewScreen.kt", "r") as f:
    content = f.read()

print("ModalBottomSheet" in content)
