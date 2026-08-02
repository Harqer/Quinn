import re
import os

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

print("File loaded, size:", len(content))
