package com.musically.studio

import com.musically.studio.ui.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetDigitalCredentialOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.compose.foundation.layout.fillMaxSize
import coil.compose.AsyncImage
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import timber.log.Timber
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

import com.musically.studio.ui.AuthSideEffect
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.navigation.MaveApp
import com.musically.studio.ui.navigation.Route
import com.musically.studio.ui.theme.MaveAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import java.security.MessageDigest
import androidx.credentials.exceptions.NoCredentialException

import android.content.ComponentName
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.musically.studio.audio.PlaybackService
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.types.RegistrationState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionsGranted = mutableStateOf(false)
    private lateinit var mainViewModel: MainViewModel
    private var controllerFuture: ListenableFuture<MediaController>? = null
    
    // Telemetry and Gesture properties for Meta Wearables SDK
    var currentBatteryLevel: Int = -1
    var isWearDetected: Boolean = false
    var lastLoggedGesture: String? = null
    var isAppSwitcherOpen: Boolean = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        val bluetoothGranted = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            permissions[Manifest.permission.BLUETOOTH_CONNECT] ?: false
        } else true

        if (cameraGranted && audioGranted && bluetoothGranted) {
            permissionsGranted.value = true
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        
        FirebaseAuth.getInstance().pendingAuthResult
            ?.addOnSuccessListener { result ->
                Timber.d("Pending auth result consumed successfully: ${result.user?.uid}")
            }
            ?.addOnFailureListener { e ->
                Timber.e(e, "Pending auth result failed")
            }
            
        setContent {
            mainViewModel = viewModel<MainViewModel>()
            
            LaunchedEffect(intent) {
                handleIntent(intent, mainViewModel)
            }
            
            LaunchedEffect(Unit) {
                // Monitor DAT Registration state
                launch {
                    Wearables.registrationState.collectLatest { state ->
                        Timber.d("Wearable Registration State: $state")
                        if (state == RegistrationState.REGISTERED && permissionsGranted.value) {
                            Timber.i("Wearables SDK successfully registered.")
                        }
                    }
                }
                
                mainViewModel.authSideEffect.collectLatest { effect ->
                    when (effect) {

                        is AuthSideEffect.LaunchMfaVerification -> mainViewModel.navigateTo(Route.MfaVerification)
                        AuthSideEffect.LaunchVerifiedEmail -> { /* no-op or handled elsewhere */ }

                        // Navigation on sign-out and deletion is handled by UserProfileScreen's
                        // onSignedOut callback which routes to Route.Login. No additional
                        // activity-level action required.
                        AuthSideEffect.SignedOut -> {
                            mainViewModel.clearNavigation()
                            mainViewModel.navigateTo(Route.Welcome)
                        }
                        AuthSideEffect.AccountDeleted -> {
                            mainViewModel.clearNavigation()
                            mainViewModel.navigateTo(Route.Welcome)
                        }
                    }
                }
            }

            var isPipMode by remember { mutableStateOf(isInPictureInPictureMode) }
            DisposableEffect(this@MainActivity) {
                val listener = androidx.core.util.Consumer<androidx.core.app.PictureInPictureModeChangedInfo> { info ->
                    isPipMode = info.isInPictureInPictureMode
                }
                addOnPictureInPictureModeChangedListener(listener)
                onDispose {
                    removeOnPictureInPictureModeChangedListener(listener)
                }
            }

            val isPlaying by mainViewModel.isPlaying.collectAsStateWithLifecycle()
            val track by mainViewModel.currentPlayingTrack.collectAsStateWithLifecycle()

            LaunchedEffect(isPlaying) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val params = android.app.PictureInPictureParams.Builder()
                        .setAutoEnterEnabled(isPlaying)
                        .build()
                    setPictureInPictureParams(params)
                }
            }

            MaveAppTheme(dynamicColor = true) {
                if (isPipMode) {
                    val imageUrl = track?.album?.images?.firstOrNull()?.url
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Cover",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    MaveApp(
                        viewModel = mainViewModel,
                        onAcknowledgePermissions = { checkPermissions() },
                        hasPermissions = permissionsGranted.value
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the activity's intent
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (mainViewModel.isPlaying.value) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val params = android.app.PictureInPictureParams.Builder()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    params.setAutoEnterEnabled(true)
                }
                enterPictureInPictureMode(params.build())
            }
        }
    }

    private fun handleIntent(intent: Intent, viewModel: MainViewModel) {
        if (intent.action == Intent.ACTION_VIEW) {
            val link = intent.dataString
            if (link != null) {
                val auth = FirebaseAuth.getInstance()
                if (auth.isSignInWithEmailLink(link)) {
                    viewModel.handleEmailLink(link) { success, _ ->
                        if (success) {
                            viewModel.clearNavigation()
                            viewModel.navigateTo(Route.Home)
                        }
                    }
                    return
                }
            }
        }

        val destination = intent.getStringExtra("DESTINATION")
        val prompt = intent.getStringExtra("PROMPT")
        
        // Prevent Deep Link Intent Spoofing: Only process internal commands if not ACTION_VIEW
        if (intent.action != Intent.ACTION_VIEW) {
            if (destination == "library") {
                viewModel.navigateTo(Route.Library)
            } else if (destination == "home") {
                viewModel.navigateTo(Route.Home)
            }
            
            if (!prompt.isNullOrBlank()) {
                viewModel.sendTextCommand(prompt)
            }
        }
    }

    fun handleTelemetryUpdate(battery: Int, onHead: Boolean, usbConnected: Boolean) {
        currentBatteryLevel = battery
        isWearDetected = onHead
    }

    fun registerGesture(gesture: String) {
        lastLoggedGesture = gesture
        if (gesture == "middle_finger_to_thumb_hold") {
            isAppSwitcherOpen = !isAppSwitcherOpen
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val future = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture = future
        
        future.addListener({
            try {
                val controller = future.get()
                controller.addListener(object : androidx.media3.common.Player.Listener {
                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                        mainViewModel.setPlayingState(playWhenReady)
                    }
                })
            } catch (e: Exception) {
                Timber.e(e, "Failed to connect to PlaybackService MediaController")
            }
        }, androidx.core.content.ContextCompat.getMainExecutor(this))
    }

    override fun onStop() {
        super.onStop()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
    }


    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }


}

