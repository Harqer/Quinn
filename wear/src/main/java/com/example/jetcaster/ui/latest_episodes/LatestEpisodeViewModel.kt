package com.example.jetcaster.ui.latest_episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.musically.studio.data.repository.DataConnectRepository
import com.musically.studio.dataconnect.GetPodcastsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.OffsetDateTime
import java.time.Duration

@HiltViewModel
class LatestEpisodeViewModel @Inject constructor(
    private val dataConnectRepository: DataConnectRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    val uiState: StateFlow<LatestEpisodeScreenState> =
        dataConnectRepository.getPodcasts().map { shows ->
            if (shows.isNotEmpty()) {
                LatestEpisodeScreenState.Loaded(
                    shows.map { it.toPlayerEpisode() },
                )
            } else {
                LatestEpisodeScreenState.Empty
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LatestEpisodeScreenState.Loading,
        )

    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        if (episodes.isNotEmpty()) {
            episodePlayer.currentEpisode = episodes[0]
            episodePlayer.play(episodes)
        }
    }

    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }
}

private fun GetPodcastsQuery.Data.ShowsItem.toPlayerEpisode() = PlayerEpisode(
    podcastInfo = com.musically.studio.ui.jetcaster.core.model.PodcastInfo(
        uri = id,
        title = title,
        author = publisher,
        imageUrl = coverUrl ?: "",
        description = description ?: ""
    ),
    episodeInfo = com.musically.studio.ui.jetcaster.core.model.EpisodeInfo(
        uri = id,
        podcastUri = id,
        title = title,
        summary = description ?: "",
        published = OffsetDateTime.now(),
        duration = Duration.ofMinutes(30),
        author = publisher,
        mediaUrls = emptyList()
    )
)

sealed interface LatestEpisodeScreenState {
    data object Loading : LatestEpisodeScreenState
    data class Loaded(val episodeList: List<PlayerEpisode>) : LatestEpisodeScreenState
    data object Empty : LatestEpisodeScreenState
}
