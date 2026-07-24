package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.theme.MaveStyles
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun WelcomeScreen(
    viewModel: MainViewModel,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val hasAcceptedPrivacyPolicy by viewModel.hasAcceptedPrivacyPolicy.collectAsState()

    if (!hasAcceptedPrivacyPolicy) {
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss without explicitly accepting or denying */ },
            title = {
                Text(
                    text = "Privacy & Data Usage",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Mave uses your camera in the background during active sessions to capture visual context for generating AI music. The camera frames are sent to our servers for processing.\n\nWe also collect crash reports to improve app stability.\n\nBy continuing, you agree to these data practices.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.acceptPrivacyPolicy() }
                ) {
                    Text("I Agree", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                val activity = androidx.compose.ui.platform.LocalContext.current as? android.app.Activity
                TextButton(onClick = { activity?.finish() }) {
                    Text("Decline", color = MaterialTheme.colorScheme.error)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }

    Scaffold(containerColor = Color(0xFF121212)) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDkStbOBQ4sccmT9egT0jR8eujGoi4_MYyolBqw03Dwk6iIfxEc3a1iulaO4Jv9ApLoHaAhSBU9UnZKndnJAAHN0MKm5ywQhmRYX6K1IQYpHvg8_oXSA3-para9CAQjJy3_CEJs63DssaVOqzHnm2GmSeR-Kx8LqC_SIn4n_d_6LQY5b4FE5NgsLXmVR13UJ6z037OJ1nScbCWUcMuI3ySfq5qaciVP4h7gRIh-Z__r72VqTM51QFcyGDvI1MCPWL7LduI",
            contentDescription = "Background Collage",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.8f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF121212), Color(0xFF121212)),
                        startY = 300f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            com.musically.studio.ui.components.atoms.MaveLogo(size = 220)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Millions of Vibes.\nOrchestrated by Mave.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            MaveButton(
                text = "Sign up free",
                onClick = onSignUpClick,
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.primaryButton
            )
            Spacer(modifier = Modifier.height(12.dp))
            MaveButton(
                text = "Continue with Google",
                onClick = { viewModel.triggerGoogleSignIn() },
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            Spacer(modifier = Modifier.height(12.dp))
            MaveButton(
                text = "Continue with Facebook",
                onClick = { viewModel.triggerFacebookSignIn() },
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            Spacer(modifier = Modifier.height(12.dp))
            MaveButton(
                text = "Continue with Apple",
                onClick = { viewModel.triggerAppleSignIn() },
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onLoginClick) {
                Text("Log in", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
}

