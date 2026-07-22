import os
from PIL import Image, ImageFilter
import numpy as np

def make_dark_logo_transparent(input_path, output_path):
    img = Image.open(input_path).convert("RGBA")
    data = np.array(img)
    
    # Calculate brightness / max RGB
    r, g, b, a = data[:, :, 0], data[:, :, 1], data[:, :, 2], data[:, :, 3]
    
    # Dark background detection: low RGB values (e.g. max(r,g,b) < 30)
    # Smooth transition for anti-aliasing around text/shapes
    max_rgb = np.maximum(np.maximum(r, g), b)
    
    # Alpha mask: 0 for max_rgb <= 15, 255 for max_rgb >= 45, linear transition in between
    alpha = np.clip((max_rgb.astype(float) - 15.0) / 30.0 * 255.0, 0, 255).astype(np.uint8)
    
    data[:, :, 3] = alpha
    result = Image.fromarray(data, mode="RGBA")
    result.save(output_path, "PNG")
    print(f"Saved transparent dark logo to {output_path}")

def make_light_logo_transparent(input_path, output_path):
    img = Image.open(input_path).convert("RGBA")
    data = np.array(img)
    
    r, g, b, a = data[:, :, 0], data[:, :, 1], data[:, :, 2], data[:, :, 3]
    
    # Light background detection: high RGB values (e.g. min(r,g,b) > 230)
    min_rgb = np.minimum(np.minimum(r, g), b)
    
    # Alpha mask: 0 for min_rgb >= 248, 255 for min_rgb <= 220, linear transition in between
    alpha = np.clip((248.0 - min_rgb.astype(float)) / 28.0 * 255.0, 0, 255).astype(np.uint8)
    
    data[:, :, 3] = alpha
    result = Image.fromarray(data, mode="RGBA")
    result.save(output_path, "PNG")
    print(f"Saved transparent light logo to {output_path}")

if __name__ == "__main__":
    dark_src = "/home/shaolin/.gemini/antigravity/brain/0887c58b-bbeb-4c60-83d9-3df619b034f4/media__1784694374370.png"
    light_src = "/home/shaolin/.gemini/antigravity/brain/0887c58b-bbeb-4c60-83d9-3df619b034f4/media__1784694377059.png"
    
    make_dark_logo_transparent(dark_src, "src/web/assets/mave_brand_dark.png")
    make_light_logo_transparent(light_src, "src/web/assets/mave_brand_light.png")
    
    make_dark_logo_transparent(dark_src, "app/src/main/res/drawable/mave_brand_dark.png")
    make_light_logo_transparent(light_src, "app/src/main/res/drawable/mave_brand_light.png")
