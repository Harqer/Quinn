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
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.musically.studio.WearableStreamingService
import com.musically.studio.billing.GenerationBlockReason
import com.musically.studio.billing.PlayBillingManager
import com.musically.studio.billing.SubscriptionTierLimits
import com.musically.studio.billing.TierLimits
import java.time.YearMonth
import com.musically.studio.dataconnect.DefaultConnector
import com.musically.studio.dataconnect.GetPaymentHistoryQuery.Data.PaymentHistoriesItem
import com.musically.studio.dataconnect.GetUserSettingsQuery.Data.UserSettings
import com.musically.studio.dataconnect.execute
import com.musically.studio.dataconnect.instance
import com.musically.studio.logging.CrashlyticsTree
import com.musically.studio.network.GeminiLiveManager
import com.musically.studio.network.MaveTrack
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
    @param:ApplicationContext internal val context: Context,
    internal val geminiLiveManager: GeminiLiveManager,
    internal val auth: FirebaseAuth,
    internal val rtdb: FirebaseDatabase,
    internal val dataConnectRepository: com.musically.studio.data.repository.DataConnectRepository,
    internal val rtdbSessionManager: com.musically.studio.network.RtdbSessionManager
) : ViewModel() {

    internal val playBillingManager = PlayBillingManager(
        context = context,
        onPurchaseAcknowledged = { productId ->
            // Optimistic update: re-sync settings so isPremium reflects the new state.
            // The authoritative DB update happens via the Play Developer Notification webhook
            // (server-side, not implemented in this client PR — see implementation_plan.md).
            fetchUserSettings()
        }
    )
    val billingProductDetails = playBillingManager.productDetails

    /** The Play product ID of the user's active subscription, or null if on free tier. */
    val currentProductId: StateFlow<String?> = playBillingManager.currentProductId

    /** Entitlement limits derived reactively from [currentProductId]. */
    val tierLimits: TierLimits
        get() = SubscriptionTierLimits.forProductId(currentProductId.value)

    fun launchBillingFlow(activity: Activity, productId: String) {
        playBillingManager.launchBillingFlow(activity, productId)
    }

    fun restorePurchases() {
        playBillingManager.restorePurchases()
    }

    internal val prefs = context.getSharedPreferences("mave_prefs", Context.MODE_PRIVATE)

    internal val _hasAcceptedPrivacyPolicy = MutableStateFlow(prefs.getBoolean("has_accepted_privacy_policy", false))
    val hasAcceptedPrivacyPolicy: StateFlow<Boolean> = _hasAcceptedPrivacyPolicy.asStateFlow()

    internal val _hasDeclinedPrivacyPolicy = MutableStateFlow(prefs.getBoolean("has_declined_privacy_policy", false))
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

    internal val _isMusicAccountConnected = MutableStateFlow(false)
    val isMusicAccountConnected: StateFlow<Boolean> = _isMusicAccountConnected.asStateFlow()

    internal val _tracks = MutableStateFlow<List<MaveTrack>>(emptyList())
    val tracks: StateFlow<List<MaveTrack>> = _tracks.asStateFlow()

    internal val _likedTracks = MutableStateFlow<List<MaveTrack>>(emptyList())
    val likedTracks: StateFlow<List<MaveTrack>> = _likedTracks.asStateFlow()

    internal val _bookmarkedTracks = MutableStateFlow<List<MaveTrack>>(emptyList())
    val bookmarkedTracks: StateFlow<List<MaveTrack>> = _bookmarkedTracks.asStateFlow()

    internal val _downloadedTracks = MutableStateFlow<List<MaveTrack>>(emptyList())
    val downloadedTracks: StateFlow<List<MaveTrack>> = _downloadedTracks.asStateFlow()

    internal val _playlists = MutableStateFlow<List<com.musically.studio.network.MavePlaylist>>(emptyList())
    val playlists: StateFlow<List<com.musically.studio.network.MavePlaylist>> = _playlists.asStateFlow()

    internal val _recentTracks = MutableStateFlow<List<MaveTrack>>(emptyList())
    val recentTracks: StateFlow<List<MaveTrack>> = _recentTracks.asStateFlow()

    internal val _isOfflineMode = MutableStateFlow(prefs.getBoolean("offline_mode", false))
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    internal val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("notifications_enabled", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    internal val _appsDevicesEnabled = MutableStateFlow(prefs.getBoolean("apps_devices_enabled", true))
    val appsDevicesEnabled: StateFlow<Boolean> = _appsDevicesEnabled.asStateFlow()

    fun toggleOfflineMode(enabled: Boolean) {
        prefs.edit { putBoolean("offline_mode", enabled) }
        _isOfflineMode.value = enabled
    }

    fun toggleNotifications(enabled: Boolean) {
        prefs.edit { putBoolean("notifications_enabled", enabled) }
        _notificationsEnabled.value = enabled
    }

    fun toggleAppsDevices(enabled: Boolean) {
        prefs.edit { putBoolean("apps_devices_enabled", enabled) }
        _appsDevicesEnabled.value = enabled
    }

    private val gson = Gson()

    fun loadRecentTracks() {
        val json = prefs.getString("recent_tracks", "[]")
        try {
            val type = object : TypeToken<List<MaveTrack>>() {}.type
            val tracks: List<MaveTrack> = gson.fromJson(json, type) ?: emptyList()
            _recentTracks.value = tracks
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse recent tracks")
            _recentTracks.value = emptyList()
        }
    }

    fun loadDownloadedTracks() {
        val json = prefs.getString("downloaded_tracks", "[]")
        try {
            val type = object : TypeToken<List<MaveTrack>>() {}.type
            val tracks: List<MaveTrack> = gson.fromJson(json, type) ?: emptyList()
            _downloadedTracks.value = tracks
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse downloaded tracks")
            _downloadedTracks.value = emptyList()
        }
    }

    fun addRecentTrack(track: MaveTrack) {
        val current = _recentTracks.value.toMutableList()
        current.removeAll { it.id == track.id }
        current.add(0, track)
        val trimmed = current.take(50)
        _recentTracks.value = trimmed

        val json = gson.toJson(trimmed)
        prefs.edit { putString("recent_tracks", json) }
    }

    internal val _categories = MutableStateFlow<List<com.musically.studio.network.MaveCategory>>(emptyList())
    val categories: StateFlow<List<com.musically.studio.network.MaveCategory>> = _categories.asStateFlow()

    internal val _albums = MutableStateFlow<List<com.musically.studio.network.MaveAlbum>>(emptyList())
    val albums: StateFlow<List<com.musically.studio.network.MaveAlbum>> = _albums.asStateFlow()

    internal val _podcasts = MutableStateFlow<List<com.musically.studio.network.MavePodcast>>(emptyList())
    val podcasts: StateFlow<List<com.musically.studio.network.MavePodcast>> = _podcasts.asStateFlow()

    internal val _audiobooks = MutableStateFlow<List<com.musically.studio.network.MaveAudiobook>>(emptyList())
    val audiobooks: StateFlow<List<com.musically.studio.network.MaveAudiobook>> = _audiobooks.asStateFlow()

    internal val _communityTracks = MutableStateFlow<List<MaveTrack>>(emptyList())
    val communityTracks: StateFlow<List<MaveTrack>> = _communityTracks.asStateFlow()
    
    internal val _isWearableConnected = MutableStateFlow(false)
    val isWearableConnected: StateFlow<Boolean> = _isWearableConnected.asStateFlow()

    internal val _catalogErrorMessage = MutableStateFlow<String?>(null)
    val catalogErrorMessage: StateFlow<String?> = _catalogErrorMessage.asStateFlow()

    internal val _searchResults = MutableStateFlow<List<MaveTrack>>(emptyList())
    val searchResults: StateFlow<List<MaveTrack>> = _searchResults.asStateFlow()


    internal val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    internal val _currentPlayingTrack = MutableStateFlow<MaveTrack?>(null)
    val currentPlayingTrack: StateFlow<MaveTrack?> = _currentPlayingTrack.asStateFlow()

    internal val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    internal val _currentCoverUrl = MutableStateFlow<String?>(null)
    val currentCoverUrl: StateFlow<String?> = _currentCoverUrl.asStateFlow()

    internal val _currentVideoUrl = MutableStateFlow<String?>(null)
    val currentVideoUrl: StateFlow<String?> = _currentVideoUrl.asStateFlow()

    internal val _trackProgress = MutableStateFlow(0f)
    val trackProgress: StateFlow<Float> = _trackProgress.asStateFlow()

    internal val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    internal val _isRepeatEnabled = MutableStateFlow("none") // "none", "all", "one"
    val isRepeatEnabled: StateFlow<String> = _isRepeatEnabled.asStateFlow()

    internal val _queue = MutableStateFlow<List<MaveTrack>>(emptyList())
    val queue: StateFlow<List<MaveTrack>> = _queue.asStateFlow()

    internal val _originalQueue = MutableStateFlow<List<MaveTrack>>(emptyList())
    
    internal val _queueIndex = MutableStateFlow(-1)
    val queueIndex: StateFlow<Int> = _queueIndex.asStateFlow()

    internal val _isHapticFeedbackEnabled = MutableStateFlow(true)
    val isHapticFeedbackEnabled: StateFlow<Boolean> = _isHapticFeedbackEnabled.asStateFlow()

    // Settings State
    internal val _userSettings = MutableStateFlow<UserSettings?>(null)
    val userSettings: StateFlow<UserSettings?> = _userSettings.asStateFlow()

    internal val _paymentHistory = MutableStateFlow<List<PaymentHistoriesItem>>(emptyList())
    val paymentHistory: StateFlow<List<PaymentHistoriesItem>> = _paymentHistory.asStateFlow()

    internal val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    // ---------------------------------------------------------------------------
    // Usage Counters (RTDB — path: users/{uid}/usage/{YYYY-MM}/)
    // Reset is handled by a Cloud Scheduler job (server-side, separate from this PR).
    // ---------------------------------------------------------------------------
    internal val _songsThisMonth = MutableStateFlow(0)
    val songsThisMonth: StateFlow<Int> = _songsThisMonth.asStateFlow()

    internal val _podcastEpsThisMonth = MutableStateFlow(0)
    val podcastEpsThisMonth: StateFlow<Int> = _podcastEpsThisMonth.asStateFlow()

    internal val _realtimeMinutesThisMonth = MutableStateFlow(0)
    val realtimeMinutesThisMonth: StateFlow<Int> = _realtimeMinutesThisMonth.asStateFlow()

    /**
     * Emits whenever a generation attempt is blocked by the quota system.
     * Consumed by [AppNavigation] to navigate to [Route.UsageLimitSheet].
     * Prefer a SharedFlow over a StateFlow here so the event fires once and doesn't
     * re-trigger on re-composition.
     */
    internal val _generationBlockedEvent = MutableSharedFlow<GenerationBlockReason>(extraBufferCapacity = 1)
    val generationBlockedEvent = _generationBlockedEvent.asSharedFlow()

    internal val _stripeUrl = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val stripeUrl = _stripeUrl.asSharedFlow()

    internal val _oauthUrl = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val oauthUrl = _oauthUrl.asSharedFlow()

    internal val _spotifyConnected = MutableStateFlow(false)
    val spotifyConnected = _spotifyConnected.asStateFlow()

    internal val _youtubeConnected = MutableStateFlow(false)
    val youtubeConnected = _youtubeConnected.asStateFlow()

    // Navigation and UI State
    internal val _shouldExpandBottomSheet = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val shouldExpandBottomSheet = _shouldExpandBottomSheet.asSharedFlow()

    internal val _navigationEvent = MutableSharedFlow<Route>(extraBufferCapacity = 1)
    val navigationEvent = _navigationEvent.asSharedFlow()

    internal val _clearNavigationEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val clearNavigationEvent = _clearNavigationEvent.asSharedFlow()

    fun navigateTo(route: Route) {
        viewModelScope.launch {
            _navigationEvent.emit(route)
        }
    }

    fun clearNavigation() {
        viewModelScope.launch {
            _clearNavigationEvent.emit(Unit)
        }
    }

    // Auth Side Effects
    internal val _authSideEffect = MutableSharedFlow<AuthSideEffect>(extraBufferCapacity = 1)
    val authSideEffect = _authSideEffect.asSharedFlow()

    val messages = mutableStateListOf<ChatMessage>()
    
    internal val _thinkingText = MutableStateFlow("")
    val thinkingText: StateFlow<String> = _thinkingText.asStateFlow()

    internal val _currentModality = MutableStateFlow("music")
    val currentModality: StateFlow<String> = _currentModality.asStateFlow()

    internal val _generatedPrompts = MutableStateFlow<List<String>>(emptyList())
    val generatedPrompts: StateFlow<List<String>> = _generatedPrompts.asStateFlow()

    internal val _lyrics = MutableStateFlow<String?>(null)
    val lyrics: StateFlow<String?> = _lyrics.asStateFlow()

    internal var rtdbSyncJob: Job? = null
    internal var currentRtdbUid: String? = null
    internal var mfaResolver: com.google.firebase.auth.MultiFactorResolver? = null

    // Registration State Accumulator
    var regEmail = ""
    var regPassword = ""
    var regBirthday = ""
    var regGender = ""
    var regName = ""

    internal val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    internal val _hasSeenTooltipTour = MutableStateFlow(false)
    val hasSeenTooltipTour: StateFlow<Boolean> = _hasSeenTooltipTour.asStateFlow()

    fun markTooltipTourSeen() {
        _hasSeenTooltipTour.value = true
    }

    // Live Session State
    internal val _isLiveSessionActive = MutableStateFlow(false)
    val isLiveSessionActive: StateFlow<Boolean> = _isLiveSessionActive.asStateFlow()

    internal val _volume = MutableStateFlow(1f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // Gallery/Video image-to-music state
    internal val _pendingGalleryImageBase64 = MutableStateFlow<String?>(null)
    val pendingGalleryImageBase64: StateFlow<String?> = _pendingGalleryImageBase64.asStateFlow()

    // Wearable frame streaming toggle (default off to preserve battery)
    internal val _isWearableFrameStreamingEnabled = MutableStateFlow(false)
    val isWearableFrameStreamingEnabled: StateFlow<Boolean> = _isWearableFrameStreamingEnabled.asStateFlow()

    internal var wearableFrameJob: Job? = null

    // Throttle wearable frame streaming to 1 frame per 2 seconds to avoid overloading backend
    internal val WEARABLE_FRAME_INTERVAL_MS = 2000L

    internal val bluetoothReceiver = object : BroadcastReceiver() {
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
        loadRecentTracks()
        loadDownloadedTracks()
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

                // Determine premium status from Data Connect (server-verified source).
                // currentProductId comes from Play Billing (client-verified source).
                // Both are kept in sync; neither alone is sufficient.
                _isPremium.value = settingsResult.data.userSettings?.isPremium == true
            } catch (e: Exception) {
                Timber.e(e, "Failed to load user settings from Data Connect")
            }
        }
        // Load usage counters from RTDB for the current billing month
        loadUsageCounters()
    }

    /**
     * Reads this month's generation usage from RTDB.
     * Path: users/{uid}/usage/{YYYY-MM}/{counter}
     *
     * RTDB is used rather than Data Connect because counters need fast increments per
     * generation event and monthly resets via Cloud Scheduler — RTDB handles both
     * without index or query complexity.
     */
    internal fun loadUsageCounters() {
        val uid = auth.currentUser?.uid ?: return
        val monthKey = YearMonth.now().toString() // e.g. "2026-07"
        val usageRef = rtdb.getReference("users/$uid/usage/$monthKey")
        usageRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                _songsThisMonth.value = snapshot.child("songs_generated").getValue(Int::class.java) ?: 0
                _podcastEpsThisMonth.value = snapshot.child("podcast_eps_generated").getValue(Int::class.java) ?: 0
                _realtimeMinutesThisMonth.value = snapshot.child("realtime_minutes").getValue(Int::class.java) ?: 0
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Timber.e("Usage counter load cancelled: ${error.message}")
            }
        })
    }

    /** Increments a generation counter in RTDB for the current month. */
    internal fun incrementUsageCounter(counter: String, by: Int = 1) {
        val uid = auth.currentUser?.uid ?: return
        val monthKey = YearMonth.now().toString()
        rtdb.getReference("users/$uid/usage/$monthKey/$counter")
            .setValue(com.google.firebase.database.ServerValue.increment(by.toLong()))
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
        val user = auth.currentUser
        if (user == null) {
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val functions = com.google.firebase.functions.FirebaseFunctions.getInstance()
                val data = hashMapOf("returnUrl" to returnUrl)
                functions
                    .getHttpsCallable("ext-firestore-stripe-payments-createPortalLink")
                    .call(data)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result?.data as? Map<*, *>
                            val url = result?.get("url") as? String
                            if (url != null) {
                                // Navigate to url, e.g. using Intent
                            }
                        }
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error launching Stripe portal")
                _isLoading.value = false
            }
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

    internal var categoryJob: kotlinx.coroutines.Job? = null

    internal var playlistJob: kotlinx.coroutines.Job? = null

    internal var albumJob: kotlinx.coroutines.Job? = null

    internal var podcastJob: kotlinx.coroutines.Job? = null

    internal var audiobookJob: kotlinx.coroutines.Job? = null
















    internal fun setupWebSocketCollector() {
        viewModelScope.launch {
            geminiLiveManager.thoughts.collect { thought ->
                _thinkingText.value = thought
            }
        }
        
        viewModelScope.launch {
            geminiLiveManager.transcripts.collect { text ->
                if (text.isNotBlank()) {
                    messages.add(0, ChatMessage(text, false))
                    WearableStreamingService.updateUi(
                        songTitle = _currentPlayingTrack.value?.name ?: "Mave Studio",
                        geminiResponse = text,
                        coverArtUrl = _currentCoverUrl.value,
                        isThinking = false,
                        isPlaying = _isPlaying.value
                    )
                }
            }
        }

        viewModelScope.launch {
            geminiLiveManager.functionCalls.collect { call ->
                try {
                    val name = call.optString("name")
                    val args = call.optJSONObject("args")
                    if (name == "generate_visual_media") {
                        val intent = args?.optString("intent")
                        val pitch = args?.optString("creative_pitch") ?: "Generating visual..."
                        _thinkingText.value = pitch
                        
                        geminiLiveManager.sendResponse(call.optString("id", "0"), name, JSONObject().apply {
                            put("status", "success")
                            put("message", "Triggered $intent generation")
                        })
                    } else if (name == "search_concerts") {
                        val query = args?.optString("query") ?: ""
                        _thinkingText.value = "Searching for $query concerts near you..."
                        
                        viewModelScope.launch {
                            try {
                                val locationManager = com.musically.studio.location.LocationManager(context)
                                val location = locationManager.getCurrentLocation()
                                
                                val functionArgs = mutableMapOf<String, Any>("q" to query)
                                if (location != null) {
                                    functionArgs["lat"] = location.latitude
                                    functionArgs["lon"] = location.longitude
                                    functionArgs["range"] = "50mi"
                                }
                                
                                val functions = com.google.firebase.Firebase.functions
                                val result = functions.getHttpsCallable("searchConcerts").call(functionArgs).await()
                                
                                val data = result.data as? Map<String, Any>
                                val jsonResponse = JSONObject(data ?: emptyMap<String, Any>())
                                
                                geminiLiveManager.sendResponse(call.optString("id", "0"), name, jsonResponse)
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to search concerts")
                                geminiLiveManager.sendResponse(call.optString("id", "0"), name, JSONObject().apply {
                                    put("error", e.message)
                                })
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to handle tool call")
                }
            }
        }
    }

    fun startRtdbSync() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        currentRtdbUid = uid
        
        rtdbSyncJob?.cancel()
        rtdbSyncJob = viewModelScope.launch {
            rtdbSessionManager.observeSessionState(uid).collect { data ->
                try {
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
        }
    }

    // Collect wearable POV camera frames and forward to backend when streaming is enabled




    /** Starts the live real-time Mave session (WebSocket + RTDB sync). */

    /** Clears the live chat session history and resets the session */

    /** Stops the live session including any ongoing recording. */

    /** Legacy alias kept for compatibility. */



    internal var currentAudioPlayer: com.musically.studio.audio.StreamAudioPlayer? = null


    


    // --- Playback Controls ---





















    /**
     * Processes a gallery-picked or camera-captured image for music generation.
     * Sends the image to the backend orchestration layer which calls Gemini to generate
     * music prompts from visual context.
     */

    /** Called when the user has selected a gallery image (base64 encoded). */

    /** Clear any pending gallery image state. */









    internal var userTracksJob: kotlinx.coroutines.Job? = null
    internal var likedTracksJob: kotlinx.coroutines.Job? = null


    internal var communityTrackJob: kotlinx.coroutines.Job? = null

    internal val _userVibes = MutableStateFlow<List<MaveTrack>>(emptyList())
    val userVibes: StateFlow<List<MaveTrack>> = _userVibes.asStateFlow()

    internal val _devices = MutableStateFlow<List<AudioDevice>>(emptyList())
    val devices: StateFlow<List<AudioDevice>> = _devices.asStateFlow()

    internal val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

    internal val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // Account deletion state
    internal val _accountDeletionState = MutableStateFlow<AccountDeletionState>(AccountDeletionState.Idle)
    val accountDeletionState: StateFlow<AccountDeletionState> = _accountDeletionState.asStateFlow()

    fun loadAudioDevices() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val isBluetoothOn = outputs.any { 
            it.type == android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || 
            it.type == android.media.AudioDeviceInfo.TYPE_BLUETOOTH_SCO
        }
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


    /**
     * Signs the current user out of Firebase Auth and disconnects all active sessions.
     * Callers should navigate to the Login screen upon completion.
     */

    /**
     * Permanently deletes the Firebase Auth account and all associated RTDB session data.
     *
     * Firebase requires a recent sign-in before account deletion. If the current credential
     * is too old, Firebase throws FirebaseAuthRecentLoginRequiredException — in that case
     * the UI should prompt the user to re-authenticate first.
     *
     * Per Play Store policy this must be discoverable from the account/profile screen.
     */

    /** Resets the deletion state after the UI has handled it. */

    override fun onCleared() {
        if (_isRecording.value) {
            stopRecording()
        }
        geminiLiveManager.disconnect()
        rtdbSyncJob?.cancel()
        rtdbSyncJob = null
        try {
            context.unregisterReceiver(bluetoothReceiver)
        } catch (e: Exception) {
            Timber.e(e, "Failed to unregister bluetoothReceiver")
        }
    }

    fun getUserDisplayName(): String {
        return auth.currentUser?.displayName ?: "User"
    }

    fun getUserPhotoUrl(): String? {
        return auth.currentUser?.photoUrl?.toString()
    }

}

sealed interface AuthSideEffect {
    data object LaunchVerifiedEmail : AuthSideEffect
    data class LaunchMfaVerification(val resolver: com.google.firebase.auth.MultiFactorResolver) : AuthSideEffect

    data object SignedOut : AuthSideEffect
    data object AccountDeleted : AuthSideEffect
}

sealed interface AccountDeletionState {
    data object Idle : AccountDeletionState
    data object Loading : AccountDeletionState
    data object Deleted : AccountDeletionState
    data class Error(val message: String) : AccountDeletionState
}
