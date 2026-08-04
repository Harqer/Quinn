import os
from PIL import Image

src_img = "/home/shaolin/.gemini/antigravity/brain/fa09298c-9755-4879-b0dd-dbaf22ae499f/media__1785825769527.png"
dest_play_store = "/home/shaolin/lyria/play_store_icon.png"

# Sizes for Android mipmap
sizes = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192
}

try:
    img = Image.open(src_img)
    # Ensure it's RGBA
    img = img.convert("RGBA")
    
    # Save Play Store 512x512 icon
    img_512 = img.resize((512, 512), Image.Resampling.LANCZOS)
    img_512.save(dest_play_store)
    print(f"Saved {dest_play_store}")
    
    # Generate mipmaps
    for density, size in sizes.items():
        mipmap_dir = f"/home/shaolin/lyria/app/src/main/res/mipmap-{density}"
        os.makedirs(mipmap_dir, exist_ok=True)
        out_path = os.path.join(mipmap_dir, "ic_launcher.png")
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        resized.save(out_path)
        print(f"Saved {out_path}")
        
except Exception as e:
    print(f"Error: {e}")
