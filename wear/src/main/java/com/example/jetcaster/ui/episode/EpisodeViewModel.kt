package com.example.jetcaster.ui.episode

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastToEpisodeInfo
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.musically.studio.data.repository.DataConnectRepository
import com.musically.studio.dataconnect.GetEpisodeQuery
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.OffsetDateTime
import java.time.Duration

@OptIn(ExperimentalHorologistApi::class)
@HiltViewModel
class EpisodeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataConnectRepository: DataConnectRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val episodeUri: String =
        savedStateHandle.get<String>("episodeUri")?.let {
            Uri.decode(it)
        } ?: ""

    val uiState: StateFlow<EpisodeScreenState> =
        flow {
            if (episodeUri.isNotEmpty()) {
                val ep = dataConnectRepository.getEpisode(episodeUri)
                if (ep != null) {
                    emit(EpisodeScreenState.Loaded(ep.toPodcastToEpisodeInfo()))
                } else {
                    emit(EpisodeScreenState.Empty)
                }
            } else {
                emit(EpisodeScreenState.Empty)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            EpisodeScreenState.Loading,
        )

    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }
    fun addToQueue(episode: PlayerEpisode) {
        episodePlayer.addToQueue(episode)
    }
}

private fun GetEpisodeQuery.Data.Episode.toPodcastToEpisodeInfo() = PodcastToEpisodeInfo(
    episode = EpisodeInfo(
        uri = id,
        podcastUri = show.id,
        title = title,
        subTitle = show.title,
        summary = description ?: "",
        author = show.publisher,
        published = OffsetDateTime.now(),
        duration = Duration.ofMillis(durationMs.toLong()),
        mediaUrls = emptyList()
    ),
    podcast = PodcastInfo(
        uri = show.id,
        title = show.title,
        author = show.publisher,
        imageUrl = show.coverUrl ?: "",
        description = "",
        isSubscribed = true
    )
)

@ExperimentalHorologistApi
sealed interface EpisodeScreenState {
    data object Loading : EpisodeScreenState
    data class Loaded(val episode: PodcastToEpisodeInfo) : EpisodeScreenState
    data object Empty : EpisodeScreenState
}
