package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.theme.FormFactorPreviews
import com.musically.studio.ui.components.atoms.MaveLogo
import com.musically.studio.ui.components.atoms.MaveTextField
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.organisms.DiscoverCategoriesGrid
import com.musically.studio.ui.components.organisms.FreshReleasesSection
import com.musically.studio.ui.components.organisms.TooltipTourOverlay
import com.musically.studio.ui.components.organisms.featuredPlaylistsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: MainViewModel,
    isWearableConnected: Boolean = false,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToDevices: () -> Unit = {},
    onNavigateToMore: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {},
    onNavigateToLiveSession: () -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {},
    onNavigateToTrack: (String) -> Unit = {},
    onNavigateToPlaylist: (String) -> Unit = {},
    onNavigateToSearch: (String) -> Unit = {}
) {
    val communityTracks by viewModel.communityTracks.collectAsStateWithLifecycle()
    val userTracks by viewModel.tracks.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val errorMessage by viewModel.catalogErrorMessage.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val hasSeenTooltipTour by viewModel.hasSeenTooltipTour.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchCommunityTracks()
        viewModel.fetchUserTracks()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MaveLogo(size = 100)
                        }
                    },
                    navigationIcon = {
                        Box(modifier = Modifier.padding(start = 16.dp)) {
                            com.musically.studio.ui.components.atoms.UserAvatarButton(
                                photoUrl = viewModel.getUserPhotoUrl(),
                                displayName = viewModel.getUserDisplayName(),
                                onClick = onNavigateToSettings
                            )
                        }
                    },
                    actions = {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                Button(
                                    onClick = { viewModel.fetchCommunityTracks() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                                ) {
                                    Text("Discover", fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = onNavigateToLibrary) {
                                    Text("Library", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                        .padding(24.dp)
                ) {
                    MaveTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = "Search for more podcasts...",
                        trailingIcon = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onNavigateToCamera) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { if (searchQuery.isNotEmpty()) onNavigateToSearch(searchQuery) }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) 
                                }
                            }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { 
                            if (searchQuery.isNotEmpty()) onNavigateToSearch(searchQuery)
                        }),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MaveButton(
                        text = "Start Listening",
                        onClick = onNavigateToLiveSession,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        ) { innerPadding ->
            androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { 
                    viewModel.fetchCommunityTracks()
                    viewModel.fetchUserTracks()
                },
                modifier = Modifier.fillMaxSize().padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        FreshReleasesSection(
                            communityTracks = communityTracks,
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onRetry = { viewModel.fetchCommunityTracks() },
                            onNavigateToMore = onNavigateToMore,
                            onNavigateToTrack = onNavigateToTrack
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        DiscoverCategoriesGrid(
                            categories = categories,
                            onNavigateToCategory = onNavigateToCategory
                        )
                    }
                    
                    featuredPlaylistsSection(
                        playlists = playlists,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onRetry = { viewModel.fetchPlaylists() },
                        onNavigateToMore = onNavigateToMore,
                        onNavigateToPlaylist = onNavigateToPlaylist
                    )
                }
            }
        } // Close Scaffold
        
        if (!hasSeenTooltipTour) {
            TooltipTourOverlay(
                onDismiss = { viewModel.markTooltipTourSeen() }
            )
        }
    } // Close Box
}

@FormFactorPreviews
@Composable
fun DiscoverScreenPreview() {
    MaterialTheme {
        DiscoverScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
        )
    }
}
