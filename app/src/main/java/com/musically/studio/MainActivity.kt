package com.musically.studio

import android.Manifest
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
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.musically.studio.ui.AuthSideEffect
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.MiniPlayer
import com.musically.studio.ui.navigation.*
import com.musically.studio.ui.screens.*
import com.musically.studio.ui.screens.onboarding.*
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

    private var permissionsGranted = mutableStateOf(false)
    private lateinit var mainViewModel: MainViewModel
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (cameraGranted && audioGranted) {
            permissionsGranted.value = true
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
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
            Toast.makeText(this, "Something went wrong during sign-in. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        FirebaseApp.initializeApp(this)
        
        com.musically.studio.engage.EngageBroadcastReceiver.register(this)
        
        setContent {
            mainViewModel = viewModel()
            
            LaunchedEffect(Unit) {
                mainViewModel.authSideEffect.collectLatest { effect ->
                    when (effect) {
                        AuthSideEffect.LaunchGoogleSignIn -> launchGoogleSignIn()
                        AuthSideEffect.LaunchAppleSignIn -> launchAppleSignIn(mainViewModel)
                        AuthSideEffect.LaunchVerifiedEmail -> launchVerifiedEmail(mainViewModel)
                        AuthSideEffect.LaunchFacebookSignIn -> { /* Facebook sign-in */ }
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
            .requestIdToken(getString(R.string.google_api_key)) 
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
                Toast.makeText(this, "Apple Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.POST_NOTIFICATIONS
        )
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
                             Toast.makeText(this@MainActivity, "Verified Login Success", Toast.LENGTH_SHORT).show()
                         } else {
                             Toast.makeText(this@MainActivity, error ?: "Verification failed", Toast.LENGTH_SHORT).show()
                         }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Unexpected credential type", Toast.LENGTH_SHORT).show()
                }
            } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                Toast.makeText(this@MainActivity, "No verified credentials found on device.", Toast.LENGTH_SHORT).show()
            } catch (e: GetCredentialException) {
                Toast.makeText(this@MainActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

