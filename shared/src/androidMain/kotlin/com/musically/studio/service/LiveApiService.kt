package com.musically.studio.service

import android.util.Log
import timber.log.Timber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import com.google.firebase.functions.functions
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class LiveApiService @Inject constructor(
    private val auth: FirebaseAuth
) {
    // Production Cloud Run URL (routed via Firebase Hosting) — defined in shared/build.gradle.kts
    private val baseUrl = com.musically.studio.shared.BuildConfig.API_BASE_URL
    
    // Define the three primary tools for music generation and real-time tweaking
    private val tools = listOf(
        com.google.firebase.ai.type.Tool.functionDeclarations(
            listOf(
                com.google.firebase.ai.type.FunctionDeclaration(
                    "generate_full_track",
                    "Generate a new, full professional music track or background score (Lyria 3).",
                    mapOf("prompt" to com.google.firebase.ai.type.Schema.string("Musical style and description"))
                ),
                com.google.firebase.ai.type.FunctionDeclaration(
                    "tweak_instrumentation",
                    "Modify or tweak the instruments, density, or style of the current playing track (Lyria RealTime).",
                    mapOf("prompt" to com.google.firebase.ai.type.Schema.string("What to tweak (e.g. add more bass, make it faster)"))
                ),
                com.google.firebase.ai.type.FunctionDeclaration(
                    "jam_live",
                    "Enter live jamming mode using a MIDI controller or live instrument input (MRT2).",
                    mapOf("intent" to com.google.firebase.ai.type.Schema.string("The user intent for jamming"))
                ),
                com.google.firebase.ai.type.FunctionDeclaration(
                    "generate_cover_image",
                    "Generate album/cover art for the track.",
                    mapOf("prompt" to com.google.firebase.ai.type.Schema.string("The coverArtPrompt to generate the image for."))
                ),
                com.google.firebase.ai.type.FunctionDeclaration(
                    "generate_music_video",
                    "Generate a music video synchronized with the generated music track.",
                    mapOf(
                        "prompt" to com.google.firebase.ai.type.Schema.string("The visual prompt for the music video."),
                        "audioUrl" to com.google.firebase.ai.type.Schema.string("The URL of the generated audio to synchronize the video with.")
                    )
                )
            )
        )
    )
    
    // Initialize Vertex AI with the Live API model
    val generativeModel = Firebase.ai.generativeModel(
        modelName = "gemini-3.1-flash-live-preview",
        systemInstruction = content { text("You are Mave, the Executive Creative Director and Master Musical Orchestrator. Help the user create and tweak music.") },
        tools = tools
    )

    /**
     * Proxies the function call from the Gemini Live stream to our Genkit backend harness.
     */
    suspend fun executeTool(name: String, args: Map<String, Any?>): JSONObject = withContext(Dispatchers.IO) {
        val payload = mapOf(
            "name" to name,
            "args" to args
        )
        try {
            val result = Firebase.functions.getHttpsCallable("executeTool").call(payload).await()
            val data = result.data as? Map<*, *>
            if (data != null) {
                JSONObject(data)
            } else {
                JSONObject().apply { put("result", result.data.toString()) }
            }
        } catch (e: Exception) {
            Timber.e(e, "Tool execution failed: ${e.message}")
            JSONObject().apply { put("error", e.message) }
        }
    }
}
