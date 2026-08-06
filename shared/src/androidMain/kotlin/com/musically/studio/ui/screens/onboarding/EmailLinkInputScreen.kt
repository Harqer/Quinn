package com.musically.studio.ui.screens.onboarding
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.atoms.MaveTextField
import com.musically.studio.ui.components.organisms.MaveStepLayout
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.sendEmailLink

@Composable
fun EmailLinkInputScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf(viewModel.regEmail) }
    var isSending by remember { mutableStateOf(false) }
    var hasSent by remember { mutableStateOf(false) }
    val isValid = email.contains("@") && email.contains(".")

    MaveStepLayout(
        title = "Sign in / Sign up with Email",
        onBackClick = onBackClick,
        bottomAction = {
            if (!hasSent) {
                MaveButton(
                    text = if (isSending) "Sending..." else "Send Magic Link",
                    onClick = { 
                        if (!isSending) {
                            isSending = true
                            viewModel.regEmail = email
                            viewModel.sendEmailLink(email) { success, _ ->
                                isSending = false
                                if (success) {
                                    hasSent = true
                                }
                            }
                        }
                    },
                    enabled = isValid && !isSending,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                MaveButton(
                    text = "Check your email",
                    onClick = { },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "What's your email?",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!hasSent) {
                MaveTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = ""
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "We will send a magic link to this email to sign you in passwordlessly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "A sign-in link has been sent to $email. Please check your inbox and click the link to continue.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
