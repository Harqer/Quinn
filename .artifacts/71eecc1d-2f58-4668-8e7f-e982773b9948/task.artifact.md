# task.md: Gemini Streaming & RTDB Integration

- [ ] `[/]` Research and Setup
    - [ ] Verify `firebase-admin/database` availability
    - [ ] Research Gemini `cachedContents` REST/SDK API for Node.js
- [ ] `[/]` Implementation
    - [ ] Update `src/config/firebase.ts` with RTDB
    - [ ] Update `src/services/ai.ts` with Context Caching logic
    - [ ] Refactor `src/services/quinn-graph.ts` for streaming compatibility
    - [ ] Refactor `src/services/MusicService.ts` to use `quinnGraph.stream()` and RTDB sync
- [ ] `[/]` Verification
    - [ ] Build and Test
    - [ ] Final push to main
