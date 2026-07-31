import re
from bs4 import BeautifulSoup
import sys

def summarize_inspections(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"Failed to read file: {e}")
        return

    soup = BeautifulSoup(content, 'html.parser')
    
    # Find all labels that represent groups or inspections
    labels = soup.find_all('label')
    
    summary = []
    
    for label in labels:
        text = label.get_text()
        if 'inspection' in text or 'group' in text or 'ERROR' in text or 'WARNING' in text:
            # clean up the text
            clean_text = text.replace('\xa0', ' ').strip()
            # Only print top level or significant ones, we'll just collect them
            if clean_text:
                summary.append(clean_text)

    # Let's just output the lines that have a count of items to understand the distribution
    for line in summary:
        if '(' in line and 'items)' in line:
            print(line)
        elif 'warnings' in line or 'errors' in line or 'warning' in line or 'error' in line:
            print(line)

if __name__ == '__main__':
    summarize_inspections(sys.argv[1])
