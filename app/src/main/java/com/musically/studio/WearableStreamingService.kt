package com.musically.studio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.meta.wearable.dat.camera.Stream
import com.meta.wearable.dat.camera.addStream
import com.meta.wearable.dat.camera.removeStream
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.VideoQuality
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector
import com.meta.wearable.dat.core.session.DeviceSession
import com.meta.wearable.dat.core.session.DeviceSessionState
import com.meta.wearable.dat.display.Display
import com.meta.wearable.dat.display.addDisplay
import com.meta.wearable.dat.display.removeDisplay
import com.meta.wearable.dat.display.types.DisplayConfiguration
import androidx.xr.projected.ProjectedContext
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

    companion object {
        private val _cameraFrames = MutableSharedFlow<ByteArray>(extraBufferCapacity = 1)
        val cameraFrames = _cameraFrames.asSharedFlow()
        
        private val _interactionEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
        val interactionEvents = _interactionEvents.asSharedFlow()
        
        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()
        
        private var instance: WearableStreamingService? = null

        fun updateUi(songTitle: String, geminiResponse: String) {
            instance?.updateWearableUi(songTitle, geminiResponse)
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
        startForeground(1, notification)
        startWearableSession()
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
        return Notification.Builder(this, "wearable_service_channel")
            .setContentTitle("Mave Wearable")
            .setContentText("Streaming POV to Mave Studio...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
    }

    private fun startWearableSession() {
        scope.launch {
            val result = Wearables.createSession(AutoDeviceSelector())
            result.onSuccess { session ->
                activeSession = session
                
                // Monitor session state for proper lifecycle handling
                scope.launch {
                    session.state.collect { state ->
                        Timber.d("Session state changed: $state")
                        if (state == DeviceSessionState.IDLE || state == DeviceSessionState.STOPPED) {
                            stopSelf()
                        }
                    }
                }

                session.start()
                attachCapabilities(session)
            }
        }
    }

    private suspend fun attachCapabilities(session: DeviceSession) {
        session.addDisplay(DisplayConfiguration()).onSuccess { display ->
            activeDisplay = display
            
            // Launch WearableActivity on the projected display using Glimmer's ProjectedContext
            @OptIn(androidx.xr.projected.experimental.ExperimentalProjectedApi::class)
            val projectedContext = ProjectedContext.createProjectedDeviceContext(this)
            val intent = Intent(projectedContext, WearableActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            projectedContext.startActivity(intent)
            
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

    // Legacy updateWearableUi removed in favor of Glimmer WearableActivity
    fun updateWearableUi(songTitle: String, geminiResponse: String) {
        // No-op: The activity handles UI updates via ViewModel observation
    }

    private fun emitInteraction(type: String) {
        scope.launch {
            _interactionEvents.emit(type)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        _isServiceActive.value = false
        scope.cancel()
        activeSession?.let { session ->
            session.removeStream()
            session.removeDisplay()
            session.stop()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
