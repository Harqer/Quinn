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
    val hasDeclinedPrivacyPolicy by viewModel.hasDeclinedPrivacyPolicy.collectAsState()

    if (!hasAcceptedPrivacyPolicy && !hasDeclinedPrivacyPolicy) {
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss without explicitly accepting or denying */ },
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            title = {
                Text(
                    text = "Prominent Disclosure & Consent",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Mave collects and transmits camera and microphone data to enable real-time AI music generation during active sessions.\n\nMave also collects crash logs to improve app stability.\n\nTap 'I Agree' to consent to this data collection.",
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
                TextButton(onClick = { viewModel.declinePrivacyPolicy() }) {
                    Text("Decline", color = MaterialTheme.colorScheme.error)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }

    Scaffold(containerColor = com.musically.studio.ui.theme.MaveBackground) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, com.musically.studio.ui.theme.MaveBackground, com.musically.studio.ui.theme.MaveBackground),
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
            if (hasDeclinedPrivacyPolicy) {
                Text(
                    text = "Mave requires your consent to provide its core generative audio experience.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { viewModel.resetPrivacyPolicy() }) {
                    Text("Review Consent", color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            MaveButton(
                text = "Sign up",
                onClick = onSignUpClick,
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.primaryButton,
                enabled = hasAcceptedPrivacyPolicy
            )
            Spacer(modifier = Modifier.height(12.dp))
            MaveButton(
                text = "Log in",
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton,
                enabled = hasAcceptedPrivacyPolicy
            )
        }
    }
}
}

