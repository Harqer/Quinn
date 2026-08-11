package com.example.jetcaster.ui.podcast

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.musically.studio.data.repository.DataConnectRepository
import com.musically.studio.dataconnect.GetPodcastsQuery
import com.musically.studio.dataconnect.GetEpisodesForShowQuery
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.OffsetDateTime
import java.time.Duration

@OptIn(ExperimentalHorologistApi::class)
@HiltViewModel
class PodcastDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataConnectRepository: DataConnectRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val podcastUri: String =
        savedStateHandle.get<String>("podcastUri")?.let {
            Uri.decode(it)
        } ?: ""

    val uiState: StateFlow<PodcastDetailsScreenState> =
        combine(
            dataConnectRepository.getPodcasts(),
            dataConnectRepository.getEpisodesForShow(podcastUri)
        ) { shows, episodes ->
            val show = shows.find { it.id == podcastUri }
            if (show != null) {
                PodcastDetailsScreenState.Loaded(
                    podcast = show.toPodcastInfo(),
                    episodeList = episodes.map { it.toPlayerEpisode(show) }
                )
            } else {
                PodcastDetailsScreenState.Empty
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PodcastDetailsScreenState.Loading,
        )

    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        if (episodes.isNotEmpty()) {
            episodePlayer.currentEpisode = episodes[0]
            episodePlayer.play(episodes)
        }
    }
}

private fun GetPodcastsQuery.Data.ShowsItem.toPodcastInfo() = PodcastInfo(
    uri = id,
    title = title,
    author = publisher,
    imageUrl = coverUrl ?: "",
    description = description ?: "",
    isSubscribed = true
)

private fun GetEpisodesForShowQuery.Data.EpisodesItem.toPlayerEpisode(show: GetPodcastsQuery.Data.ShowsItem) = PlayerEpisode(
    podcastInfo = show.toPodcastInfo(),
    episodeInfo = com.musically.studio.ui.jetcaster.core.model.EpisodeInfo(
        uri = id,
        podcastUri = show.id,
        title = title,
        subTitle = show.title,
        summary = description ?: "",
        author = show.publisher,
        published = OffsetDateTime.now(),
        duration = Duration.ofMillis(durationMs.toLong()),
        mediaUrls = listOf(audioUrl),
    )
)

@ExperimentalHorologistApi
sealed class PodcastDetailsScreenState {
    data object Loading : PodcastDetailsScreenState()
    data class Loaded(val episodeList: List<PlayerEpisode>, val podcast: PodcastInfo) : PodcastDetailsScreenState()
    data object Empty : PodcastDetailsScreenState()
}
