---
name: otter
description: Comprehensive audit and code quality engine covering Security Best Practices, Code Quality & Logic Integrity, Testing & Coverage Edge Cases, and Documentation & API Standards. Use when auditing, reviewing, or evaluating code across any repository or module for correctness, security hygiene, test coverage, and documentation completeness.
---

# Otter — Comprehensive Code & Systems Audit Skill

`otter` is a 4-pillar audit and engineering quality engine designed to inspect, evaluate, and elevate software codebases across Android, Kotlin Multiplatform, Go microservices, Cloud Functions, and Web surfaces.

## The 4 Audit Pillars of Otter

When executing an `otter` audit or invoking this skill, inspect the codebase across these four distinct domains:

```
                  ┌──────────────────────────────────────────┐
                  │               OTTER AUDIT                │
                  └────────────────────┬─────────────────────┘
                                       │
      ┌────────────────┬───────────────┴───────────────┬────────────────┐
      │                │                               │                │
┌─────▼──────┐  ┌──────▼─────┐                   ┌─────▼──────┐  ┌──────▼─────┐
│ 1. SECURITY│  │ 2. QUALITY │                   │ 3. TESTING │  │ 4. DOCS    │
│  HYGIENE   │  │  & LOGIC   │                   │ & COVERAGE │  │ & SCHEMAS  │
└────────────┘  └────────────┘                   └────────────┘  └────────────┘
```

1. **Security & Defensive Hygiene**: Secrets management, parameterization, authentication verification, input validation. (See [security-guidelines.md](references/security-guidelines.md)).
2. **Code Quality & Logic Integrity**: Correctness, concurrency/thread safety, resource leaks, code smells, complexity reduction. (See [quality-checklist.md](references/quality-checklist.md)).
3. **Testing & Coverage Edge Cases**: ViewModel/StateFlow coverage, edge condition handling, async/network failure tests, non-brittle assertions. (See [testing-matrix.md](references/testing-matrix.md)).
4. **Documentation & API Standards**: KDoc/GoDoc/JSDoc completeness, API contract specifications, architectural decision records (ADRs). (See [documentation-standards.md](references/documentation-standards.md)).

---

## Quick Workflow Guide

### Step 1: Scope & Categorize
Identify the target modules (`shared/`, `backend/`, `functions/`, `wear/`, `tv/`, `app/`).

### Step 2: Multi-Pillar Review
Run systematic checks using the corresponding reference document:
- **Security Check**: Verify secrets separation, auth guards, input sanitization ([references/security-guidelines.md](references/security-guidelines.md)).
- **Quality Check**: Inspect function lengths, coroutine scope safety, state updates ([references/quality-checklist.md](references/quality-checklist.md)).
- **Test Check**: Identify untested ViewModels, missing error state branches, boundary conditions ([references/testing-matrix.md](references/testing-matrix.md)).
- **Docs Check**: Verify public API comments, type docstrings, ADR records ([references/documentation-standards.md](references/documentation-standards.md)).

### Step 3: Produce Audit Matrix
Structure audit results using Otter's standard reporting format:

```markdown
### Otter Audit Matrix

| Pillar | File / Module | Line # | Finding / Smell | Severity | Proposed Fix |
|---|---|---|---|---|---|
| Security | `pkg/auth/middleware.go` | 42 | Missing token expiration fallback check | High | Validate exp claim before context propagation |
| Quality | `MyApplication.kt` | 34 | CoroutineScope missing SupervisorJob | Medium | Add SupervisorJob() to prevent scope cancellation |
| Testing | `EpisodeViewModel.kt` | 18 | Untested error state branch in Flow | High | Add unit test with mock error flow |
| Docs | `session_hub.go` | 12 | Exported type SessionHub missing GoDoc | Low | Add package GoDoc comment |
```

---

## Detailed References

- [Security Guidelines & Defensive Patterns](references/security-guidelines.md)
- [Code Quality, Logic & Complexity Reduction](references/quality-checklist.md)
- [Testing Matrix & Edge Case Strategy](references/testing-matrix.md)
- [Documentation & API Standards](references/documentation-standards.md)
