package com.musically.studio.ui.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.bookmarkTrack
import com.musically.studio.ui.components.organisms.AppBottomSheet
import com.musically.studio.ui.components.organisms.AppNavigationSuite
import com.musically.studio.ui.theme.MaveStyles
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaveApp(
    viewModel: MainViewModel,
    onAcknowledgePermissions: () -> Unit,
    hasPermissions: Boolean
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val topLevelRoutes = setOf<Route>(Route.Welcome, Route.Home, Route.Library, Route.Search, Route.Chat, Route.Camera)
    
    val sceneStrategies: List<SceneStrategy<Route>> = listOf(
        BottomSheetSceneStrategy(),
        androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy(),
        SinglePaneSceneStrategy()
    )

    val navigationState = rememberNavigationState(
        startRoute = Route.Welcome,
        topLevelRoutes = topLevelRoutes,
        serializer = androidx.savedstate.compose.serialization.serializers.MutableStateSerializer(Route.serializer())
    )
    val navigator = remember { Navigator(navigationState) }
    
    val currentPlayingTrack by viewModel.currentPlayingTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.shouldExpandBottomSheet.collectLatest { expand ->
            if (expand) sheetState.expand()
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { route ->
            navigator.navigate(route)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearNavigationEvent.collectLatest {
            navigator.clearAll()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.generationBlockedEvent.collectLatest { reason ->
            navigator.navigate(Route.UsageLimitSheet(reason.name))
        }
    }

    val currentRoute = navigationState.backStacks[navigationState.topLevelRoute]?.last() ?: navigationState.topLevelRoute
    val showNavSuite = currentRoute in listOf(Route.Home, Route.Library, Route.Search, Route.Chat, Route.Camera) || currentRoute is Route.AlbumView || currentRoute is Route.UserProfile
    
    val layoutType = if (showNavSuite) {
        val defaultType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        if (defaultType == NavigationSuiteType.NavigationDrawer) NavigationSuiteType.None else defaultType
    } else NavigationSuiteType.None

    val entryProvider = maveEntryProvider(
        viewModel = viewModel,
        navigator = navigator,
        hasPermissions = hasPermissions,
        onAcknowledgePermissions = onAcknowledgePermissions,
        onMenuClick = {
            coroutineScope.launch { drawerState.open() }
        },
        onLikeClick = { id -> viewModel.bookmarkTrack(id) },
        onDownloadClick = { id -> 
            val track = viewModel.tracks.value.find { it.id == id } ?: viewModel.communityTracks.value.find { it.id == id }
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.addOnSuccessListener { result ->
                val token = result.token
                val url = "https://mave.studio/api/v1/tracks/\${track?.id}/audio"
                val request = android.app.DownloadManager.Request(url.toUri())
                    .setTitle(track?.name ?: "Unknown Track")
                    .setDescription("Downloading track")
                    .addRequestHeader("Authorization", "Bearer $token")
                    .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, "${track?.name?.replace(" ", "_") ?: "Track"}.mp3")
                    .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                val downloadManager = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
                downloadManager.enqueue(request)
            }?.addOnFailureListener { e ->
                android.util.Log.e("Download", "Failed to get auth token for download", e)
            }
        }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Mave Settings",
                    modifier = Modifier.padding(16.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Premium") },
                    selected = false,
                    onClick = { 
                        coroutineScope.launch { drawerState.close() }
                        navigator.navigate(Route.Premium)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Settings & Privacy") },
                    selected = false,
                    onClick = { 
                        coroutineScope.launch { drawerState.close() }
                        navigator.navigate(Route.Settings)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Recents") },
                    selected = false,
                    onClick = { 
                        coroutineScope.launch { drawerState.close() }
                        navigator.navigate(Route.Recents)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Downloaded") },
                    selected = false,
                    onClick = { 
                        coroutineScope.launch { drawerState.close() }
                        navigator.navigate(Route.Downloaded)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        AppNavigationSuite(
        layoutType = layoutType,
        currentRoute = currentRoute,
        navigator = navigator
    ) {
        AppBottomSheet(
            sheetState = sheetState,
            currentPlayingTrack = currentPlayingTrack,
            isPlaying = isPlaying,
            viewModel = viewModel,
            navigator = navigator
        ) { paddingValues ->
            val interactionSource = remember { MutableInteractionSource() }
            val styleState = rememberUpdatedStyleState(interactionSource) {}
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues),
                contentAlignment = Alignment.Center
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
}
