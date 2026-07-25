package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.theme.FormFactorPreviews
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import com.musically.studio.ui.theme.MaveStyles
import timber.log.Timber

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
    var searchQuery by remember { mutableStateOf("") }

    val hasSeenTooltipTour by viewModel.hasSeenTooltipTour.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchCommunityTracks()
        viewModel.fetchUserTracks()
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color(0xFF121414),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            com.musically.studio.ui.components.atoms.MaveLogo(size = 100)
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
                            color = Color(0xFF1E2020)
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                Button(
                                    onClick = { viewModel.fetchCommunityTracks() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37393A), contentColor = Color.White)
                                ) {
                                    Text("Discover", fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = onNavigateToLibrary) {
                                    Text("Library", color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121414)),
                    scrollBehavior = scrollBehavior
                )
            },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121414).copy(alpha = 0.9f))
                    .imePadding()
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                com.musically.studio.ui.components.atoms.MaveTextField(
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
                com.musically.studio.ui.components.atoms.MaveButton(
                    text = "Start Listening",
                    onClick = onNavigateToLiveSession,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(bottom = 32.dp)) {
                    PaddingValues(horizontal = 24.dp).let {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(it).padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Fresh Releases",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            TextButton(onClick = onNavigateToMore) {
                                Text("More >", color = Color.Gray)
                            }
                        }
                    }

                    if (communityTracks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("No tracks found", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            listItems(communityTracks) { track ->
                                LargePodcastCard(track = track, onClick = { 
                                    onNavigateToTrack(track.id)
                                })
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                PaddingValues(horizontal = 24.dp).let {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(it).padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    
                    val colors = listOf(Color(0xFFFF9800), Color(0xFF2196F3), Color(0xFF9C27B0))
                    
                    Column(modifier = Modifier.padding(it).padding(bottom = 24.dp)) {
                        for (i in categories.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val color1 = try { categories[i].colorHex?.let { hex -> Color(android.graphics.Color.parseColor(hex)) } } catch (e: Exception) { null } ?: colors[(i/2) % colors.size]
                                val interactionSource1 = remember { MutableInteractionSource() }
                                val styleState1 = rememberUpdatedStyleState(interactionSource1) { it.isEnabled = true }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(16f/9f)
                                        .background(color1)
                                        .clickable(interactionSource = interactionSource1, indication = null) {
                                            onNavigateToCategory(categories[i].id)
                                        }
                                        .styleable(styleState1, MaveStyles.categoryGridItemStyle)
                                ) {
                                    Text(categories[i].name, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                if (i + 1 < categories.size) {
                                    val color2 = try { categories[i+1].colorHex?.let { hex -> Color(android.graphics.Color.parseColor(hex)) } } catch (e: Exception) { null } ?: colors[((i+1)/2) % colors.size]
                                    val interactionSource2 = remember { MutableInteractionSource() }
                                    val styleState2 = rememberUpdatedStyleState(interactionSource2) { it.isEnabled = true }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(16f/9f)
                                            .background(color2)
                                            .clickable(interactionSource = interactionSource2, indication = null) {
                                                onNavigateToCategory(categories[i+1].id)
                                            }
                                            .styleable(styleState2, MaveStyles.categoryGridItemStyle)
                                    ) {
                                        Text(categories[i+1].name, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
            
            item(span = { GridItemSpan(maxLineSpan) }) {
                PaddingValues(horizontal = 24.dp).let {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(it).padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Featured Playlists",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        TextButton(onClick = onNavigateToMore) {
                            Text("More >", color = Color.Gray)
                        }
                    }
                }
            }

            if (playlists.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No playlists found", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                gridItems(playlists) { playlist ->
                    PlaylistRowItem(playlist = playlist, onClick = { 
                        onNavigateToPlaylist(playlist.id)
                    })
                }
            }
        }
    } // Close Scaffold
    
    if (!hasSeenTooltipTour) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Welcome to Mave Studio!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tap the Camera icon below to create music from your photos, or press 'Start Listening' to begin a live session with Mave.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.markTooltipTourSeen() }) {
                    Text("Got it!", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
} // Close Box
} // Close DiscoverScreen

@FormFactorPreviews
@Composable
fun DiscoverScreenPreview() {
    MaterialTheme {
        DiscoverScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onNavigateToSettings = {},
            onNavigateToLibrary = {},
            onNavigateToDevices = {},
            onNavigateToMore = {},
            onNavigateToCamera = {},
            onNavigateToLiveSession = {},
            onNavigateToCategory = {},
            onNavigateToTrack = {},
            onNavigateToPlaylist = {}
        )
    }
}

@Composable
fun LargePodcastCard(
    track: MaveTrack,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    Column(
        modifier = modifier
            .width(240.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .styleable(styleState, MaveStyles.largePodcastCardStyle, style)
    ) {
        Box(modifier = Modifier.aspectRatio(3f/4f)) {
            AsyncImage(
                model = track.album.images.firstOrNull()?.url,
                contentDescription = track.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().background(Color.DarkGray, shape = MaterialTheme.shapes.small)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = track.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
        )
        Text(
            text = track.artists.joinToString { it.name },
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 1
        )
    }
}

@Composable
fun TrendingItemRow(
    track: MaveTrack,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .styleable(styleState, MaveStyles.listRowItemStyle, style),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.album.images.firstOrNull()?.url,
            contentDescription = track.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp).background(Color.DarkGray, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = track.artists.joinToString { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PlaylistRowItem(
    playlist: com.musically.studio.network.MavePlaylist,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .styleable(styleState, MaveStyles.listRowItemStyle, style),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.coverUrl,
            contentDescription = playlist.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp).background(Color.DarkGray, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Playlist • ${playlist.creator}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}
