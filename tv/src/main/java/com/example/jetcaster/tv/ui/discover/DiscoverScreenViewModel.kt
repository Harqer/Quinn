package com.example.jetcaster.tv.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.CategoryInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.model.CategoryInfoList
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import com.musically.studio.data.repository.DataConnectRepository
import com.musically.studio.dataconnect.GetCategoriesQuery
import com.musically.studio.dataconnect.GetPodcastsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DiscoverScreenViewModel @Inject constructor(
    private val dataConnectRepository: DataConnectRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<CategoryInfo?>(null)

    private val categoryListFlow = dataConnectRepository.getCategories()
        .map { categoryList ->
            categoryList.map { category ->
                category.toCategoryInfo()
            }
        }

    private val selectedCategoryFlow = combine(
        categoryListFlow,
        _selectedCategory,
    ) { categoryList, category ->
        category ?: categoryList.firstOrNull()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val podcastInSelectedCategory = selectedCategoryFlow.flatMapLatest {
        dataConnectRepository.getPodcasts()
    }.map { list ->
        list.map { it.toPodcastInfo() }
    }

    private val latestEpisodeFlow = flowOf(EpisodeList(emptyList()))

    val uiState = combine(
        categoryListFlow,
        selectedCategoryFlow,
        podcastInSelectedCategory,
        latestEpisodeFlow,
    ) { categoryList, category, podcastList, latestEpisodes ->
        if (category != null) {
            DiscoverScreenUiState.Ready(
                CategoryInfoList(categoryList),
                category,
                podcastList,
                latestEpisodes,
            )
        } else {
            DiscoverScreenUiState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DiscoverScreenUiState.Loading,
    )

    fun selectCategory(category: CategoryInfo) {
        _selectedCategory.value = category
    }

    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }

    private fun GetPodcastsQuery.Data.ShowsItem.toPodcastInfo() = PodcastInfo(
        uri = id,
        title = title,
        author = publisher,
        imageUrl = coverUrl ?: "",
        description = description ?: "",
    )

    private fun GetCategoriesQuery.Data.CategoriesItem.toCategoryInfo() = CategoryInfo(
        id = id,
        name = name,
    )
}

sealed interface DiscoverScreenUiState {
    data object Loading : DiscoverScreenUiState
    data class Ready(
        val categoryInfoList: CategoryInfoList,
        val selectedCategory: CategoryInfo,
        val podcastList: PodcastList,
        val latestEpisodeList: EpisodeList,
    ) : DiscoverScreenUiState
}
