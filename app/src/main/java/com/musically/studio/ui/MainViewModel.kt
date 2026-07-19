package com.musically.studio.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.WearableStreamingService
import com.musically.studio.network.QuinnSessionManager
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainViewModel : ViewModel() {
    private val _isSpotifyConnected = MutableStateFlow(false)
    val isSpotifyConnected: StateFlow<Boolean> = _isSpotifyConnected.asStateFlow()

    private val _tracks = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val tracks: StateFlow<List<SpotifyTrack>> = _tracks.asStateFlow()
    
    private val _isWearableConnected = MutableStateFlow(false)
    val isWearableConnected: StateFlow<Boolean> = _isWearableConnected.asStateFlow()

    val messages = mutableStateListOf<ChatMessage>()
    val podcastMessages = mutableStateListOf<ChatMessage>()
    
    private val _currentMode = MutableStateFlow("music")
    val currentMode: StateFlow<String> = _currentMode.asStateFlow()

    private val okHttpClient = OkHttpClient()
    private val quinnSessionManager = QuinnSessionManager(okHttpClient)

    init {
        viewModelScope.launch {
            quinnSessionManager.events.collect { event ->
                if (event.contains("podcast_update")) {
                    podcastMessages.add(ChatMessage("Quinn: [Narrating vibe...]", false))
                } else if (event.contains("agent_update")) {
                    messages.add(ChatMessage("Quinn: [New musical vibe set]", false))
                } else {
                    if (_currentMode.value == "podcast") {
                        podcastMessages.add(ChatMessage("Quinn: $event", false))
                    } else {
                        messages.add(ChatMessage("Quinn: $event", false))
                    }
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
    }

    fun sendTextCommand(text: String) {
        if (_currentMode.value == "podcast") {
            podcastMessages.add(ChatMessage(text, true))
        } else {
            messages.add(ChatMessage(text, true))
            quinnSessionManager.sendPrompts(listOf(mapOf("text" to text, "weight" to 1.0)))
        }
    }

    fun recordVoice() {
        // Implementation
    }

    fun savePodcastToSpotify(trackUri: String) {
        viewModelScope.launch {
            // Implementation
        }
    }

    fun connectSpotify() {
        viewModelScope.launch {
            _isSpotifyConnected.value = true
        }
    }

    fun fetchUserTracks() {
        // Implementation
    }

    override fun onCleared() {
        super.onCleared()
        quinnSessionManager.disconnect()
    }
}
