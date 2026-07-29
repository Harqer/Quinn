package com.musically.studio.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.musically.studio.ui.MainViewModel
import timber.log.Timber
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: return
    val clientIdResId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    val webClientId = if (clientIdResId != 0) stringResource(id = clientIdResId) else ""

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (webClientId.isNotEmpty()) {
                    coroutineScope.launch {
                        try {
                            val credentialManager = CredentialManager.create(context)
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(webClientId)
                                .setAutoSelectEnabled(true)
                                .build()
                            
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()
                            
                            val result = credentialManager.getCredential(request = request, context = context)
                            val credential = result.credential
                            
                            if (credential is CustomCredential && 
                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                viewModel.loginWithGoogle(googleIdTokenCredential.idToken) { success, _ ->
                                    if (success) {
                                        onAuthSuccess()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Google Sign-In failed")
                        }
                    }
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
