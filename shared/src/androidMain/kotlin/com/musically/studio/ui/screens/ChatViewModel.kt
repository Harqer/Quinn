package com.musically.studio.ui.screens

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.data.repository.ChatRepository
import com.musically.studio.services.ChatGenerativeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

data class MaveChatTrack(
    val title: String,
    val artist: String,
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
    private val chatRepository: ChatRepository,
    private val generativeService: ChatGenerativeService
) : ViewModel() {
    private val _messages = MutableStateFlow<List<MaveChatMessage>>(emptyList())
    val messages: StateFlow<List<MaveChatMessage>> = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            loadChatHistory()
        }
    }

    private suspend fun loadChatHistory() {
        try {
            val res = chatRepository.loadChatHistory()
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
                chatRepository.saveChatHistory(body)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to save chat history", e)
            }
        }
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
                generativeService.generateContentStream(text.trim()).collect { chunk ->
                    val calls = chunk.candidates.firstOrNull()?.content?.parts?.filterIsInstance<com.google.firebase.ai.type.FunctionCallPart>() ?: emptyList()
                    calls.forEach { call ->
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
                handleError(e, responseId, addedEmptyMessage)
            }
        }
    }

    private fun handleError(e: Exception, responseId: String, addedEmptyMessage: Boolean) {
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

    private fun handleToolCall(name: String, args: Map<String, Any?>) {
        viewModelScope.launch {
            try {
                when (name) {
                    "generate_full_track" -> handleGenerateTrack(name, args)
                    "tweak_instrumentation" -> handleTweakInstrumentation(name, args)
                    "generate_cover_art" -> handleGenerateCoverArt(args)
                    "generate_video" -> handleGenerateVideo(args)
                    else -> handleGenericToolCall(name, args)
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Tool call '$name' failed", e)
                _messages.value = _messages.value + MaveChatMessage(
                    id = System.currentTimeMillis().toString(),
                    sender = "ai",
                    text = "[Error] Tool error (${name}): ${e.message}"
                )
                saveChatHistory()
            }
        }
    }
    
    private suspend fun handleGenerateTrack(name: String, args: Map<String, Any?>) {
        val result = chatRepository.executeTool(name, args)
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
        
        if (!audioUrl.isNullOrBlank()) {
            withContext(Dispatchers.Main) {
                try {
                    val player = MediaPlayer()
                    player.setDataSource(audioUrl)
                    player.setOnPreparedListener { it.start() }
                    player.setOnCompletionListener { it.release() }
                    player.prepareAsync()
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "MediaPlayer setup failed", e)
                }
            }
        }
    }

    private suspend fun handleTweakInstrumentation(name: String, args: Map<String, Any?>) {
        val result = chatRepository.executeTool(name, args)
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

    private suspend fun handleGenerateCoverArt(args: Map<String, Any?>) {
        val prompt = args["prompt"] as? String ?: ""
        val hq = args["hq"] as? Boolean ?: false
        val result = chatRepository.generateCover(prompt, hq)
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
                text = "[Error] Cover art generation failed"
            )
        }
        saveChatHistory()
    }

    private suspend fun handleGenerateVideo(args: Map<String, Any?>) {
        val loadingId = System.currentTimeMillis().toString()
        _messages.value = _messages.value + MaveChatMessage(
            id = loadingId,
            sender = "ai",
            text = "Generating your music video..."
        )
        val prompt = args["prompt"] as? String ?: ""
        val result = chatRepository.generateVideo(prompt)
        val url = result.optString("url")
        if (url.isNotBlank()) {
            _messages.value = _messages.value.map {
                if (it.id == loadingId) it.copy(text = "Your music video is ready!", videoUrl = url, type = "video")
                else it
            }
        } else {
            _messages.value = _messages.value.map {
                if (it.id == loadingId) it.copy(text = "[Error] Video generation failed")
                else it
            }
        }
        saveChatHistory()
    }

    private suspend fun handleGenericToolCall(name: String, args: Map<String, Any?>) {
        val result = chatRepository.executeTool(name, args)
        _messages.value = _messages.value + MaveChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "ai",
            text = result.optString("message", "Done.")
        )
        saveChatHistory()
    }

    fun generateCoverArt(prompt: String, hq: Boolean = false) {
        handleToolCall("generate_cover_art", mapOf("prompt" to prompt, "hq" to hq))
    }

    fun generateVideo(prompt: String) {
        handleToolCall("generate_video", mapOf("prompt" to prompt))
    }

    fun recordVoice(context: android.content.Context) {
        _messages.value = _messages.value + MaveChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "ai",
            text = "[Error] Live voice is not yet available in this build. Use the Live Session screen for voice input."
        )
    }

    fun sendVisionFrame(base64: String, mimeType: String) {
        // Stub for image attachment in chat
    }
}
