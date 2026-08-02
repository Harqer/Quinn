import re

with open('./components/organisms/android-album-details.ts', 'r') as f:
    content = f.read()

# Replace hardcoded track list
old_tracks = """const tracks = [
      { id: 1, title: "Love Me Do - Mono / Remastered", vibe: "upbeat retro 60s pop with harmonica" },
      { id: 2, title: "From Me to You - Mono / Remastered", vibe: "classic beatles pop rock harmonies" },
      { id: 3, title: "She Loves You - Mono / Remastered", vibe: "energetic early rock with vocal harmonies" },
      { id: 4, title: "I Want To Hold Your Hand - Remastered", vibe: "joyful 60s pop rock classic" }
    ];"""

new_tracks = """const tracks = [
      { id: 1, title: "Generative Track 1", vibe: "upbeat retro synth with smooth bass" },
      { id: 2, title: "Generative Track 2", vibe: "classic pop rock harmonies" },
      { id: 3, title: "Generative Track 3", vibe: "energetic early rock with vocal harmonies" },
      { id: 4, title: "Generative Track 4", vibe: "joyful pop rock classic" }
    ];"""
    
content = content.replace(old_tracks, new_tracks)
content = re.sub(r'THE BEATLES', 'GENERATIVE', content)
content = re.sub(r'The Beatles', 'Unknown Artist', content)
content = re.sub(r'beatles_one_cover_simulated', 'default_album_cover', content)
content = re.sub(r'playBeatlesTrack', 'playTrack', content)

with open('./components/organisms/android-album-details.ts', 'w') as f:
    f.write(content)

with open('./components/lyria_camera.ts', 'r') as f:
    lyria = f.read()
    lyria = re.sub(r'playBeatlesTrack', 'playTrack', lyria)
    with open('./components/lyria_camera.ts', 'w') as f:
        f.write(lyria)

with open('./components/organisms/android-expanded-player.ts', 'r') as f:
    content = f.read()
    content = re.sub(r'THE BEATLES', 'GENERATIVE', content)
    content = re.sub(r'The Beatles', 'Unknown Artist', content)
    with open('./components/organisms/android-expanded-player.ts', 'w') as f:
        f.write(content)

with open('./components/organisms/android-options-menu.ts', 'r') as f:
    content = f.read()
    content = re.sub(r'THE BEATLES', 'GENERATIVE', content)
    content = re.sub(r'The Beatles', 'Unknown Artist', content)
    with open('./components/organisms/android-options-menu.ts', 'w') as f:
        f.write(content)
        
print("Fixed album details")
