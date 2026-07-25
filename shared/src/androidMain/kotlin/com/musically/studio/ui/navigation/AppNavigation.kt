package com.musically.studio.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowWidthSizeClass
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.MiniPlayer
import com.musically.studio.ui.screens.NowPlayingScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaveApp(
    viewModel: MainViewModel,
    onAcknowledgePermissions: () -> Unit,
    hasPermissions: Boolean
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    val topLevelRoutes = setOf<Route>(Route.Home, Route.Search, Route.Chat, Route.Podcast, Route.Devices, Route.Library)
    val startRoute: Route = Route.Welcome

    val sceneStrategies: List<androidx.navigation3.scene.SceneStrategy<Route>> = listOf(
        BottomSheetSceneStrategy<Route>(),
        androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy<Route>(),
        androidx.navigation3.scene.SinglePaneSceneStrategy<Route>()
    )

    val navigationState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = topLevelRoutes,
        serializer = androidx.savedstate.compose.serialization.serializers.MutableStateSerializer(Route.serializer())
    )
    val navigator = remember { Navigator(navigationState) }
    
    val currentPlayingTrack by viewModel.currentPlayingTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val currentModality by viewModel.currentModality.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.shouldExpandBottomSheet.collectLatest { expand ->
            if (expand) {
                scaffoldState.bottomSheetState.expand()
            }
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { route ->
            navigator.navigate(route)
        }
    }

    val currentRoute = navigationState.backStacks[navigationState.topLevelRoute]?.last() ?: navigationState.topLevelRoute
    val showNavSuite = currentRoute in listOf(Route.Home, Route.Discover, Route.Search, Route.Chat, Route.Podcast, Route.Library, Route.Devices) || currentRoute is Route.AlbumView || currentRoute is Route.UserProfile
    
    val layoutType = if (showNavSuite) {
        androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    } else {
        androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.None
    }

    val entryProvider = maveEntryProvider(
        viewModel = viewModel,
        navigator = navigator,
        onAcknowledgePermissions = onAcknowledgePermissions,
        onMenuClick = {
            navigator.navigate(Route.UserProfile("me"))
        },
        onLikeClick = { id -> viewModel.bookmarkTrack(id) },
        onDownloadClick = { id -> 
            val track = viewModel.tracks.value.find { it.id == id } ?: viewModel.communityTracks.value.find { it.id == id }
            val url = "https://mave.studio/api/v1/tracks/\${track?.id}/audio"
            val request = android.app.DownloadManager.Request(android.net.Uri.parse(url))
                .setTitle(track?.name ?: "Unknown Track")
                .setDescription("Downloading track")
                .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val downloadManager = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
            downloadManager.enqueue(request)
        }
    )

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            item(
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Home") },
                selected = currentRoute == Route.Home,
                onClick = { navigator.navigate(Route.Home) }
            )
            item(
                icon = { Icon(Icons.Default.Search, contentDescription = null) },
                label = { Text("Search") },
                selected = currentRoute == Route.Search,
                onClick = { navigator.navigate(Route.Search) }
            )
            item(
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                label = { Text("Mave") },
                selected = currentRoute == Route.Chat,
                onClick = { navigator.navigate(Route.Chat) }
            )
            item(
                icon = { Icon(Icons.Default.Podcasts, contentDescription = null) },
                label = { Text("Podcast") },
                selected = currentRoute == Route.Podcast,
                onClick = { navigator.navigate(Route.Podcast) }
            )
            item(
                icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
                label = { Text("Devices") },
                selected = currentRoute == Route.Devices,
                onClick = { navigator.navigate(Route.Devices) }
            )
            item(
                icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                label = { Text("Library") },
                selected = currentRoute == Route.Library,
                onClick = { navigator.navigate(Route.Library) }
            )
        }
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = if (currentPlayingTrack != null) 72.dp else 0.dp,
            sheetDragHandle = null,
            sheetContent = {
                if (currentPlayingTrack != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        NowPlayingScreen(
                            track = currentPlayingTrack,
                            viewModel = viewModel,
                            modality = currentModality,
                            onCollapse = {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                            },
                            onMoreOptions = { navigator.navigate(Route.TrackOptions(currentPlayingTrack!!.id)) },
                            onQueueClick = { 
                                navigator.navigate(Route.Queue)
                            },
                            onLyricsClick = { 
                                navigator.navigate(Route.Lyrics(currentPlayingTrack!!.id))
                            }
                        )
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            MiniPlayer(
                                track = currentPlayingTrack!!,
                                isPlaying = isPlaying,
                                onPlayPauseClick = { viewModel.togglePlayPause() },
                                onClick = {
                                    coroutineScope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.height(1.dp))
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                
                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = { navigator.goBack() },
                    transitionSpec = maveTransitionSpec(),
                    popTransitionSpec = mavePopTransitionSpec(),
                    sceneStrategies = sceneStrategies
                )
            }
        }
    }
}
