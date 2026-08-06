---
name: rlm-infinite-context
description: Use this skill whenever dealing with massive texts, entire books, full codebases, or any context that exceeds a standard context window. Instead of loading the entire text into the prompt, this skill guides the LLM to use Recursive Language Models (RLM) principles: keeping the context external and writing code (REPL, grep, slice) to search, filter, and spawn sub-agents to process the data chunk by chunk without suffering from context rot.
---

# Recursive Language Models (RLM) Context Strategy

You have been asked to process an extremely large document, book, or codebase that cannot or should not be stuffed entirely into your context window. You will act as a **Recursive Language Model (RLM)** controller.

Instead of reading the entire file directly, you will treat the massive text as an external environment that you interact with programmatically.

## Core Principles

1. **Keep Context External**: Do NOT use file-reading tools to dump millions of tokens into your context window. That leads to context rot and loss of reasoning accuracy.
2. **Programmatic Interaction**: Write Python code or use grep/bash commands to slice, dice, search, and extract only the relevant snippets of the document.
3. **Recursive Decomposition**: When a task requires reading a large chunk of text, spawn a sub-agent (or a separate LLM call) to process just that chunk, summarize it or extract the needed facts, and return the condensed information to you.

## Workflow

1. **Analyze the Request**: Understand what information needs to be extracted or processed from the massive text.
2. **Search and Localize**: Use search tools (`grep_search`) or write a quick Python script to find the specific chapters, sections, or line numbers relevant to the query.
3. **Chunking**: If the relevant section is still too large, write a Python script to chunk it into smaller segments (e.g., 5000 tokens each).
4. **Sub-Agent Delegation**: Spawn sub-agents (e.g., using `invoke_subagent`) to process each chunk in parallel. Give each sub-agent a specific, narrow prompt (e.g., "Extract all physical descriptions of the main character in this text").
5. **Aggregate**: Once the sub-agents return their condensed findings, combine them to form your final output.

## Example Scenario: Book to Movie Adaptation

If asked to maintain character consistency across a 300-page book to generate movie scenes:
- **Bad**: Trying to load all 300 pages into the prompt and asking "What does the character look like?"
- **Good**: 
  - Writing a script to `grep` for the character's name to find line numbers.
  - Slicing the text around those line numbers.
  - Spawning a sub-agent to read those slices and build a "Character Bible".
  - Using the Character Bible in your prompt for Veo 3 video generation.
