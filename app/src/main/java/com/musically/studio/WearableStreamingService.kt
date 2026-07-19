package com.musically.studio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.meta.wearable.dat.camera.Stream
import com.meta.wearable.dat.camera.addStream
import com.meta.wearable.dat.camera.removeStream
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.VideoQuality
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector
import com.meta.wearable.dat.core.session.DeviceSession
import com.meta.wearable.dat.display.Display
import com.meta.wearable.dat.display.addDisplay
import com.meta.wearable.dat.display.removeDisplay
import com.meta.wearable.dat.display.types.DisplayConfiguration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class WearableStreamingService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var activeSession: DeviceSession? = null
    private var activeStream: Stream? = null
    private var activeDisplay: Display? = null

    companion object {
        private val _cameraFrames = MutableSharedFlow<ByteArray>(extraBufferCapacity = 1)
        val cameraFrames = _cameraFrames.asSharedFlow()
        
        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()
        
        private var instance: WearableStreamingService? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        _isServiceActive.value = true
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        startWearableSession()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "wearable_service_channel",
                "Wearable Streaming",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "wearable_service_channel")
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
        return builder
            .setContentTitle("Musically Wearable Active")
            .setContentText("Streaming POV from your Meta glasses...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
    }

    private fun startWearableSession() {
        scope.launch {
            val result = Wearables.createSession(AutoDeviceSelector())
            result.onSuccess { session ->
                activeSession = session
                session.start()
                attachCapabilities(session)
            }
        }
    }

    private suspend fun attachCapabilities(session: DeviceSession) {
        session.addDisplay(DisplayConfiguration()).onSuccess { display ->
            activeDisplay = display
            updateWearableUi("Cinematic Synthwave")
            
            val config = StreamConfiguration(VideoQuality.MEDIUM, 24, false)
            session.addStream(config).onSuccess { stream ->
                activeStream = stream
                stream.start().onSuccess {
                    scope.launch {
                        stream.videoStream.collect { frame ->
                            val bytes = ByteArray(frame.buffer.remaining())
                            frame.buffer.get(bytes)
                            _cameraFrames.emit(bytes)
                        }
                    }
                }
            }
        }
    }

    fun updateWearableUi(vibeTitle: String) {
        scope.launch {
            activeDisplay?.sendContent(
                WearableUi.mainDashboard(
                    vibeTitle = vibeTitle,
                    onPlay = { /* Handle Play */ },
                    onPause = { /* Handle Pause */ }
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        scope.cancel()
        activeSession?.let { session ->
            session.removeStream()
            session.removeDisplay()
            session.stop()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
