package com.musically.studio.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import timber.log.Timber

/**
 * An audio player that can play specific snippets of a track,
 * which is critical for "Guess That Song in 3 Notes" (playing ~1.5s)
 * and "Guess That Song by 2 seconds" (playing exactly 2.0s).
 */
class TriviaAudioController(context: Context) {
    
    private val player = ExoPlayer.Builder(context).build()
    private var playbackJob: Job? = null
    
    /**
     * Plays a track for an exact duration (in milliseconds) and then pauses.
     */
    fun playSnippet(trackUrl: String, durationMs: Long, onPlaybackComplete: () -> Unit) {
        // Cancel any existing playback
        playbackJob?.cancel()
        
        playbackJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                Timber.d("Starting audio snippet playback for $durationMs ms: $trackUrl")
                player.setMediaItem(MediaItem.fromUri(trackUrl))
                player.prepare()
                player.play()
                
                // Wait for the specific duration
                delay(durationMs)
                
                Timber.d("Stopping audio snippet playback after $durationMs ms")
                player.pause()
                
                onPlaybackComplete()
            } catch (e: CancellationException) {
                Timber.d("Audio snippet playback cancelled")
                player.pause()
            }
        }
    }
    
    fun stop() {
        playbackJob?.cancel()
        player.pause()
    }
    
    fun release() {
        stop()
        player.release()
    }
}
