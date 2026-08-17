# Subagent Delegation

**Rule**: Use subagents for broad refactors or component-by-component audits.

## Principles
1. **Parallel Execution**: For independent tasks (e.g., updating Web UI and updating Android UI), use `invoke_subagent` to spawn agents with scoped tasks.
2. **Centralized Task Management**: Use a central artifact like `task.md` or `ui_action_audit.md` to communicate progress and remaining work between the primary agent and subagents.
3. **Frictionless Fixing**: When auditing large numbers of files for a specific issue (e.g., removing mocks), delegate the file-by-file fixing to a specialized subagent to keep the primary orchestrator's context window clean.
