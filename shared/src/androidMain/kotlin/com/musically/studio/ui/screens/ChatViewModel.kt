package com.musically.studio.ui.screens

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.musically.studio.data.repository.DataConnectRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.content

data class MaveChatTrack(
    val title: String,
    val artist: String,
    /** Data Connect track ID — populated after generation persists to Cloud SQL via Data Connect. */
    val trackId: String? = null
)

data class MaveChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val tracks: List<MaveChatTrack>? = null,
    val audioUrl: String? = null,
    val coverArtUrl: String? = null,
    val videoUrl: String? = null,
    val type: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val dataConnectRepository: DataConnectRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<MaveChatMessage>>(emptyList())
    val messages: StateFlow<List<MaveChatMessage>> = _messages.asStateFlow()

    private val tools = listOf(
        com.google.firebase.vertexai.type.Tool(
            listOf(
                com.google.firebase.vertexai.type.defineFunction(
                    "generate_full_track",
                    "Generate a new, complete professional music track or song using Lyria 3. Use when the user wants a full song created from scratch.",
                    com.google.firebase.vertexai.type.Schema.str("prompt", "Musical style, genre, and description of the full song to create")
                ) { prompt -> JSONObject() },
                com.google.firebase.vertexai.type.defineFunction(
                    "tweak_instrumentation",
                    "Modify or tweak the instruments, density, BPM, brightness, or style of the currently playing track in real-time using Lyria RealTime. Use when the user wants to change how the song sounds without regenerating from scratch.",
                    com.google.firebase.vertexai.type.Schema.str("prompt", "What to tweak (e.g. add more bass, make it faster, add jazz piano)")
                ) { prompt -> JSONObject() },
                com.google.firebase.vertexai.type.defineFunction(
                    "generate_cover_art",
                    "Generate or update the album cover art for the current track.",
                    com.google.firebase.vertexai.type.Schema.str("prompt", "Visual description for the cover art")
                ) { prompt -> JSONObject() },
                com.google.firebase.vertexai.type.defineFunction(
                    "generate_video",
                    "Generate a music video for the current track. Only use when the user explicitly asks for a video.",
                    com.google.firebase.vertexai.type.Schema.str("prompt", "Visual and cinematic description for the music video")
                ) { prompt -> JSONObject() }
            )
        )
    )

    private val generativeModel = com.google.firebase.Firebase.vertexAI.generativeModel(
        modelName = "gemini-3.1-flash",
        systemInstruction = com.google.firebase.vertexai.type.content { text("You are Mave, the Executive Creative Director and Master Musical Orchestrator. Please put your thoughts in <think> and </think> tags. Use maximum reasoning effort and ultrathink step by step. Provide a raw, unstructured, stream-of-consciousness thinking process. Do NOT use numbered lists or formal steps. Do NOT prefix with 'Thinking Process:'. After the closing </think> tag, respond in natural, conversational text ONLY. Do NOT use any markdown formatting.") },
        tools = tools
    )

    init {
        viewModelScope.launch {
            loadChatHistory()
        }
    }

    private suspend fun loadChatHistory() {
        try {
            val res = httpGet("/api/chat/history")
            if (res.has("messages")) {
                val msgsArray = res.getJSONArray("messages")
                if (msgsArray.length() > 0) {
                    val loadedMsgs = mutableListOf<MaveChatMessage>()
                    for (i in 0 until msgsArray.length()) {
                        val obj = msgsArray.getJSONObject(i)
                        loadedMsgs.add(
                            MaveChatMessage(
                                id = obj.optString("id", i.toString()),
                                sender = obj.optString("sender", "ai"),
                                text = obj.optString("text", "")
                            )
                        )
                    }
                    _messages.value = loadedMsgs
                    return
                }
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Failed to load chat history", e)
        }
        
        // Fallback to welcome message
        _messages.value = listOf(
            MaveChatMessage(
                id = "0",
                sender = "ai",
                text = "Hi! I'm Mave, your personal audio curator. How can I help you today?"
            )
        )
    }

    private fun saveChatHistory() {
        val currentMessages = _messages.value
        if (currentMessages.isEmpty()) return
        
        viewModelScope.launch {
            try {
                val msgsArray = org.json.JSONArray()
                for (msg in currentMessages) {
                    val obj = JSONObject()
                    obj.put("id", msg.id)
                    obj.put("sender", msg.sender)
                    obj.put("text", msg.text)
                    msgsArray.put(obj)
                }
                val body = JSONObject()
                body.put("messages", msgsArray)
                httpPost("/api/chat/history", body)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to save chat history", e)
            }
        }
    }

    // Derives HTTP base URL from the WS_BASE_URL build config
    private fun getBaseUrl(): String =
        com.musically.studio.shared.BuildConfig.WS_BASE_URL
            .replace("ws://", "http://")
            .replace("wss://", "https://")
            .removeSuffix("/api/music/ws")

    private suspend fun httpPost(path: String, body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        val baseUrl = getBaseUrl()
        val token = try { com.musically.studio.network.TokenManager.getValidToken() } catch (e: Exception) { null }
        val url = java.net.URL("$baseUrl$path")
        val connection = url.openConnection() as java.net.HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        if (token != null) connection.setRequestProperty("Authorization", "Bearer $token")
        connection.doOutput = true
        connection.outputStream.use { it.write(body.toString().toByteArray()) }
        val code = connection.responseCode
        if (code in 200..299) {
            val responseString = connection.inputStream.bufferedReader().use { it.readText() }
            if (responseString.isEmpty()) JSONObject() else JSONObject(responseString)
        } else {
            throw Exception("HTTP $code")
        }
    }

    private suspend fun httpGet(path: String): JSONObject = withContext(Dispatchers.IO) {
        val baseUrl = getBaseUrl()
        val token = try { com.musically.studio.network.TokenManager.getValidToken() } catch (e: Exception) { null }
        val url = java.net.URL("$baseUrl$path")
        val connection = url.openConnection() as java.net.HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")
        if (token != null) connection.setRequestProperty("Authorization", "Bearer $token")
        
        val code = connection.responseCode
        val text = if (code in 200..299) connection.inputStream.bufferedReader().readText()
                   else connection.errorStream?.bufferedReader()?.readText() ?: "{\"error\":\"HTTP $code\"}"
        JSONObject(text)
    }

    private suspend fun executeTool(name: String, args: Map<String, Any?>): JSONObject {
        val body = JSONObject().apply {
            put("name", name)
            put("args", JSONObject(args))
        }
        return httpPost("/api/music/execute-tool", body)
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = MaveChatMessage(id = System.currentTimeMillis().toString(), sender = "user", text = text.trim())
        _messages.value = _messages.value + userMsg
        saveChatHistory()

        val responseId = (System.currentTimeMillis() + 1).toString()
        var fullText = ""
        var addedEmptyMessage = false

        viewModelScope.launch {
            try {
                generativeModel.generateContentStream(text.trim()).collect { chunk ->
                    chunk.functionCalls.forEach { call ->
                        addedEmptyMessage = true
                        handleToolCall(call.name, call.args)
                    }
                    val chunkText = chunk.text ?: ""
                    if (chunkText.isNotEmpty()) {
                        fullText += chunkText
                        if (!addedEmptyMessage) {
                            _messages.value = _messages.value + MaveChatMessage(id = responseId, sender = "ai", text = fullText)
                            addedEmptyMessage = true
                        } else {
                            _messages.value = _messages.value.map { if (it.id == responseId) it.copy(text = fullText) else it }
                        }
                    }
                }
                saveChatHistory()
            } catch (e: Exception) {
                Log.e("ChatViewModel", "sendMessage failed", e)
                var errMsg = e.message ?: "Unknown error"
                if (errMsg.contains("429") || errMsg.contains("quota", ignoreCase = true) || errMsg.contains("RESOURCE_EXHAUSTED")) {
                    errMsg = "Quota reached. Please try again later."
                } else if (errMsg.contains("503") || errMsg.contains("overloaded", ignoreCase = true)) {
                    errMsg = "Server overloaded. Please try again later."
                } else if (errMsg.startsWith("{") || errMsg.contains("\"error\"")) {
                    errMsg = "An unexpected error occurred. Please try again."
                }
                if (addedEmptyMessage) {
                    _messages.value = _messages.value.map {
                        if (it.id == responseId) it.copy(text = "[Error] Error: $errMsg") else it
                    }
                } else {
                    _messages.value = _messages.value + MaveChatMessage(
                        id = responseId,
                        sender = "ai",
                        text = "[Error] Error: $errMsg"
                    )
                }
                saveChatHistory()
            }
        }
    }

    private fun handleToolCall(name: String, args: Map<String, Any?>) {
        viewModelScope.launch {
            try {
                when (name) {
                    "generate_full_track" -> {
                        val result = executeTool(name, args)
                        val resObj = result.optJSONObject("result")
                        val audioUrl = resObj?.optString("audioUrl")
                        val trackName = resObj?.optString("trackName", "New Track (Lyria 3)") ?: "New Track (Lyria 3)"
                        val artistName = resObj?.optString("artistName", "Mave") ?: "Mave"
                        val responseText = resObj?.optString("response", "Here is your track!") ?: "Here is your track!"
                        val msg = MaveChatMessage(
                            id = System.currentTimeMillis().toString(),
                            sender = "ai",
                            text = responseText,
                            tracks = listOf(MaveChatTrack(title = trackName, artist = artistName)),
                            audioUrl = audioUrl,
                            type = "track"
                        )
                        _messages.value = _messages.value + msg
                        saveChatHistory()
                        // Auto-play via MediaPlayer if we have an audio URL
                        if (!audioUrl.isNullOrBlank()) {
                            withContext(Dispatchers.Main) {
                                try {
                                    val player = MediaPlayer()
                                    player.setDataSource(audioUrl)
                                    player.setOnPreparedListener { it.start() }
                                    player.setOnCompletionListener { it.release() }
                                    player.setOnErrorListener { mp, what, extra ->
                                        Log.e("ChatViewModel", "MediaPlayer error what=$what extra=$extra")
                                        mp.release()
                                        true
                                    }
                                    player.prepareAsync()
                                } catch (e: Exception) {
                                    Log.e("ChatViewModel", "MediaPlayer setup failed", e)
                                }
                            }
                        }
                    }
                    "tweak_instrumentation" -> {
                        val result = executeTool(name, args)
                        val audioUrl = result.optString("audioUrl").takeIf { it.isNotBlank() }
                        _messages.value = _messages.value + MaveChatMessage(
                            id = System.currentTimeMillis().toString(),
                            sender = "ai",
                            text = result.optString("message", "Instrumentation updated."),
                            audioUrl = audioUrl,
                            type = if (audioUrl != null) "track" else null
                        )
                        saveChatHistory()
                    }
                    "generate_cover_art" -> {
                        val body = JSONObject().apply {
                            put("prompt", args["prompt"] ?: "")
                            put("hq", args["hq"] ?: false)
                        }
                        val result = httpPost("/api/music/cover", body)
                        val url = result.optString("url")
                        if (url.isNotBlank()) {
                            _messages.value = _messages.value + MaveChatMessage(
                                id = System.currentTimeMillis().toString(),
                                sender = "ai",
                                text = "Cover art updated!",
                                coverArtUrl = url,
                                type = "cover_art"
                            )
                        } else {
                            _messages.value = _messages.value + MaveChatMessage(
                                id = System.currentTimeMillis().toString(),
                                sender = "ai",
                                text = "[Error] Cover art generation failed: ${result.optString("error", "Unknown error")}"
                            )
                        }
                        saveChatHistory()
                    }
                    "generate_video" -> {
                        val loadingId = System.currentTimeMillis().toString()
                        _messages.value = _messages.value + MaveChatMessage(
                            id = loadingId,
                            sender = "ai",
                            text = "Generating your music video..."
                        )
                        val body = JSONObject().apply { put("prompt", args["prompt"] ?: "") }
                        val result = httpPost("/api/music/video", body)
                        val url = result.optString("url")
                        if (url.isNotBlank()) {
                            _messages.value = _messages.value.map {
                                if (it.id == loadingId) it.copy(text = "Your music video is ready!", videoUrl = url, type = "video")
                                else it
                            }
                        } else {
                            _messages.value = _messages.value.map {
                                if (it.id == loadingId) it.copy(text = "[Error] Video generation failed: ${result.optString("error", "Unknown error")}")
                                else it
                            }
                        }
                        saveChatHistory()
                    }
                    else -> {
                        val result = executeTool(name, args)
                        _messages.value = _messages.value + MaveChatMessage(
                            id = System.currentTimeMillis().toString(),
                            sender = "ai",
                            text = result.optString("message", "Done.")
                        )
                        saveChatHistory()
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Tool call '$name' failed", e)
                var errMsg = e.message ?: "Unknown error"
                if (errMsg.contains("429") || errMsg.contains("quota", ignoreCase = true) || errMsg.contains("RESOURCE_EXHAUSTED")) {
                    errMsg = "Quota reached. Please try again later."
                } else if (errMsg.contains("503") || errMsg.contains("overloaded", ignoreCase = true)) {
                    errMsg = "Server overloaded. Please try again later."
                } else if (errMsg.startsWith("{") || errMsg.contains("\"error\"")) {
                    errMsg = "An unexpected error occurred. Please try again."
                }
                _messages.value = _messages.value + MaveChatMessage(
                    id = System.currentTimeMillis().toString(),
                    sender = "ai",
                    text = "[Error] Tool error (${name}): $errMsg"
                )
                saveChatHistory()
            }
        }
    }

    fun generateCoverArt(prompt: String, hq: Boolean = false) {
        handleToolCall("generate_cover_art", mapOf("prompt" to prompt, "hq" to hq))
    }

    fun generateVideo(prompt: String) {
        handleToolCall("generate_video", mapOf("prompt" to prompt))
    }

    fun sendVisionFrame(base64: String, mimeType: String = "image/jpeg") {
        val userMsg = MaveChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "user",
            text = "[Sent media for analysis]"
        )
        
        _messages.value = _messages.value + userMsg
        saveChatHistory()
        
        viewModelScope.launch {
            val responseId = (System.currentTimeMillis() + 1).toString()
            val loadingMsg = MaveChatMessage(
                id = responseId,
                sender = "ai",
                text = "Analyzing media and composing track..."
            )
            _messages.value = _messages.value + loadingMsg

            try {
                val apiClient = com.musically.studio.network.RealApiClient(okhttp3.OkHttpClient())
                val track = apiClient.generateMusicFromMedia(base64, mimeType)
                if (track != null) {
                    _messages.value = _messages.value.map { msg ->
                        if (msg.id == responseId) msg.copy(
                            text = "Here is the track inspired by your media!",
                            audioUrl = track.audioUrl,
                            type = "music"
                        ) else msg
                    }
                } else {
                    throw Exception("Failed to generate track from media")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "sendVisionFrame failed", e)
                _messages.value = _messages.value.map { msg ->
                    if (msg.id == responseId) msg.copy(text = "[Error] Error analyzing media and generating music: ${e.message ?: "Unknown error"}") else msg
                }
            }
        }
    }

    fun recordVoice(context: android.content.Context) {
        // Voice via Firebase AI Live API is handled in LiveSessionScreen.
        // Surface a clear error state instead of silently doing nothing.
        _messages.value = _messages.value + MaveChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "ai",
            text = "[Error] Live voice is not yet available in this build. Use the Live Session screen for voice input."
        )
    }

    override fun onCleared() {
        // super.onCleared()
    }
}
