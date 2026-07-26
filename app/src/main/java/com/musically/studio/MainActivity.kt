package com.musically.studio
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
import androidx.credentials.GetDigitalCredentialOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.window.core.layout.WindowWidthSizeClass
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.musically.studio.R
import com.musically.studio.engage.EngageBroadcastReceiver
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

import android.content.ComponentName
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.musically.studio.audio.PlaybackService

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionsGranted = mutableStateOf(false)
    private lateinit var mainViewModel: MainViewModel
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (cameraGranted && audioGranted) {
            permissionsGranted.value = true
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: androidx.activity.result.ActivityResult ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                mainViewModel.loginWithGoogle(idToken.toString()) { success, _ ->
                    // Navigation handled by state observations in MaveApp
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Something went wrong during sign-in.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        
        EngageBroadcastReceiver.register(this)
        
        setContent {
            mainViewModel = viewModel<MainViewModel>()
            
            LaunchedEffect(intent) {
                handleIntent(intent, mainViewModel)
            }
            
            LaunchedEffect(Unit) {
                mainViewModel.authSideEffect.collectLatest { effect ->
                    when (effect) {
                        AuthSideEffect.LaunchGoogleSignIn -> launchGoogleSignIn()
                        AuthSideEffect.LaunchAppleSignIn -> launchAppleSignIn(mainViewModel)
                        AuthSideEffect.LaunchVerifiedEmail -> launchVerifiedEmail(mainViewModel)

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

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val future = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture = future
        
        future.addListener({
            val controller = future.get()
            controller.addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    mainViewModel.setPlayingState(playWhenReady)
                }
            })
        }, androidx.core.content.ContextCompat.getMainExecutor(this))
    }

    override fun onStop() {
        super.onStop()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
    }

    private fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id)) 
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, gso)
        googleSignInLauncher.launch(client.signInIntent)
    }

    private fun launchAppleSignIn(viewModel: MainViewModel) {
        val provider = OAuthProvider.newBuilder("apple.com")
        FirebaseAuth.getInstance().startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
                viewModel.startRtdbSync()
                // Navigation will update via isUserLoggedIn check or state observation
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Apple Sign-In failed")
            }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
        )
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        permissionLauncher.launch(permissions.toTypedArray())
    }

    @OptIn(androidx.credentials.ExperimentalDigitalCredentialApi::class)
    private fun launchVerifiedEmail(viewModel: MainViewModel) {
        val credentialManager = CredentialManager.create(this)
        val nonce = UUID.randomUUID().toString()

        val openId4vpRequest = """
        {
          "requests": [
            {
              "protocol": "openid4vp-v1-unsigned",
              "data": {
                "response_type": "vp_token",
                "response_mode": "dc_api",
                "nonce": "$nonce",
                "dcql_query": {
                  "credentials": [
                    {
                      "id": "user_info_query",
                      "format": "dc+sd-jwt",
                       "meta": { 
                          "vct_values": ["UserInfoCredential"] 
                       },
                      "claims": [ 
                        {"path": ["email"]}, 
                        {"path": ["name"]},  
                        {"path": ["given_name"]},
                        {"path": ["family_name"]},
                        {"path": ["picture"]},
                        {"path": ["hd"]},
                        {"path": ["email_verified"]}
                      ]
                    }
                  ]
                }
              }
            }
          ]
        }
        """

        val getDigitalCredentialOption = GetDigitalCredentialOption(requestJson = openId4vpRequest)
        val request = GetCredentialRequest(listOf(getDigitalCredentialOption))

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@MainActivity, request)
                val credential = result.credential
                if (credential is DigitalCredential) {
                    val responseJsonString = credential.credentialJson
                    viewModel.loginWithVerifiedEmail(responseJsonString, nonce) { success, error ->
                         if (success) {
                             Timber.i("Verified Login Success")
                         } else {
                             Timber.e("Verification failed: $error")
                         }
                    }
                } else {
                    Timber.e("Unexpected credential type")
                }
            } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                Timber.e(e, "No verified credentials found on device.")
            } catch (e: GetCredentialException) {
                Timber.e(e, "Verification failed")
            } catch (e: Exception) {
                Timber.e(e, "Error during verified email login")
            }
        }
    }
}

