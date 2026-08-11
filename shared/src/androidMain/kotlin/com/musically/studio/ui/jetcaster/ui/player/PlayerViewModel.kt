package com.musically.studio.ui.jetcaster.ui.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.data.repository.EpisodeStore
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayerState
import com.musically.studio.ui.jetcaster.core.player.model.toPlayerEpisode
import java.time.Duration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class PlayerUiState(val episodePlayerState: EpisodePlayerState = EpisodePlayerState())

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
    private val episodeUri: String,
    private val episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer
) : ViewModel() {

    var uiState by mutableStateOf(PlayerUiState())
        private set

    init {
        viewModelScope.launch {
            episodeStore.episodeAndPodcastWithUri(episodeUri).flatMapConcat {
                episodePlayer.currentEpisode = it.toPlayerEpisode()
                episodePlayer.playerState
            }.map {
                PlayerUiState(episodePlayerState = it)
            }.collect {
                uiState = it
            }
        }
    }

    fun onPlay() {
        episodePlayer.play()
    }

    fun onPause() {
        episodePlayer.pause()
    }

    fun onStop() {
        episodePlayer.stop()
    }

    fun onPrevious() {
        episodePlayer.previous()
    }

    fun onNext() {
        episodePlayer.next()
    }

    fun onAdvanceBy(duration: Duration) {
        episodePlayer.advanceBy(duration)
    }

    fun onRewindBy(duration: Duration) {
        episodePlayer.rewindBy(duration)
    }

    fun onSeekingStarted() {
        episodePlayer.onSeekingStarted()
    }

    fun onSeekingFinished(duration: Duration) {
        episodePlayer.onSeekingFinished(duration)
    }

    fun onAddToQueue() {
        uiState.episodePlayerState.currentEpisode?.let {
            episodePlayer.addToQueue(it)
        }
    }
}
