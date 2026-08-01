package com.musically.studio.ui.screens.onboarding

import com.musically.studio.ui.*

import androidx.compose.foundation.clickable
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayInputScreen(
    viewModel: com.musically.studio.ui.MainViewModel,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var dateMillis by remember { mutableStateOf<Long?>(null) }
    val dateLabel = if (dateMillis != null) {
        SimpleDateFormat("dd MMMM yyyy", Locale.US).format(Date(dateMillis!!))
    } else {
        viewModel.regBirthday
    }

    MaveStepLayout(
        title = "Create account",
        onBackClick = onBackClick,
        bottomAction = {
            MaveButton(
                text = "Next",
                onClick = { 
                    viewModel.regBirthday = dateLabel
                    onNextClick() 
                },
                enabled = dateLabel.isNotBlank(),
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
                text = "What's your date of birth?",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box {
                MaveTextField(
                    value = dateLabel,
                    onValueChange = { viewModel.regBirthday = it; dateMillis = null },
                    label = "YYYY-MM-DD",
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
