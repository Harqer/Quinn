# Lyria - AI Music Orchestra & Realtime Studio

Lyria is a real-time, multimodal AI music generation studio that acts as your personal orchestra. It generates, transforms, and fine-tunes original songs based on a variety of inputs, including live video, photos, text prompts, natural voice commands, and your personal Spotify taste.

## Core Features

- **Unified Cross-Platform Experience**: The core workflow is identical whether you are using the Web interface, the native Android app, or Meta Wearables (Smart Glasses). The only difference is the UI output, which dynamically adapts to specific platform design guidelines.
- **Visual & Multimodal Inspiration**: Generate custom songs based on what the AI sees. You can use your live camera stream (real-time video), snap a photo, or upload an image from your library to instantly generate a track that matches the visual vibe.
- **Real-Time Generation & Customization**: Compose and tweak full songs on the fly. You can dynamically adjust the track as it plays by issuing natural language voice commands or sending texts—e.g., "add a clarinet", "change the pitch", "adjust the tone", or "swap the instruments."
- **Vocal Assistance & Autotune**: The app can listen to you sing in real-time, providing live musical accompaniment and fine-tuning your voice with AI-driven autotune to make you sound perfect.
- **Deep Spotify Integration**:
  - Save your AI-generated songs directly to your Spotify playlists.
  - Automatically fetch your Spotify listening history and taste profile.
  - Generate new AI songs that perfectly match the vibe of your existing Spotify playlists.
- **Personalized "For You" Page**: Lyria constantly learns your unique style of music over time. It uses this context to curate a dedicated "For You" feed of entirely custom, AI-generated songs tailored specifically to your taste.

## Architecture & Technology Stack

- **Frontend**: React (Web) and Jetpack Compose (Android).
- **Meta Wearables**: Seamless integration with Meta Smart Glasses for real-time video/audio streaming and gesture control.
- **AI Connectivity**: Connects directly to the **Gemini Live API** from the clients using the **Firebase AI SDK (Vertex AI for Firebase)** for ultra-low latency, bidirectional communication.
- **Data & Security (CRUD)**: Fully integrated into the Google ecosystem. Firebase Auth for identity, Firebase AppCheck for security, and Cloud Firestore for all real-time CRUD operations and Spotify context storage.
- **Backend Orchestration**: Node.js backend utilizing **Firebase Genkit** to orchestrate complex AI workflows and bridge the gap between Gemini's tool calls and local models.
- **Music Generation (MRT2)**: Powered by a dedicated Dockerized Python microservice running **Magenta Realtime (MRT2)** to generate instrumented music dynamically based on Spotify embeddings.
