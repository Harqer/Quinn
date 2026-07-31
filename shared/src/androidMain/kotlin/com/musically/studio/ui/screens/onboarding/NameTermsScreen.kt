package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.atoms.MaveTextField
import com.musically.studio.ui.components.organisms.MaveStepLayout

@Composable
fun NameTermsScreen(
    viewModel: com.musically.studio.ui.MainViewModel,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf(viewModel.regName) }
    var shareData by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    MaveStepLayout(
        title = "Create account",
        onBackClick = onBackClick,
        bottomAction = {
            MaveButton(
                text = "Create account",
                onClick = { 
                    viewModel.regName = name
                    viewModel.completeRegistration { success, error ->
                        if (success) {
                            onNextClick()
                        } else {
                            errorMessage = error ?: "Registration failed"
                        }
                    }
                },
                enabled = name.isNotBlank() && agreeTerms && !isLoading,
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
                text = "What's your name?",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MaveTextField(
                value = name,
                onValueChange = { name = it },
                label = ""
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "This appears on your Mave profile.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest, thickness = 0.5.dp)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "By tapping \"Create account\", you agree to the Mave Terms of Use.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Share my registration data with Mave's content providers for marketing purposes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = shareData,
                    onCheckedChange = { shareData = it },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
            }
    
            Spacer(modifier = Modifier.height(16.dp))
    
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable { agreeTerms = !agreeTerms }
            ) {
                Text(
                    text = "I agree to the Terms of Use and Privacy Policy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { agreeTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}
