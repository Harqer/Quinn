package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
fun PasswordInputScreen(
    viewModel: com.musically.studio.ui.MainViewModel,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var password by remember { mutableStateOf(viewModel.regPassword) }
    var isVisible by remember { mutableStateOf(false) }
    val isValid = password.length >= 10

    MaveStepLayout(
        title = "Create account",
        onBackClick = onBackClick,
        bottomAction = {
            MaveButton(
                text = "Next",
                onClick = { 
                    viewModel.regPassword = password
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
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Create a password",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MaveTextField(
                value = password,
                onValueChange = { password = it },
                label = "",
                isPassword = !isVisible,
                trailingIcon = {
                    IconButton(onClick = { isVisible = !isVisible }) {
                        Icon(
                            imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Visibility",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Use at least 10 characters.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
