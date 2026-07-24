import re

with open("/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/screens/NowPlayingScreen.kt", "r") as f:
    content = f.read()

# Just print the first 200 lines to see the signature
print(content[:500])
