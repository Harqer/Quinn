package com.musically.studio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.musically.studio.R
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.atoms.MaveLogo
import com.musically.studio.ui.components.atoms.MaveTextField

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MaveLogo(size = 200)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            MaveTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MaveTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            MaveButton(
                text = if (isLoading) "Logging in..." else "Log In",
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        errorMessage = null
                        viewModel.loginWithEmail(email, password) { success, error ->
                            if (success) {
                                onLoginSuccess()
                            } else {
                                errorMessage = error ?: "Login failed"
                            }
                        }
                    }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onNavigateToSignUp) {
                Text("Don't have an account? Sign Up", color = Color.LightGray)
            }
        }
    }
}
