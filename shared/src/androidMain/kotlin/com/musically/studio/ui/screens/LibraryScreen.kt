package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.musically.studio.shared.R
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.TrackItem
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.atoms.MaveLogo
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.clickable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import timber.log.Timber

import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.musically.studio.ui.theme.FormFactorPreviews
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.ui.graphics.vector.path
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.remember
import com.musically.studio.ui.theme.MaveStyles

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
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.catalogErrorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchUserTracks()
        viewModel.fetchLikedTracks()
    }

    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    
    val spotifyConnected by viewModel.spotifyConnected.collectAsStateWithLifecycle()
    val youtubeConnected by viewModel.youtubeConnected.collectAsStateWithLifecycle()

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.oauthUrl.collect { url ->
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            context.startActivity(intent)
        }
    }
    val selectedFilterState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).background(Color.DarkGray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("M", color = Color.White, fontWeight = FontWeight.Bold)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = com.musically.studio.ui.theme.MaveBackground),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sort/Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search, // Replace with SwapVert icon if available
                        contentDescription = "Sort",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recently played",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    FilterPill(
                        text = "Liked",
                        isSelected = selectedFilterState.value == "Liked",
                        onClick = {
                            selectedFilterState.value = if (selectedFilterState.value == "Liked") null else "Liked"
                        }
                    )
                }
                Icon(
                    imageVector = Icons.Default.Menu, // FormatListBulleted equivalent
                    contentDescription = "View as list",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (isLoading && tracks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (errorMessage != null && tracks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        MaveButton(
                            text = "Retry",
                            onClick = { 
                                viewModel.clearCatalogError()
                                viewModel.fetchUserTracks() 
                            }
                        )
                    }
                }
            } else if (tracks.isEmpty()) {
                EmptyLibraryState(onNavigateToHome = onNavigateToHome)
            } else {
                val filteredTracks = if (selectedFilterState.value == "Liked") {
                    likedTracks
                } else {
                    tracks
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        ConnectionsSection(
                            viewModel = viewModel,
                            spotifyConnected = spotifyConnected,
                            youtubeConnected = youtubeConnected
                        )
                    }
                    items(filteredTracks) { track ->
                        TrackItem(
                            track = track,
                            onClick = { onNavigateToNowPlaying(track.id) },
                            onAlbumClick = { onNavigateToAlbum(track.album.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryAlbumItem(
    album: com.musically.studio.network.MaveAlbum,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = true
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .styleable(styleState, MaveStyles.libraryRowItemStyle, style),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = album.images.firstOrNull()?.url,
                contentDescription = album.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = album.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "Album • ${album.artists?.firstOrNull()?.name ?: "Unknown Artist"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun LibraryPlaylistItem(
    playlist: com.musically.studio.network.MavePlaylist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = true
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .styleable(styleState, MaveStyles.libraryRowItemStyle, style),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = playlist.coverUrl,
                contentDescription = playlist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "Playlist • ${playlist.creator}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyLibraryState(onNavigateToHome: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Mave Molecule: High-fidelity empty state
            MaveLogo(size = 100, modifier = Modifier.alpha(0.5f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Your Studio is empty",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Strike your first song with Mave to start building your personal orchestra.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            MaveButton(
                text = "Create a Song",
                onClick = onNavigateToHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FilterPill(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = true
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .styleable(styleState, MaveStyles.filterPillStyle, style)
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ConnectionsSection(
    viewModel: MainViewModel,
    spotifyConnected: Boolean,
    youtubeConnected: Boolean
) {
    val isCompact = !androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2().windowSizeClass.isWidthAtLeastBreakpoint(600)
    
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Connections", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ConnectionButton("Spotify", spotifyConnected) { viewModel.connectSpotify() }
                ConnectionButton("YouTube", youtubeConnected) { viewModel.connectYouTube() }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ConnectionButton("Spotify", spotifyConnected, modifier = Modifier.weight(1f)) { viewModel.connectSpotify() }
                ConnectionButton("YouTube", youtubeConnected, modifier = Modifier.weight(1f)) { viewModel.connectYouTube() }
            }
        }
    }
}

@Composable
fun ConnectionButton(
    platform: String,
    isConnected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = if (isConnected) {
        if (platform == "Spotify") Color(0xFF1DB954).copy(alpha = 0.2f) else Color(0xFFFF0000).copy(alpha = 0.2f)
    } else {
        Color.White.copy(alpha = 0.05f)
    }
    val textColor = if (isConnected) {
        if (platform == "Spotify") Color(0xFF1DB954) else Color(0xFFFF0000)
    } else {
        Color.White
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { if (!isConnected) onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (platform == "Spotify") {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.musically.studio.shared.R.drawable.ic_spotify),
                    contentDescription = "Spotify",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            } else {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.musically.studio.shared.R.drawable.ic_youtube),
                    contentDescription = "YouTube",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isConnected) "$platform Connected" else "Connect $platform",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (!isConnected) {
            Icon(
                imageVector = OpenInNewIcon,
                contentDescription = "Open in new window",
                tint = Color(0xFFE3E3E3),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

val OpenInNewIcon: androidx.compose.ui.graphics.vector.ImageVector
    get() = androidx.compose.ui.graphics.vector.ImageVector.Builder(
        name = "OpenInNew",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(280f)
            verticalLineToRelative(80f)
            horizontalLineTo(200f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-280f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(280f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            horizontalLineTo(200f)
            close()
            moveToRelative(188f, -212f)
            lineToRelative(-56f, -56f)
            lineToRelative(372f, -372f)
            horizontalLineTo(560f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(280f)
            verticalLineToRelative(280f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-144f)
            lineTo(388f, 628f)
            close()
        }
    }.build()

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
