package com.musically.studio.ui.screens.onboarding

import com.musically.studio.ui.*

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

@Composable
fun EmailInputScreen(
    viewModel: com.musically.studio.ui.MainViewModel,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf(viewModel.regEmail) }
    val isValid = email.contains("@") && email.contains(".")

    MaveStepLayout(
        title = "Create account",
        onBackClick = onBackClick,
        bottomAction = {
            MaveButton(
                text = "Next",
                onClick = { 
                    viewModel.regEmail = email
                    onNextClick() 
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            )
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
            
            MaveTextField(
                value = email,
                onValueChange = { email = it },
                label = "" // Follows Mave Studio minimal input style
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "You'll need to confirm this email later.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
