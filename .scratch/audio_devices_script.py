import re

with open("/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel.kt", "r") as f:
    content = f.read()

# Check if AudioManager is used
print("MainViewModel length:", len(content))
