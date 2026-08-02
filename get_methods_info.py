import re

with open('./components/lyria_camera.ts', 'r') as f:
    content = f.read()

props = re.findall(r'^\s*@property\([^\)]*\)\s*([a-zA-Z0-9_]+)', content, re.MULTILINE)
print(f"Number of properties: {len(props)}")

print("Lines count:")
lines = content.split('\n')
print(len(lines))

state = re.findall(r'^\s*@state\(\)\s*([a-zA-Z0-9_]+)', content, re.MULTILINE)
print(f"Number of state variables: {len(state)}")
