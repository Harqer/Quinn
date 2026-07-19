package com.musically.studio

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.ui.NavDisplay
import com.google.firebase.FirebaseApp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.MiniPlayer
import com.musically.studio.ui.screens.*
import com.musically.studio.ui.theme.MusicallyAppTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Login : Route
    @Serializable data object Onboarding : Route
    @Serializable data object Home : Route
    @Serializable data object Library : Route
    @Serializable data object Devices : Route
    @Serializable data class AlbumView(val albumId: String) : Route
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

class MainActivity : ComponentActivity() {

    private var permissionsGranted = mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (cameraGranted && audioGranted) {
            permissionsGranted.value = true
            startSession()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        
        handleIntent(intent)

        setContent {
            MusicallyAppTheme {
                MusicallyApp(
                    onAcknowledgePermissions = { checkPermissions() },
                    hasPermissions = permissionsGranted.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.let { uri ->
            if (uri.scheme == "musically" && uri.host == "callback") {
                // val code = uri.getQueryParameter("code")
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun startSession() {
        val intent = Intent(this, WearableStreamingService::class.java)
        startService(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicallyApp(
    viewModel: MainViewModel = viewModel(),
    onAcknowledgePermissions: () -> Unit,
    hasPermissions: Boolean
) {
    val topLevelRoutes = listOf(
        TopLevelRoute("Studio", Route.Home, Icons.Default.Home),
        TopLevelRoute("Library", Route.Library, Icons.Default.LibraryMusic)
    )

    var currentRoute: Route by remember { 
        mutableStateOf(if (viewModel.isUserLoggedIn()) Route.Home else Route.Login) 
    }
    
    LaunchedEffect(hasPermissions) {
        if (hasPermissions && currentRoute == Route.Onboarding) {
            currentRoute = Route.Home
        }
    }
    
    val currentPlayingTrack by viewModel.currentPlayingTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isWearableConnected by viewModel.isWearableConnected.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )
    val coroutineScope = rememberCoroutineScope()

    if (currentRoute == Route.Login) {
        LoginScreen(
            onLoginSuccess = { currentRoute = if (hasPermissions) Route.Home else Route.Onboarding },
            onNavigateToSignUp = { /* TODO */ },
            viewModel = viewModel
        )
    } else if (currentRoute == Route.Onboarding) {
        OnboardingScreen(onContinue = onAcknowledgePermissions)
    } else {
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
                            onCollapse = {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                            }
                        )
                        // Overlay the MiniPlayer when partially expanded so it covers NowPlaying
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded || 
                            scaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded) {
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
                    Box(modifier = Modifier.height(1.dp)) // empty bottom sheet
                }
            }
        ) { paddingValues ->
            NavigationSuiteScaffold(
                modifier = Modifier.padding(bottom = if (currentPlayingTrack != null && scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) 72.dp else 0.dp),
                navigationSuiteItems = {
                    topLevelRoutes.forEach { topLevelRoute ->
                        item(
                            icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) },
                            label = { Text(topLevelRoute.name) },
                            selected = currentRoute == topLevelRoute.route,
                            onClick = { currentRoute = topLevelRoute.route as Route }
                        )
                    }
                }
            ) {
                NavDisplay(
                    backStack = listOf(currentRoute),
                    onBack = { currentRoute = Route.Home }
                ) { key: Route ->
                    androidx.navigation3.runtime.NavEntry<Route>(key) {
                        when (key) {
                            Route.Home -> HomeScreen(
                                viewModel = viewModel,
                                isWearableConnected = isWearableConnected,
                                onNavigateToDevices = { currentRoute = Route.Devices }
                            )
                            Route.Library -> LibraryScreen(
                                viewModel = viewModel,
                                onNavigateToNowPlaying = { trackId -> 
                                    // Play the track and expand
                                    viewModel.tracks.value.find { it.id == trackId }?.let {
                                        viewModel.playTrack(it)
                                        coroutineScope.launch { scaffoldState.bottomSheetState.expand() }
                                    }
                                },
                                onNavigateToAlbum = { albumId -> currentRoute = Route.AlbumView(albumId) },
                                onNavigateToHome = { currentRoute = Route.Home }
                            )
                            is Route.AlbumView -> AlbumViewScreen(
                                albumId = key.albumId,
                                viewModel = viewModel,
                                onNavigateBack = { currentRoute = Route.Library },
                                onTrackClick = { trackId -> 
                                    viewModel.tracks.value.find { it.id == trackId }?.let {
                                        viewModel.playTrack(it)
                                        coroutineScope.launch { scaffoldState.bottomSheetState.expand() }
                                    }
                                }
                            )
                            Route.Devices -> DevicesScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentRoute = Route.Home }
                            )
                            Route.Onboarding, Route.Login -> {}
                        }
                    }
                }
            }
        }
    }
}
