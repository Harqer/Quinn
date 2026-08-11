package com.musically.studio.ui.screens

import com.musically.studio.dataconnect.instance
import com.musically.studio.dataconnect.execute

import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import kotlinx.coroutines.tasks.await
import android.media.MediaPlayer
import timber.log.Timber
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
import com.musically.studio.network.GeminiLiveManager
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat

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
    private val generativeService: ChatGenerativeService,
    private val geminiLiveManager: GeminiLiveManager
) : ViewModel() {
    private val _messages = MutableStateFlow<List<MaveChatMessage>>(emptyList())
    val messages: StateFlow<List<MaveChatMessage>> = _messages.asStateFlow()

    private var isRecordingVoice = false
    private var recordingJob: kotlinx.coroutines.Job? = null
    private var audioRecord: AudioRecord? = null

    init {
        viewModelScope.launch {
            collectGeminiEvents()
        }
        viewModelScope.launch {
            loadChatHistory()
        }
    }

    private suspend fun collectGeminiEvents() {
        viewModelScope.launch {
            geminiLiveManager.transcripts.collect { transcript ->
                val newMsg = MaveChatMessage(
                    id = System.currentTimeMillis().toString(),
                    sender = if (transcript.startsWith("You: ")) "user" else "mave",
                    text = transcript.removePrefix("You: ").removePrefix("Mave: ")
                )
                _messages.value = _messages.value + newMsg
                saveMessage(newMsg)
            }
        }
        viewModelScope.launch {
            geminiLiveManager.functionCalls.collect { call ->
                val name = call.optString("name")
                val callId = call.optString("id")
                try {
                    val argsMap = mutableMapOf<String, Any?>()
                    val argsObj = call.optJSONObject("args")
                    if (argsObj != null) {
                        val keys = argsObj.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            argsMap[key] = argsObj.get(key)
                        }
                    }
                    val result = chatRepository.executeTool(name, argsMap)
                    geminiLiveManager.sendResponse(callId, name, result)
                } catch (e: Exception) {
                    val errorObj = JSONObject().apply { put("error", e.message) }
                    geminiLiveManager.sendResponse(callId, name, errorObj)
                }
            }
        }
    }

    private suspend fun loadChatHistory() {
        try {
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
            val result = com.musically.studio.dataconnect.DefaultConnector.instance.listChatMessages.execute(userId = userId)
            val msgs = result.data.chatMessages
            
            if (msgs.isNotEmpty()) {
                val loadedMsgs = msgs.map {
                    MaveChatMessage(
                        id = it.id,
                        sender = it.sender,
                        text = it.text
                    )
                }
                _messages.value = loadedMsgs
                return
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load chat history")
        }
        
        _messages.value = emptyList()
    }

    private fun saveChatHistory() {
        // DataConnect handles messages individually as they are added, so this is unused.
        // We will call saveMessage(msg) below.
    }

    private fun saveMessage(msg: MaveChatMessage) {
        viewModelScope.launch {
            collectGeminiEvents()
        }
        viewModelScope.launch {
            try {
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
                com.musically.studio.dataconnect.DefaultConnector.instance.addChatMessage.execute(
                    userId = userId,
                    sender = msg.sender,
                    text = msg.text
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to save message to Data Connect")
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = MaveChatMessage(id = System.currentTimeMillis().toString(), sender = "user", text = text.trim())
        _messages.value = _messages.value + userMsg
        saveMessage(userMsg)

        val responseId = (System.currentTimeMillis() + 1).toString()
        var fullText = ""
        var addedEmptyMessage = false

        viewModelScope.launch {
            collectGeminiEvents()
        }
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
                
                // Save AI message when stream completes
                val finalMsg = _messages.value.find { it.id == responseId }
                finalMsg?.let { saveMessage(it) }
            } catch (e: Exception) {
                Timber.e(e, "Error generating response")
                handleError(e, responseId, addedEmptyMessage)
            }
        }
    }

    fun generateNarrativeSeries(topic: String, type: String = "podcast", targetEpisodes: Int = 3) {
        val userMsg = MaveChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "user",
            text = "Generate a $type about $topic"
        )
        _messages.value = _messages.value + userMsg
        saveMessage(userMsg)

        val responseId = (System.currentTimeMillis() + 1).toString()
        _messages.value = _messages.value + MaveChatMessage(
            id = responseId,
            sender = "ai",
            text = "Writing $type script..."
        )

        viewModelScope.launch {
            collectGeminiEvents()
        }
        viewModelScope.launch {
            try {
                val functions = Firebase.functions
                val result = functions
                    .getHttpsCallable("generateNarrativeSeries")
                    .withTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                    .call(
                        mapOf(
                            "topic" to topic,
                            "type" to type,
                            "targetEpisodes" to targetEpisodes
                        )
                    )
                    .await()
                
                val resultData = result.data as? Map<*, *>
                val newContext = resultData?.get("newContext") as? String
                val episodesList = resultData?.get("episodes") as? List<Map<String, String>> ?: emptyList()
                
                var generatedSeriesId: String? = null
                val renderJobs = mutableListOf<Map<String, String>>()
                
                if (type == "podcast") {
                    val createShowRes = com.musically.studio.dataconnect.DefaultConnector.instance.createPodcast.execute(
                        title = "$topic Podcast",
                        publisher = "Mave AI"
                    ) {
                        coverUrl = null
                        description = "A generated podcast about $topic"
                        storyContext = newContext
                    }
                    generatedSeriesId = createShowRes.data.show_insert?.id
                    
                    if (generatedSeriesId != null) {
                        episodesList.forEach { ep ->
                            val res = com.musically.studio.dataconnect.DefaultConnector.instance.seedEpisode.execute(
                                showId = generatedSeriesId!!,
                                title = ep["title"] ?: "Untitled Episode",
                                publishDate = com.google.firebase.Timestamp.now()
                            ) {
                                description = ep["script"]
                                audioUrl = null
                                durationMs = 0
                            }
                            res.data.episode_insert?.id?.let {
                                renderJobs.add(mapOf("episodeId" to it, "script" to (ep["script"] ?: "")))
                            }
                        }
                    }
                } else {
                    val createBookRes = com.musically.studio.dataconnect.DefaultConnector.instance.createAudiobook.execute(
                        title = "$topic Audiobook",
                        authorId = "mave_ai"
                    ) {
                        narrator = "Mave AI"
                        coverUrl = null
                        storyContext = newContext
                    }
                    generatedSeriesId = createBookRes.data.audiobook_insert?.id
                    
                    if (generatedSeriesId != null) {
                        episodesList.forEachIndexed { index, chapter ->
                            val res = com.musically.studio.dataconnect.DefaultConnector.instance.seedChapter.execute(
                                audiobookId = generatedSeriesId!!,
                                title = chapter["title"] ?: "Chapter ${index + 1}",
                                chapterNumber = index + 1
                            ) {
                                audioUrl = null
                                durationMs = 0
                            }
                            res.data.chapter_insert?.id?.let {
                                renderJobs.add(mapOf("episodeId" to it, "script" to (chapter["script"] ?: "")))
                            }
                        }
                    }
                }

                _messages.value = _messages.value.map {
                    if (it.id == responseId) it.copy(text = "Successfully generated and saved $type script with ${episodesList.size} episodes/chapters! Rendering audio now...") else it
                }
                
                // Trigger background audio rendering here
                launch(Dispatchers.IO) {
                    renderJobs.forEach { job ->
                        try {
                            Firebase.functions
                                .getHttpsCallable("renderNarrativeAudio")
                                .call(job)
                        } catch (e: Exception) {
                            Timber.e(e, "Error triggering background audio render")
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error generating narrative")
                _messages.value = _messages.value.map {
                    if (it.id == responseId) it.copy(text = "[Error] Failed to generate script: ${e.message}") else it
                }
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
        _messages.value.lastOrNull()?.let { saveMessage(it) }
    }

    private fun handleToolCall(name: String, args: Map<String, Any?>) {
        viewModelScope.launch {
            collectGeminiEvents()
        }
        viewModelScope.launch {
            try {
                when (name) {
                    "generate_full_track" -> handleGenerateTrack(name, args)
                    "tweak_instrumentation" -> handleTweakInstrumentation(name, args)
                    "generate_cover_image" -> handleGenerateCoverArt(args)
                    "generate_music_video" -> handleGenerateVideo(args)
                    else -> handleGenericToolCall(name, args)
                }
            } catch (e: Exception) {
                Timber.e(e, "Tool call '$name' failed")
                _messages.value = _messages.value + MaveChatMessage(
                    id = System.currentTimeMillis().toString(),
                    sender = "ai",
                    text = "[Error] Tool error (${name}): ${e.message}"
                )
                _messages.value.lastOrNull()?.let { saveMessage(it) }
            }
        }
    }
    
    private suspend fun handleGenerateTrack(name: String, args: Map<String, Any?>) {
        val result = chatRepository.executeTool(name, args)
        val resObj = result.optJSONObject("result")
        val audioUrl = resObj?.optString("audioUrl")
        val trackName = resObj?.optString("trackName")
        val artistName = resObj?.optString("artistName")
        val responseText = resObj?.optString("response")?.takeIf { it.isNotBlank() } ?: "Here is your track!"
        
        val chatTracks = if (!trackName.isNullOrBlank() && !artistName.isNullOrBlank()) {
            listOf(MaveChatTrack(title = trackName, artist = artistName))
        } else {
            emptyList()
        }

        val msg = MaveChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "ai",
            text = responseText,
            tracks = chatTracks,
            audioUrl = audioUrl,
            type = "track"
        )
        _messages.value = _messages.value + msg
        _messages.value.lastOrNull()?.let { saveMessage(it) }
        
        if (!audioUrl.isNullOrBlank()) {
            withContext(Dispatchers.Main) {
                try {
                    val player = MediaPlayer()
                    player.setDataSource(audioUrl)
                    player.setOnPreparedListener { it.start() }
                    player.setOnCompletionListener { it.release() }
                    player.prepareAsync()
                } catch (e: Exception) {
                    Timber.e(e, "MediaPlayer setup failed")
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
        _messages.value.lastOrNull()?.let { saveMessage(it) }
    }

    private suspend fun handleGenerateCoverArt(args: Map<String, Any?>) {
        val prompt = args["prompt"] as? String ?: ""
        val hq = args["hq"] as? Boolean ?: false
        val result = chatRepository.generateCover(prompt, hq)
        val url = result.optString("imageUrl").ifBlank { result.optString("url") }
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
        _messages.value.lastOrNull()?.let { saveMessage(it) }
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
        val url = result.optString("videoUrl").ifBlank { result.optString("url") }
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
        _messages.value.lastOrNull()?.let { saveMessage(it) }
    }

    private suspend fun handleGenericToolCall(name: String, args: Map<String, Any?>) {
        val result = chatRepository.executeTool(name, args)
        _messages.value = _messages.value + MaveChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "ai",
            text = result.optString("message", "Done.")
        )
        _messages.value.lastOrNull()?.let { saveMessage(it) }
    }

    fun generateCoverArt(prompt: String, hq: Boolean = false) {
        handleToolCall("generate_cover_image", mapOf("prompt" to prompt, "hq" to hq))
    }

    fun generateVideo(prompt: String) {
        handleToolCall("generate_music_video", mapOf("prompt" to prompt))
    }

    @android.annotation.SuppressLint("MissingPermission")
    fun recordVoice(context: android.content.Context) {
        if (isRecordingVoice) {
            isRecordingVoice = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            recordingJob?.cancel()
            _messages.value = _messages.value + MaveChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = "ai",
                text = "Voice sent to live session."
            )
            return
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            _messages.value = _messages.value + MaveChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = "ai",
                text = "[Error] Please grant microphone permission."
            )
            return
        }

        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            audioRecord?.startRecording()
            isRecordingVoice = true

            recordingJob = viewModelScope.launch(Dispatchers.IO) {
                val buffer = ByteArray(bufferSize)
                while (isRecordingVoice) {
                    val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                    if (read > 0) {
                        val pcmData = buffer.copyOfRange(0, read)
                        geminiLiveManager.sendAudio(pcmData)
                    }
                }
            }
            _messages.value = _messages.value + MaveChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = "ai",
                text = "Recording voice... Tap again to stop."
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to start recording")
        }
    }

    fun sendVisionFrame(base64: String, mimeType: String) {
        geminiLiveManager.sendVideoFrame(base64, mimeType)
    }
}
