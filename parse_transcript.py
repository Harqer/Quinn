import json

with open("/home/shaolin/.gemini/antigravity/brain/073c2afb-2ee7-4ddf-9fe2-9c7b832f8b62/.system_generated/logs/transcript.jsonl") as f:
    for line in f:
        try:
            data = json.loads(line)
            if "content" in data and ("mock" in data["content"].lower() or "static" in data["content"].lower() or "hardcode" in data["content"].lower()):
                if data["type"] in ("PLANNER_RESPONSE", "USER_INPUT") and data["step_index"] < 1000:
                    print(f"{data['step_index']} {data['source']}: {data['content'][:200]}...")
        except json.JSONDecodeError:
            pass
