package com.musically.studio.network

import timber.log.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

open class MaveSessionManager(
    private val client: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 10)
    open val events = _events.asSharedFlow()

    fun connect() {
        if (webSocket != null) return
        scope.launch {
            val token = TokenManager.getValidToken() ?: return@launch
            val request = Request.Builder()
                .url("wss://musically-studio.run.app/api/music/ws?token=$token")
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Timber.d("Connected to Mave Studio Live")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    scope.launch { _events.emit(text) }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Timber.d("Mave Session Closed: $reason")
                    this@MaveSessionManager.webSocket = null
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Timber.e(t, "Mave Session Failure")
                    this@MaveSessionManager.webSocket = null
                }
            })
        }
    }

    fun sendEvent(type: String, data: Map<String, Any>) {
        val json = JSONObject()
        json.put("type", type)
        data.forEach { (key, value) -> json.put(key, value) }
        webSocket?.send(json.toString())
    }

    fun sendPrompts(prompts: List<Map<String, Any>>) {
        val json = JSONObject()
        json.put("type", "setWeightedPrompts")
        val promptsArray = org.json.JSONArray()
        prompts.forEach { prompt ->
            val promptObj = JSONObject()
            prompt.forEach { (k, v) -> promptObj.put(k, v) }
            promptsArray.put(promptObj)
        }
        json.put("prompts", promptsArray)
        webSocket?.send(json.toString())
    }

    fun play() = webSocket?.send("""{"type":"play"}""")
    fun pause() = webSocket?.send("""{"type":"pause"}""")
    fun stop() = webSocket?.send("""{"type":"stop"}""")

    fun sendAudio(base64: String) {
        val json = JSONObject()
        json.put("type", "audio")
        json.put("data", base64)
        webSocket?.send(json.toString())
    }

    fun sendVideoFrame(frameBytes: ByteArray) {
        val base64 = android.util.Base64.encodeToString(frameBytes, android.util.Base64.NO_WRAP)
        val json = JSONObject()
        json.put("type", "vision")
        json.put("image", base64)
        webSocket?.send(json.toString())
    }

    fun disconnect() {
        webSocket?.close(1000, "User logout")
        webSocket = null
    }
}
