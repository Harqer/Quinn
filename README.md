# Mave Studio - AI Music Orchestra

Mave is a real-time, multimodal AI music generation studio that acts as your personal orchestra. It generates original songs based on a variety of inputs, including live video, photos, text prompts, or natural voice commands. 

## Features

- **Multimodal Inspiration:** Generate songs based on what the AI sees through your camera (video or photos), what you type, or what you speak.
- **Real-Time Generation & Customization:** Compose music on the fly. You can dynamically adjust the song as it plays by issuing natural language commands—ask the AI to "add a clarinet", "make it more hipster", or change the pitch and tone.
- **Vocal & Instrumental Options:** Generate full tracks with or without vocals and lyrics.
- **Meta Wearables Integration:** Mave extends to Meta Wearables (like Ray-Ban Meta Smart Glasses) with the same powerful features as the Android app, plus the added ability to use physical gestures (like swipes or taps) to increase the pitch, beats, and other musical elements in real-time.

## Architecture

- **Android App:** Jetpack Compose frontend for Android phones.
- **Wearables:** Seamless integration with Meta Smart Glasses using the Meta Wearables SDK for real-time video/audio streaming and gesture control.
- **Backend Orchestrator:** LangGraph reasoning loop (`gemini-2.0-flash-exp`) handles conversational intent and routes requests.
- **Music Generation:** Powered by Google's Lyria models (`lyria-3-pro-preview` for full-length tracks, `lyria-realtime-exp` for real-time jamming).
