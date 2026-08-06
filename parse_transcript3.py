import json

with open("/home/shaolin/.gemini/antigravity/brain/073c2afb-2ee7-4ddf-9fe2-9c7b832f8b62/.system_generated/logs/transcript_full.jsonl") as f:
    for line in f:
        try:
            data = json.loads(line)
            if data.get("step_index") == 781:
                print("FOUND STEP 781")
                print(data["content"])
        except Exception:
            pass
