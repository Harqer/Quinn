package com.musically.studio.network

import android.util.Base64
import okhttp3.*
import org.json.JSONObject
import timber.log.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GeminiLiveManager(
    private val client: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private var currentToken: String? = null
    private var isResuming = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _audioOutput = MutableSharedFlow<ByteArray>(extraBufferCapacity = 100)
    val audioOutput = _audioOutput.asSharedFlow()

    private val _transcripts = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val transcripts = _transcripts.asSharedFlow()

    private val _thoughts = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val thoughts = _thoughts.asSharedFlow()

    private val _functionCalls = MutableSharedFlow<JSONObject>(extraBufferCapacity = 5)
    val functionCalls = _functionCalls.asSharedFlow()

    private val _connectionState = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val connectionState = _connectionState.asSharedFlow()

    fun connect(token: String, resume: Boolean = false) {
        this.currentToken = token
        this.isResuming = resume
        
        val url = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeLanguageService/ConnectInteraction?token=$token"
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Timber.d("Gemini Live: Connected (Resuming: $isResuming)")
                _connectionState.tryEmit(true)
                sendSetup()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleServerMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Timber.e(t, "Gemini Live Failure")
                _connectionState.tryEmit(false)
                this@GeminiLiveManager.webSocket = null
                // Attempt auto-reconnect if failure was network-related
                attemptReconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Timber.d("Gemini Live Closed: $reason")
                _connectionState.tryEmit(false)
                this@GeminiLiveManager.webSocket = null
            }
        })
    }

    private fun sendSetup() {
        try {
            val json = JSONObject()
            val setup = JSONObject()
            setup.put("model", "models/gemini-2.0-flash-exp") // Use latest live capable model
            
            val generationConfig = JSONObject()
            generationConfig.put("response_modalities", "AUDIO")
            generationConfig.put("speech_config", JSONObject().apply {
                val voiceConfig = JSONObject()
                voiceConfig.put("prebuilt_voice_config", JSONObject().apply {
                    put("voice_name", "Aoife") // Art Director persona
                })
                put("voice_config", voiceConfig)
            })
            // Enable agentic reasoning
            generationConfig.put("thinking_config", JSONObject().apply {
                put("include_thoughts", true)
            })
            
            setup.put("generation_config", generationConfig)
            
            // Add visual production tool
            val tools = org.json.JSONArray()
            val tool = JSONObject()
            val functionDeclarations = org.json.JSONArray()
            functionDeclarations.put(JSONObject().apply {
                put("name", "generate_visual_media")
                put("description", "Triggers the backend to generate an album cover or music video motion loop based on the current creative vision.")
                put("parameters", JSONObject().apply {
                    put("type", "OBJECT")
                    put("properties", JSONObject().apply {
                        put("intent", JSONObject().apply {
                            put("type", "STRING")
                            put("enum", org.json.JSONArray().apply { put("cover_art"); put("video_motion") })
                        })
                        put("creative_pitch", JSONObject().apply {
                            put("type", "STRING")
                            put("description", "The director's pitch for the visual style.")
                        })
                    })
                    put("required", org.json.JSONArray().apply { put("intent") })
                })
            })
            tool.put("function_declarations", functionDeclarations)
            tools.put(tool)
            setup.put("tools", tools)

            json.put("setup", setup)
            webSocket?.send(json.toString())
        } catch (e: Exception) {
            Timber.e(e, "Error sending setup")
        }
    }

    private fun handleServerMessage(text: String) {
        try {
            val json = JSONObject(text)
            
            // Handle Thoughts if present
            val serverContent = json.optJSONObject("serverContent")
            if (serverContent != null) {
                val modelTurn = serverContent.optJSONObject("modelTurn")
                if (modelTurn != null) {
                    val parts = modelTurn.optJSONArray("parts")
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            
                            // Check for thoughts
                            val thought = part.optString("thought")
                            if (thought.isNotBlank()) {
                                Timber.d("Gemini Thought: $thought")
                                scope.launch { _thoughts.emit(thought) }
                            }

                            // Handle Audio
                            val inlineData = part.optJSONObject("inlineData")
                            if (inlineData != null) {
                                val data = inlineData.optString("data")
                                if (data.isNotBlank()) {
                                    _audioOutput.tryEmit(Base64.decode(data, Base64.NO_WRAP))
                                }
                            }

                            // Handle Function Calls
                            val call = part.optJSONObject("functionCall")
                            if (call != null) {
                                scope.launch { _functionCalls.emit(call) }
                            }
                        }
                    }
                }
                
                val outputTranscription = serverContent.optJSONObject("outputTranscription")
                if (outputTranscription != null) {
                    val transcript = outputTranscription.optString("text")
                    if (transcript.isNotBlank()) {
                        scope.launch { _transcripts.emit(transcript) }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error parsing server message")
        }
    }

    private fun attemptReconnect() {
        val token = currentToken ?: return
        scope.launch {
            delay(2000) // Basic backoff
            Timber.i("Attempting session resumption...")
            connect(token, resume = true)
        }
    }

    fun sendAudio(pcmData: ByteArray) {
        try {
            val base64 = Base64.encodeToString(pcmData, Base64.NO_WRAP)
            val json = JSONObject()
            val realtimeInput = JSONObject()
            val inputAudio = JSONObject()
            inputAudio.put("data", base64)
            inputAudio.put("mimeType", "audio/pcm;rate=16000")
            realtimeInput.put("audio", inputAudio)
            json.put("realtimeInput", realtimeInput)
            webSocket?.send(json.toString())
        } catch (e: Exception) {
            Timber.e(e, "Error sending audio")
        }
    }

    fun sendVideoFrame(jpegData: ByteArray) {
        try {
            val base64 = Base64.encodeToString(jpegData, Base64.NO_WRAP)
            val json = JSONObject()
            val realtimeInput = JSONObject()
            val inputVideo = JSONObject()
            inputVideo.put("data", base64)
            inputVideo.put("mimeType", "image/jpeg")
            realtimeInput.put("video", inputVideo)
            json.put("realtimeInput", realtimeInput)
            webSocket?.send(json.toString())
        } catch (e: Exception) {
            Timber.e(e, "Error sending video")
        }
    }

    fun sendResponse(callId: String, name: String, result: JSONObject) {
        try {
            val json = JSONObject()
            val response = JSONObject()
            val functionResponse = JSONObject()
            functionResponse.put("id", callId)
            functionResponse.put("name", name)
            functionResponse.put("response", result)
            response.put("functionResponse", functionResponse)
            json.put("realtimeInput", response)
            webSocket?.send(json.toString())
        } catch (e: Exception) {
            Timber.e(e, "Error sending tool response")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnect")
        webSocket = null
    }
}
