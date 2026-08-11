package com.example.jetcaster.tv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.jetcaster.core.model.CategoryInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.tv.model.CategoryInfoList
import com.example.jetcaster.tv.model.CategorySelection
import com.example.jetcaster.tv.model.CategorySelectionList
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val dataConnectRepository: DataConnectRepository,
) : ViewModel() {

    private val keywordFlow = MutableStateFlow("")
    private val selectedCategoryListFlow = MutableStateFlow<List<CategoryInfo>>(emptyList())

    private val categoryInfoListFlow =
        dataConnectRepository.getCategories().map { list ->
            CategoryInfoList(list.map { it.toCategoryInfo() })
        }

    private val searchConditionFlow =
        combine(
            keywordFlow,
            selectedCategoryListFlow,
            categoryInfoListFlow,
        ) { keyword, selectedCategories, categories ->
            val selected = selectedCategories.ifEmpty {
                categories
            }
            SearchCondition(keyword, selected)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResultFlow = searchConditionFlow.flatMapLatest { condition ->
        dataConnectRepository.getPodcasts().map { list ->
            val mapped = list.map { it.toPodcastInfo() }
            if (condition.keyword.isNotBlank()) {
                mapped.filter { it.title.contains(condition.keyword, ignoreCase = true) }
            } else {
                mapped
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )

    private val categorySelectionFlow =
        combine(
            categoryInfoListFlow,
            selectedCategoryListFlow,
        ) { categoryList, selectedCategories ->
            val list = categoryList.map {
                CategorySelection(it, selectedCategories.contains(it))
            }
            CategorySelectionList(list)
        }

    val uiStateFlow =
        combine(
            keywordFlow,
            categorySelectionFlow,
            searchResultFlow,
        ) { keyword, categorySelection, result ->
            val podcastList = result
            when {
                result.isEmpty() -> SearchScreenUiState.Ready(keyword, categorySelection)
                else -> SearchScreenUiState.HasResult(keyword, categorySelection, podcastList)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SearchScreenUiState.Loading,
        )

    fun setKeyword(keyword: String) {
        keywordFlow.value = keyword
    }

    fun addCategoryToSelectedCategoryList(category: CategoryInfo) {
        val list = selectedCategoryListFlow.value
        if (!list.contains(category)) {
            selectedCategoryListFlow.value = list + listOf(category)
        }
    }

    fun removeCategoryFromSelectedCategoryList(category: CategoryInfo) {
        val list = selectedCategoryListFlow.value
        if (list.contains(category)) {
            val mutable = list.toMutableList()
            mutable.remove(category)
            selectedCategoryListFlow.value = mutable.toList()
        }
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

private data class SearchCondition(val keyword: String, val selectedCategories: CategoryInfoList) {
    constructor(keyword: String, categoryInfoList: List<CategoryInfo>) : this(
        keyword,
        CategoryInfoList(categoryInfoList),
    )
}

sealed interface SearchScreenUiState {
    data object Loading : SearchScreenUiState
    data class Ready(val keyword: String, val categorySelectionList: CategorySelectionList) : SearchScreenUiState

    data class HasResult(val keyword: String, val categorySelectionList: CategorySelectionList, val result: PodcastList) :
        SearchScreenUiState
}
