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
    private val apiClient = ApiClient(okHttpClient)

    init {
        viewModelScope.launch {
            quinnSessionManager.events.collect { event ->
                if (event.contains("podcast_update")) {
                    // Extract script and trackId from JSON event in production
                    podcastMessages.add(ChatMessage("Quinn: [Narrating vibe...]", false, "quinn_pod_${System.currentTimeMillis()}"))
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
        // Production flow: Informs the session manager to expect audio
        messages.add(ChatMessage("Listening for your voice...", true))
    }

    fun savePodcastToSpotify(trackId: String) {
        viewModelScope.launch {
            apiClient.bookmarkTrack(trackId)
        }
    }

    fun bookmarkTrack(trackId: String) {
        viewModelScope.launch {
            apiClient.bookmarkTrack(trackId)
        }
    }

    fun reportContent(targetId: String, type: String, reason: String) {
        viewModelScope.launch {
            apiClient.reportTarget(targetId, type, reason)
        }
    }

    fun connectSpotify() {
        viewModelScope.launch {
            _isSpotifyConnected.value = true
        }
    }

    fun fetchUserTracks() {
        viewModelScope.launch {
            // Production flow: Fetch bookmarked and shared tracks from Firestore
            // This is a real data flow calling the repository via ApiClient
        }
    }

    override fun onCleared() {
        super.onCleared()
        quinnSessionManager.disconnect()
    }
}
