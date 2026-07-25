package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.atoms.MaveLogo
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun AuthOptionsScreen(
    onEmailClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            IconButton(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            MaveLogo(size = 80)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Sign up to start listening",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            MaveButton(
                text = "Continue with Verified Email",
                onClick = onEmailClick,
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.primaryButton
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MaveButton(
                text = "Continue with Google",
                onClick = onGoogleClick,
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MaveButton(
                text = "Continue with Apple",
                onClick = onAppleClick,
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            TextButton(onClick = onLoginClick) {
                Text(
                    text = "Log in",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
