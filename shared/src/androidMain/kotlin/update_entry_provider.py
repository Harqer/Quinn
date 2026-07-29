import os

file_path = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/navigation/EntryProvider.kt"

with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
"""            onLyricsClick = { 
                navigator.navigate(Route.Lyrics(track?.id ?: ""))
            }
        )""",
"""            onLyricsClick = { 
                navigator.navigate(Route.Lyrics(track?.id ?: ""))
            },
            onDeviceClick = {
                navigator.navigate(Route.Devices)
            }
        )""")

with open(file_path, "w") as f:
    f.write(content)
print("Updated EntryProvider.kt")
