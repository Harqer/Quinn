package com.musically.studio.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.media.AudioDeviceInfo
import android.media.AudioManager
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.musically.studio.WearableStreamingService
import com.musically.studio.network.ApiClient
import com.musically.studio.network.MaveSessionManager
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.models.ChatMessage
import com.musically.studio.ui.models.AudioDevice
import com.musically.studio.ui.models.DeviceType
import com.musically.studio.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.musically.studio.logging.CrashlyticsTree

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiClient: ApiClient,
    private val maveSessionManager: MaveSessionManager,
    private val auth: FirebaseAuth,
    private val rtdb: FirebaseDatabase
) : ViewModel() {

    
    private val prefs = context.getSharedPreferences("mave_prefs", Context.MODE_PRIVATE)

    private val _hasAcceptedPrivacyPolicy = MutableStateFlow(prefs.getBoolean("has_accepted_privacy_policy", false))
    val hasAcceptedPrivacyPolicy: StateFlow<Boolean> = _hasAcceptedPrivacyPolicy.asStateFlow()

    fun acceptPrivacyPolicy() {
        prefs.edit().putBoolean("has_accepted_privacy_policy", true).apply()
        _hasAcceptedPrivacyPolicy.value = true
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        Timber.plant(CrashlyticsTree())
    }

    private val _isMusicAccountConnected = MutableStateFlow(false)
    val isMusicAccountConnected: StateFlow<Boolean> = _isMusicAccountConnected.asStateFlow()

    private val _tracks = MutableStateFlow<List<MaveTrack>>(emptyList())
    val tracks: StateFlow<List<MaveTrack>> = _tracks.asStateFlow()

    private val _playlists = MutableStateFlow<List<com.musically.studio.network.MavePlaylist>>(emptyList())
    val playlists: StateFlow<List<com.musically.studio.network.MavePlaylist>> = _playlists.asStateFlow()

    private val _categories = MutableStateFlow<List<com.musically.studio.network.MaveCategory>>(emptyList())
    val categories: StateFlow<List<com.musically.studio.network.MaveCategory>> = _categories.asStateFlow()

    private val _albums = MutableStateFlow<List<com.musically.studio.network.MaveAlbum>>(emptyList())
    val albums: StateFlow<List<com.musically.studio.network.MaveAlbum>> = _albums.asStateFlow()

    private val _podcasts = MutableStateFlow<List<com.musically.studio.network.MavePodcast>>(emptyList())
    val podcasts: StateFlow<List<com.musically.studio.network.MavePodcast>> = _podcasts.asStateFlow()

    private val _audiobooks = MutableStateFlow<List<com.musically.studio.network.MaveAudiobook>>(emptyList())
    val audiobooks: StateFlow<List<com.musically.studio.network.MaveAudiobook>> = _audiobooks.asStateFlow()

    private val _communityTracks = MutableStateFlow<List<MaveTrack>>(emptyList())
    val communityTracks: StateFlow<List<MaveTrack>> = _communityTracks.asStateFlow()
    
    private val _isWearableConnected = MutableStateFlow(false)
    val isWearableConnected: StateFlow<Boolean> = _isWearableConnected.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPlayingTrack = MutableStateFlow<MaveTrack?>(null)
    val currentPlayingTrack: StateFlow<MaveTrack?> = _currentPlayingTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _trackProgress = MutableStateFlow(0f)
    val trackProgress: StateFlow<Float> = _trackProgress.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled.asStateFlow()

    private val _isHapticFeedbackEnabled = MutableStateFlow(true)
    val isHapticFeedbackEnabled: StateFlow<Boolean> = _isHapticFeedbackEnabled.asStateFlow()

    // Navigation and UI State
    private val _shouldExpandBottomSheet = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val shouldExpandBottomSheet = _shouldExpandBottomSheet.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Route>(extraBufferCapacity = 1)
    val navigationEvent = _navigationEvent.asSharedFlow()

    // Auth Side Effects
    private val _authSideEffect = MutableSharedFlow<AuthSideEffect>(extraBufferCapacity = 1)
    val authSideEffect = _authSideEffect.asSharedFlow()

    val messages = mutableStateListOf<ChatMessage>()
    
    private val _thinkingText = MutableStateFlow("")
    val thinkingText: StateFlow<String> = _thinkingText.asStateFlow()

    private val _currentModality = MutableStateFlow("music")
    val currentModality: StateFlow<String> = _currentModality.asStateFlow()

    private val _generatedPrompts = MutableStateFlow<List<String>>(emptyList())
    val generatedPrompts: StateFlow<List<String>> = _generatedPrompts.asStateFlow()

    private var rtdbListener: ValueEventListener? = null

    // Registration State Accumulator
    var regEmail = ""
    var regPassword = ""
    var regBirthday = ""
    var regGender = ""
    var regName = ""

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _hasSeenTooltipTour = MutableStateFlow(false)
    val hasSeenTooltipTour: StateFlow<Boolean> = _hasSeenTooltipTour.asStateFlow()

    fun markTooltipTourSeen() {
        _hasSeenTooltipTour.value = true
    }

    // Live Session State
    private val _isLiveSessionActive = MutableStateFlow(false)
    val isLiveSessionActive: StateFlow<Boolean> = _isLiveSessionActive.asStateFlow()

    // Gallery/Video image-to-music state
    private val _pendingGalleryImageBase64 = MutableStateFlow<String?>(null)
    val pendingGalleryImageBase64: StateFlow<String?> = _pendingGalleryImageBase64.asStateFlow()

    // Wearable frame streaming toggle (default off to preserve battery)
    private val _isWearableFrameStreamingEnabled = MutableStateFlow(false)
    val isWearableFrameStreamingEnabled: StateFlow<Boolean> = _isWearableFrameStreamingEnabled.asStateFlow()

    private var wearableFrameJob: Job? = null

    // Throttle wearable frame streaming to 1 frame per 2 seconds to avoid overloading backend
    private val WEARABLE_FRAME_INTERVAL_MS = 2000L

    init {
        setupWebSocketCollector()
        setupWearableCollector()
        setupWearableFrameStreaming()
        if (isUserLoggedIn()) {
            startRtdbSync()
        }
        fetchCatalog()
    }

    private fun fetchCatalog() {
        fetchCategories()
        fetchPlaylists()
        fetchAlbums()
        fetchPodcasts()
        fetchAudiobooks()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            val result = apiClient.getCategories()
            if (result != null) _categories.value = result
        }
    }

    fun fetchPlaylists() {
        viewModelScope.launch {
            val result = apiClient.getPlaylists()
            if (result != null) _playlists.value = result
        }
    }

    fun fetchAlbums() {
        viewModelScope.launch {
            val result = apiClient.getAlbums()
            if (result != null) _albums.value = result
        }
    }

    fun fetchPodcasts() {
        viewModelScope.launch {
            val result = apiClient.getPodcasts()
            if (result != null) _podcasts.value = result
        }
    }

    fun fetchAudiobooks() {
        viewModelScope.launch {
            val result = apiClient.getAudiobooks()
            if (result != null) _audiobooks.value = result
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getUserId(): String = auth.currentUser?.uid ?: ""

    fun loginWithEmail(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    startRtdbSync()
                    callback(true, null)
                } else {
                    callback(false, "Check your email and password, or sign up for a new account.")
                }
            }
    }

    fun guestLogin(callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    startRtdbSync()
                    callback(true, null)
                } else {
                    callback(false, "Could not start guest session. Please check your connection.")
                }
            }
    }

    fun triggerGoogleSignIn() {
        viewModelScope.launch {
            _authSideEffect.emit(AuthSideEffect.LaunchGoogleSignIn)
        }
    }

    fun triggerAppleSignIn() {
        viewModelScope.launch {
            _authSideEffect.emit(AuthSideEffect.LaunchAppleSignIn)
        }
    }

    fun triggerVerifiedEmailSignIn() {
        viewModelScope.launch {
            _authSideEffect.emit(AuthSideEffect.LaunchVerifiedEmail)
        }
    }

    fun triggerFacebookSignIn() {
        viewModelScope.launch {
            _authSideEffect.emit(AuthSideEffect.LaunchFacebookSignIn)
        }
    }

    fun loginWithVerifiedEmail(credentialJson: String, nonce: String, callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val customToken = apiClient.verifyDigitalCredential(credentialJson, nonce)
                if (customToken != null) {
                    auth.signInWithCustomToken(customToken)
                        .addOnCompleteListener { task ->
                            _isLoading.value = false
                            if (task.isSuccessful) {
                                startRtdbSync()
                                callback(true, null)
                            } else {
                                callback(false, "Authentication failed with verified credential.")
                            }
                        }
                } else {
                    _isLoading.value = false
                    callback(false, "Digital credential verification failed on server.")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                callback(false, "An error occurred during verification: ${e.message}")
            }
        }
    }

    fun loginWithGoogle(idToken: String, callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    startRtdbSync()
                    callback(true, null)
                } else {
                    callback(false, "Google account connection encountered an issue.")
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
                                callback(false, "Could not complete your profile. Please try again.")
                            }
                        }
                    }
                } else {
                    _isLoading.value = false
                    callback(false, task.exception?.message ?: "Registration encountered an issue.")
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
                        "mave_thinking", "mave_chunk", "vision_thinking", "director_thinking" -> {
                            val chunk = json.optString("chunk") ?: json.optString("text")
                            _thinkingText.value += chunk
                        }
                        "agent_update" -> {
                            _thinkingText.value = ""
                            val vision = json.optString("vision")
                            val prompts = json.optJSONArray("prompts")
                            val script = json.optString("script")
                            val trackId = json.optString("trackId")
                            val reasoning = json.optString("reasoning")
                            val modality = json.optString("modality")

                            if (!modality.isNullOrBlank()) {
                                _currentModality.value = modality
                            }
                            
                            val message = if (!reasoning.isNullOrBlank()) {
                                reasoning
                            } else if (prompts != null && prompts.length() > 0) {
                                "New vibe: ${prompts.getString(0)}"
                            } else if (!script.isNullOrBlank()) {
                                script
                            } else {
                                vision
                            }
                            
                            if (message.isNotBlank()) {
                                messages.add(0, ChatMessage(message, false, trackId))
                                WearableStreamingService.updateUi("Mave Studio", message)
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

    // Collect wearable POV camera frames and forward to backend when streaming is enabled
    private fun setupWearableFrameStreaming() {
        wearableFrameJob = viewModelScope.launch {
            WearableStreamingService.cameraFrames.collect { frameBytes ->
                if (_isWearableFrameStreamingEnabled.value && _isLiveSessionActive.value) {
                    maveSessionManager.sendVideoFrame(frameBytes)
                    delay(WEARABLE_FRAME_INTERVAL_MS)
                }
            }
        }
    }

    fun toggleWearableFrameStreaming() {
        _isWearableFrameStreamingEnabled.value = !_isWearableFrameStreamingEnabled.value
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
                    "speak" -> recordVoice(null)
                }
            }
        }
        
        viewModelScope.launch {
            WearableStreamingService.audioFrames.collect { base64 ->
                maveSessionManager.sendAudio(base64)
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

    /** Starts the live real-time Mave session (WebSocket + RTDB sync). */
    fun startLiveSession() {
        if (_isLiveSessionActive.value) return
        _isLiveSessionActive.value = true
        maveSessionManager.connect()
        startRtdbSync()
    }

    /** Stops the live session including any ongoing recording. */
    fun stopLiveSession() {
        if (_isRecording.value) {
            stopRecording()
        }
        _isLiveSessionActive.value = false
        maveSessionManager.disconnect()
    }

    /** Legacy alias kept for compatibility. */
    fun connectToMave() {
        startLiveSession()
    }

    fun generateCoverMedia(prompt: String, type: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val result = apiClient.generateCoverMedia(prompt, type)
            onResult(result)
        }
    }

    fun sendTextCommand(text: String) {
        messages.add(0, ChatMessage(text, true))
        _thinkingText.value = ""
        maveSessionManager.sendEvent("feedback", mapOf("text" to text))
    }

    @SuppressLint("MissingPermission")
    fun recordVoice(context: Context?) {
        if (_isRecording.value) {
            stopRecording()
        } else {
            startRecording(context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording(context: Context?) {
        context?.let {
            val intent = Intent(it, WearableStreamingService::class.java)
            it.startForegroundService(intent)
        }
        _isRecording.value = true
        maveSessionManager.sendEvent("start_voice", emptyMap())
        WearableStreamingService.startVoiceRecording()
    }

    private fun stopRecording() {
        _isRecording.value = false
        WearableStreamingService.stopVoiceRecording()
        maveSessionManager.sendEvent("stop_voice", emptyMap())
    }

    // --- Playback Controls ---

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            maveSessionManager.play()
        } else {
            maveSessionManager.pause()
        }
    }

    fun setPlayingState(playing: Boolean) {
        if (_isPlaying.value != playing) {
            _isPlaying.value = playing
            // We don't call maveSessionManager.play() here because this is likely driven BY ExoPlayer changing state
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

    fun toggleHapticFeedback() {
        _isHapticFeedbackEnabled.value = !_isHapticFeedbackEnabled.value
    }

    fun playTrack(track: MaveTrack) {
        _currentPlayingTrack.value = track
        _isPlaying.value = true
        maveSessionManager.sendEvent("play_track", mapOf("trackId" to track.id))
        viewModelScope.launch { _shouldExpandBottomSheet.emit(true) }
    }

    fun sendFrame(base64: String) {
        maveSessionManager.sendEvent("vision", mapOf("image" to base64))
    }

    suspend fun getTrack(trackId: String): MaveTrack? {
        return apiClient.getTrack(trackId)
    }

    /**
     * Processes a gallery-picked or camera-captured image for music generation.
     * Sends the image to the backend orchestration layer which calls Gemini to generate
     * music prompts from visual context.
     */
    fun generateMusicPrompts(base64: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _pendingGalleryImageBase64.value = base64
            // Also send as a vision frame to the live session if active
            if (_isLiveSessionActive.value) {
                maveSessionManager.sendVideoFrame(
                    Base64.decode(base64, Base64.NO_WRAP)
                )
            }
            val result = apiClient.generateMusicPrompts(base64)
            if (result != null) {
                _generatedPrompts.value = result
                // Auto-send generated prompts to session if active
                if (_isLiveSessionActive.value && result.isNotEmpty()) {
                    val promptMaps = result.map { mapOf("text" to it, "weight" to 1.0) }
                    maveSessionManager.sendPrompts(promptMaps)
                }
            }
            _isLoading.value = false
        }
    }

    /** Called when the user has selected a gallery image (base64 encoded). */
    fun onGalleryImageSelected(base64: String) {
        generateMusicPrompts(base64)
    }

    /** Clear any pending gallery image state. */
    fun clearPendingGalleryImage() {
        _pendingGalleryImageBase64.value = null
    }

    fun applySteering(params: Map<String, Any>) {
        maveSessionManager.sendEvent("steering_action", mapOf("params" to params))
    }

    fun viewArtist(context: Context, track: MaveTrack) {
        if (track.userId != null) {
            // Case 2: Mave Community track -> route to user profile
            viewModelScope.launch {
                _navigationEvent.emit(Route.UserProfile(track.userId))
            }
        } else {
            // Case 1: Spotify track -> query from Spotify
            val artistId = track.artists.firstOrNull()?.id
            if (artistId != null) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = android.net.Uri.parse("spotify:artist:$artistId")
                    putExtra(Intent.EXTRA_REFERRER, android.net.Uri.parse("android-app://${context.packageName}"))
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback to web
                    val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://open.spotify.com/artist/$artistId"))
                    context.startActivity(webIntent)
                }
            }
        }
    }

    fun shareTrack(trackId: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            val url = apiClient.shareVibe(trackId)
            callback(url)
        }
    }

    fun addToPlaylist(trackId: String, playlistId: String? = null) {
        viewModelScope.launch {
            apiClient.addToPlaylist(trackId, playlistId)
        }
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

    fun connectMusicAccount() {
        viewModelScope.launch {
            _isMusicAccountConnected.value = true
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

    fun fetchCommunityTracks() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = apiClient.getCommunityTracks()
            if (result != null) {
                _communityTracks.value = result
            }
            _isLoading.value = false
        }
    }

    private val _userVibes = MutableStateFlow<List<MaveTrack>>(emptyList())
    val userVibes: StateFlow<List<MaveTrack>> = _userVibes.asStateFlow()

    private val _devices = MutableStateFlow<List<AudioDevice>>(emptyList())
    val devices: StateFlow<List<AudioDevice>> = _devices.asStateFlow()

    // Account deletion state
    private val _accountDeletionState = MutableStateFlow<AccountDeletionState>(AccountDeletionState.Idle)
    val accountDeletionState: StateFlow<AccountDeletionState> = _accountDeletionState.asStateFlow()

    fun loadAudioDevices() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val isBluetoothOn = audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
        @Suppress("DEPRECATION")
        val isWiredOn = audioManager.isWiredHeadsetOn

        val actualDevices = outputs.map { deviceInfo ->
            val type = when (deviceInfo.type) {
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                AudioDeviceInfo.TYPE_BLE_HEADSET -> DeviceType.BLUETOOTH
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> DeviceType.PHONE
                else -> DeviceType.SPEAKER
            }
            
            val isCurrent = when (deviceInfo.type) {
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP, AudioDeviceInfo.TYPE_BLUETOOTH_SCO, AudioDeviceInfo.TYPE_BLE_HEADSET -> isBluetoothOn
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES, AudioDeviceInfo.TYPE_WIRED_HEADSET -> isWiredOn
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> !isBluetoothOn && !isWiredOn
                else -> false
            }
            
            AudioDevice(
                id = deviceInfo.id.toString(),
                name = deviceInfo.productName.toString().takeIf { it.isNotBlank() } ?: "Unknown Audio Device",
                subtitle = if (type == DeviceType.BLUETOOTH) "Bluetooth" else "Local",
                type = type,
                isCurrent = isCurrent
            )
        }.distinctBy { it.name }
        
        val devicesToEmit = if (actualDevices.isNotEmpty() && actualDevices.none { it.isCurrent }) {
            actualDevices.mapIndexed { index, device -> if (index == 0) device.copy(isCurrent = true) else device }
        } else actualDevices
        _devices.value = devicesToEmit
    }

    fun selectDevice(device: AudioDevice) {
        val currentDevices = _devices.value.toMutableList()
        val updatedDevices = currentDevices.map { 
            it.copy(isCurrent = it.id == device.id)
        }
        _devices.value = updatedDevices
    }

    fun fetchVibesByUserId(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = apiClient.getVibesByUserId(userId)
            if (result != null) {
                _userVibes.value = result
            }
            _isLoading.value = false
        }
    }

    /**
     * Signs the current user out of Firebase Auth and disconnects all active sessions.
     * Callers should navigate to the Login screen upon completion.
     */
    fun signOut() {
        stopLiveSession()
        rtdbListener?.let { listener ->
            val user = auth.currentUser
            if (user != null) {
                rtdb.getReference("sessions/${user.uid}/state").removeEventListener(listener)
            }
        }
        rtdbListener = null
        auth.signOut()
        Timber.i("User signed out")
        viewModelScope.launch {
            _authSideEffect.emit(AuthSideEffect.SignedOut)
        }
    }

    /**
     * Permanently deletes the Firebase Auth account and all associated RTDB session data.
     *
     * Firebase requires a recent sign-in before account deletion. If the current credential
     * is too old, Firebase throws FirebaseAuthRecentLoginRequiredException — in that case
     * the UI should prompt the user to re-authenticate first.
     *
     * Per Play Store policy this must be discoverable from the account/profile screen.
     */
    fun deleteAccount() {
        val user = auth.currentUser
        if (user == null) {
            _accountDeletionState.value = AccountDeletionState.Error("No authenticated user found.")
            return
        }
        _accountDeletionState.value = AccountDeletionState.Loading
        viewModelScope.launch {
            try {
                // 1. Remove RTDB session data before deleting the auth account.
                val uid = user.uid
                rtdb.getReference("sessions/$uid").removeValue().await()
                Timber.i("RTDB session data cleared for uid=$uid")

                // 2. Disconnect live session and stop all ongoing services.
                stopLiveSession()
                rtdbListener?.let { listener ->
                    rtdb.getReference("sessions/$uid/state").removeEventListener(listener)
                }
                rtdbListener = null

                // 3. Delete the Firebase Auth account.
                user.delete().await()
                Timber.i("Firebase Auth account deleted for uid=$uid")

                _accountDeletionState.value = AccountDeletionState.Deleted
                _authSideEffect.emit(AuthSideEffect.AccountDeleted)
            } catch (e: com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                Timber.w(e, "Re-authentication required before account deletion")
                _accountDeletionState.value = AccountDeletionState.Error(
                    "For your security, please sign in again before deleting your account."
                )
            } catch (e: Exception) {
                Timber.e(e, "Account deletion failed")
                _accountDeletionState.value = AccountDeletionState.Error(
                    "Could not delete account: ${e.message}"
                )
            }
        }
    }

    /** Resets the deletion state after the UI has handled it. */
    fun resetAccountDeletionState() {
        _accountDeletionState.value = AccountDeletionState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        if (_isRecording.value) {
            stopRecording()
        }
        maveSessionManager.disconnect()
        rtdbListener?.let { listener ->
            val user = auth.currentUser
            if (user != null) {
                rtdb.getReference("sessions/${user.uid}/state").removeEventListener(listener)
            }
        }
    }
}

sealed interface AuthSideEffect {
    data object LaunchGoogleSignIn : AuthSideEffect
    data object LaunchAppleSignIn : AuthSideEffect
    data object LaunchVerifiedEmail : AuthSideEffect
    data object LaunchFacebookSignIn : AuthSideEffect
    data object SignedOut : AuthSideEffect
    data object AccountDeleted : AuthSideEffect
}

sealed interface AccountDeletionState {
    data object Idle : AccountDeletionState
    data object Loading : AccountDeletionState
    data object Deleted : AccountDeletionState
    data class Error(val message: String) : AccountDeletionState
}
