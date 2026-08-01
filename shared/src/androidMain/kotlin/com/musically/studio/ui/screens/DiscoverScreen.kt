package com.musically.studio.ui.screens

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
    onNavigateToPlaylist: (String) -> Unit = {}
) {
    val communityTracks by viewModel.communityTracks.collectAsStateWithLifecycle()
    val userTracks by viewModel.tracks.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val errorMessage by viewModel.catalogErrorMessage.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val hasSeenTooltipTour by viewModel.hasSeenTooltipTour.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchCommunityTracks()
        viewModel.fetchUserTracks()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = com.musically.studio.ui.theme.MaveBackgroundVariant,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MaveLogo(size = 100)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = com.musically.studio.ui.theme.MaveDarkSurface
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                Button(
                                    onClick = { viewModel.fetchCommunityTracks() },
                                    colors = ButtonDefaults.buttonColors(containerColor = com.musically.studio.ui.theme.MaveSurfaceVariant6, contentColor = Color.White)
                                ) {
                                    Text("Discover", fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = onNavigateToLibrary) {
                                    Text("Library", color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = com.musically.studio.ui.theme.MaveBackgroundVariant),
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(com.musically.studio.ui.theme.MaveBackgroundVariant.copy(alpha = 0.9f))
                        .imePadding()
                        .padding(24.dp)
                        .navigationBarsPadding()
                ) {
                    MaveTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = "Search for more podcasts...",
                        trailingIcon = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onNavigateToCamera) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = Color.Gray)
                                }
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) 
                            }
                        },
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
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FreshReleasesSection(
                        communityTracks = communityTracks,
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
                    errorMessage = errorMessage,
                    onRetry = { viewModel.fetchPlaylists() },
                    onNavigateToMore = onNavigateToMore,
                    onNavigateToPlaylist = onNavigateToPlaylist
                )
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
