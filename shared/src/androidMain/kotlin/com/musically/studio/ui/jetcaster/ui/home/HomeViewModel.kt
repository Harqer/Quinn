package com.musically.studio.ui.jetcaster.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.CategoryInfo
import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.FilterableCategoriesModel
import com.musically.studio.ui.jetcaster.core.model.LibraryInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastCategoryFilterResult
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.musically.studio.data.repository.DataConnectRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class HomeViewModel(
    private val dataConnectRepository: DataConnectRepository? = null
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenUiState())
    val state: StateFlow<HomeScreenUiState> = _state

    init {
        refresh()
    }

    fun refresh(force: Boolean = true) {
        _state.value = _state.value.copy(isLoading = true)
        
        viewModelScope.launch {
            dataConnectRepository?.getPodcasts()?.catch { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }?.collect { shows ->
                val podcastList = shows.map { show ->
                    PodcastInfo(
                        uri = show.id,
                        title = show.title,
                        author = show.publisher,
                        imageUrl = show.coverUrl ?: "",
                        description = show.description ?: ""
                    )
                }
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    featuredPodcasts = podcastList,
                    podcastCategoryFilterResult = _state.value.podcastCategoryFilterResult.copy(
                        topPodcasts = podcastList
                    ),
                    library = LibraryInfo(
                        episodes = emptyList()
                    )
                )
            }
        }
    }

    fun onHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.CategorySelected -> {
                _state.value = _state.value.copy(
                    filterableCategoriesModel = _state.value.filterableCategoriesModel.copy(
                        selectedCategory = action.category
                    )
                )
            }
            is HomeAction.HomeCategorySelected -> {
                _state.value = _state.value.copy(selectedHomeCategory = action.category)
            }
            is HomeAction.LibraryPodcastSelected -> {
                // Handle library podcast selection
            }
            is HomeAction.PodcastUnfollowed -> {
                // Unfollow
            }
            is HomeAction.TogglePodcastFollowed -> {
                // Toggle follow
            }
            is HomeAction.QueueEpisode -> {
                // Queue episode
            }
            is HomeAction.RemoveEpisode -> {
                // Remove episode
            }
        }
    }

    fun searchPodcasts(query: String) {
        // Implement search logic here, potentially querying dataConnectRepository
        _state.value = _state.value.copy(
            errorMessage = "Search for '$query' is not yet implemented"
        )
    }
}

enum class HomeCategory {
    Library,
    Discover,
}

@Immutable
sealed interface HomeAction {
    data class CategorySelected(val category: CategoryInfo) : HomeAction
    data class HomeCategorySelected(val category: HomeCategory) : HomeAction
    data class PodcastUnfollowed(val podcast: PodcastInfo) : HomeAction
    data class TogglePodcastFollowed(val podcast: PodcastInfo) : HomeAction
    data class LibraryPodcastSelected(val podcast: PodcastInfo?) : HomeAction
    data class QueueEpisode(val episode: PlayerEpisode) : HomeAction
    data class RemoveEpisode(val episodeInfo: EpisodeInfo) : HomeAction
}

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val featuredPodcasts: List<PodcastInfo> = emptyList(),
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = listOf(HomeCategory.Library, HomeCategory.Discover),
    val filterableCategoriesModel: FilterableCategoriesModel = FilterableCategoriesModel(),
    val podcastCategoryFilterResult: PodcastCategoryFilterResult = PodcastCategoryFilterResult(),
    val library: LibraryInfo = LibraryInfo(),
)
