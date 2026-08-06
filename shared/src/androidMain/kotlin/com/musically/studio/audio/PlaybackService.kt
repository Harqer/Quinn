package com.musically.studio.audio

import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import androidx.core.net.toUri
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.musically.studio.network.GeminiLiveManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.merge
import timber.log.Timber
import java.io.PipedInputStream
import java.io.PipedOutputStream
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var geminiLiveManager: GeminiLiveManager

    private var mediaSession: MediaSession? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Pipe to feed the ExoPlayer DataSource from the WebSocket SharedFlow
    private val pipedOutputStream = PipedOutputStream()
    private val pipedInputStream = PipedInputStream(pipedOutputStream, 1024 * 64)

    override fun onCreate() {
        super.onCreate()
        
        val player = ExoPlayer.Builder(this).build()
        
        val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
            .setUsage(androidx.media3.common.C.USAGE_MEDIA)
            .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        
        player.setAudioAttributes(audioAttributes, true)
        
        createNotificationChannel()
        
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(CustomMediaSessionCallback())
            .build()
            
        // Collect audio bytes and write them to the pipe from both sources
        scope.launch {
            geminiLiveManager.audioOutput.collect { bytes ->
                try {
                    pipedOutputStream.write(bytes)
                    pipedOutputStream.flush()
                } catch (e: Exception) {
                    Timber.e(e, "Error writing bytes to ExoPlayer pipe")
                }
            }
        }
        
        val mediaItem = MediaItem.Builder()
            .setUri("mave://stream".toUri())
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setTitle("Mave Studio")
                    .setArtist("AI Generated Vibe")
                    .build()
            )
            .build()
        
        val factory = DataSource.Factory { FlowDataSource(pipedInputStream) }
        val mediaSource = ProgressiveMediaSource.Factory(factory)
            .createMediaSource(mediaItem)
        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = true
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    geminiLiveManager.sendText("User resumed audio playback")
                } else {
                    geminiLiveManager.sendText("User paused audio playback")
                }
            }
        })
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    // Media3 automatically handles foreground service and rich media notifications
    // based on the MediaSession state.

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "playback_channel",
            "Mave Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        scope.cancel()
        pipedOutputStream.close()
        pipedInputStream.close()
        super.onDestroy()
    }
    
    private inner class CustomMediaSessionCallback : MediaSession.Callback {
        // Removed unsupported overrides onSkipToNext and onSkipToPrevious
    }
}

@OptIn(UnstableApi::class)
class FlowDataSource(private val inputStream: java.io.InputStream) : BaseDataSource(/* isNetwork = */ false) {
    private var opened = false

    override fun open(dataSpec: DataSpec): Long {
        opened = true
        transferInitializing(dataSpec)
        transferStarted(dataSpec)
        return C.LENGTH_UNSET.toLong()
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        return try {
            if (inputStream.available() <= 0 && opened) {
                Thread.sleep(20)
            }
            val bytesRead = inputStream.read(buffer, offset, length)
            if (bytesRead == -1) {
                C.RESULT_END_OF_INPUT
            } else {
                bytesTransferred(bytesRead)
                bytesRead
            }
        } catch (e: Exception) {
            C.RESULT_END_OF_INPUT
        }
    }

    override fun getUri(): Uri? = "mave://stream".toUri()

    override fun close() {
        if (opened) {
            opened = false
            transferEnded()
        }
    }
}
