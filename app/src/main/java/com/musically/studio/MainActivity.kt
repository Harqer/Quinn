package com.musically.studio

import com.musically.studio.ui.*
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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        
        
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
                        AuthSideEffect.LaunchGoogleSignIn -> launchGoogleSignIn()
                        AuthSideEffect.LaunchAppleSignIn -> launchAppleSignIn(mainViewModel)
                        is AuthSideEffect.LaunchMfaVerification -> mainViewModel.navigateTo(Route.MfaVerification)
                        AuthSideEffect.LaunchVerifiedEmail -> { /* no-op or handled elsewhere */ }

                        // Navigation on sign-out and deletion is handled by UserProfileScreen's
                        // onSignedOut callback which routes to Route.Login. No additional
                        // activity-level action required.
                        AuthSideEffect.SignedOut -> { /* handled in UserProfileScreen */ }
                        AuthSideEffect.AccountDeleted -> { /* handled in UserProfileScreen */ }
                    }
                }
            }

            MaveAppTheme {
                MaveApp(
                    viewModel = mainViewModel,
                    onAcknowledgePermissions = { checkPermissions() },
                    hasPermissions = permissionsGranted.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the activity's intent
    }

    private fun handleIntent(intent: Intent, viewModel: MainViewModel) {
        if (intent.action == Intent.ACTION_VIEW) {
            val link = intent.dataString
            if (link != null) {
                val auth = FirebaseAuth.getInstance()
                if (auth.isSignInWithEmailLink(link)) {
                    viewModel.handleEmailLink(link) { success, _ ->
                        if (success) {
                            viewModel.navigateTo(Route.Home)
                        }
                    }
                    return
                }
            }
        }

        val destination = intent.getStringExtra("DESTINATION")
        val prompt = intent.getStringExtra("PROMPT")
        
        if (destination == "library") {
            viewModel.navigateTo(Route.Library)
        } else if (destination == "home") {
            viewModel.navigateTo(Route.Home)
        }
        
        if (!prompt.isNullOrBlank()) {
            viewModel.sendTextCommand(prompt)
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

    @OptIn(ExperimentalDigitalCredentialApi::class)
    private fun launchGoogleSignIn() {
        lifecycleScope.launch {
            try {
                val rawNonce = UUID.randomUUID().toString()
                val bytes = rawNonce.toByteArray(Charsets.UTF_8)
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

                val credentialManager = CredentialManager.create(this@MainActivity)
                
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(getString(R.string.google_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .setNonce(hashedNonce)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = this@MainActivity
                )

                val credential = result.credential
                if (credential is androidx.credentials.CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    mainViewModel.loginWithGoogle(googleIdTokenCredential.idToken, rawNonce) { success, errorMsg ->
                        if (success) {
                            mainViewModel.navigateTo(Route.Home)
                        } else {
                            Timber.e("Firebase login with Google credential failed: $errorMsg")
                            android.widget.Toast.makeText(
                                this@MainActivity,
                                errorMsg ?: "Google sign in failed. Please try again.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Timber.e("Unexpected type of credential: ${credential::class.java.name}")
                }
            } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                Timber.d("User cancelled Google Sign-In picker")
            } catch (e: Exception) {
                Timber.e(e, "Google Sign-In failed via CredentialManager, falling back to Web OAuth")
                val provider = OAuthProvider.newBuilder("google.com")
                FirebaseAuth.getInstance().startActivityForSignInWithProvider(this@MainActivity, provider.build())
                    .addOnSuccessListener { authResult ->
                        mainViewModel.startRtdbSync()
                        mainViewModel.navigateTo(Route.Home)
                    }
                    .addOnFailureListener { webErr ->
                        Timber.e(webErr, "Fallback Web OAuth Sign-In failed")
                        val message = "Google Sign-In failed: ${webErr.localizedMessage ?: "Unknown error"}"
                        android.widget.Toast.makeText(this@MainActivity, message, android.widget.Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun launchAppleSignIn(viewModel: MainViewModel) {
        val provider = OAuthProvider.newBuilder("apple.com")
        FirebaseAuth.getInstance().startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
                viewModel.startRtdbSync()
                viewModel.navigateTo(Route.Home)
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Apple Sign-In failed")
            }
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

