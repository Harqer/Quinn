import os
import json
import asyncio
from google import genai
from google.genai import types

CORPUS_FILE = "/home/shaolin/lyria/backend/scripts/mit_corpus.txt"
OUTPUT_FILE = "/home/shaolin/lyria/backend/scripts/mit_storytelling_framework.md"

# Configure the Gemini API client
# Assuming GOOGLE_API_KEY is set in the environment or available through default credentials
client = genai.Client()

def chunk_text(text, chunk_size=50000):
    """Splits a massive string into smaller chunks of roughly `chunk_size` characters."""
    return [text[i:i+chunk_size] for i in range(0, len(text), chunk_size)]

async def extract_framework_from_chunk(chunk_text, chunk_index):
    """Sub-LLM call (Depth=1) to process a specific chunk of the massive corpus."""
    prompt = f"""
    Analyze the following chunk of MIT writing/storytelling coursework.
    Extract any actionable storytelling rules, character development mechanics, narrative pacing templates, or structural frameworks.
    Ignore administrative details, syllabi, and student names. Focus only on the core writing advice.
    Format your output as a concise Markdown list of rules and templates.
    
    Coursework Chunk:
    {chunk_text}
    """
    
    try:
        print(f"Starting analysis for chunk {chunk_index}...")
        response = await client.aio.models.generate_content(
            model='gemini-2.5-flash',
            contents=prompt,
        )
        print(f"Finished analysis for chunk {chunk_index}.")
        return response.text
    except Exception as e:
        print(f"Error processing chunk {chunk_index}: {e}")
        return ""

async def main():
    if not os.path.exists(CORPUS_FILE):
        print(f"Error: {CORPUS_FILE} not found. Run ingest_mit_courses.py first.")
        return

    print("Loading massive corpus into REPL environment (Depth=0)...")
    with open(CORPUS_FILE, 'r', encoding='utf-8') as f:
        massive_prompt = f.read()

    print(f"Corpus loaded. Size: {len(massive_prompt)} characters.")
    
    # 1. RLM Logic: Split the massive environment into chunks
    chunks = chunk_text(massive_prompt)
    print(f"Split into {len(chunks)} chunks. Spawning sub-LLMs (Depth=1)...")
    
    # 2. Spawn Sub-LLMs using llm_batch equivalent (asyncio.gather)
    tasks = [extract_framework_from_chunk(chunk, i) for i, chunk in enumerate(chunks)]
    results = await asyncio.gather(*tasks)
    
    # 3. Combine the analyses
    print("Combining sub-LLM analyses into Master Storytelling Framework...")
    combined_analyses = "\n\n".join([res for res in results if res])
    
    # 4. Final synthesis by Root LLM (Optional, but good for cleanup)
    synthesis_prompt = f"""
    You are the Master Storytelling AI. I am providing you with raw storytelling rules extracted from MIT writing courses by various sub-agents.
    Synthesize these raw rules into a single, highly dense, beautiful Markdown document titled "The MIT Master Storytelling Framework".
    Organize it into clear sections (e.g., Narrative Arc, Character Development, Worldbuilding, Pacing).
    Remove duplicates and contradictions.
    
    Raw Extracted Rules:
    {combined_analyses}
    """
    
    print("Synthesizing final document...")
    final_response = await client.aio.models.generate_content(
        model='gemini-2.5-pro',
        contents=synthesis_prompt,
    )
    
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write(final_response.text)
        
    print(f"Success! Master Storytelling Framework saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    asyncio.run(main())
