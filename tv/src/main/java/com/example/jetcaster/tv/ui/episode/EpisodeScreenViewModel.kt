package com.example.jetcaster.tv.ui.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.musically.studio.data.repository.DataConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastToEpisodeInfo
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.OffsetDateTime
import java.time.ZoneOffset

@HiltViewModel
class EpisodeScreenViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataConnectRepository: DataConnectRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val episodeUriFlow = handle.getStateFlow<String?>("episodeUri", null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodeToPodcastFlow = episodeUriFlow.flatMapLatest { episodeUri ->
        flow {
            if (episodeUri == null) {
                emit(null)
            } else {
                val ep = dataConnectRepository.getEpisode(episodeUri)
                if (ep != null) {
                    val playerEpisode = PlayerEpisode(
                        podcastInfo = PodcastInfo(
                            uri = ep.show.id,
                            title = ep.show.title,
                            author = ep.show.publisher,
                            imageUrl = ep.show.coverUrl ?: "",
                            description = ""
                        ),
                        episodeInfo = EpisodeInfo(
                            uri = ep.id,
                            podcastUri = ep.show.id,
                            title = ep.title,
                            summary = ep.description ?: "",
                            published = OffsetDateTime.now(), // Fallback if timestamp not mapped easily
                            duration = java.time.Duration.ofMillis(ep.durationMs.toLong()),
                            author = ep.show.publisher,
                            mediaUrls = listOf(ep.audioUrl)
                        )
                    )
                    emit(playerEpisode)
                } else {
                    emit(null)
                }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null,
    )

    val uiStateFlow = episodeToPodcastFlow.map {
        if (it != null) {
            EpisodeScreenUiState.Ready(it as PlayerEpisode)
        } else {
            EpisodeScreenUiState.Error
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EpisodeScreenUiState.Loading,
    )

    fun addPlayList(episode: PlayerEpisode) {
        episodePlayer.addToQueue(episode)
    }

    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }
}

sealed interface EpisodeScreenUiState {
    data object Loading : EpisodeScreenUiState
    data object Error : EpisodeScreenUiState
    data class Ready(val playerEpisode: PlayerEpisode) : EpisodeScreenUiState
}
