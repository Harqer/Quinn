package com.musically.studio

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.ui.NavDisplay
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.screens.HomeScreen
import com.musically.studio.ui.screens.LibraryScreen
import com.musically.studio.ui.screens.PodcastScreen
import com.musically.studio.ui.theme.MusicallyAppTheme
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data object Library : Route
    @Serializable data object Podcast : Route
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (cameraGranted && audioGranted) {
            startSession()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        checkPermissions()
        handleIntent(intent)

        setContent {
            MusicallyAppTheme {
                MusicallyApp()
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

@Composable
fun MusicallyApp(viewModel: MainViewModel = viewModel()) {
    val topLevelRoutes = listOf(
        TopLevelRoute("Studio", Route.Home, Icons.Default.Home),
        TopLevelRoute("Podcast", Route.Podcast, Icons.Default.Mic),
        TopLevelRoute("Library", Route.Library, Icons.Default.LibraryMusic)
    )

    var currentRoute: Route by remember { mutableStateOf(Route.Home) }
    val isSpotifyConnected by viewModel.isSpotifyConnected.collectAsStateWithLifecycle()
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val isWearableConnected by viewModel.isWearableConnected.collectAsStateWithLifecycle()

    NavigationSuiteScaffold(
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
            onBack = { /* handle back */ }
        ) { key: Route ->
            androidx.navigation3.runtime.NavEntry<Route>(key) {
                when (key) {
                    Route.Home -> HomeScreen(
                        viewModel = viewModel,
                        isWearableConnected = isWearableConnected
                    )
                    Route.Podcast -> PodcastScreen(
                        viewModel = viewModel,
                        isWearableConnected = isWearableConnected
                    )
                    Route.Library -> LibraryScreen()
                }
            }
        }
    }
}
