package com.example.jetcaster.tv.ui.podcast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.model.EpisodeList
import com.musically.studio.data.repository.DataConnectRepository
import com.musically.studio.dataconnect.GetPodcastsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PodcastDetailsScreenViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataConnectRepository: DataConnectRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val podcastUri = handle.get<String>("podcastUri")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val podcastFlow =
        handle.getStateFlow<String?>("podcastUri", null).flatMapLatest { uri ->
            if (uri != null) {
                dataConnectRepository.getPodcasts().map { list ->
                    list.find { it.id == uri }?.toPodcastInfo()
                }
            } else {
                flowOf(null)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodeListFlow = handle.getStateFlow<String?>("podcastUri", null).flatMapLatest { uri ->
        if (uri != null) {
            dataConnectRepository.getEpisodesForShow(uri).map { list ->
                list.map { ep ->
                    PlayerEpisode(
                        podcastInfo = PodcastInfo(
                            uri = ep.show.id,
                            title = ep.show.title,
                            author = ep.show.publisher,
                            imageUrl = ep.show.coverUrl ?: "",
                            description = ""
                        ),
                        episodeInfo = com.musically.studio.ui.jetcaster.core.model.EpisodeInfo(
                            uri = ep.id,
                            podcastUri = ep.show.id,
                            title = ep.title,
                            summary = ep.description ?: "",
                            published = java.time.OffsetDateTime.now(),
                            duration = java.time.Duration.ofMillis(ep.durationMs.toLong()),
                            author = ep.show.publisher,
                            mediaUrls = listOf(ep.audioUrl)
                        )
                    )
                }
            }
        } else {
            flowOf(emptyList())
        }
    }

    private val subscribedPodcastListFlow = dataConnectRepository.getPodcasts().map { list ->
        list.map { it.toPodcastInfo() }
    }

    val uiStateFlow = combine(
        podcastFlow,
        episodeListFlow,
        subscribedPodcastListFlow,
    ) { podcast: PodcastInfo?, episodeList: List<PlayerEpisode>, subscribedPodcastList: List<PodcastInfo> ->
        if (podcast != null) {
            val isSubscribed = subscribedPodcastList.any { it.uri == podcastUri }
            PodcastScreenUiState.Ready(podcast, episodeList, isSubscribed)
        } else {
            PodcastScreenUiState.Error
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PodcastScreenUiState.Loading,
    )

    fun subscribe(podcastInfo: PodcastInfo, isSubscribed: Boolean) {
        // DataConnect mutation not implemented
    }

    fun unsubscribe(podcastInfo: PodcastInfo, isSubscribed: Boolean) {
        // DataConnect mutation not implemented
    }

    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }

    fun enqueue(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }

    private fun GetPodcastsQuery.Data.ShowsItem.toPodcastInfo() = PodcastInfo(
        uri = id,
        title = title,
        author = publisher,
        imageUrl = coverUrl ?: "",
        description = description ?: "",
    )
}

sealed interface PodcastScreenUiState {
    data object Loading : PodcastScreenUiState
    data object Error : PodcastScreenUiState
    data class Ready(val podcastInfo: PodcastInfo, val episodeList: EpisodeList, val isSubscribed: Boolean) : PodcastScreenUiState
}
