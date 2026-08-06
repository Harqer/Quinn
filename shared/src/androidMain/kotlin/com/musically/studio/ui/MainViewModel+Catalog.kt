package com.musically.studio.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.musically.studio.network.MaveTrack
import com.musically.studio.network.MaveAlbum
import com.musically.studio.network.MaveArtist
import com.musically.studio.network.MaveImage

fun MainViewModel.searchCatalog(query: String) {
    if (query.isBlank()) {
        _searchResults.value = emptyList()
        return
    }
    viewModelScope.launch {
        _isLoading.value = true
        dataConnectRepository.searchTracks(query).collect { results ->
            // Map the DataConnect result type to MaveTrack
            val mappedResults = results.map { item ->
                MaveTrack(
                    id = item.id,
                    name = item.title,
                    album = MaveAlbum(
                        id = item.album?.id ?: "",
                        name = item.album?.title ?: "",
                        artists = listOf(
                            MaveArtist(
                                id = item.album?.primaryArtist?.id ?: "",
                                name = item.album?.primaryArtist?.name ?: ""
                            )
                        ),
                        images = listOf(
                            MaveImage(url = item.coverUrl ?: "")
                        )
                    ),
                    artists = listOf(
                        MaveArtist(
                            id = item.album?.primaryArtist?.id ?: "",
                            name = item.album?.primaryArtist?.name ?: ""
                        )
                    ),
                    audioUrl = item.audioUrl ?: ""
                )
            }
            _searchResults.value = mappedResults
            _isLoading.value = false
        }
    }
}

fun MainViewModel.exploreCatalog() {
    fetchCatalog()
}
