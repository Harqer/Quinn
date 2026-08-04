package com.musically.studio.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.musically.studio.network.*
import com.musically.studio.data.repository.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import android.util.Base64

    fun MainViewModel.togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        // Player state is strictly local, but we can notify Gemini of user action if live.
        if (_isLiveSessionActive.value) {
            val state = if (_isPlaying.value) "playing" else "paused"
            geminiLiveManager.sendText("User $state the playback.")
        }
    }

    fun MainViewModel.setVolume(volumeLevel: Float) {
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

    fun MainViewModel.setPlayingState(playing: Boolean) {
        if (_isPlaying.value != playing) {
            _isPlaying.value = playing
            // We don't call maveSessionManager.play() here because this is likely driven BY ExoPlayer changing state
        }
    }

    fun MainViewModel.stopPlayback() {
        _isPlaying.value = false
        if (_isLiveSessionActive.value) {
            geminiLiveManager.sendText("User stopped the playback.")
        }
    }

    fun MainViewModel.playQueue(tracks: List<MaveTrack>, startIndex: Int = 0) {
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

    fun MainViewModel.skipNext(autoAdvance: Boolean = false) {
        val currentQueue = _queue.value
        val currentIndex = _queueIndex.value
        
        if (currentQueue.isEmpty()) {
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
        }
    }

    fun MainViewModel.skipPrevious() {
        val currentQueue = _queue.value
        val currentIndex = _queueIndex.value
        
        if (currentQueue.isEmpty()) {
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

    fun MainViewModel.seekTo(position: Float) {
        _trackProgress.value = position
    }

    fun MainViewModel.toggleShuffle() {
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
        
    }

    fun MainViewModel.toggleRepeat() {
        _isRepeatEnabled.value = when (_isRepeatEnabled.value) {
            "none" -> "all"
            "all" -> "one"
            else -> "none"
        }
    }

    fun MainViewModel.toggleHapticFeedback() {
        _isHapticFeedbackEnabled.value = !_isHapticFeedbackEnabled.value
    }

    fun MainViewModel.requestCoverArt() {
        sendTextCommand("Generate a high-fidelity album cover for this vibe.")
    }

    fun MainViewModel.requestMusicVideo() {
        sendTextCommand("Generate a 35mm cinematic music video loop for this track.")
    }

    fun MainViewModel.playTrack(track: MaveTrack) {
        addRecentTrack(track)
        _currentPlayingTrack.value = track
        _isPlaying.value = true
        if (_isLiveSessionActive.value) {
            geminiLiveManager.sendText("User is now playing track: ${track.name}")
        }
        viewModelScope.launch { _shouldExpandBottomSheet.emit(true) }
    }

    suspend fun MainViewModel.getTrack(trackId: String): MaveTrack? {
        Timber.w("getTrack is deprecated without external backend")
        return null
    }

