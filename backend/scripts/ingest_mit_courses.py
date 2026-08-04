import os
import json
import glob
from bs4 import BeautifulSoup
import PyPDF2

WRITER_DIR = "/home/shaolin/Downloads/writer/"
OUTPUT_FILE = "/home/shaolin/lyria/backend/scripts/mit_corpus.txt"

def extract_text_from_html(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            soup = BeautifulSoup(f.read(), 'html.parser')
            # Extract main content, usually in a div with id 'course-content' or similar, but getting all text is fine
            text = soup.get_text(separator='\n', strip=True)
            return text
    except Exception as e:
        print(f"Error reading HTML {file_path}: {e}")
        return ""

def extract_text_from_pdf(file_path):
    try:
        text = ""
        with open(file_path, 'rb') as f:
            reader = PyPDF2.PdfReader(f)
            for page in reader.pages:
                page_text = page.extract_text()
                if page_text:
                    text += page_text + "\n"
        return text
    except Exception as e:
        print(f"Error reading PDF {file_path}: {e}")
        return ""

def main():
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    
    total_html = 0
    total_pdf = 0
    
    with open(OUTPUT_FILE, 'w', encoding='utf-8', errors='replace') as out_f:
        for root, dirs, files in os.walk(WRITER_DIR):
            for file in files:
                file_path = os.path.join(root, file)
                
                if file.endswith('.html'):
                    text = extract_text_from_html(file_path)
                    if text:
                        out_f.write(f"\n\n--- Source: {file_path} ---\n\n")
                        out_f.write(text)
                        total_html += 1
                        
                elif file.endswith('.pdf'):
                    text = extract_text_from_pdf(file_path)
                    if text:
                        out_f.write(f"\n\n--- Source (PDF): {file_path} ---\n\n")
                        out_f.write(text)
                        total_pdf += 1
                        
    print(f"Extraction complete! Processed {total_html} HTML files and {total_pdf} PDF files.")
    print(f"Corpus saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    main()
