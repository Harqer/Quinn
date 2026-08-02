import re

with open('./components/organisms/android-search-results.ts', 'r') as f:
    content = f.read()
    
# Replace mock beatles array item
content = re.sub(r'\{ name: "Unknown Artist", type: "Artist", vibe: "classic pop rock harmonies and vintage chord progressions", img: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4\?w=100&auto=format&fit=crop&q=60" \},', '', content)
content = re.sub(r'if \(artist\.name === "Unknown Artist"\) \{.*?\n\s*\} else \{', '{', content, flags=re.DOTALL)
content = re.sub(r'\{ name: "The Beatles".*?\},', '', content)
content = re.sub(r'if \(artist\.name === "The Beatles"\) \{.*?\n\s*\} else \{', '{', content, flags=re.DOTALL)

with open('./components/organisms/android-search-results.ts', 'w') as f:
    f.write(content)

with open('./components/organisms/android-companion-view.ts', 'r') as f:
    content = f.read()
    content = re.sub(r'beatles_one_cover_simulated', 'default_album_cover', content)
    with open('./components/organisms/android-companion-view.ts', 'w') as f:
        f.write(content)
        
print("Fixed search results")
