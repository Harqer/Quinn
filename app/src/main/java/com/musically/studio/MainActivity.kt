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
import com.musically.studio.ui.screens.onboarding.*
import com.musically.studio.ui.theme.MaveAppTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Login : Route
    @Serializable data object Home : Route
    @Serializable data object Library : Route
    @Serializable data object Devices : Route
    @Serializable data class AlbumView(val albumId: String) : Route
    
    // Onboarding Sequence
    @Serializable data object Welcome : Route
    @Serializable data object AuthOptions : Route
    @Serializable data object EmailInput : Route
    @Serializable data object PasswordInput : Route
    @Serializable data object BirthdayInput : Route
    @Serializable data object GenderInput : Route
    @Serializable data object NameTerms : Route
    @Serializable data object Loading : Route
    @Serializable data object Notification : Route
    @Serializable data object ArtistSelection : Route
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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        
        setContent {
            MaveAppTheme {
                MaveApp(
                    onAcknowledgePermissions = { checkPermissions() },
                    hasPermissions = permissionsGranted.value
                )
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaveApp(
    viewModel: MainViewModel = viewModel(),
    onAcknowledgePermissions: () -> Unit,
    hasPermissions: Boolean
) {
    val topLevelRoutes = listOf(
        TopLevelRoute("Studio", Route.Home, Icons.Default.Home),
        TopLevelRoute("Library", Route.Library, Icons.Default.LibraryMusic)
    )

    var currentRoute: Route by remember { 
        mutableStateOf(if (viewModel.isUserLoggedIn()) Route.Home else Route.Welcome) 
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

    val showBottomNav = currentRoute in listOf(Route.Home, Route.Library, Route.Devices) || currentRoute is Route.AlbumView
    val showPlayerBar = showBottomNav && currentPlayingTrack != null

    Scaffold { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavDisplay(
                backStack = listOf(currentRoute),
                onBack = { currentRoute = Route.Home }
            ) { key: Route ->
                androidx.navigation3.runtime.NavEntry<Route>(key) {
                    when (key) {
                        // --- Onboarding Screens ---
                        Route.Welcome -> WelcomeScreen(
                            onSignUpClick = { currentRoute = Route.AuthOptions },
                            onLoginClick = { currentRoute = Route.Login }
                        )
                        Route.AuthOptions -> AuthOptionsScreen(
                            onEmailClick = { currentRoute = Route.EmailInput },
                            onGoogleClick = { viewModel.guestLogin { s, _ -> if (s) currentRoute = Route.Loading } },
                            onAppleClick = { /* logic */ },
                            onLoginClick = { currentRoute = Route.Login },
                            onBackClick = { currentRoute = Route.Welcome }
                        )
                        Route.EmailInput -> EmailInputScreen(
                            viewModel = viewModel,
                            onNextClick = { currentRoute = Route.PasswordInput },
                            onBackClick = { currentRoute = Route.AuthOptions }
                        )
                        Route.PasswordInput -> PasswordInputScreen(
                            viewModel = viewModel,
                            onNextClick = { currentRoute = Route.BirthdayInput },
                            onBackClick = { currentRoute = Route.EmailInput }
                        )
                        Route.BirthdayInput -> BirthdayInputScreen(
                            viewModel = viewModel,
                            onNextClick = { currentRoute = Route.GenderInput },
                            onBackClick = { currentRoute = Route.PasswordInput }
                        )
                        Route.GenderInput -> GenderInputScreen(
                            viewModel = viewModel,
                            onNextClick = { currentRoute = Route.NameTerms },
                            onBackClick = { currentRoute = Route.BirthdayInput }
                        )
                        Route.NameTerms -> NameTermsScreen(
                            viewModel = viewModel,
                            onNextClick = { currentRoute = Route.Loading },
                            onBackClick = { currentRoute = Route.GenderInput }
                        )
                        Route.Loading -> {
                            LoadingScreen()
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(2000)
                                currentRoute = Route.Notification
                            }
                        }
                        Route.Notification -> NotificationScreen(
                            onTurnOn = { onAcknowledgePermissions(); currentRoute = Route.ArtistSelection },
                            onNotNow = { currentRoute = Route.ArtistSelection }
                        )
                        Route.ArtistSelection -> ArtistSelectionScreen(
                            viewModel = viewModel,
                            onDone = { currentRoute = Route.Home }
                        )
                        
                        // --- Main Studio Screens ---
                        Route.Login -> LoginScreen(
                            onLoginSuccess = { currentRoute = Route.Home },
                            onNavigateToSignUp = { currentRoute = Route.Welcome },
                            viewModel = viewModel
                        )
                        Route.Home -> HomeScreen(
                            viewModel = viewModel,
                            isWearableConnected = isWearableConnected,
                            onNavigateToDevices = { currentRoute = Route.Devices }
                        )
                        Route.Library -> LibraryScreen(
                            viewModel = viewModel,
                            onNavigateToNowPlaying = { /* logic */ },
                            onNavigateToAlbum = { currentRoute = Route.AlbumView(it) },
                            onNavigateToHome = { currentRoute = Route.Home }
                        )
                        is Route.AlbumView -> AlbumViewScreen(
                            albumId = key.albumId,
                            viewModel = viewModel,
                            onNavigateBack = { currentRoute = Route.Library },
                            onTrackClick = { /* logic */ }
                        )
                        Route.Devices -> DevicesScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentRoute = Route.Home }
                        )
                    }
                }
            }

            if (showBottomNav) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)) {
                        if (showPlayerBar) {
                            MiniPlayer(
                                track = currentPlayingTrack!!,
                                isPlaying = isPlaying,
                                onPlayPauseClick = { viewModel.togglePlayPause() },
                                onClick = { /* expand logic */ }
                            )
                        }
                        NavigationBar {
                            topLevelRoutes.forEach { route ->
                                NavigationBarItem(
                                    icon = { Icon(route.icon, contentDescription = route.name) },
                                    label = { Text(route.name) },
                                    selected = currentRoute == route.route,
                                    onClick = { currentRoute = route.route as Route }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
