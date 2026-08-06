package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.completeRegistration
import com.musically.studio.ui.loginWithEmail
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun SignInForm(viewModel: MainViewModel) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

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
}
