from bs4 import BeautifulSoup
import re

with open('scratch.html', 'r') as f:
    html = f.read()

# Extract just the body content
soup = BeautifulSoup(html, 'html.parser')
main_content = soup.body

# Remove script tags or style tags if any inside body
for s in main_content(['script', 'style']):
    s.extract()

# Find all tags and convert attributes
for tag in main_content.find_all(True):
    attrs = list(tag.attrs.items())
    for attr, value in attrs:
        if attr == 'class':
            tag['className'] = ' '.join(value)
            del tag['class']
        elif attr == 'stroke-linecap':
            tag['strokeLinecap'] = value
            del tag['stroke-linecap']
        elif attr == 'stroke-linejoin':
            tag['strokeLinejoin'] = value
            del tag['stroke-linejoin']
        elif attr == 'stroke-width':
            tag['strokeWidth'] = value
            del tag['stroke-width']
        elif attr == 'fill-rule':
            tag['fillRule'] = value
            del tag['fill-rule']
        elif attr == 'clip-rule':
            tag['clipRule'] = value
            del tag['clip-rule']
        elif attr == 'for':
            tag['htmlFor'] = value
            del tag['for']
        elif attr == 'viewbox':
            tag['viewBox'] = value
            del tag['viewbox']

html_str = str(main_content)

# Fix void elements (input, img, hr, br, etc)
void_elements = ['input', 'img', 'hr', 'br', 'meta', 'link', 'path']
for el in void_elements:
    html_str = re.sub(r'<(%s[^>]*?[^\/])>' % el, r'<\1/>', html_str)
    
# Also fix path tags that have closing tags
html_str = re.sub(r'</path>', '', html_str)

print(html_str)
