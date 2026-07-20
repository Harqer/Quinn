package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    var showPicker by remember { mutableStateOf(false) }
    
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
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "What's your date of birth?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(modifier = Modifier.clickable { showPicker = true }) {
            MaveTextField(
                value = dateLabel,
                onValueChange = {},
                label = "",
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        if (showPicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        dateMillis = datePickerState.selectedDateMillis
                        showPicker = false
                    }) {
                        Text("Done", color = MaterialTheme.colorScheme.primary)
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
