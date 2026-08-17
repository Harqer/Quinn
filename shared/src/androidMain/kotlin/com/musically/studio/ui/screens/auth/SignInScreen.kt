/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for SignInScreen.kt
 */

package com.musically.studio.ui.screens.auth
import androidx.compose.material3.MaterialTheme

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveLogo
import com.musically.studio.ui.components.molecules.SocialLoginButtons
import com.musically.studio.ui.components.organisms.SignInForm
import timber.log.Timber

@android.annotation.SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun SignInScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(androidx.compose.ui.graphics.Color.Transparent, com.musically.studio.ui.theme.MaveBackground, com.musically.studio.ui.theme.MaveBackground),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MaveLogo(size = 120)
                Spacer(modifier = Modifier.height(24.dp))
                
                SignInForm(viewModel)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.5f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                SocialLoginButtons(viewModel)
            
            val isDebug = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
            if (isDebug) {
                var debugToken by remember { mutableStateOf("Loading token...") }
                LaunchedEffect(Unit) {
                    try {
                        val prefsDir = java.io.File(context.applicationInfo.dataDir, "shared_prefs")
                        val prefsFile = prefsDir.listFiles()?.firstOrNull { it.name.contains("appcheck.debug") }
                        if (prefsFile != null) {
                            val prefs = context.getSharedPreferences(prefsFile.nameWithoutExtension, android.content.Context.MODE_PRIVATE)
                            // Search all keys for a 36 character UUID string
                            val token = prefs.all.values.firstOrNull { it is String && it.length == 36 } as? String
                            debugToken = token ?: "Not found"
                        } else {
                            debugToken = "Prefs file missing"
                        }
                    } catch(e: Exception) {
                        debugToken = "Error"
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(
                        text = "Debug Token: $debugToken",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        }
    }
}
