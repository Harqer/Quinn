package com.musically.studio.ui.jetcaster.core.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Duration
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealEpisodePlayer @Inject constructor() : EpisodePlayer {

    private val _playerState = MutableStateFlow(EpisodePlayerState())
    override val playerState: StateFlow<EpisodePlayerState> = _playerState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    override var currentEpisode: PlayerEpisode?
        get() = _playerState.value.currentEpisode
        set(value) {
            _playerState.update { it.copy(currentEpisode = value) }
            value?.let { prepareAndPlay(it) }
        }

    override var playerSpeed: Duration
        get() = _playerState.value.playbackSpeed
        set(value) {
            _playerState.update { it.copy(playbackSpeed = value) }
        }

    private fun prepareAndPlay(episode: PlayerEpisode) {
        stop()
        if (episode.contentUrl.isBlank()) {
            Timber.w("Episode audio contentUrl is blank")
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(episode.contentUrl)
                setOnPreparedListener { mp ->
                    mp.start()
                    _playerState.update { it.copy(isPlaying = true) }
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _playerState.update { it.copy(isPlaying = false) }
                    stopProgressTracker()
                    next()
                }
                setOnErrorListener { _, what, extra ->
                    Timber.e("MediaPlayer error: what=$what, extra=$extra")
                    _playerState.update { it.copy(isPlaying = false) }
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to prepare MediaPlayer for episode ${episode.title}")
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val currentMs = mp.currentPosition.toLong()
                        _playerState.update { it.copy(timeElapsed = Duration.ofMillis(currentMs)) }
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun addToQueue(episode: PlayerEpisode) {
        val currentQueue = _playerState.value.queue.toMutableList()
        if (!currentQueue.contains(episode)) {
            currentQueue.add(episode)
            _playerState.update { it.copy(queue = currentQueue) }
        }
        if (_playerState.value.currentEpisode == null) {
            currentEpisode = episode
        }
    }

    override fun removeAllFromQueue() {
        _playerState.update { it.copy(queue = emptyList()) }
    }

    override fun play() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _playerState.update { state -> state.copy(isPlaying = true) }
                startProgressTracker()
            }
        } ?: run {
            _playerState.value.currentEpisode?.let { prepareAndPlay(it) }
        }
    }

    override fun play(playerEpisode: PlayerEpisode) {
        currentEpisode = playerEpisode
    }

    override fun play(playerEpisodes: List<PlayerEpisode>) {
        if (playerEpisodes.isNotEmpty()) {
            _playerState.update { it.copy(queue = playerEpisodes) }
            currentEpisode = playerEpisodes.first()
        }
    }

    override fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playerState.update { state -> state.copy(isPlaying = false) }
                stopProgressTracker()
            }
        }
    }

    override fun stop() {
        stopProgressTracker()
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
            } catch (e: Exception) {
                Timber.e(e, "Error stopping MediaPlayer")
            }
        }
        mediaPlayer = null
        _playerState.update { it.copy(isPlaying = false) }
    }

    override fun next() {
        val queue = _playerState.value.queue
        val current = _playerState.value.currentEpisode
        if (queue.isNotEmpty() && current != null) {
            val index = queue.indexOf(current)
            if (index != -1 && index + 1 < queue.size) {
                currentEpisode = queue[index + 1]
            }
        }
    }

    override fun previous() {
        mediaPlayer?.let {
            if (it.currentPosition > 3000) {
                it.seekTo(0)
                _playerState.update { state -> state.copy(timeElapsed = Duration.ZERO) }
                return
            }
        }
        val queue = _playerState.value.queue
        val current = _playerState.value.currentEpisode
        if (queue.isNotEmpty() && current != null) {
            val index = queue.indexOf(current)
            if (index > 0) {
                currentEpisode = queue[index - 1]
            }
        }
    }

    override fun advanceBy(duration: Duration) {
        mediaPlayer?.let { mp ->
            val target = mp.currentPosition + duration.toMillis().toInt()
            mp.seekTo(target.coerceAtMost(mp.duration))
        }
    }

    override fun rewindBy(duration: Duration) {
        mediaPlayer?.let { mp ->
            val target = mp.currentPosition - duration.toMillis().toInt()
            mp.seekTo(target.coerceAtLeast(0))
        }
    }

    override fun onSeekingStarted() {
        pause()
    }

    override fun onSeekingFinished(duration: Duration) {
        mediaPlayer?.let { mp ->
            mp.seekTo(duration.toMillis().toInt())
            _playerState.update { it.copy(timeElapsed = duration) }
            play()
        }
    }

    override fun increaseSpeed(speed: Duration) {
        val current = _playerState.value.playbackSpeed
        val next = current.plus(speed)
        playerSpeed = next
    }

    override fun decreaseSpeed(speed: Duration) {
        val current = _playerState.value.playbackSpeed
        val next = current.minus(speed)
        if (!next.isNegative && !next.isZero) {
            playerSpeed = next
        }
    }
}
