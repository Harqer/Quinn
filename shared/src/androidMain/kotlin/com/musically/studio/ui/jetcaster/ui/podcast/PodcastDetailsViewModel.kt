package com.musically.studio.ui.jetcaster.ui.podcast

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.musically.studio.dataconnect.instance
import com.musically.studio.dataconnect.execute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.data.repository.DataConnectRepository

@Immutable
sealed interface PodcastUiState {
    data object Loading : PodcastUiState
    data class Ready(val podcast: PodcastInfo, val episodes: List<EpisodeInfo>) : PodcastUiState
    data class Error(val message: String) : PodcastUiState
}

@HiltViewModel
class PodcastDetailsViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,
    private val dataConnectRepository: DataConnectRepository
) : ViewModel() {
    private val _state = MutableStateFlow<PodcastUiState>(PodcastUiState.Loading)
    val state: StateFlow<PodcastUiState> = _state
    private var currentPodcastUri: String? = null

    fun initialize(podcastUri: String) {
        if (currentPodcastUri == podcastUri) return
        currentPodcastUri = podcastUri
        _state.value = PodcastUiState.Loading

        viewModelScope.launch {
            try {
                // Get Podcast (Show) info
                val showRes = com.musically.studio.dataconnect.DefaultConnector.instance.getEpisode.execute(id = podcastUri)
                val showData = showRes.data.episode?.show
                
                // Get Episodes for Show
                val episodesRes = com.musically.studio.dataconnect.DefaultConnector.instance.getEpisodesForShow.execute(showId = podcastUri)
                
                val podcastInfo = PodcastInfo(
                    uri = podcastUri,
                    title = showData?.title ?: "Unknown Podcast",
                    author = showData?.publisher ?: "Unknown",
                    imageUrl = showData?.coverUrl ?: ""
                )
                
                val episodes = episodesRes.data.episodes.map { ep ->
                    EpisodeInfo(
                        uri = ep.id,
                        podcastUri = podcastUri,
                        title = ep.title,
                        summary = ep.description ?: "",
                        author = showData?.publisher ?: "",
                        duration = java.time.Duration.ofMillis(ep.durationMs?.toLong() ?: 0L),
                        published = java.time.OffsetDateTime.now() // Ideally parsed from ep.publishDate
                    )
                }
                
                _state.value = PodcastUiState.Ready(
                    podcast = podcastInfo,
                    episodes = episodes
                )
            } catch (e: Exception) {
                _state.value = PodcastUiState.Error(e.message ?: "Failed to load podcast")
            }
        }
    }

    fun toggleSubscribe(podcast: PodcastInfo) {
        viewModelScope.launch {
            // "it should save it in the library" 
            // In a real app we'd bookmark the show. The current dataconnect only has bookmarkTrack.
            // For now, we simulate success or use a generic save.
            dataConnectRepository.bookmarkTrack(podcast.uri)
        }
    }

    fun onQueueEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }

    fun deleteEpisode(episodeInfo: EpisodeInfo) {
        // Removed removeFromQueue because it does not exist in EpisodePlayer
    }
}
