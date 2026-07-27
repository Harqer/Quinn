---
name: lyria-architecture
description: Project architecture and user flow for the Lyria app, covering Firebase native integration, Gemini Live API, and backend orchestration.
---

# Lyria Architecture & User Flow

This skill documents the core architectural decisions and user flows for the Lyria application. It must be used as the source of truth to ensure all future development aligns with the established Firebase-first approach.

## 1. Core Principles

- **Firebase-Native**: Always leverage Firebase SDKs directly on the clients (Web and Android) for Authentication, Databases (Firestore/RTDB), AppCheck, and AI. Avoid building custom Node.js middlemen for these services unless absolutely necessary.
- **Client-to-Edge AI**: The Gemini Live API must be connected to directly from the client using the **Firebase AI SDK (Vertex AI for Firebase)**. Do not route high-bandwidth bidirectional audio streams through the Node.js backend.
- **Backend as Executor**: The Node.js backend exists to orchestrate complex tasks, interface with Python microservices (e.g., MRT2 music generation), and execute tool calls on behalf of the client.

## 2. Frontend Architecture (Web & Android)

- **Web**: React frontend built with Vite.
- **Android**: Native Android application.
- **Security & Identity**: Both platforms use Firebase Auth for identity and Firebase AppCheck to secure API and cloud service calls.
- **Data (CRUD)**: All CRUD operations (Create, Read, Update, Delete) are handled natively on the clients using **Cloud Firestore** and/or **Firebase Realtime Database**. This ensures data is synchronized across clients in real-time and kept within the Google ecosystem.
- **AI Connectivity**: Both platforms use the Firebase AI SDK to instantiate and manage real-time bidirectional streaming sessions (Live API) directly with Google's edge.

## 3. Backend Architecture

- **Node.js Orchestrator** (`src/server.ts` / `src/index.ts`):
  - Exposes REST and WebSocket endpoints for specialized client-to-backend communication.
  - Receives forwarded tool execution requests from the client.
  - Utilizes **Firebase Genkit** for AI flow orchestration, managing complex agent interactions, and bridging the gap between Gemini models and custom local models.
- **Magenta Realtime (MRT2) Microservice** (`src/python-mrt2`):
  - A dedicated Docker container running Python for generating instrumented music using Spotify playlist embeddings and local/HuggingFace models (`mrt2_base`).
  - Integrated via Genkit flows from the Node.js backend to ensure all AI operations remain centralized and traceable within the Google developer ecosystem.

## 4. End-to-End User Flow & LLM Orchestration

The system supports multiple modes of communication: Web interface, native Android UI, and **Meta Wearables (Smart Glasses)**. The user flow details how raw live interaction is captured, processed, and transformed into generative music.

### Phase 1: Live Interaction & Capture
- **Meta Wearables Mode**: The user unfolds their Meta Smart Glasses. The Android app connects via Bluetooth and initiates a session. The glasses continuously stream both **camera frames** (visual context) and **real-time audio** (voice commands).
- **Web/Android App Mode**: The user interacts via standard microphone and screen UI.
- **Client-to-Edge AI**: In all modes, the client application uses the **Firebase AI SDK** to stream this raw audiovisual data directly to the **Gemini Live API**.

### Phase 2: AI Reasoning (Mave)
- Gemini operates as "Mave", the Executive Creative Director.
- It analyzes the streaming audio and camera frames (e.g., recognizing the user's physical environment, mood, or explicit requests).
- Mave engages in conversational, low-latency voice dialogue streamed back directly to the client (and outputted to the user's glasses/headphones).

### Phase 3: Tool Execution & Spotify Transformation
- When Mave determines the user wants to generate, modify, or transform a track, the Gemini Live API sends a structured `toolCall` (e.g., `generate_instrumentation`) directly to the **Client**.
- **Genkit Orchestration**: The client pauses the AI audio stream and forwards the tool payload to the Node.js Backend. The backend uses **Firebase Genkit** to manage this workflow.
- **Spotify Context**: Genkit retrieves the user's Spotify playlist embeddings (metadata and musical taste profile) from Firestore.
- **Generation via MRT2**: Genkit triggers the Python `mrt2` (Magenta Realtime) Docker microservice, passing the `toolCall` intent and the Spotify context. The `mrt2_base` model processes this to generate or transform the requested music.

### Phase 4: Fulfillment & Wearable UI
- **Delivery**: The Python container returns the generated audio asset (e.g., MP3/WAV URL) and/or generated visual vibe (e.g., video loop) back to the Node backend, which forwards it to the Client.
- **Glasses Rendering**: If using Meta Wearables, the Android app constructs a rendering payload using a root `flexBox` or `video(player = player)` and transmits it to the glasses using `display.sendContent`.
- **AI Acknowledgment**: Simultaneously, the Client submits a `toolResponse` back to the Gemini Live API session. Mave (Gemini) verbally acknowledges the generation (e.g., *"I just dropped a synth-heavy track inspired by your indie playlist and that sunset you're looking at. Give it a listen!"*).
