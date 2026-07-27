---
name: lyria-architecture
description: Project architecture and user flow for the Lyria app, covering Firebase native integration, Gemini Live API, backend orchestration, and the distinct roles of Lyria 3 vs Magenta Realtime 2.
---

# Lyria Architecture & User Flow

This skill documents the core architectural decisions and user flows for the Lyria application. It must be used as the source of truth to ensure all future development aligns with the established Firebase-first approach.

## 1. Core Principles

- **Firebase-Native**: Always leverage Firebase SDKs directly on the clients (Web and Android) for Authentication, Databases (Firestore/RTDB), AppCheck, and AI. Avoid building custom Node.js middlemen for these services unless absolutely necessary.
- **Client-to-Edge AI**: The Gemini Live API must be connected to directly from the client using the **Firebase AI SDK (Vertex AI for Firebase)**. Do not route high-bandwidth bidirectional audio streams through the Node.js backend.
- **Backend as Executor**: The Node.js backend exists to orchestrate complex tasks, interface with Python microservices (e.g., MRT2 music generation), and execute tool calls on behalf of the client.

## 2. Music Generation Models (The "Tools")

Lyria utilizes two distinct Google DeepMind music generation models, which are exposed as **Tools (Function Calls)** that the central LLM (Mave) can invoke based on user intent:

### Google DeepMind Lyria 3 (Full-Track Composition)
- **Role**: Offline, full-track compositional model.
- **Use Case**: Deep creative composition, producing structured, cohesive, professional-grade full tracks (up to 3 minutes long). Acts as a high-end music generator for finishing complete pieces, background scores, or media tracks.
- **Output**: Generates longer-form, fully arranged musical pieces with natural narrative flow from note to note, complete vocals, lyrics, and complex genre-specific instrumentation.
- **Control**: Controlled via text descriptions, reference images, lyrical inputs, and deep stylistic/vocal parameters.

### Magenta RealTime 2 (MRT2) (Live Performance)
- **Role**: Live, low-latency performance instrument.
- **Use Case**: Interactive, playable AI instrument designed for real-time jamming. It continuously responds to MIDI controllers, text, and audio inputs to generate streaming audio live on-the-fly.
- **Output**: Focuses on continuous, real-time streaming generation rather than fixed songs with beginnings and ends.
- **Control**: Controlled via live MIDI, text style prompts, and audio inputs with minimal latency (~200ms end-to-end control delay).

## 3. Frontend Architecture (Web & Android)

- **Web**: React frontend built with Vite.
- **Android**: Native Android application.
- **Security & Identity**: Both platforms use Firebase Auth for identity and Firebase AppCheck to secure API and cloud service calls.
- **Data (CRUD)**: All CRUD operations (Create, Read, Update, Delete) are handled natively on the clients using **Cloud Firestore** and/or **Firebase Realtime Database**.
- **AI Connectivity**: Both platforms use the Firebase AI SDK to instantiate and manage real-time bidirectional streaming sessions (Gemini Live API) directly with Google's edge.

## 4. Backend Architecture

- **Node.js Orchestrator** (`src/server.ts` / `src/index.ts`):
  - Exposes REST and WebSocket endpoints for specialized client-to-backend communication.
  - Receives forwarded tool execution requests from the client.
  - Utilizes **Firebase Genkit** for AI flow orchestration, managing complex agent interactions, and acting as the harness that makes Lyria 3 and MRT2 accessible as tools to the LLM.
- **Magenta Realtime (MRT2) Microservice** (`src/python-mrt2`):
  - A dedicated Docker container running Python for generating live streaming music using Apple MLX.

## 5. End-to-End User Flow & LLM Orchestration

### Phase 1: Live Interaction & Capture
- **Client-to-Edge AI**: The client application (Web, Android, or Meta Wearables) uses the **Firebase AI SDK** to stream raw audiovisual data directly to the **Gemini Live API**.

### Phase 1 & 2: Live Interaction, Capture & AI Reasoning (Mave)
- **Modality-Based LLM Routing**: 
  - If the user communicates via **Voice** (real-time conversational audio), the client uses the **Gemini Live API** (Gemini 3.1 Flash Live).
  - If the user communicates via **Text** only, the client uses **Gemini 3.1 Pro** via standard text generation (Firebase AI Logic).
- In both modes, the LLM processes continuous streams of camera frames or videos, allowing the user to instantly work alongside the LLM as a multimodal musical assistant.
- Gemini operates as "Mave", the Executive Creative Director, reasoning over the text/voice and visual inputs.

### Phase 3: Tool Execution (Function Calling)
- When Mave determines the user wants to generate or modify music, it sends a structured `toolCall` (Function Call) directly to the **Client**.
  - **Lyria 3 Tool**: Invoked if the user requests a *new song, a finished track, or a background score*.
  - **Lyria RealTime Tool**: Invoked if the user *requests an instrument change or wants to tweak parts of the instrumentation* on an existing song (handling the prompt/parameter-driven real-time morphing).
  - **Magenta RealTime 2 (MRT2) Tool**: Invoked if the user plugs in a hardware MIDI controller to *jam or play an instrument live*.
- **Genkit Orchestration**: The client forwards the tool payload to the Node.js Backend. The backend uses **Firebase Genkit** (acting as the tool harness) to interface with the respective model (Lyria 3 API, Lyria RealTime API, or the local MRT2 Docker container).

### Phase 4: Fulfillment
- The generated audio asset (Lyria 3) or live audio stream (MRT2) is returned to the Node backend and forwarded to the Client.
- The Client submits a `toolResponse` back to the Gemini Live API session. Mave verbally acknowledges the generation.

### Lyria RealTime (Cloud API)
- **Role**: Cloud-backed, API-driven real-time generative music model.
- **Ecosystem**: Cloud API (Google AI Studio / Gemini API / WebSockets). Runs on Google's servers.
- **Latency**: ~2 seconds (chunk-based autoregression).
- **Control**: Text prompts (Weighted Prompt mixing), audio style hints, UI sliders (density, brightness, BPM).
- **Use Case**: Best for web apps, SaaS, and browser-based creative tools where you want real-time AI music without local hardware constraints. Excels at smoothly blending concepts on the fly via software interfaces.

## 2.1 Lyria 3 Semantic Understanding vs Real-Time Limitations
- **Lyria 3** possesses advanced semantic and musical understanding. If given a detailed, multi-layered prompt (genre, tempo, mood, lyrical themes, structure like verses/choruses/bridges), it translates conceptual text into a cohesive, professional 48kHz stereo track (up to 3 minutes).
- **The Catch**: It is **not** real-time. It operates offline as a batch generation engine. It cannot listen to a live microphone feed or respond instantly to live hardware MIDI keystrokes.
- **The Solution**: If live, sub-second contextual responsiveness is required (actively steering, warping, playing a continuous stream via keyboard or live controls), the LLM must invoke **Lyria RealTime** or **Magenta RealTime 2 (MRT2)** instead.
