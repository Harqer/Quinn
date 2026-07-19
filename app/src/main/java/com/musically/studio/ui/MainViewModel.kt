package com.musically.studio.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.WearableStreamingService
import com.musically.studio.network.ApiClient
import com.musically.studio.network.QuinnSessionManager
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import timber.log.Timber

class MainViewModel : ViewModel() {
    private val _isSpotifyConnected = MutableStateFlow(false)
    val isSpotifyConnected: StateFlow<Boolean> = _isSpotifyConnected.asStateFlow()

    private val _tracks = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val tracks: StateFlow<List<SpotifyTrack>> = _tracks.asStateFlow()
    
    private val _isWearableConnected = MutableStateFlow(false)
    val isWearableConnected: StateFlow<Boolean> = _isWearableConnected.asStateFlow()

    val messages = mutableStateListOf<ChatMessage>()
    
    private val _currentMode = MutableStateFlow("music")
    val currentMode: StateFlow<String> = _currentMode.asStateFlow()

    private val okHttpClient = OkHttpClient()
    private val quinnSessionManager = QuinnSessionManager(okHttpClient)
    private val apiClient = ApiClient(okHttpClient)

    init {
        viewModelScope.launch {
            quinnSessionManager.events.collect { event ->
                try {
                    val json = JSONObject(event)
                    when (json.optString("type")) {
                        "agent_update" -> {
                            val vision = json.optString("vision")
                            val prompts = json.optJSONArray("prompts")
                            val trackId = json.optString("trackId")
                            val message = if (prompts != null && prompts.length() > 0) {
                                "New vibe: ${prompts.getString(0)}"
                            } else {
                                vision
                            }
                            messages.add(ChatMessage(message, false, trackId))
                        }
                        "podcast_update" -> {
                            val script = json.optString("script")
                            val trackId = json.optString("trackId")
                            messages.add(ChatMessage(script, false, trackId))
                        }
                        "error" -> {
                            messages.add(ChatMessage("Error: ${json.optString("error")}", false))
                        }
                        else -> {
                            messages.add(ChatMessage(event, false))
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to parse Quinn event")
                    messages.add(ChatMessage(event, false))
                }
            }
        }
        
        viewModelScope.launch {
            WearableStreamingService.isServiceActive.collectLatest { active ->
                _isWearableConnected.value = active
            }
        }
    }

    fun connectToQuinn() {
        quinnSessionManager.connect()
    }

    fun switchMode(mode: String) {
        _currentMode.value = mode
        quinnSessionManager.sendEvent("switch_mode", mapOf("mode" to mode))
    }

    fun sendTextCommand(text: String) {
        messages.add(ChatMessage(text, true))
        val type = if (_currentMode.value == "podcast") "text_command" else "feedback"
        quinnSessionManager.sendEvent(type, mapOf("text" to text))
    }

    fun recordVoice() {
        // Implementation for voice capture and streaming
    }

    fun saveTrackToLibrary(trackId: String) {
        viewModelScope.launch {
            apiClient.bookmarkTrack(trackId)
        }
    }

    fun connectSpotify() {
        viewModelScope.launch {
            _isSpotifyConnected.value = true
        }
    }

    fun fetchUserTracks() {
        viewModelScope.launch {
            val result = apiClient.getUserTracks()
            if (result != null) {
                _tracks.value = result
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        quinnSessionManager.disconnect()
    }
}
