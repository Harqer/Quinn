package com.example.jetcaster.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalHorologistApi::class)
@HiltViewModel
class QueueViewModel @Inject constructor(private val episodePlayer: EpisodePlayer) : ViewModel() {

    val uiState: StateFlow<QueueScreenState> = episodePlayer.playerState.map {
        if (it.queue.isNotEmpty()) {
            QueueScreenState.Loaded(it.queue)
        } else {
            QueueScreenState.Empty
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        QueueScreenState.Loading,
    )

    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }

    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        if (episodes.isNotEmpty()) {
            episodePlayer.currentEpisode = episodes[0]
            episodePlayer.play(episodes)
        }
    }

    fun onDeleteQueueEpisodes() {
        episodePlayer.removeAllFromQueue()
    }
}

sealed interface QueueScreenState {
    data object Loading : QueueScreenState
    data class Loaded(val episodeList: List<PlayerEpisode>) : QueueScreenState
    data object Empty : QueueScreenState
}
