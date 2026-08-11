package com.example.jetcaster.tv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayerState
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer
) : ViewModel() {

    val uiStateFlow = episodePlayer.playerState.map {
        if (it.currentEpisode == null && it.queue.isEmpty()) {
            PlayerScreenUiState.NoEpisodeInQueue
        } else {
            PlayerScreenUiState.Ready(it)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlayerScreenUiState.Loading,
    )

    private val skipAmount = Duration.ofSeconds(10L)

    fun play() {
        if (episodePlayer.playerState.value.currentEpisode == null) {
            episodePlayer.next()
        }
        episodePlayer.play()
    }
    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }

    fun pause() = episodePlayer.pause()
    fun next() = episodePlayer.next()
    fun previous() = episodePlayer.previous()
    fun skip() {
        episodePlayer.advanceBy(skipAmount)
    }

    fun rewind() {
        episodePlayer.rewindBy(skipAmount)
    }

    fun enqueue(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }
}

sealed interface PlayerScreenUiState {
    data object Loading : PlayerScreenUiState
    data class Ready(val playerState: EpisodePlayerState) : PlayerScreenUiState
    data object NoEpisodeInQueue : PlayerScreenUiState
}
