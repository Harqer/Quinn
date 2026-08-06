package com.musically.studio.ui.components.molecules

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.loginWithApple
import com.musically.studio.ui.loginWithGoogle
import com.musically.studio.ui.theme.MaveStyles
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun SocialLoginButtons(viewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    val serverClientId = if (resId != 0) androidx.compose.ui.res.stringResource(resId) else ""

    if (errorMessage != null) {
        Text(
            text = errorMessage ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    MaveButton(
        text = "Sign in with Google",
        enabled = !isLoading,
        onClick = {
            coroutineScope.launch {
                try {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(serverClientId)
                        .setAutoSelectEnabled(true)
                        .build()
                        
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()
                        
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context as Activity
                    )
                    
                    val credential = result.credential
                    if (credential is androidx.credentials.CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        
                        viewModel.loginWithGoogle(idToken, null) { success, error ->
                            if (success) {
                                viewModel.clearNavigation()
                                viewModel.navigateTo(com.musically.studio.ui.navigation.Route.Home)
                            } else {
                                errorMessage = error ?: "Unknown error"
                            }
                        }
                    } else {
                        errorMessage = "Unexpected credential type."
                    }
                } catch (e: GetCredentialException) {
                    Timber.e(e, "GetCredentialException")
                    errorMessage = e.message ?: "GetCredentialException"
                } catch (e: Exception) {
                    Timber.e(e, "Google Sign-In failed")
                    errorMessage = e.message ?: "Google Sign-In failed"
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        style = MaveStyles.outlinedButton
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    MaveButton(
        text = "Sign in with Apple",
        enabled = !isLoading,
        onClick = {
            viewModel.loginWithApple(context as Activity) { success, error ->
                if (success) {
                    viewModel.clearNavigation()
                    viewModel.navigateTo(com.musically.studio.ui.navigation.Route.Home)
                } else {
                    errorMessage = error ?: "Unknown error"
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        style = MaveStyles.outlinedButton
    )
}
