package com.musically.studio.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.network.QuinnSessionManager
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.screens.ChatMessage
import com.musically.studio.WearableStreamingService
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
    
    private val okHttpClient = OkHttpClient()
    private val quinnSessionManager = QuinnSessionManager(okHttpClient)

    init {
        viewModelScope.launch {
            quinnSessionManager.events.collect { event ->
                messages.add(ChatMessage("Quinn: $event", false))
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

    fun sendTextCommand(text: String) {
        messages.add(ChatMessage(text, true))
        quinnSessionManager.sendPrompts(listOf(mapOf("text" to text, "weight" to 1.0)))
    }

    fun recordVoice() {
        // Toggle listening state and stream audio to quinnSessionManager
    }

    fun connectSpotify() {
        viewModelScope.launch {
            _isSpotifyConnected.value = true
        }
    }

    fun fetchUserTracks() {
        viewModelScope.launch {
            // In production, call AIApiService.getUserTracks()
            // For now, we use the tracks flow which is already initialized
        }
    }

    override fun onCleared() {
        super.onCleared()
        quinnSessionManager.disconnect()
    }
}
