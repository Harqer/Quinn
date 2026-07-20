package com.musically.studio.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Base64
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.musically.studio.WearableStreamingService
import com.musically.studio.network.ApiClient
import com.musically.studio.network.MaveSessionManager
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.models.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import timber.log.Timber

class MainViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val rtdb: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _isSpotifyConnected = MutableStateFlow(false)
    val isSpotifyConnected: StateFlow<Boolean> = _isSpotifyConnected.asStateFlow()

    private val _tracks = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val tracks: StateFlow<List<SpotifyTrack>> = _tracks.asStateFlow()
    
    private val _isWearableConnected = MutableStateFlow(false)
    val isWearableConnected: StateFlow<Boolean> = _isWearableConnected.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    val messages = mutableStateListOf<ChatMessage>()
    
    private val _thinkingText = MutableStateFlow("")
    val thinkingText: StateFlow<String> = _thinkingText.asStateFlow()

    private val _currentMode = MutableStateFlow("music")
    val currentMode: StateFlow<String> = _currentMode.asStateFlow()

    private val okHttpClient = OkHttpClient()
    private val maveSessionManager = MaveSessionManager(okHttpClient)
    private val apiClient = ApiClient(okHttpClient)
    
    private var rtdbListener: ValueEventListener? = null

    // Registration State Accumulator
    var regEmail = ""
    var regPassword = ""
    var regBirthday = ""
    var regGender = ""
    var regName = ""

    private var isRecording = false
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    init {
        setupWebSocketCollector()
        setupWearableCollector()
        if (isUserLoggedIn()) {
            startRtdbSync()
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun loginWithEmail(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    startRtdbSync()
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun guestLogin(callback: (Boolean, String?) -> Unit) {
        // For production "Audio First", we use anonymous auth
        _isLoading.value = true
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    startRtdbSync()
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun completeRegistration(callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(regEmail, regPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        viewModelScope.launch {
                            val success = apiClient.saveProfile(regName, regBirthday, regGender)
                            _isLoading.value = false
                            if (success) {
                                startRtdbSync()
                                callback(true, null)
                            } else {
                                callback(false, "Failed to sync profile to backend")
                            }
                        }
                    }
                } else {
                    _isLoading.value = false
                    callback(false, task.exception?.message)
                }
            }
    }

    fun saveArtistPreferences(artists: List<String>, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = apiClient.savePreferences(artists)
            callback(success)
        }
    }

    private fun setupWebSocketCollector() {
        viewModelScope.launch {
            maveSessionManager.events.collect { event ->
                try {
                    val json = JSONObject(event)
                    when (json.optString("type")) {
                        "mave_thinking", "mave_chunk" -> {
                            val chunk = json.optString("chunk")
                            _thinkingText.value += chunk
                        }
                        "agent_update" -> {
                            _thinkingText.value = ""
                            val vision = json.optString("vision")
                            val prompts = json.optJSONArray("prompts")
                            val script = json.optString("script")
                            val trackId = json.optString("trackId")
                            
                            val message = if (prompts != null && prompts.length() > 0) {
                                "New vibe: ${prompts.getString(0)}"
                            } else if (!script.isNullOrBlank()) {
                                script
                            } else {
                                vision
                            }
                            
                            if (message.isNotBlank()) {
                                messages.add(0, ChatMessage(message, false, trackId))
                            }
                        }
                        "error" -> {
                            _thinkingText.value = ""
                            messages.add(0, ChatMessage("Error: ${json.optString("error")}", false))
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to parse Mave event")
                }
            }
        }
    }

    fun startRtdbSync() {
        val user = auth.currentUser ?: return
        val stateRef = rtdb.getReference("sessions/${user.uid}/state")
        
        rtdbListener = stateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data = snapshot.value as? Map<*, *>
                    val isThinking = data?.get("isThinking") as? Boolean ?: false
                    if (isThinking) {
                        val chunk = data?.get("chunk") as? String ?: ""
                        if (chunk.length > _thinkingText.value.length) {
                            _thinkingText.value = chunk
                        }
                    } else {
                        _thinkingText.value = ""
                    }
                    
                    // Sync playback states if present in RTDB
                    (data?.get("isPlaying") as? Boolean)?.let { _isPlaying.value = it }
                    (data?.get("progress") as? Number)?.let { _trackProgress.value = it.toFloat() }
                } catch (e: Exception) {
                    Timber.e(e, "RTDB Sync Parse Error")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "RTDB Sync Error")
            }
        })
    }

    private fun setupWearableCollector() {
        viewModelScope.launch {
            WearableStreamingService.isServiceActive.collectLatest { active ->
                _isWearableConnected.value = active
            }
        }

        viewModelScope.launch {
            WearableStreamingService.interactionEvents.collect { event ->
                Timber.d("Wearable Interaction: $event")
                when (event) {
                    "play" -> togglePlayPause()
                    "pause" -> togglePlayPause()
                    "stop" -> stopPlayback()
                    "generate" -> sendTextCommand("Generate a new atmosphere")
                    "speak" -> recordVoice()
                }
            }
        }
    }

    fun setWearableConnected(context: Context, connected: Boolean) {
        val intent = Intent(context, WearableStreamingService::class.java)
        if (connected) {
            context.startForegroundService(intent)
        } else {
            context.stopService(intent)
        }
    }

    fun connectToMave() {
        maveSessionManager.connect()
        startRtdbSync()
    }

    fun switchMode(mode: String) {
        _currentMode.value = mode
        maveSessionManager.sendEvent("switch_mode", mapOf("mode" to mode))
    }

    fun sendTextCommand(text: String) {
        messages.add(0, ChatMessage(text, true))
        _thinkingText.value = ""
        val type = if (_currentMode.value == "podcast") "text_command" else "feedback"
        maveSessionManager.sendEvent(type, mapOf("text" to text))
    }

    @SuppressLint("MissingPermission")
    fun recordVoice() {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        val sampleRate = 16000
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        isRecording = true
        maveSessionManager.sendEvent("start_voice", emptyMap())
        audioRecord?.startRecording()

        recordingJob = viewModelScope.launch(Dispatchers.IO) {
            val audioBuffer = ShortArray(bufferSize)
            while (isRecording) {
                val read = audioRecord?.read(audioBuffer, 0, bufferSize) ?: 0
                if (read > 0) {
                    val byteBuffer = java.nio.ByteBuffer.allocate(read * 2)
                    byteBuffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    for (i in 0 until read) {
                        byteBuffer.putShort(audioBuffer[i])
                    }
                    val base64 = Base64.encodeToString(byteBuffer.array(), Base64.NO_WRAP)
                    maveSessionManager.sendAudio(base64)
                }
            }
        }
    }

    private fun stopRecording() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        maveSessionManager.sendEvent("stop_voice", emptyMap())
    }

    // --- Playback Controls ---

    fun togglePlayPause() {
        if (_isPlaying.value) {
            _isPlaying.value = false
            maveSessionManager.pause()
        } else {
            _isPlaying.value = true
            maveSessionManager.play()
        }
    }

    fun stopPlayback() {
        _isPlaying.value = false
        maveSessionManager.stop()
    }

    fun skipNext() {
        maveSessionManager.sendEvent("skip_next", emptyMap())
    }

    fun skipPrevious() {
        maveSessionManager.sendEvent("skip_previous", emptyMap())
    }

    fun seekTo(position: Float) {
        _trackProgress.value = position
        maveSessionManager.sendEvent("seek_to", mapOf("position" to position))
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
        maveSessionManager.sendEvent("toggle_shuffle", mapOf("enabled" to _isShuffleEnabled.value))
    }

    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
        maveSessionManager.sendEvent("toggle_repeat", mapOf("enabled" to _isRepeatEnabled.value))
    }

    fun playTrack(track: SpotifyTrack) {
        _currentPlayingTrack.value = track
        _isPlaying.value = true
        maveSessionManager.sendEvent("play_track", mapOf("trackId" to track.id))
    }

    fun sendFrame(base64: String) {
        maveSessionManager.sendEvent("vision", mapOf("image" to base64))
    }

    fun applySteering(params: Map<String, Any>) {
        maveSessionManager.sendEvent("steering_action", mapOf("params" to params))
    }

    fun bookmarkTrack(trackId: String) {
        viewModelScope.launch {
            apiClient.bookmarkTrack(trackId)
        }
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
            _isLoading.value = true
            val result = apiClient.getUserTracks()
            if (result != null) {
                _tracks.value = result
            }
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        maveSessionManager.disconnect()
        rtdbListener?.let { listener ->
            val user = auth.currentUser
            if (user != null) {
                rtdb.getReference("sessions/${user.uid}/state").removeEventListener(listener)
            }
        }
    }
}
