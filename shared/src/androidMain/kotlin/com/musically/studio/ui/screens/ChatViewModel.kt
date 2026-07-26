package com.musically.studio.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import com.musically.studio.shared.BuildConfig

data class MaveTrack(
    val title: String,
    val artist: String
)

data class MaveChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val tracks: List<MaveTrack>? = null
)

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<MaveChatMessage>>(emptyList())
    val messages: StateFlow<List<MaveChatMessage>> = _messages.asStateFlow()

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    init {
        // Initialize with welcome message
        _messages.value = listOf(
            MaveChatMessage(
                id = "0",
                sender = "ai",
                text = "Hi! I'm Mave, your personal audio curator. How can I help you today?"
            )
        )
        connectWebSocket()
    }

    private fun connectWebSocket() {
        viewModelScope.launch {
            val token = com.musically.studio.network.TokenManager.getValidToken()
            val url = if (token != null) {
                "${BuildConfig.WS_BASE_URL}?token=$token"
            } else {
                BuildConfig.WS_BASE_URL
            }
            val request = Request.Builder().url(url).build()
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    // Connection opened
                }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                try {
                    val json = JSONObject(text)
                    if (json.optString("type") == "ai_response") {
                        val aiText = json.optString("text", "")
                        val aiMsg = MaveChatMessage(
                            id = System.currentTimeMillis().toString(),
                            sender = "ai",
                            text = aiText,
                            tracks = emptyList() // Parse tracks if provided
                        )
                        _messages.value = _messages.value + aiMsg
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
            }
        })
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        val userMsg = MaveChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "user",
            text = text.trim()
        )
        
        _messages.value = _messages.value + userMsg
        
        try {
            val json = JSONObject().apply {
                put("type", "user_message")
                put("text", text.trim())
            }
            webSocket?.send(json.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        webSocket?.close(1000, "ViewModel cleared")
    }
}
