package com.musically.studio.ui

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {
    private val _isSpotifyConnected = MutableStateFlow(false)
    val isSpotifyConnected: StateFlow<Boolean> = _isSpotifyConnected.asStateFlow()

    private val _tracks = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val tracks: StateFlow<List<SpotifyTrack>> = _tracks.asStateFlow()
    
    private val _currentPlayingTrack = MutableStateFlow<SpotifyTrack?>(null)
    val currentPlayingTrack: StateFlow<SpotifyTrack?> = _currentPlayingTrack.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _trackProgress = MutableStateFlow(0f)
    val trackProgress: StateFlow<Float> = _trackProgress.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled.asStateFlow()

    private val _isWearableConnected = MutableStateFlow(false)
    val isWearableConnected: StateFlow<Boolean> = _isWearableConnected.asStateFlow()

    val messages = mutableStateListOf<ChatMessage>()
    
    private val _currentMode = MutableStateFlow("music")
    val currentMode: StateFlow<String> = _currentMode.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val okHttpClient = OkHttpClient()
    private val quinnSessionManager = QuinnSessionManager(okHttpClient)
    private val apiClient = ApiClient(okHttpClient)
    private val auth = FirebaseAuth.getInstance()

    init {
        viewModelScope.launch {
            quinnSessionManager.events.collect { event ->
                try {
                    _isLoading.value = false
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
    
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    fun loginWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun switchMode(mode: String) {
        _currentMode.value = mode
        quinnSessionManager.sendEvent("switch_mode", mapOf("mode" to mode))
    }

    fun sendTextCommand(text: String) {
        messages.add(ChatMessage(text, true))
        _isLoading.value = true
        val type = if (_currentMode.value == "podcast") "text_command" else "feedback"
        quinnSessionManager.sendEvent(type, mapOf("text" to text))
    }

    fun recordVoice() {
        _isRecording.value = !_isRecording.value
        // Implementation for voice capture and streaming
    }

    fun saveTrackToLibrary(trackId: String) {
        viewModelScope.launch {
            apiClient.bookmarkTrack(trackId)
        }
    }

    fun bookmarkTrack(trackId: String) {
        viewModelScope.launch {
            apiClient.bookmarkTrack(trackId)
        }
    }

    fun connectSpotify() {
        viewModelScope.launch {
            val intent = Intent(
                Intent.ACTION_VIEW,
                android.net.Uri.parse("https://accounts.spotify.com/authorize")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // Launch in activity
        }
    }

    fun playTrack(track: SpotifyTrack) {
        _currentPlayingTrack.value = track
        _isPlaying.value = true
        // If we had a real player, we'd start playback here
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        // Real playback toggle logic here
    }

    fun skipNext() {
        // Implement real skip logic here
    }

    fun skipPrevious() {
        // Implement real skip back logic here
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }

    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
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
