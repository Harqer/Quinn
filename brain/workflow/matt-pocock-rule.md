# Matt Pocock Rule (Red Light, Yellow Light, Green Light)

**Rule**: There isn't a line of code that should ever be implemented without cross-reference and verification.

## Principles
1. **Verification**: Always double-check library specifics, framework requirements, and platform APIs (like Android Wear/TV or Vite/Lit) before committing code.
2. **Ambiguity Tolerance**: If the probability of an approach being correct drops, stop and research (red light/yellow light). Do not guess or hallucinate.
3. **Execution**: Once confident, proceed (green light) while maintaining testability and following the architecture constraints (no mocks, M3 compose, etc.).
