package com.example.myapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log

import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.session.DeviceSession
import com.meta.wearable.dat.core.session.DeviceSessionState
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector
import com.meta.wearable.dat.core.types.DatResult
import com.meta.wearable.dat.camera.Stream
import com.meta.wearable.dat.camera.addStream
import com.meta.wearable.dat.camera.removeStream
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.display.Display
import com.meta.wearable.dat.display.addDisplay
import com.meta.wearable.dat.display.removeDisplay
import com.meta.wearable.dat.display.views.Direction
import com.meta.wearable.dat.display.views.Alignment
import com.meta.wearable.dat.display.views.TextStyle
import com.meta.wearable.dat.display.views.IconStyle
import com.meta.wearable.dat.display.views.IconName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

/**
 * Production-ready Android Foreground Service for handling Wearable POV camera streaming and audio playback.
 * Integrates Meta Wearables SDK (DAT) capabilities safely.
 */
class WearableStreamingService : Service() {

    private val TAG = "WearableStreamingService"
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "wearable_streaming_channel"

    private var activeSession: DeviceSession? = null
    private var activeStream: Stream? = null
    private var activeDisplay: Display? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Wearable Streaming Foreground Service created.")
        createNotificationChannel()
        startWearableSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Starting Wearable Streaming Foreground Service...")
        
        val notification = buildForegroundNotification()
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val foregroundType = ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or 
                                     ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or 
                                     ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                
                startForeground(NOTIFICATION_ID, notification, foregroundType)
                Log.i(TAG, "Service started with dynamic capability indications.")
            } else {
                startForeground(NOTIFICATION_ID, notification)
                Log.i(TAG, "Service started in foreground.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service in foreground: ${e.message}", e)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWearableSession()
        serviceScope.cancel()
        Log.i(TAG, "Wearable Streaming Foreground Service stopped.")
    }


    private fun startWearableSession() {
        try {
            Wearables.initialize(this)
                .onFailure { error, _ ->
                    Log.e(TAG, "Failed to initialize DAT: ${error.description}")
                }
            
            val sessionResult = Wearables.createSession(AutoDeviceSelector())
            val session = sessionResult.getOrNull()
            
            if (session == null) {
                sessionResult.onFailure { error, _ ->
                    Log.e(TAG, "Failed to create session: ${error.description}")
                }
                return
            }
            
            activeSession = session
            session.start()
            
            serviceScope.launch {
                session.state.collect { state ->
                    if (state == DeviceSessionState.STARTED) {
                        // Add Stream
                        if (activeStream == null) {
                            val streamResult = session.addStream(StreamConfiguration())
                            val stream = streamResult.getOrNull()
                            if (stream != null) {
                                activeStream = stream
                                stream.start()
                            } else {
                                streamResult.onFailure { error, _ ->
                                    Log.e(TAG, "Failed to add stream: ${error.description}")
                                }
                            }
                        }
                            
                        // Add Display
                        if (activeDisplay == null) {
                            val displayResult = session.addDisplay()
                            val display = displayResult.getOrNull()
                            if (display != null) {
                                activeDisplay = display
                                renderMusicUI(display)
                            } else {
                                displayResult.onFailure { error, _ ->
                                    Log.e(TAG, "Failed to add display: ${error.description}")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during session setup: ${e.message}", e)
        }
    }
    private fun renderMusicUI(display: Display) {
        serviceScope.launch {
            display.sendContent {
                // Exactly one root view per sendContent (a flexBox)
                flexBox(direction = Direction.COLUMN, gap = 8) {
                    text("Now Playing", style = TextStyle.HEADING)
                    
                    flexBox(direction = Direction.ROW, gap = 8, crossAlignment = Alignment.CENTER) {
                        // Use IconName enum
                        icon(name = IconName.HEADPHONES, style = IconStyle.FILLED)
                        text("Vibe Curated Track", style = TextStyle.BODY)
                    }

                    // Music Controls with fast callbacks
                    flexBox(direction = Direction.ROW, gap = 16, crossAlignment = Alignment.CENTER) {
                        button(label = "Skip Back", onClick = { Log.d(TAG, "Skip Back pressed") })
                        button(label = "Play", onClick = { Log.d(TAG, "Play pressed") })
                        button(label = "Pause", onClick = { Log.d(TAG, "Pause pressed") })
                        button(label = "Skip Forward", onClick = { Log.d(TAG, "Skip Forward pressed") })
                    }
                }
            }.onFailure { error, _ ->
                Log.e(TAG, "Failed to send content to display: ${error.description}")
            }
        }
    }

    private fun stopWearableSession() {
        // Cleanup flow
        activeStream?.stop()
        activeStream = null
        
        activeDisplay?.stop()
        activeDisplay = null
        
        activeSession?.let { session ->
            // Remove capabilities before stopping session
            session.removeDisplay()
            session.removeStream()
            session.stop()
        }
        activeSession = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Meta Wearables Stream Session"
            val descriptionText = "Monitors and processes real-time camera POV stream and AI music orchestration."
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $CHANNEL_ID")
        }
    }

    private fun buildForegroundNotification(): Notification {
        val title = "Ray-Ban Meta Stream Active"
        val message = "Capturing glasses POV camera stream & coordinating AI music triggers"
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.presence_video_busy)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.presence_video_busy)
                .setOngoing(true)
                .build()
        }
    }
}
