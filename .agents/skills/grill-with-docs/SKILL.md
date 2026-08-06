---
name: grill-with-docs
description: "Grills the user interactively with documentation-focused questions to clarify requirements or test their knowledge."
---

# Grill With Docs

This skill is designed to interactively quiz or grill the user using existing documentation.

## Workflow
1. Load relevant documentation for the current task.
2. Ask the user precise, targeted questions (using the ask_question tool if appropriate, or standard chat).
3. Wait for the user to respond before proceeding.
4. Correct the user if their assumptions violate the documentation.
