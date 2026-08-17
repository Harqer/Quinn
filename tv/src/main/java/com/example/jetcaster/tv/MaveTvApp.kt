package com.example.jetcaster.tv

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import com.musically.studio.ui.MainViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme

@Composable
fun MaveTvApp(viewModel: MainViewModel = hiltViewModel()) {
    val isConnected by viewModel.geminiLiveManager.connectionState.collectAsState(initial = false)
    val transcript by viewModel.geminiLiveManager.transcripts.collectAsState(initial = "")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = if (isConnected) "Mave TV is listening..." else "Tap to Speak to Mave",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (isConnected) {
                        viewModel.geminiLiveManager.disconnect()
                    } else {
                        viewModel.geminiLiveManager.connect()
                    }
                },
                modifier = Modifier.size(120.dp),
                shape = ButtonDefaults.shape(shape = CircleShape),
                colors = ButtonDefaults.colors(
                    containerColor = if (isConnected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isConnected) "Stop" else "Mic",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = transcript,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 4
            )
        }
    }
}
