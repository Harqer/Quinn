package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.TrackItem
import com.musically.studio.ui.components.molecules.LibraryFilterRow
import com.musically.studio.ui.components.organisms.ConnectionsSection
import com.musically.studio.ui.components.organisms.EmptyLibraryState
import com.musically.studio.ui.components.organisms.LibraryErrorState
import com.musically.studio.ui.navigation.Route
import com.musically.studio.ui.theme.FormFactorPreviews
import com.musically.studio.ui.theme.MaveBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    onNavigateToNowPlaying: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPlaylist: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val likedTracks by viewModel.likedTracks.collectAsStateWithLifecycle()
    val bookmarkedTracks by viewModel.bookmarkedTracks.collectAsStateWithLifecycle()
    val downloadedTracks by viewModel.downloadedTracks.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.catalogErrorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchUserTracks()
        viewModel.fetchLikedTracks()
        viewModel.fetchBookmarkedTracks()
    }

    val spotifyConnected by viewModel.spotifyConnected.collectAsStateWithLifecycle()
    val youtubeConnected by viewModel.youtubeConnected.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.oauthUrl.collect { url ->
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            context.startActivity(intent)
        }
    }
    
    val selectedFilterState = remember { mutableStateOf<String?>(null) }
    var isListView by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf("Recently played") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaveBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val photoUrl = viewModel.getUserPhotoUrl()
                        val displayName = viewModel.getUserDisplayName()
                        if (photoUrl != null) {
                            coil.compose.AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(displayName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Your Library",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaveBackground),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LibraryFilterRow(
                selectedFilter = selectedFilterState.value,
                onFilterSelected = { selectedFilterState.value = it },
                onSortClick = { sortOrder = if (sortOrder == "Recently played") "Alphabetical" else "Recently played" },
                onViewToggleClick = { isListView = !isListView }
            )

            if (isLoading && tracks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (errorMessage != null && tracks.isEmpty()) {
                LibraryErrorState(
                    errorMessage = errorMessage,
                    onRetry = {
                        viewModel.clearCatalogError()
                        viewModel.fetchUserTracks()
                    }
                )
            } else if (tracks.isEmpty()) {
                EmptyLibraryState(onNavigateToHome = onNavigateToHome)
            } else {
                val filteredTracks by remember {
                    derivedStateOf {
                        val tracksList = when (selectedFilterState.value) {
                            "Liked" -> likedTracks
                            "Bookmarks" -> bookmarkedTracks
                            "Downloads" -> downloadedTracks
                            else -> tracks
                        }
                        
                        if (sortOrder == "Alphabetical") {
                            tracksList.sortedBy { it.name }
                        } else {
                            tracksList
                        }
                    }
                }

                androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                    isRefreshing = isLoading,
                    onRefresh = { 
                        viewModel.fetchUserTracks()
                        viewModel.fetchLikedTracks()
                        viewModel.fetchBookmarkedTracks()
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(360.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                            ConnectionsSection(
                                viewModel = viewModel,
                                spotifyConnected = spotifyConnected,
                                youtubeConnected = youtubeConnected
                            )
                        }
                        items(filteredTracks, key = { it.id }) { track ->
                            TrackItem(
                                track = track,
                                onClick = { onNavigateToNowPlaying(track.id) },
                                onAlbumClick = { onNavigateToAlbum(track.album.id) },
                                onRemixClick = { viewModel.navigateTo(Route.JamRemix) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@FormFactorPreviews
@Composable
fun LibraryScreenPreview() {
    MaterialTheme {
        LibraryScreen(
            viewModel = viewModel(),
            onNavigateToNowPlaying = {},
            onNavigateToAlbum = {},
            onNavigateToHome = {},
            onNavigateToPlaylist = {},
            onNavigateToSearch = {},
            onNavigateToAdd = {}
        )
    }
}
