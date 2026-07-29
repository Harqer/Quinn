package com.musically.studio.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.FunctionDeclaration
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.Tool
import com.google.firebase.vertexai.type.content
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
    private val baseUrl = "http://10.0.2.2:8081" // Backend endpoint for emulators
    
    // Define the three primary tools for music generation and real-time tweaking
    private val tools = listOf(
        Tool(
            listOf(
                com.google.firebase.vertexai.type.defineFunction(
                    "generate_full_track",
                    "Generate a new, full professional music track or background score (Lyria 3).",
                    com.google.firebase.vertexai.type.Schema.str("prompt", "Musical style and description")
                ) { prompt -> org.json.JSONObject() },
                com.google.firebase.vertexai.type.defineFunction(
                    "tweak_instrumentation",
                    "Modify or tweak the instruments, density, or style of the current playing track (Lyria RealTime).",
                    com.google.firebase.vertexai.type.Schema.str("prompt", "What to tweak (e.g. add more bass, make it faster)")
                ) { prompt -> org.json.JSONObject() },
                com.google.firebase.vertexai.type.defineFunction(
                    "jam_live",
                    "Enter live jamming mode using a MIDI controller or live instrument input (MRT2).",
                    com.google.firebase.vertexai.type.Schema.str("intent", "The user intent for jamming")
                ) { intent -> org.json.JSONObject() }
            )
        )
    )
    
    // Initialize Vertex AI with the Live API model
    val generativeModel = Firebase.vertexAI.generativeModel(
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
