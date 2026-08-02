import re
import glob

files = glob.glob('./components/organisms/*.ts') + ['./components/lyria_camera.ts']

for file in files:
    with open(file, 'r') as f:
        content = f.read()

    # Replace specific mock tracks with generic ones
    content = content.replace('"The Beatles"', 'this.androidActiveSongArtist || "Unknown Artist"')
    content = content.replace('beatles_one_cover_simulated', 'default_album_cover')
    content = content.replace('this.playBeatlesTrack', 'this.playTrack')
    content = content.replace('playBeatlesTrack:', 'playTrack:')
    
    with open(file, 'w') as f:
        f.write(content)

print("Removed Beatles references")
