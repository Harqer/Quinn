# Quinn - AI Music Generation Studio

Quinn is a real-time AI music generation application designed to transform how users create, interact with, and customize music. By interpreting the world around you or taking direct instructions, Quinn brings your imagination to life as a fully customizable, interactive orchestra.

## Features

### 🎧 Multimodal Music Generation
Quinn is powered by advanced AI capable of generating music from various inputs:
- **Vision to Music**: Generate songs based on what the app sees through your camera via video or photo.
- **Voice & Text to Music**: Use natural voice commands or text prompts to describe the music you want to create.

### 🎛️ Real-time Customization
You are the conductor. As your song plays, you can customize it dynamically in real-time:
- **Instrumentation**: Add or remove instruments on the fly (e.g., "add a clarinet" or "bring in heavy bass").
- **Vibe & Style**: Shift the genre or feeling instantly (e.g., "make it more hipster" or "change to a lo-fi beat").
- **Pitch & Tone**: Finely tune the pitch, tone, and tempo of the generated tracks.
- **Vocals**: Generate your tracks with AI-generated lyrics and vocals, or keep them as instrumental beats.

### 🕶️ Meta Wearables Integration
Quinn natively supports Meta Wearables for a truly immersive and hands-free musical experience. 
- Enjoy all the same Android features completely hands-free.
- **Gesture Control**: Use spatial gestures to seamlessly increase pitch, drop the beat, or change instruments dynamically while wearing the smart glasses.

## Architecture 

The app consists of a deeply integrated stack:
- **Frontend**: Built with modern Android UI (Jetpack Compose), featuring interactive screens, live generation displays, and real-time streaming services.
- **Wearables Pipeline**: Features a background native Android service `WearableStreamingService` to handle camera frames and gestures independent of the UI layer for smooth, low-latency performance.
- **Backend Node Server**: A centralized Node.js/TypeScript backend handles AI aggregation, Spotify integrations, live WebSockets for real-time music transmission, and complex processing routes.

## Setup & Deployment

### Android
The Android client targets the latest SDKs and handles all UI and hardware integration.
```bash
# Build and run the Android app via Gradle
./gradlew assembleDebug
./gradlew lintDebug
```

### Backend
The centralized backend relies on Firebase and a Node.js API environment for data processing, real-time sync, and integrations.
```bash
# Install dependencies
npm install

# Run the backend
npm run dev
```
