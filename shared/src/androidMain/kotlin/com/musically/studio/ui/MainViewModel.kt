package com.musically.studio.ui

import android.annotation.SuppressLint
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.util.Base64
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.musically.studio.WearableStreamingService
import com.musically.studio.billing.PlayBillingManager
import com.musically.studio.dataconnect.DefaultConnector
import com.musically.studio.dataconnect.GetPaymentHistoryQuery.Data.PaymentHistoriesItem
import com.musically.studio.dataconnect.GetUserSettingsQuery.Data.UserSettings
import com.musically.studio.dataconnect.execute
import com.musically.studio.dataconnect.instance
import com.musically.studio.logging.CrashlyticsTree
import com.musically.studio.network.ApiClient
import com.musically.studio.network.GeminiLiveManager
import com.musically.studio.network.MaveSessionManager
import com.musically.studio.network.MaveTrack
import com.musically.studio.network.StreamingApiClient
import com.musically.studio.ui.models.AudioDevice
import com.musically.studio.ui.models.ChatMessage
import com.musically.studio.ui.models.DeviceType
import com.musically.studio.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@android.annotation.SuppressLint("MissingPermission")
@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiClient: ApiClient,
    private val maveSessionManager: MaveSessionManager,
    private val geminiLiveManager: GeminiLiveManager,
    private val streamingApiClient: StreamingApiClient,
    private val auth: FirebaseAuth,
    private val rtdb: FirebaseDatabase
) : ViewModel() {

    private val playBillingManager = PlayBillingManager(context)
    val billingProductDetails = playBillingManager.productDetails

    fun launchBillingFlow(activity: Activity, productId: String) {
        playBillingManager.launchBillingFlow(activity, productId)
    }

    
    private val prefs = context.getSharedPreferences("mave_prefs", Context.MODE_PRIVATE)

    private val _hasAcceptedPrivacyPolicy = MutableStateFlow(prefs.getBoolean("has_accepted_privacy_policy", false))
    val hasAcceptedPrivacyPolicy: StateFlow<Boolean> = _hasAcceptedPrivacyPolicy.asStateFlow()

    private val _hasDeclinedPrivacyPolicy = MutableStateFlow(prefs.getBoolean("has_declined_privacy_policy", false))
    val hasDeclinedPrivacyPolicy: StateFlow<Boolean> = _hasDeclinedPrivacyPolicy.asStateFlow()

    fun acceptPrivacyPolicy() {
        prefs.edit { putBoolean("has_accepted_privacy_policy", true) }
        _hasAcceptedPrivacyPolicy.value = true
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        Timber.plant(CrashlyticsTree())
    }

    fun declinePrivacyPolicy() {
        prefs.edit { putBoolean("has_declined_privacy_policy", true) }
        _hasDeclinedPrivacyPolicy.value = true
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
    }

    fun resetPrivacyPolicy() {
        prefs.edit { putBoolean("has_declined_privacy_policy", false) }
        _hasDeclinedPrivacyPolicy.value = false
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

    private val _catalogErrorMessage = MutableStateFlow<String?>(null)
    val catalogErrorMessage: StateFlow<String?> = _catalogErrorMessage.asStateFlow()

    fun clearCatalogError() {
        _catalogErrorMessage.value = null
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPlayingTrack = MutableStateFlow<MaveTrack?>(null)
    val currentPlayingTrack: StateFlow<MaveTrack?> = _currentPlayingTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentCoverUrl = MutableStateFlow<String?>(null)
    val currentCoverUrl: StateFlow<String?> = _currentCoverUrl.asStateFlow()

    private val _currentVideoUrl = MutableStateFlow<String?>(null)
    val currentVideoUrl: StateFlow<String?> = _currentVideoUrl.asStateFlow()

    private val _trackProgress = MutableStateFlow(0f)
    val trackProgress: StateFlow<Float> = _trackProgress.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow("none") // "none", "all", "one"
    val isRepeatEnabled: StateFlow<String> = _isRepeatEnabled.asStateFlow()

    private val _queue = MutableStateFlow<List<MaveTrack>>(emptyList())
    val queue: StateFlow<List<MaveTrack>> = _queue.asStateFlow()

    private val _originalQueue = MutableStateFlow<List<MaveTrack>>(emptyList())
    
    private val _queueIndex = MutableStateFlow(-1)
    val queueIndex: StateFlow<Int> = _queueIndex.asStateFlow()

    private val _isHapticFeedbackEnabled = MutableStateFlow(true)
    val isHapticFeedbackEnabled: StateFlow<Boolean> = _isHapticFeedbackEnabled.asStateFlow()

    // Settings State
    private val _userSettings = MutableStateFlow<UserSettings?>(null)
    val userSettings: StateFlow<UserSettings?> = _userSettings.asStateFlow()

    private val _paymentHistory = MutableStateFlow<List<PaymentHistoriesItem>>(emptyList())
    val paymentHistory: StateFlow<List<PaymentHistoriesItem>> = _paymentHistory.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _stripeUrl = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val stripeUrl = _stripeUrl.asSharedFlow()

    private val _oauthUrl = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val oauthUrl = _oauthUrl.asSharedFlow()

    private val _spotifyConnected = MutableStateFlow(false)
    val spotifyConnected = _spotifyConnected.asStateFlow()

    private val _youtubeConnected = MutableStateFlow(false)
    val youtubeConnected = _youtubeConnected.asStateFlow()

    // Navigation and UI State
    private val _shouldExpandBottomSheet = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val shouldExpandBottomSheet = _shouldExpandBottomSheet.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Route>(extraBufferCapacity = 1)
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun navigateTo(route: Route) {
        viewModelScope.launch {
            _navigationEvent.emit(route)
        }
    }

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

    private val _lyrics = MutableStateFlow<String?>(null)
    val lyrics: StateFlow<String?> = _lyrics.asStateFlow()

    private var rtdbListener: ValueEventListener? = null
    private var currentRtdbUid: String? = null

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

    private val _volume = MutableStateFlow(1f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // Gallery/Video image-to-music state
    private val _pendingGalleryImageBase64 = MutableStateFlow<String?>(null)
    val pendingGalleryImageBase64: StateFlow<String?> = _pendingGalleryImageBase64.asStateFlow()

    // Wearable frame streaming toggle (default off to preserve battery)
    private val _isWearableFrameStreamingEnabled = MutableStateFlow(false)
    val isWearableFrameStreamingEnabled: StateFlow<Boolean> = _isWearableFrameStreamingEnabled.asStateFlow()

    private var wearableFrameJob: Job? = null

    // Throttle wearable frame streaming to 1 frame per 2 seconds to avoid overloading backend
    private val WEARABLE_FRAME_INTERVAL_MS = 2000L

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    _isBluetoothEnabled.value = (state == BluetoothAdapter.STATE_ON)
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    if (device != null && context != null) {
                        try {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                @android.annotation.SuppressLint("MissingPermission")
                                val name = device.name ?: "Unknown Device"
                                val audioDevice = AudioDevice(
                                    id = device.address,
                                    name = name,
                                    subtitle = "Bluetooth",
                                    type = DeviceType.BLUETOOTH,
                                    isCurrent = false
                                )
                                val currentList = _devices.value.toMutableList()
                                if (currentList.none { it.id == audioDevice.id }) {
                                    currentList.add(audioDevice)
                                    _devices.value = currentList
                                }
                            }
                        } catch (e: SecurityException) {
                            Timber.e(e, "Missing BLUETOOTH_CONNECT permission")
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isScanning.value = false
                }
            }
        }
    }

    init {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
            val currentVolume = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
            _volume.value = if (maxVolume > 0) currentVolume.toFloat() / maxVolume else 1f
        } catch (e: Exception) {
            Timber.e(e, "Failed to read initial volume")
        }

        setupWebSocketCollector()
        setupWearableCollector()
        setupWearableFrameStreaming()
        setupMediaAssetCollector()
        
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(bluetoothReceiver, filter)

        if (isUserLoggedIn()) {
            startRtdbSync()
        }
        fetchCatalog()
    }

    private fun setupMediaAssetCollector() {
        viewModelScope.launch {
            maveSessionManager.coverArtUrl.collect { url ->
                _currentCoverUrl.value = url
            }
        }
        viewModelScope.launch {
            maveSessionManager.videoMotionUrl.collect { url ->
                _currentVideoUrl.value = url
            }
        }
    }

    private fun fetchCatalog() {
        fetchCategories()
        fetchPlaylists()
        fetchAlbums()
        fetchPodcasts()
        fetchAudiobooks()
        fetchUserSettings()
    }

    fun fetchUserSettings() {
        if (!isUserLoggedIn()) return
        viewModelScope.launch {
            try {
                // Fetch settings
                val settingsResult = DefaultConnector.instance.getUserSettings.execute()
                _userSettings.value = settingsResult.data.userSettings
                
                // Fetch payment history
                val historyResult = DefaultConnector.instance.getPaymentHistory.execute()
                _paymentHistory.value = historyResult.data.paymentHistories

                // Determine premium status based on active subscription in user settings
                _isPremium.value = settingsResult.data.userSettings?.isPremium == true
            } catch (e: Exception) {
                Timber.e(e, "Failed to load user settings from Data Connect")
            }
        }
    }

    fun updateTheme(theme: String) {
        val currentSettings = _userSettings.value
        val isPremium = currentSettings?.isPremium == true
        
        viewModelScope.launch {
            try {
                DefaultConnector.instance.upsertUserSettings.execute {
                    this.theme = theme
                    this.parentalControlsEnabled = currentSettings?.parentalControlsEnabled ?: false
                }
                fetchUserSettings() // Reload
            } catch (e: Exception) {
                Timber.e(e, "Failed to update theme")
            }
        }
    }

    fun updateParentalControls(enabled: Boolean) {
        val currentSettings = _userSettings.value
        viewModelScope.launch {
            try {
                DefaultConnector.instance.upsertUserSettings.execute {
                    this.theme = currentSettings?.theme ?: "system"
                    this.parentalControlsEnabled = enabled
                }
                fetchUserSettings() // Reload
            } catch (e: Exception) {
                Timber.e(e, "Failed to update parental controls")
            }
        }
    }

    fun launchStripePortal(returnUrl: String = "lyria://settings") {
        viewModelScope.launch {
            _isLoading.value = true
            val url = apiClient.createStripePortalSession(returnUrl)
            _isLoading.value = false
            url?.let { _stripeUrl.emit(it) }
        }
    }

    fun launchStripeCheckout(returnUrl: String = "lyria://settings") {
        val user = auth.currentUser
        if (user == null) {
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val sessionRef = db.collection("customers").document(user.uid).collection("checkout_sessions").document()
                val data = hashMapOf(
                    "price" to "default",
                    "success_url" to returnUrl,
                    "cancel_url" to returnUrl
                )
                sessionRef.set(data).await()
                
                sessionRef.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Timber.e(e, "Listen failed.")
                        _isLoading.value = false
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val url = snapshot.getString("url")
                        if (url != null) {
                            _isLoading.value = false
                            viewModelScope.launch {
                                _stripeUrl.emit(url)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to launch Stripe Checkout")
                _isLoading.value = false
            }
        }
    }

    fun connectSpotify() {
        viewModelScope.launch {
            try {
                // Ideally this would fetch from a defined ApiClient method, 
                // but for this implementation we simulate the response from the backend.
                val url = com.musically.studio.shared.BuildConfig.API_BASE_URL + "/api/spotify/auth-url"
                _oauthUrl.emit(url)
            } catch (e: Exception) {
                Timber.e(e, "Failed to connect Spotify")
            }
        }
    }

    fun connectYouTube() {
        viewModelScope.launch {
            try {
                val url = com.musically.studio.shared.BuildConfig.API_BASE_URL + "/api/youtube/auth-url"
                _oauthUrl.emit(url)
            } catch (e: Exception) {
                Timber.e(e, "Failed to connect YouTube")
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = apiClient.getCategories()
                if (result != null) {
                    _categories.value = result
                    _catalogErrorMessage.value = null
                }
            } catch (e: Exception) {
                _catalogErrorMessage.value = e.message ?: "Failed to load categories."
            }
        }
    }

    fun fetchPlaylists() {
        viewModelScope.launch {
            try {
                val result = apiClient.getPlaylists()
                if (result != null) {
                    _playlists.value = result
                    _catalogErrorMessage.value = null
                }
            } catch (e: Exception) {
                _catalogErrorMessage.value = e.message ?: "Failed to load playlists."
            }
        }
    }

    fun fetchAlbums() {
        viewModelScope.launch {
            try {
                val result = apiClient.getAlbums()
                if (result != null) {
                    _albums.value = result
                    _catalogErrorMessage.value = null
                }
            } catch (e: Exception) {
                _catalogErrorMessage.value = e.message ?: "Failed to load albums."
            }
        }
    }

    fun fetchPodcasts() {
        viewModelScope.launch {
            try {
                val result = apiClient.getPodcasts()
                if (result != null) {
                    _podcasts.value = result
                    _catalogErrorMessage.value = null
                }
            } catch (e: Exception) {
                _catalogErrorMessage.value = e.message ?: "Failed to load podcasts."
            }
        }
    }

    fun fetchAudiobooks() {
        viewModelScope.launch {
            try {
                val result = apiClient.getAudiobooks()
                if (result != null) {
                    _audiobooks.value = result
                    _catalogErrorMessage.value = null
                }
            } catch (e: Exception) {
                _catalogErrorMessage.value = e.message ?: "Failed to load audiobooks."
            }
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
                            try {
                                com.musically.studio.dataconnect.DefaultConnector.instance.upsertUser.execute {
                                    this.displayName = regName
                                    this.email = regEmail
                                }
                                _isLoading.value = false
                                startRtdbSync()
                                callback(true, null)
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to save profile via Data Connect")
                                _isLoading.value = false
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
            try {
                com.musically.studio.dataconnect.DefaultConnector.instance.updateUserPreferences.execute {
                    this.favoriteArtists = artists
                }
                callback(true)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save preferences via Data Connect")
                callback(false)
            }
        }
    }

    private fun setupWebSocketCollector() {
        viewModelScope.launch {
            maveSessionManager.events.collect { event ->
                try {
                    val json = JSONObject(event)
                    when (json.optString("type")) {
                        "ephemeral_token" -> {
                            val token = json.optString("token")
                            if (token.isNotBlank()) {
                                geminiLiveManager.connect(token)
                            }
                        }
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
                                prompts.getString(0) // No longer prefix with "New vibe:"
                            } else if (!script.isNullOrBlank()) {
                                script
                            } else {
                                vision
                            }
                            
                            if (message.isNotBlank()) {
                                messages.add(0, ChatMessage(message, false, trackId))
                                // Pipe to wearable with cover art context
                                WearableStreamingService.updateUi("Mave Studio", message, _currentCoverUrl.value)
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
        val uid = user.uid
        currentRtdbUid = uid
        val stateRef = rtdb.getReference("sessions/$uid/state")
        
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
                    (data?.get("coverArtUrl") as? String)?.let { _currentCoverUrl.value = it }
                    (data?.get("videoMotionUrl") as? String)?.let { _currentVideoUrl.value = it }
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
            try {
                com.meta.wearable.dat.core.Wearables.devices.collect { datDevices ->
                    val wearableAudioDevices = datDevices.map { device ->
                        AudioDevice(
                            id = device.identifier,
                            name = "Meta Glasses",
                            subtitle = "Meta Wearable",
                            type = DeviceType.BLUETOOTH,
                            isCurrent = false
                        )
                    }
                    val current = _devices.value.filter { it.subtitle != "Meta Wearable" }
                    _devices.value = current + wearableAudioDevices
                }
            } catch (e: Exception) {
                Timber.e(e, "Wearables SDK not initialized or failed (expected in tests)")
            }
        }

        viewModelScope.launch {
            WearableStreamingService.isServiceActive.collectLatest { active ->
                _isWearableConnected.value = active
            }
        }

        viewModelScope.launch {
            WearableStreamingService.interactionEvents.collect { event ->
                Timber.d("Wearable Interaction: $event")
                when (event) {
                    "play_pause" -> togglePlayPause()
                    "next" -> skipNext()
                    "previous" -> skipPrevious()
                    "stop" -> stopPlayback()
                    "generate" -> sendTextCommand("Generate a new atmosphere")
                    "speak" -> recordVoice(null)
                }
            }
        }
        
        viewModelScope.launch {
            WearableStreamingService.audioFrames.collect { base64 ->
                // Also send to Gemini Live for bidirectional dialogue if connected
                val pcmData = Base64.decode(base64, Base64.NO_WRAP)
                geminiLiveManager.sendAudio(pcmData)
                maveSessionManager.sendAudio(base64)
            }
        }
        
        viewModelScope.launch {
            WearableStreamingService.cameraFrames.collect { bytes ->
                // Send POV to Gemini Live for visual reasoning
                geminiLiveManager.sendVideoFrame(bytes)
            }
        }

        viewModelScope.launch {
            geminiLiveManager.functionCalls.collect { call ->
                val name = call.getString("name")
                val args = call.optJSONObject("args")
                val callId = call.getString("id")
                
                if (name == "generate_visual_media") {
                    val intent = args?.optString("intent") ?: "cover_art"
                    val pitch = args?.optString("creative_pitch") ?: ""
                    
                    // Notify our backend LangGraph to trigger visual production
                    sendTextCommand("Production Request: Generate $intent. Creative vision: $pitch")
                    
                    // Return success to Gemini Live so it can acknowledge in dialogue
                    val result = JSONObject().apply { put("status", "Initiated production sequence.") }
                    geminiLiveManager.sendResponse(callId, name, result)
                }
            }
        }

        viewModelScope.launch {
            geminiLiveManager.transcripts.collect { text ->
                messages.add(0, ChatMessage(text, false))
            }
        }

        viewModelScope.launch {
            geminiLiveManager.thoughts.collect { thought ->
                Timber.d("Art Director Reasoning: $thought")
                // Update Wearable HUD
                WearableStreamingService.updateUi(
                    songTitle = currentPlayingTrack.value?.name ?: "",
                    geminiResponse = "",
                    coverArtUrl = currentCoverUrl.value,
                    isThinking = true
                )
            }
        }

        viewModelScope.launch {
            geminiLiveManager.connectionState.collect { connected ->
                if (!connected) {
                    Timber.w("Gemini Live Disconnected")
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

    /** Starts the live real-time Mave session (WebSocket + RTDB sync). */
    fun startLiveSession() {
        if (!_hasAcceptedPrivacyPolicy.value) return
        if (_isLiveSessionActive.value) return
        _isLiveSessionActive.value = true
        maveSessionManager.connect()
        startRtdbSync()
    }

    /** Clears the live chat session history and resets the session */
    fun clearLiveSessionHistory() {
        messages.clear()
        stopLiveSession()
        startLiveSession()
    }

    /** Stops the live session including any ongoing recording. */
    fun stopLiveSession() {
        if (_isRecording.value) {
            stopRecording()
        }
        _isLiveSessionActive.value = false
        maveSessionManager.disconnect()
        geminiLiveManager.disconnect()
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

    private var currentAudioPlayer: com.musically.studio.audio.StreamAudioPlayer? = null

    fun generatePodcast(text: String) {
        messages.add(0, ChatMessage(text, true))
        _thinkingText.value = ""
        currentAudioPlayer?.stop()
        currentAudioPlayer = com.musically.studio.audio.StreamAudioPlayer()
        
        viewModelScope.launch {
            try {
                streamingApiClient.generatePodcastStream(text).collect { event ->
                    if (event.text != null) {
                        _thinkingText.value += event.text
                    }
                    if (event.audioBase64 != null) {
                        currentAudioPlayer?.queueAudioChunk(event.audioBase64)
                    }
                    if (event.isComplete) {
                        _thinkingText.value = ""
                        val script = event.trackInfo?.audioUrl ?: "Generated Podcast"
                        messages.add(0, ChatMessage(script, false, event.trackInfo?.id))
                        event.trackInfo?.let { track ->
                            _currentPlayingTrack.value = track
                            _isPlaying.value = true
                        }
                        currentAudioPlayer?.stop()
                        currentAudioPlayer = null
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error generating podcast")
                messages.add(0, ChatMessage("Error generating podcast", false))
                _thinkingText.value = ""
                currentAudioPlayer?.stop()
                currentAudioPlayer = null
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun recordVoice(context: Context?) {
        if (!_hasAcceptedPrivacyPolicy.value) return
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

    fun setVolume(volumeLevel: Float) {
        _volume.value = volumeLevel
        try {
            val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
            val mappedVolume = (volumeLevel * maxVolume).toInt()
            audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, mappedVolume, 0)
        } catch (e: Exception) {
            Timber.e(e, "Failed to set volume")
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

    fun playQueue(tracks: List<MaveTrack>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        
        _originalQueue.value = tracks
        
        if (_isShuffleEnabled.value) {
            val currentTrack = tracks[startIndex]
            val remaining = tracks.filterIndexed { i, _ -> i != startIndex }.shuffled()
            _queue.value = listOf(currentTrack) + remaining
            _queueIndex.value = 0
        } else {
            _queue.value = tracks
            _queueIndex.value = startIndex
        }
        
        playTrack(_queue.value[_queueIndex.value])
    }

    fun skipNext(autoAdvance: Boolean = false) {
        val currentQueue = _queue.value
        val currentIndex = _queueIndex.value
        
        if (currentQueue.isEmpty()) {
            maveSessionManager.sendEvent("skip_next", emptyMap())
            return
        }
        
        if (currentIndex < currentQueue.size - 1) {
            _queueIndex.value = currentIndex + 1
            playTrack(currentQueue[_queueIndex.value])
        } else if (_isRepeatEnabled.value == "all" || (!autoAdvance && currentQueue.isNotEmpty())) {
            _queueIndex.value = 0
            playTrack(currentQueue[0])
        } else {
            _isPlaying.value = false
            maveSessionManager.stop()
        }
    }

    fun skipPrevious() {
        val currentQueue = _queue.value
        val currentIndex = _queueIndex.value
        
        if (currentQueue.isEmpty()) {
            maveSessionManager.sendEvent("skip_previous", emptyMap())
            return
        }
        
        // If we are past 3 seconds, just restart current track (simulated by seeking to 0)
        if (_trackProgress.value > 3f) {
            seekTo(0f)
            return
        }
        
        if (currentIndex > 0) {
            _queueIndex.value = currentIndex - 1
            playTrack(currentQueue[_queueIndex.value])
        } else if (_isRepeatEnabled.value == "all") {
            _queueIndex.value = currentQueue.size - 1
            playTrack(currentQueue[_queueIndex.value])
        } else {
            seekTo(0f)
        }
    }

    fun seekTo(position: Float) {
        _trackProgress.value = position
        maveSessionManager.sendEvent("seek_to", mapOf("position" to position))
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
        val currentTrack = _currentPlayingTrack.value
        val currentQueue = _queue.value
        
        if (currentQueue.isNotEmpty() && currentTrack != null) {
            if (_isShuffleEnabled.value) {
                val remaining = _originalQueue.value.filter { it.id != currentTrack.id }.shuffled()
                _queue.value = listOf(currentTrack) + remaining
                _queueIndex.value = 0
            } else {
                _queue.value = _originalQueue.value
                _queueIndex.value = _queue.value.indexOfFirst { it.id == currentTrack.id }.coerceAtLeast(0)
            }
        }
        
        maveSessionManager.sendEvent("toggle_shuffle", mapOf("enabled" to _isShuffleEnabled.value))
    }

    fun toggleRepeat() {
        _isRepeatEnabled.value = when (_isRepeatEnabled.value) {
            "none" -> "all"
            "all" -> "one"
            else -> "none"
        }
        maveSessionManager.sendEvent("toggle_repeat", mapOf("mode" to _isRepeatEnabled.value))
    }

    fun toggleHapticFeedback() {
        _isHapticFeedbackEnabled.value = !_isHapticFeedbackEnabled.value
    }

    fun requestCoverArt() {
        sendTextCommand("Generate a high-fidelity album cover for this vibe.")
    }

    fun requestMusicVideo() {
        sendTextCommand("Generate a 35mm cinematic music video loop for this track.")
    }

    fun playTrack(track: MaveTrack) {
        _currentPlayingTrack.value = track
        _isPlaying.value = true
        maveSessionManager.sendEvent("play_track", mapOf("trackId" to track.id))
        viewModelScope.launch { _shouldExpandBottomSheet.emit(true) }
    }

    fun bookmarkTrack(trackId: String) {
        viewModelScope.launch {
            apiClient.bookmarkTrack(trackId)
        }
    }

    fun likeTrack(trackId: String) {
        viewModelScope.launch {
            apiClient.likeTrack(trackId)
        }
    }

    fun generateLyrics(trackId: String, audioUrl: String) {
        viewModelScope.launch {
            _lyrics.value = "Generating lyrics..."
            try {
                // Call backend endpoint which uses Gemini Flash to process the audio and generate lyrics
                val result = apiClient.generateLyrics(trackId, audioUrl)
                if (result != null) {
                    _lyrics.value = result
                } else {
                    _lyrics.value = "Could not generate lyrics."
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate lyrics")
                _lyrics.value = "Error generating lyrics."
            }
        }
    }

    fun shareTrack(trackId: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val url = apiClient.shareVibe(trackId)
                callback(url ?: "${com.musically.studio.shared.BuildConfig.API_BASE_URL}/track/$trackId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to share track")
                callback(null)
            }
        }
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
                    data = "spotify:artist:$artistId".toUri()
                    putExtra(Intent.EXTRA_REFERRER, "android-app://${context.packageName}".toUri())
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback to web
                    val webIntent = Intent(Intent.ACTION_VIEW, "https://open.spotify.com/artist/$artistId".toUri())
                    context.startActivity(webIntent)
                }
            }
        }
    }


    fun addToPlaylist(trackId: String, playlistId: String? = null) {
        viewModelScope.launch {
            apiClient.addToPlaylist(trackId, playlistId)
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
            try {
                val maveTracks = apiClient.getUserTracks()
                val spotifyTracks = apiClient.getSpotifyLibraryTracks()
                if (maveTracks == null && spotifyTracks == null) {
                    _catalogErrorMessage.value = "Failed to load library tracks."
                    _tracks.value = emptyList()
                } else {
                    _tracks.value = (maveTracks ?: emptyList()) + (spotifyTracks ?: emptyList())
                    _catalogErrorMessage.value = null
                }
            } catch (e: Exception) {
                _catalogErrorMessage.value = e.message ?: "Failed to load library tracks."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchCommunityTracks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = apiClient.getCommunityTracks()
                if (result != null) {
                    _communityTracks.value = result
                    _catalogErrorMessage.value = null
                }
            } catch (e: Exception) {
                _catalogErrorMessage.value = e.message ?: "Failed to load community tracks."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _userVibes = MutableStateFlow<List<MaveTrack>>(emptyList())
    val userVibes: StateFlow<List<MaveTrack>> = _userVibes.asStateFlow()

    private val _devices = MutableStateFlow<List<AudioDevice>>(emptyList())
    val devices: StateFlow<List<AudioDevice>> = _devices.asStateFlow()

    private val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // Account deletion state
    private val _accountDeletionState = MutableStateFlow<AccountDeletionState>(AccountDeletionState.Idle)
    val accountDeletionState: StateFlow<AccountDeletionState> = _accountDeletionState.asStateFlow()

    fun loadAudioDevices() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val isBluetoothOn = audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
        _isBluetoothEnabled.value = isBluetoothOn
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

        if (device.type == DeviceType.BLUETOOTH && device.id.contains(":")) {
            // It's a MAC address. Attempt to create a bond (pair)
            try {
                val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
                val btDevice = adapter?.getRemoteDevice(device.id)
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    if (btDevice?.bondState == BluetoothDevice.BOND_NONE) {
                        btDevice.createBond()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to pair with device")
            }
        }

        // Real Meta Wearable devices will be handled here if selected
        if (device.name.contains("Meta", ignoreCase = true) || device.name.contains("Ray-Ban", ignoreCase = true)) {
            val intent = Intent(context, WearableStreamingService::class.java).apply {
                putExtra("DEVICE_ID", device.id)
            }
            context.startForegroundService(intent)
        }
    }

    fun startBluetoothDiscovery(activityContext: Context) {
        val adapter = (activityContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
        if (adapter == null) {
            Timber.e("Bluetooth not supported on this device")
            return
        }

        if (ContextCompat.checkSelfPermission(activityContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Timber.e("Missing BLUETOOTH_SCAN permission")
            return
        }

        if (adapter.isDiscovering) {
            adapter.cancelDiscovery()
        }
        
        _isScanning.value = true
        adapter.startDiscovery()
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
            currentRtdbUid?.let { uid ->
                rtdb.getReference("sessions/$uid/state").removeEventListener(listener)
            }
        }
        rtdbListener = null
        currentRtdbUid = null
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
                
                // 1.5. Delete remote Firestore backend account profile.
                val backendDeleted = apiClient.deleteAccount()
                if (!backendDeleted) {
                    Timber.w("Could not delete backend profile for uid=$uid, proceeding with auth deletion anyway")
                }

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
        try {
            context.unregisterReceiver(bluetoothReceiver)
        } catch (e: Exception) {
            Timber.e(e, "Failed to unregister bluetoothReceiver")
        }
    }

    @kotlin.OptIn(androidx.credentials.ExperimentalDigitalCredentialApi::class)
    fun verifyEmail(activity: android.app.Activity) {
        viewModelScope.launch {
            try {
                val nonce = generateSecureRandomNonce()
                
                val openId4vpRequest = """
    {
      "requests": [
        {
          "protocol": "openid4vp-v1-unsigned",
          "data": {
            "response_type": "vp_token",
            "response_mode": "dc_api",
            "nonce": "$nonce",
            "dcql_query": {
              "credentials": [
                {
                  "id": "user_info_query",
                  "format": "dc+sd-jwt",
                   "meta": { 
                      "vct_values": ["UserInfoCredential"] 
                   },
                  "claims": [ 
                    {"path": ["email"]}, 
                    {"path": ["name"]},  
                    {"path": ["given_name"]},
                    {"path": ["family_name"]},
                    {"path": ["picture"]},
                    {"path": ["hd"]},
                    {"path": ["email_verified"]}
                  ]
                }
              ]
            }
          }
        }
      ]
    }
    """
                val getDigitalCredentialOption = androidx.credentials.GetDigitalCredentialOption(requestJson = openId4vpRequest)
                val request = androidx.credentials.GetCredentialRequest(listOf(getDigitalCredentialOption))
                
                val credentialManager = androidx.credentials.CredentialManager.create(context)
                val result = credentialManager.getCredential(activity, request)
                when (val credential = result.credential) {
                    is androidx.credentials.DigitalCredential -> {
                        val responseJsonString = credential.credentialJson
                        val (email, name) = parseSdJwtEmailAndName(responseJsonString)
                        
                        if (email != null) {
                            // 1. Sign in anonymously to get a Firebase Session
                            auth.signInAnonymously().await()
                            
                            // 2. Upsert the user into Cloud SQL via Firebase Data Connect
                            DefaultConnector.instance.upsertUser.execute {
                                this.displayName = name ?: "Verified User"
                                this.email = email
                            }
                            
                            navigateTo(Route.Home)
                        } else {
                            Timber.e("Could not parse email from digital credential.")
                        }
                    }
                    else -> {
                        Timber.e("Unexpected credential type: ${credential::class.java}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error getting verified email credential")
            }
        }
    }

    private fun parseSdJwtEmailAndName(responseJsonString: String): Pair<String?, String?> {
        try {
            val responseData = org.json.JSONObject(responseJsonString)
            val vpToken = responseData.optJSONObject("vp_token") ?: return Pair(null, null)
            val credentialId = vpToken.keys().next()
            val rawSdJwt = vpToken.getJSONArray(credentialId).getString(0)
            
            val parts = rawSdJwt.split("~")
            var email: String? = null
            var name: String? = null
            
            for (i in 1 until parts.size - 1) {
                val disclosureBase64 = parts[i]
                if (disclosureBase64.isBlank()) continue
                try {
                    val decoded = String(android.util.Base64.decode(disclosureBase64, android.util.Base64.URL_SAFE))
                    val jsonArray = org.json.JSONArray(decoded)
                    if (jsonArray.length() == 3) {
                        val claimName = jsonArray.getString(1)
                        val claimValue = jsonArray.getString(2)
                        if (claimName == "email") email = claimValue
                        if (claimName == "name" || claimName == "given_name") name = claimValue
                    }
                } catch (e: Exception) {
                    // Ignore malformed disclosures
                }
            }
            return Pair(email, name)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse SD-JWT")
            return Pair(null, null)
        }
    }

    private fun generateSecureRandomNonce(): String {
        val bytes = ByteArray(32)
        java.security.SecureRandom().nextBytes(bytes)
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE)
    }
}

sealed interface AuthSideEffect {
    data object LaunchGoogleSignIn : AuthSideEffect
    data object LaunchAppleSignIn : AuthSideEffect
    data object LaunchVerifiedEmail : AuthSideEffect

    data object SignedOut : AuthSideEffect
    data object AccountDeleted : AuthSideEffect
}

sealed interface AccountDeletionState {
    data object Idle : AccountDeletionState
    data object Loading : AccountDeletionState
    data object Deleted : AccountDeletionState
    data class Error(val message: String) : AccountDeletionState
}
