package com.musically.studio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import com.meta.wearable.dat.camera.Stream
import com.meta.wearable.dat.camera.addStream
import com.meta.wearable.dat.camera.removeStream
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.VideoQuality
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector
import com.meta.wearable.dat.core.selectors.SpecificDeviceSelector
import com.meta.wearable.dat.core.session.DeviceSession
import com.meta.wearable.dat.core.session.DeviceSessionState
import com.meta.wearable.dat.display.Display
import com.meta.wearable.dat.display.addDisplay
import com.meta.wearable.dat.display.removeDisplay
import com.meta.wearable.dat.display.types.DisplayConfiguration
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Base64
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

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var isRecording = false

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

        fun updateUi(songTitle: String, geminiResponse: String, coverArtUrl: String? = null, isThinking: Boolean = false) {
            instance?.updateWearableUi(songTitle, geminiResponse, coverArtUrl, isThinking)
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
        startWearableSession(deviceId)
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
        return Notification.Builder(this@WearableStreamingService, "wearable_service_channel")
            .setContentTitle("Mave Wearable")
            .setContentText("Streaming POV to Mave Studio...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun startWearableSession(deviceId: String?) {
        scope.launch {
            val targetDevice = deviceId?.let { id -> Wearables.devices.value.find { it.identifier == id } }

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
                            DeviceSessionState.PAUSED -> {
                                Timber.w("Glasses session PAUSED. Suspending stream processing.")
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
        session.addDisplay(DisplayConfiguration()).onSuccess { display ->
            activeDisplay = display
            updateWearableUi("", "") // Initial UI render
            startStream(session)
        }.onFailure { error, _ ->
            Timber.e("Failed to add Display capability: ${error.description}")
            startStream(session)
        }
    }

    private fun startStream(session: DeviceSession) {
        val config = StreamConfiguration(VideoQuality.MEDIUM, 15, false)
        session.addStream(config).onSuccess { stream ->
            activeStream = stream
            stream.start().onSuccess {
                startFrameCollection(stream)
            }.onFailure { error, _ ->
                Timber.e("Failed to start stream: ${error.description}")
            }
        }.onFailure { error, _ ->
            Timber.e("Failed to add Stream capability: ${error.description}")
        }
    }

    private var lastFrameTimeMs = 0L
    private val frameIntervalMs = 1000L / 5L // 5 FPS max rate limiting

    private fun startFrameCollection(stream: Stream) {
        scope.launch(Dispatchers.IO) {
            stream.videoStream.collect { frame ->
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

    private var autoDismissJob: Job? = null

    fun showTransientCard(
        title: String,
        subtitle: String? = null,
        iconName: IconName = IconName.MUSIC_NOTE,
        durationMs: Long = 4000L
    ) {
        autoDismissJob?.cancel()
        scope.launch {
            activeDisplay?.sendContent {
                flexBox(
                    direction = Direction.COLUMN,
                    gap = 8,
                    paddingBottom = 16,
                    paddingEnd = 16,
                    paddingStart = 16,
                    paddingTop = 16,
                    alignment = Alignment.CENTER,
                    crossAlignment = Alignment.CENTER
                ) {
                    icon(name = iconName)
                    text(content = title, style = TextStyle.HEADING)
                    if (!subtitle.isNullOrEmpty()) {
                        text(content = subtitle, style = TextStyle.BODY)
                    }
                }
            }?.onFailure { error, _ ->
                Timber.e("Failed to send display content: ${error.description}")
            }

            // Auto-dismiss back to clean empty view after duration
            autoDismissJob = scope.launch {
                delay(durationMs)
                clearDisplay()
            }
        }
    }

    fun showSteeringNotice(bpm: Int, densityPercent: Int, brightnessPercent: Int) {
        showTransientCard(
            title = "$bpm BPM",
            subtitle = "Density: $densityPercent% • Brightness: $brightnessPercent%",
            iconName = IconName.GEAR,
            durationMs = 3000L
        )
    }

    fun showTrackNotice(songTitle: String, artist: String = "Mave AI") {
        showTransientCard(
            title = songTitle,
            subtitle = artist,
            iconName = IconName.MUSIC_NOTE,
            durationMs = 4000L
        )
    }

    fun showThinkingNotice() {
        showTransientCard(
            title = "Mave is thinking...",
            subtitle = "Crafting audio steering",
            iconName = IconName.MUSIC_NOTE,
            durationMs = 6000L
        )
    }

    fun clearDisplay() {
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

    fun updateWearableUi(songTitle: String, geminiResponse: String, coverArtUrl: String? = null, isThinking: Boolean = false) {
        when {
            isThinking -> showThinkingNotice()
            songTitle.isNotEmpty() -> showTrackNotice(songTitle, geminiResponse)
            else -> clearDisplay()
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    private fun startAudioRecording() {
        if (isRecording) return
        val sampleRate = 16000
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
                return
            }

            audioRecord = recorder
            isRecording = true
            recorder.startRecording()

            recordingJob = scope.launch(Dispatchers.IO) {
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
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Missing RECORD_AUDIO permission")
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
