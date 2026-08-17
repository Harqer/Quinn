package com.musically.studio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.IBinder
import com.meta.wearable.dat.camera.Stream
import com.meta.wearable.dat.camera.addStream
import com.meta.wearable.dat.camera.removeStream
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.VideoQuality
import androidx.core.app.NotificationCompat
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.types.Permission
import com.meta.wearable.dat.core.types.PermissionStatus
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector
import com.meta.wearable.dat.core.selectors.SpecificDeviceSelector
import com.meta.wearable.dat.core.session.DeviceSession
import com.meta.wearable.dat.core.session.DeviceSessionState
import com.meta.wearable.dat.camera.types.StreamState
import com.meta.wearable.dat.display.Display
import com.meta.wearable.dat.display.addDisplay
import com.meta.wearable.dat.display.removeDisplay
import com.meta.wearable.dat.display.types.DisplayConfiguration
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Base64
import com.musically.studio.glasses.GlassesUIController
import com.meta.wearable.dat.display.views.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class WearableStreamingService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var activeSession: DeviceSession? = null
    private var activeStream: Stream? = null
    private var activeDisplay: Display? = null
    private var uiController: GlassesUIController? = null

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var isRecording = false
    private var isSessionPaused = false

    companion object {
        private val _cameraFrames = MutableSharedFlow<ByteArray>(extraBufferCapacity = 1)
        val cameraFrames = _cameraFrames.asSharedFlow()
        
        private val _audioFrames = MutableSharedFlow<String>(extraBufferCapacity = 10)
        val audioFrames = _audioFrames.asSharedFlow()
        
        private val _interactionEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
        val interactionEvents = _interactionEvents.asSharedFlow()
        
        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()
        
        private var instance: WearableStreamingService? = null

        fun updateUi(songTitle: String, coverArtUrl: String? = null, isPlaying: Boolean = false) {
            instance?.updateWearableUi(songTitle, coverArtUrl, isPlaying)
        }
        
        fun clearUi() {
            instance?.clearDisplay()
        }

        fun startVoiceRecording() {
            instance?.startAudioRecording()
        }
        
        fun stopVoiceRecording() {
            instance?.stopAudioRecording()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        _isServiceActive.value = true
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        
        // Permission Guard for Android 14+ requirements
        val hasCamera = checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasMic = checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        if (!hasCamera || !hasMic) {
            Timber.e("Service started without mandatory permissions. Stopping.")
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        val deviceId = intent?.getStringExtra("DEVICE_ID")
        
        scope.launch {
            Wearables.checkPermissionStatus(Permission.CAMERA).onSuccess { status ->
                if (status != PermissionStatus.Granted) {
                    Timber.e("Wearables DAT SDK Camera permission not granted. Stopping.")
                    stopSelf()
                    return@onSuccess
                }
                startWearableSession(deviceId)
            }.onFailure { error, _ ->
                Timber.e("Failed to check Wearables permission: ${error.description}")
                stopSelf()
            }
        }
        
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "wearable_service_channel",
            "Mave Wearable Streaming",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        } ?: Intent()
        val pendingIntent = android.app.PendingIntent.getActivity(
            this@WearableStreamingService, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this@WearableStreamingService, "wearable_service_channel")
            .setContentTitle("Mave Wearable")
            .setContentText("Streaming POV to Mave Studio...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }
    
    private fun updateNotification(songTitle: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        } ?: Intent()
        val pendingIntent = android.app.PendingIntent.getActivity(
            this@WearableStreamingService, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this@WearableStreamingService, "wearable_service_channel")
            .setContentTitle("Mave Wearable")
            .setContentText(if (songTitle.isNotEmpty()) "Playing: $songTitle" else "Streaming POV to Mave Studio...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
        notificationManager.notify(1, notification)
    }

    private fun startWearableSession(deviceId: String?) {
        scope.launch {
            val targetDevice = deviceId?.let { id -> Wearables.devices.value.find { it.identifier == id } }
            
            if (Wearables.devices.value.isEmpty()) {
                Timber.e("No devices found. Cannot start WearableSession. Please pair your glasses in the Meta AI app.")
                _isServiceActive.value = false
                stopSelf()
                return@launch
            }

            val sessionResult = if (targetDevice != null) {
                Wearables.createSession(SpecificDeviceSelector(targetDevice))
            } else {
                Wearables.createSession(AutoDeviceSelector())
            }
            
            sessionResult.onSuccess { session ->
                activeSession = session

                scope.launch {
                    session.state.collect { state ->
                        Timber.d("Session state changed: $state")
                        when (state) {
                            DeviceSessionState.STARTED -> {
                                Timber.i("Glasses session STARTED. Resuming stream processing.")
                                isSessionPaused = false
                            }
                            DeviceSessionState.PAUSED -> {
                                Timber.w("Glasses session PAUSED. Suspending stream processing.")
                                isSessionPaused = true
                            }
                            DeviceSessionState.STOPPED -> {
                                Timber.i("Glasses session STOPPED. Stopping service.")
                                stopSelf()
                            }
                            else -> Unit
                        }
                    }
                }

                session.start()
                attachCapabilities(session)
            }.onFailure { error, _ ->
                Timber.e("Failed to create session: ${error.description}")
                stopSelf()
            }
        }
    }

    private fun attachCapabilities(session: DeviceSession) {
        activeDisplay?.let { session.removeDisplay() }
        session.addDisplay(DisplayConfiguration()).onSuccess { display ->
            activeDisplay = display
            uiController = GlassesUIController(display)
            updateWearableUi("", "") // Initial UI render
            startStream(session)
        }.onFailure { error, _ ->
            Timber.e("Failed to add Display capability: ${error.description}")
            startStream(session)
        }
    }

    private var frameCollectionJob: Job? = null
    private var lastFrameTimeMs = 0L
    private var frameIntervalMs = 1000L / 15L

    private fun startStream(session: DeviceSession) {
        activeStream?.let { session.removeStream() }
        val targetFps = 15
        frameIntervalMs = 1000L / targetFps
        val config = StreamConfiguration(VideoQuality.MEDIUM, targetFps, false)
        session.addStream(config).onSuccess { stream ->
            activeStream = stream
            
            scope.launch {
                stream.state.collect { state ->
                    Timber.d("Stream state changed: $state")
                    when (state.name) {
                        "STREAMING" -> {
                            startFrameCollection(stream)
                        }
                        "PAUSED", "STOPPED", "CLOSED" -> {
                            stopFrameCollection()
                            if (state.name == "CLOSED") {
                                Timber.i("Stream CLOSED.")
                                activeStream = null
                            }
                        }
                        else -> Unit
                    }
                }
            }
            
            stream.start().onFailure { error, _ ->
                Timber.e("Failed to start stream: ${error.description}")
            }
        }.onFailure { error, _ ->
            Timber.e("Failed to add Stream capability: ${error.description}")
        }
    }

    private fun startFrameCollection(stream: Stream) {
        if (frameCollectionJob?.isActive == true) return
        frameCollectionJob = scope.launch(Dispatchers.IO) {
            stream.videoStream.collect { frame ->
                if (isSessionPaused) return@collect
                val now = System.currentTimeMillis()
                if (now - lastFrameTimeMs >= frameIntervalMs) {
                    lastFrameTimeMs = now
                    val bytes = ByteArray(frame.buffer.remaining())
                    frame.buffer.get(bytes)
                    _cameraFrames.emit(bytes)
                }
            }
        }
    }

    private fun stopFrameCollection() {
        frameCollectionJob?.cancel()
        frameCollectionJob = null
    }

    private var autoDismissJob: Job? = null

    fun clearDisplay() {
        if (isSessionPaused) return
        autoDismissJob?.cancel()
        scope.launch {
            activeDisplay?.sendContent {
                flexBox(
                    direction = Direction.COLUMN,
                    alignment = Alignment.CENTER,
                    crossAlignment = Alignment.CENTER
                ) {}
            }?.onFailure { error, _ ->
                Timber.e("Failed to clear display: ${error.description}")
            }
        }
    }

    fun updateWearableUi(songTitle: String, coverArtUrl: String? = null, isPlaying: Boolean = false) {
        updateNotification(songTitle)
        if (songTitle.isNotEmpty()) {
            showMusicPlayerCard(songTitle, null, coverArtUrl, isPlaying)
        } else {
            clearDisplay()
        }
    }

    private var playerJob: Job? = null

    fun showMusicPlayerCard(
        title: String,
        subtitle: String? = null,
        coverArtUrl: String? = null,
        isPlaying: Boolean
    ) {
        if (isSessionPaused) return
        autoDismissJob?.cancel()
        playerJob?.cancel()
        playerJob = scope.launch {
            uiController?.showMusicPlayerCard(title, subtitle, isPlaying) {
                emitInteraction("play_pause")
            }

            // Auto-dismiss after 4 seconds per Meta Wearables minimal audio UI guidelines
            autoDismissJob = scope.launch {
                delay(4000L)
                clearDisplay()
            }
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    private fun startAudioRecording() {
        if (isRecording) return
        isRecording = true

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.startBluetoothSco()
        audioManager.setBluetoothScoOn(true)

        recordingJob = scope.launch(Dispatchers.IO) {
            delay(1000) // Wait for SCO connection

            val sampleRate = 8000
            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            try {
                val recorder = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                    Timber.e("AudioRecord failed to initialize")
                    recorder.release()
                    isRecording = false
                    return@launch
                }

                audioRecord = recorder
                recorder.startRecording()

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
                        _audioFrames.emit(base64)
                    }
                }
            } catch (e: SecurityException) {
                Timber.e(e, "Missing RECORD_AUDIO permission")
                isRecording = false
            }
        }
    }

    private fun stopAudioRecording() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.let { recorder ->
            if (recorder.state == AudioRecord.STATE_INITIALIZED) {
                try {
                    recorder.stop()
                } catch (e: Exception) {
                    Timber.e(e, "Error stopping AudioRecord")
                }
            }
            recorder.release()
        }
        audioRecord = null

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.stopBluetoothSco()
        audioManager.setBluetoothScoOn(false)
    }

    private fun emitInteraction(type: String) {
        scope.launch {
            _interactionEvents.emit(type)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudioRecording()
        autoDismissJob?.cancel()
        instance = null
        _isServiceActive.value = false
        
        runBlocking(Dispatchers.IO) {
            val session = activeSession
            if (session != null) {
                try {
                    activeDisplay?.let { session.removeDisplay() }
                    activeStream?.let { session.removeStream() }
                    session.stop()
                } catch (e: Exception) {
                    Timber.e(e, "Error during Meta Wearable session cleanup")
                }
            }
        }
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
