package com.musically.studio.service

import android.util.Log
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
import java.net.HttpURLConnection
import java.net.URL
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
        val token = auth.currentUser?.getIdToken(false)?.await()?.token
        
        val url = URL("$baseUrl/api/music/execute-tool")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        if (token != null) {
            connection.setRequestProperty("Authorization", "Bearer $token")
        }
        connection.doOutput = true
        
        val body = JSONObject().apply {
            put("name", name)
            put("args", JSONObject(args))
        }
        
        connection.outputStream.use { os ->
            os.write(body.toString().toByteArray())
        }
        
        val responseCode = connection.responseCode
        if (responseCode in 200..299) {
            val responseString = connection.inputStream.bufferedReader().readText()
            JSONObject(responseString)
        } else {
            val errorString = connection.errorStream?.bufferedReader()?.readText() ?: "{}"
            Log.e("LiveApiService", "Tool execution failed: $errorString")
            JSONObject().apply { put("error", errorString) }
        }
    }
}
