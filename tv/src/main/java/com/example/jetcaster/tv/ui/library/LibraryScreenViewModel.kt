package com.example.jetcaster.tv.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import com.musically.studio.data.repository.DataConnectRepository
import com.musically.studio.dataconnect.GetPodcastsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LibraryScreenViewModel @Inject constructor(
    private val dataConnectRepository: DataConnectRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val followingPodcastListFlow = dataConnectRepository.getPodcasts()
        .map { list ->
            list.map { it.toPodcastInfo() }
        }

    private val latestEpisodeListFlow = flowOf(EpisodeList(emptyList()))

    val uiState =
        combine(followingPodcastListFlow, latestEpisodeListFlow) { podcastList, episodeList ->
            if (podcastList.isEmpty()) {
                LibraryScreenUiState.NoSubscribedPodcast
            } else {
                LibraryScreenUiState.Ready(podcastList, episodeList)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LibraryScreenUiState.Loading,
        )

    fun playEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }

    private fun GetPodcastsQuery.Data.ShowsItem.toPodcastInfo() = PodcastInfo(
        uri = id,
        title = title,
        author = publisher,
        imageUrl = coverUrl ?: "",
        description = description ?: "",
    )
}

sealed interface LibraryScreenUiState {
    data object Loading : LibraryScreenUiState
    data object NoSubscribedPodcast : LibraryScreenUiState
    data class Ready(val subscribedPodcastList: PodcastList, val latestEpisodeList: EpisodeList) : LibraryScreenUiState
}
