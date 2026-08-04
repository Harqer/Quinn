package com.musically.studio.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.theme.MaveStyles
import com.musically.studio.ui.loginWithEmail
import com.musically.studio.ui.completeRegistration
import com.musically.studio.ui.loginWithGoogle
import com.musically.studio.ui.loginWithApple
import com.musically.studio.ui.components.atoms.MaveLogo
import kotlinx.coroutines.launch
import timber.log.Timber

@android.annotation.SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun SignInScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val credentialManager = remember { CredentialManager.create(context) }
    
    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(androidx.compose.ui.graphics.Color.Transparent, com.musically.studio.ui.theme.MaveBackground, com.musically.studio.ui.theme.MaveBackground),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MaveLogo(size = 120)
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = if (isSignUpMode) "Create Account" else "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (isSignUpMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

            MaveButton(
                text = if (isSignUpMode) "Sign Up" else "Sign In",
                enabled = !isLoading,
                onClick = {
                    errorMessage = null
                    if (isSignUpMode) {
                        viewModel.regEmail = email
                        viewModel.regPassword = password
                        viewModel.regName = name
                        viewModel.completeRegistration { success, error ->
                            if (success) {
                                viewModel.clearNavigation()
                                viewModel.navigateTo(com.musically.studio.ui.navigation.Route.Home)
                            } else {
                                errorMessage = error
                            }
                        }
                    } else {
                        viewModel.loginWithEmail(email, password) { success, error ->
                            if (success) {
                                viewModel.clearNavigation()
                                viewModel.navigateTo(com.musically.studio.ui.navigation.Route.Home)
                            } else {
                                errorMessage = error
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.primaryButton
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { 
                isSignUpMode = !isSignUpMode 
                errorMessage = null
            }) {
                Text(
                    text = if (isSignUpMode) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            HorizontalDivider(modifier = Modifier.fillMaxWidth(0.5f), color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            MaveButton(
                text = "Sign in with Google",
                enabled = !isLoading,
                onClick = {
                    coroutineScope.launch {
                        try {
                            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                            val serverClientId = if (resId != 0) context.getString(resId) else ""
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
                                        errorMessage = error
                                    }
                                }
                            } else {
                                errorMessage = "Unexpected credential type."
                            }
                        } catch (e: GetCredentialException) {
                            Timber.e(e, "GetCredentialException")
                            errorMessage = e.message
                        } catch (e: Exception) {
                            Timber.e(e, "Google Sign-In failed")
                            errorMessage = e.message
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
                            errorMessage = error
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            
            val isDebug = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
            if (isDebug) {
                var debugToken by remember { mutableStateOf("Loading token...") }
                LaunchedEffect(Unit) {
                    try {
                        val prefsDir = java.io.File(context.applicationInfo.dataDir, "shared_prefs")
                        val prefsFile = prefsDir.listFiles()?.firstOrNull { it.name.contains("appcheck.debug") }
                        if (prefsFile != null) {
                            val prefs = context.getSharedPreferences(prefsFile.nameWithoutExtension, android.content.Context.MODE_PRIVATE)
                            // Search all keys for a 36 character UUID string
                            val token = prefs.all.values.firstOrNull { it is String && it.length == 36 } as? String
                            debugToken = token ?: "Not found"
                        } else {
                            debugToken = "Prefs file missing"
                        }
                    } catch(e: Exception) {
                        debugToken = "Error"
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(
                        text = "Debug Token: $debugToken",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        }
    }
}
