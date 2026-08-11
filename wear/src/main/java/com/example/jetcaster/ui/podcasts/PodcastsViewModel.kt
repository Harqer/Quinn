package com.example.jetcaster.ui.podcasts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.data.repository.DataConnectRepository
import com.musically.studio.dataconnect.GetPodcastsQuery
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalHorologistApi::class)
@HiltViewModel
class PodcastsViewModel @Inject constructor(
    private val dataConnectRepository: DataConnectRepository
) : ViewModel() {

    val uiState: StateFlow<PodcastsScreenState> =
        dataConnectRepository.getPodcasts().map { shows ->
            if (shows.isNotEmpty()) {
                PodcastsScreenState.Loaded(shows.map { it.toPodcastInfo() })
            } else {
                PodcastsScreenState.Empty
            }
        }.catch {
            emit(PodcastsScreenState.Empty)
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PodcastsScreenState.Loading,
        )
}

private fun GetPodcastsQuery.Data.ShowsItem.toPodcastInfo() = PodcastInfo(
    uri = id,
    title = title,
    author = publisher,
    imageUrl = coverUrl ?: "",
    description = description ?: "",
    isSubscribed = true
)

@ExperimentalHorologistApi
sealed interface PodcastsScreenState {
    data object Loading : PodcastsScreenState
    data class Loaded(val podcastList: List<PodcastInfo>) : PodcastsScreenState
    data object Empty : PodcastsScreenState
}
