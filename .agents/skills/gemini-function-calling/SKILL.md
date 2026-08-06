---
name: gemini-function-calling
description: Best practices for implementing function calling with Gemini models in Kotlin/Android. Use when wiring LLM responses to app actions.
---

# Gemini Function Calling (Tool Use)

This skill provides the structure and patterns for defining and executing function calls (tools) using the Google Gemini APIs on Android (both Generative AI SDK and Vertex AI).

## Overview

Function calling allows Gemini to output structured JSON matching a schema you define, rather than just text. This is critical for connecting natural language interactions to actual app behavior (e.g., controlling a music player, generating an image, navigating).

## 1. Defining Tools and Schemas

Tools are defined using `FunctionDeclaration`. Use strict, descriptive schemas so the model understands exactly what to output.

```kotlin
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool

val tools = listOf(
    Tool.functionDeclarations(
        listOf(
            FunctionDeclaration(
                name = "generate_cover_art",
                description = "Generate or update the album cover art for the current track.",
                parameters = mapOf(
                    "prompt" to Schema.string("Visual description for the cover art"),
                    "hq" to Schema.boolean("Whether to use high quality generation")
                )
            ),
            FunctionDeclaration(
                name = "play_song",
                description = "Plays a specific song in the app.",
                parameters = mapOf(
                    "song_title" to Schema.string("The title of the song to play")
                )
            )
        )
    )
)
```

## 2. Setting Up the Generative Model

Pass the tools into the `generativeModel` configuration.

```kotlin
val generativeModel = Firebase.ai.generativeModel(
    modelName = "gemini-1.5-pro",
    systemInstruction = content { text("You are a helpful music assistant. Use tools when necessary.") },
    tools = tools
)
```

## 3. Parsing Function Calls

When the model responds, check if it returned a `FunctionCallPart`. If so, extract the name and arguments.

```kotlin
generativeModel.generateContentStream(prompt).collect { chunk ->
    val calls = chunk.candidates.firstOrNull()?.content?.parts
        ?.filterIsInstance<FunctionCallPart>() ?: emptyList()
        
    calls.forEach { call ->
        val functionName = call.name
        val args: Map<String, Any?> = call.args
        
        when (functionName) {
            "generate_cover_art" -> {
                val imagePrompt = args["prompt"] as? String ?: ""
                val hq = args["hq"] as? Boolean ?: false
                // Execute logic in your app...
            }
        }
    }
}
```

## 4. Replying with Function Responses (Multi-turn)

If you are maintaining a continuous chat history, you MUST return the result of the function call back to the model so it knows the action succeeded or failed.

```kotlin
// Example for Gemini Live/WebSocket or continuous history:
fun sendResponse(callId: String, name: String, result: JSONObject) {
    val json = JSONObject()
    val response = JSONObject()
    val functionResponse = JSONObject()
    
    functionResponse.put("id", callId)
    functionResponse.put("name", name)
    functionResponse.put("response", result)
    
    response.put("functionResponse", functionResponse)
    json.put("realtimeInput", response)
    
    webSocket?.send(json.toString())
}
```

## Best Practices

1.  **Be Descriptive:** The `description` field of `FunctionDeclaration` and `Schema` is your prompt for the tool. Write clear instructions on *when* the model should use it.
2.  **Handle Errors Gracefully:** Network calls or missing arguments can cause tool execution to fail. Always catch exceptions and return an error state (or notify the model of the failure) instead of crashing.
3.  **Validate Arguments:** Don't trust the model blindly. Check for nulls and cast safely using `as? String` or similar.
