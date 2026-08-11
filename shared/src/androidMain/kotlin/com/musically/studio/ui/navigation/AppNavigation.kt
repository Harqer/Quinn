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


import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.compose.BackHandler
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
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    var startRoute by remember { androidx.compose.runtime.mutableStateOf<Route>(if (auth.currentUser != null) Route.Home else Route.Welcome) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { firebaseAuth ->
            startRoute = if (firebaseAuth.currentUser != null) Route.Home else Route.Welcome
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }


        val adaptiveInfo = currentWindowAdaptiveInfoV2()
        val topLevelRoutes = setOf<Route>(Route.Welcome, Route.Home, Route.Library, Route.Search, Route.Chat, Route.Camera)
        
        val sceneStrategies: List<SceneStrategy<Route>> = listOf(
            BottomSheetSceneStrategy(),
            androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy(),
            SinglePaneSceneStrategy()
        )

        val navigationState = rememberNavigationState(
            startRoute = startRoute,
            topLevelRoutes = topLevelRoutes,
            serializer = androidx.savedstate.compose.serialization.serializers.MutableStateSerializer(Route.serializer())
        )
        val navigator = remember { Navigator(navigationState) }
    LaunchedEffect(auth.currentUser != null) {
        if (auth.currentUser != null) {
            navigator.clearAll()
            navigationState.topLevelRoute = Route.Home
        }
    }
    
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
            if (auth.currentUser != null) {
                startRoute = Route.Home
            }
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
                val url = track?.audioUrl ?: return@addOnSuccessListener
                val request = android.app.DownloadManager.Request(url.toUri())
                    .setTitle(track?.name ?: "Unknown Track")
                    .setDescription("Downloading track")
                    .addRequestHeader("Authorization", "Bearer $token")
                    .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, "${track?.name?.replace(" ", "_") ?: "Track"}.mp3")
                    .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                val downloadManager = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
                downloadManager.enqueue(request)
            }?.addOnFailureListener { e ->
                timber.log.Timber.e(e, "Failed to get auth token for download")
            }
        }
    )

    val isOnboarding = currentRoute is Route.Welcome || currentRoute is Route.SignIn || currentRoute is Route.MfaEnrollment || currentRoute is Route.MfaVerification

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                drawerContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            ) {
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
        navigator = navigator,
        viewModel = viewModel
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
                var backPressedOnce by remember { mutableStateOf(false) }
                val context = LocalContext.current
                
                BackHandler(enabled = navigationState.stacksInUse.size == 1 && navigationState.backStacks[navigationState.topLevelRoute]?.size == 1) {
                    if (backPressedOnce) {
                        (context as? android.app.Activity)?.finish()
                    } else {
                        backPressedOnce = true
                        android.widget.Toast.makeText(context, "Press back again to exit", android.widget.Toast.LENGTH_SHORT).show()
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(2000)
                            backPressedOnce = false
                        }
                    }
                }

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

