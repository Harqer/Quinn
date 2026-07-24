package com.musically.studio.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.musically.studio.ui.MainViewModel
import timber.log.Timber
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.res.stringResource

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: return
    val clientIdResId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    val webClientId = if (clientIdResId != 0) stringResource(id = clientIdResId) else ""

    val googleSignInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->
                    viewModel.loginWithGoogle(token) { success, _ ->
                        if (success) {
                            onAuthSuccess()
                        }
                    }
                }
            } catch (e: ApiException) {
                Timber.e(e, "Google Sign-In failed")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (webClientId.isNotEmpty()) {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(webClientId)
                        .requestEmail()
                        .build()
                    val client = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(client.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Sign In with Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val provider = OAuthProvider.newBuilder("apple.com")
                FirebaseAuth.getInstance().startActivityForSignInWithProvider(activity, provider.build())
                    .addOnSuccessListener {
                        viewModel.startRtdbSync()
                        onAuthSuccess()
                    }
                    .addOnFailureListener { e ->
                        Timber.e(e, "Apple Sign-In failed")
                    }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Sign In with Apple")
        }
    }
}
