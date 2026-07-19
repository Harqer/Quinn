package com.musically.studio.network

import timber.log.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class QuinnSessionManager(
    private val client: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val events = _events.asSharedFlow()

    fun connect() {
        scope.launch {
            val token = TokenManager.getValidToken() ?: return@launch
            val request = Request.Builder()
                .url("wss://musically-studio.run.app/api/music/ws?token=$token")
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Timber.d("Connected to Quinn Live")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    scope.launch { _events.emit(text) }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Timber.d("Session Closed: $reason")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Timber.e(t, "Session Failure")
                    // Implement reconnection logic here
                }
            })
        }
    }

    fun sendPrompts(prompts: List<Map<String, Any>>) {
        val message = """{"type":"setWeightedPrompts","prompts":${promptsToJson(prompts)}}"""
        webSocket?.send(message)
    }

    fun play() = webSocket?.send("""{"type":"play"}""")
    fun pause() = webSocket?.send("""{"type":"pause"}""")
    fun stop() = webSocket?.send("""{"type":"stop"}""")

    private fun promptsToJson(prompts: List<Map<String, Any>>): String {
        // Crude JSON generation for brevity, in production use kotlinx.serialization
        return "[" + prompts.joinToString(",") { p ->
            """{"text":"${p["text"]}","weight":${p["weight"]}}"""
        } + "]"
    }

    fun disconnect() {
        webSocket?.close(1000, "User logout")
        scope.cancel()
    }
}
