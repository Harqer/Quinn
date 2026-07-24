package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.organisms.MaveStepLayout

@Composable
fun GenderInputScreen(
    viewModel: com.musically.studio.ui.MainViewModel,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val genders = listOf("Female", "Male", "Non-binary", "Other", "Prefer not to say")

    MaveStepLayout(
        title = "Create account",
        onBackClick = onBackClick
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "What's your gender?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genders) { gender ->
                GenderOption(
                    label = gender,
                    onClick = { 
                        viewModel.regGender = gender
                        onNextClick() 
                    }
                )
            }
        }
    }
}

@Composable
private fun GenderOption(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
        }
    }
}
