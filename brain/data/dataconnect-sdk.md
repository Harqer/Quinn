# Firebase DataConnect Integration

**Rule**: All DataConnect SDK updates require perfect GraphQL syntax.

## Principles
1. **Schema and Mutations**: Changes to DataConnect tables (`schema.gql`) and actions (`mutations.gql`) must be fully validated. A single syntax error will fail SDK compilation.
2. **SDK Generation**: Always run `firebase dataconnect:sdk:generate` after making changes to the `.gql` files.
3. **Android Kotlin DSL**: The DataConnect Android SDK generates a builder DSL for mutations. If your mutation has a parameter (e.g., `seedEpisode`), the SDK expects a builder block (e.g., `execute { seedEpisode = ... }`). Calling it as a direct parameter will result in compilation errors.
