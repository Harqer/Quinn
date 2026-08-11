package com.example.jetcaster.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayerState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlin.time.toKotlinDuration
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalHorologistApi::class)
data class PlayerUiState(
    val episodePlayerState: EpisodePlayerState = EpisodePlayerState(),
    var trackPositionUiModel: TrackPositionUiModel = TrackPositionUiModel.Actual.ZERO,
)

@HiltViewModel
@OptIn(ExperimentalHorologistApi::class)
class PlayerViewModel @Inject constructor(private val episodePlayer: EpisodePlayer) : ViewModel() {

    val uiState = episodePlayer.playerState.map {
        if (it.currentEpisode == null && it.queue.isEmpty()) {
            PlayerScreenUiState.Empty
        } else {
            PlayerScreenUiState.Ready(PlayerUiState(it, buildPositionModel(it)))
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlayerScreenUiState.Loading,
    )

    private fun buildPositionModel(it: EpisodePlayerState) = if (it.currentEpisode != null) {
        val durationMs = it.currentEpisode?.duration?.toMillis()?.toFloat() ?: 1f
        TrackPositionUiModel.Actual(
            percent = (it.timeElapsed.toMillis().toFloat() / durationMs).coerceIn(0f, 1f),
            duration = it.currentEpisode?.duration?.toKotlinDuration()
                ?: Duration.ZERO.toKotlinDuration(),
            position = it.timeElapsed.toKotlinDuration(),
        )
    } else {
        TrackPositionUiModel.Actual.ZERO
    }

    fun onPlay() {
        episodePlayer.play()
    }

    fun onPause() {
        episodePlayer.pause()
    }

    fun onAdvanceBy() {
        episodePlayer.advanceBy(Duration.ofSeconds(10))
    }

    fun onRewindBy() {
        episodePlayer.rewindBy(Duration.ofSeconds(10))
    }

    fun onPlaybackSpeedChange() {
        // Not implemented in EpisodePlayer
    }
}

sealed class PlayerScreenUiState {
    data object Loading : PlayerScreenUiState()
    data class Ready(val playerState: PlayerUiState) : PlayerScreenUiState()
    data object Empty : PlayerScreenUiState()
}
