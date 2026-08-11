package com.example.jetcaster.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
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

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val dataConnectRepository: DataConnectRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    val uiState: StateFlow<LibraryScreenUiState> =
        dataConnectRepository.getPodcasts().map { shows ->
            if (shows.isNotEmpty()) {
                LibraryScreenUiState.Loaded(shows.map { it.toPodcastInfo() })
            } else {
                LibraryScreenUiState.Empty
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LibraryScreenUiState.Loading,
        )

    fun playEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }

    fun onTogglePodcastFollowed(podcastUri: String) {
        // No-op for now
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

sealed interface LibraryScreenUiState {
    data object Loading : LibraryScreenUiState
    data class Loaded(val podcastList: List<PodcastInfo>) : LibraryScreenUiState
    data object Empty : LibraryScreenUiState
}
