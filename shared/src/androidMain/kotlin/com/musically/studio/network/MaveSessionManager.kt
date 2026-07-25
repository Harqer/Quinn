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

    private val _audioStream = MutableSharedFlow<ByteArray>(extraBufferCapacity = 100)
    open val audioStream = _audioStream.asSharedFlow()

    private var reconnectJob: Job? = null
    private var isIntentionalClose = false
    private var backoffTime = 1000L
    private val MAX_BACKOFF = 30000L

    open fun connect() {
        if (webSocket != null) return
        isIntentionalClose = false
        internalConnect()
    }

    private fun internalConnect() {
        scope.launch {
            val token = TokenManager.getValidToken() ?: run {
                Timber.e("Auth dropped during WebSocket connect/reconnect loop")
                throw SecurityException("Authentication token is missing or invalid.")
            }
            val request = Request.Builder()
                .url("${com.musically.studio.shared.BuildConfig.WS_BASE_URL}?token=$token")
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Timber.d("Connected to Mave Studio Live")
                    backoffTime = 1000L // Reset on successful connection
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    scope.launch { _events.emit(text) }
                }

                override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
                    scope.launch { _audioStream.emit(bytes.toByteArray()) }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Timber.d("Mave Session Closed: $reason")
                    this@MaveSessionManager.webSocket = null
                    if (!isIntentionalClose) {
                        scheduleReconnect()
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Timber.e(t, "Mave Session Failure")
                    this@MaveSessionManager.webSocket = null
                    if (!isIntentionalClose) {
                        scheduleReconnect()
                    }
                }
            })
        }
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            delay(backoffTime)
            Timber.d("Reconnecting WebSocket (backoff: ${backoffTime}ms)")
            internalConnect()
            backoffTime = (backoffTime * 2).coerceAtMost(MAX_BACKOFF)
        }
    }

    open fun sendEvent(type: String, data: Map<String, Any>) {
        val json = JSONObject()
        json.put("type", type)
        data.forEach { (key, value) -> json.put(key, value) }
        webSocket?.send(json.toString())
    }

    open fun sendPrompts(prompts: List<Map<String, Any>>) {
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

    open fun play() = webSocket?.send("""{"type":"play"}""")
    open fun pause() = webSocket?.send("""{"type":"pause"}""")
    open fun stop() = webSocket?.send("""{"type":"stop"}""")
    open fun next() = webSocket?.send("""{"type":"next"}""")
    open fun previous() = webSocket?.send("""{"type":"previous"}""")

    open fun sendAudio(base64: String) {
        val json = JSONObject()
        json.put("type", "audio")
        json.put("data", base64)
        webSocket?.send(json.toString())
    }

    open fun sendVideoFrame(frameBytes: ByteArray) {
        val base64 = android.util.Base64.encodeToString(frameBytes, android.util.Base64.NO_WRAP)
        val json = JSONObject()
        json.put("type", "vision")
        json.put("image", base64)
        webSocket?.send(json.toString())
    }

    open fun disconnect() {
        isIntentionalClose = true
        reconnectJob?.cancel()
        webSocket?.close(1000, "User logout")
        webSocket = null
    }
}
