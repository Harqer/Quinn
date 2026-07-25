import os
from PIL import Image

# Use the latest media file
input_image_path = "/home/shaolin/.gemini/antigravity/brain/ed2cfa89-a79f-4ce6-a393-557c94ac19d8/media__1784960900938.png"
if not os.path.exists(input_image_path):
    print(f"Error: {input_image_path} not found")
    exit(1)

sizes = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192
}

base_res_dir = "/home/shaolin/lyria/app/src/main/res"

img = Image.open(input_image_path).convert("RGBA")

for density, size in sizes.items():
    # Regular icon
    dir_path = os.path.join(base_res_dir, f"mipmap-{density}")
    if not os.path.exists(dir_path):
        os.makedirs(dir_path)
    
    resized_img = img.resize((size, size), Image.Resampling.LANCZOS)
    resized_img.save(os.path.join(dir_path, "ic_launcher.png"))
    
    # Round icon (we'll just use the same image but maybe crop it into a circle? 
    # Or just save it as ic_launcher_round.png. For now, just save as round.)
    
    # Create circular mask
    mask = Image.new('L', (size, size), 0)
    from PIL import ImageDraw
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size, size), fill=255)
    
    round_img = resized_img.copy()
    round_img.putalpha(mask)
    round_img.save(os.path.join(dir_path, "ic_launcher_round.png"))

print("Icons generated successfully!")
