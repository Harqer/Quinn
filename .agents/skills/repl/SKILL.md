---
name: repl
description: Guide for using Recursive Language Models (RLMs) via Python REPL to handle near-infinite context windows without information loss by delegating to sub-LLMs. Use when you need to process massive documents, do complex reasoning over long context, or extract structured data from huge strings.
---

# Recursive Language Models (RLM) / Infinite Context REPL

This skill provides the paradigm and best practices for utilizing Recursive Language Models (RLMs) to handle near-infinite context windows. Instead of loading an entire massive document into the context window (which leads to quadratic memory cost, context rot, and "lost in the middle" degradation), RLMs treat the massive prompt as an external environment that the model can programmatically explore using a Python REPL.

## Core Concepts

1. **Python REPL Environment**: The massive input is loaded into a Python REPL as a single string variable (e.g., `P`).
2. **Root LLM (Depth = 0)**: The main agent never sees the full document. It only receives instructions to inspect `P` programmatically and delegate tasks.
3. **Sub-LLMs (Depth = 1)**: The root LLM spawns sub-LLMs using a batch function (e.g., `llm_batch`) to process specific chunks of `P` in parallel.

## When to Use RLMs
**DO USE RLMs for:**
- Complex reasoning over long context (multi-hop synthesis, codebase comprehension).
- Structured data extraction (JSON/CSV processing, form filling) from huge strings.
- Tasks requiring dense access to massive documents.

**DO NOT USE RLMs for:**
- Simple retrieval or keyword search.
- Short contexts (under 100K tokens).
- Real-time applications requiring < 1s latency guarantees.

## Critical Constraint: DO NOT EXCEED DEPTH = 1
- **Why?** Deeper recursion (Depth > 1) currently causes "overthinking", exponential cost explosions, format collapse, and infinite loops.
- **Rule**: Only spawn sub-LLMs from the Root LLM. Do not allow sub-LLMs to spawn further sub-LLMs.

## Implementation Guide

Use the `rlm` library (e.g., `pip install rlm` or `pip install "rlm[prime]"`).

### Basic Usage Example

```python
from rlm import RLMChatCompletion

# 1. Load the massive document
with open('giant_document.txt') as f:
    huge_text = f.read()

# 2. Configure RLM (CRITICAL: max_depth MUST be 1)
rlm_config = {
    "max_depth": 1,           # NEVER use depth > 1
    "max_iterations": 50,     # Max root iterations
    "sandbox": "docker",      # Isolated execution
    "timeout": 300,           # seconds
    "sub_model": "gpt-5-mini",# Cheaper model for sub-tasks
    "parallel_limit": 10,     # Max parallel sub-calls
    "token_budget": 100000,   # Cost ceiling
    "early_stopping": True
}

# 3. Execute RLM call
response = RLMChatCompletion.create(
    model="gpt-5", # Root model
    messages=[{
        "role": "user",
        "content": f"Analyze this text and extract all pricing models: {huge_text}"
    }],
    rlm_config=rlm_config
)

print(response.choices[0].message.content)
```

### Under the Hood (How the Root LLM should behave)
If you are acting as the Root LLM in a REPL, write Python code like this to handle `P`:

```python
import re

# Inspect metadata (don't print all of P)
length = len(P)

# Strategically search and extract chunks
matches = re.findall(r'\$\d+(?:\.\d{2})?', P)
positions = [P.find(m) for m in matches]

# Get context around each price
contexts = [P[max(0,pos-200):pos+200] for pos in positions]

# Delegate to sub-LLMs using a batch processing function
analyses = llm_batch([
    f"What is this price for? Context: {ctx}" 
    for ctx in contexts
])

# Combine results and output the final answer
answer = combine_analyses(analyses)
```
