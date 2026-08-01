package com.musically.studio.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToPremium: () -> Unit = {},
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val paymentHistory by viewModel.paymentHistory.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.stripeUrl.collectLatest { url ->
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        }
    }

    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.musically.studio.ui.theme.MaveBackground.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Account Section
            item {
                Text("Account", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                val email = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "Not logged in"
                Text(text = "Email: $email", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }

            // Subscription Section
            item {
                HorizontalDivider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = {
                        Text(
                            "Mave Premium",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    supportingContent = {
                        Text(
                            text = if (isPremium) "Premium — active" else "Upgrade to unlock full AI creation",
                            color = if (isPremium) com.musically.studio.ui.theme.MaveGreenLight else Color.LightGray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    trailingContent = {
                        TextButton(onClick = onNavigateToPremium) {
                            Text(
                                text = if (isPremium) "Manage" else "View Plans",
                                color = com.musically.studio.ui.theme.MaveGreenLight
                            )
                        }
                    },
                    modifier = Modifier.clickable { onNavigateToPremium() }
                )
            }

            // Preferences Section
            item {
                HorizontalDivider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Preferences", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            }
            
            item {
                val isDark = userSettings?.theme == "dark" || userSettings?.theme == "system"
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Dark Theme", color = Color.White) },
                    trailingContent = {
                        Switch(
                            checked = isDark,
                            onCheckedChange = { viewModel.updateTheme(if (it) "dark" else "light") },
                            colors = SwitchDefaults.colors(checkedThumbColor = com.musically.studio.ui.theme.MaveGreenLight, checkedTrackColor = com.musically.studio.ui.theme.MaveGreenLight.copy(alpha = 0.5f))
                        )
                    }
                )
            }

            item {
                val parentalControls = userSettings?.parentalControlsEnabled ?: false
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Parental Controls", color = Color.White) },
                    supportingContent = { Text("Restrict explicit content", color = Color.LightGray) },
                    trailingContent = {
                        Switch(
                            checked = parentalControls,
                            onCheckedChange = { viewModel.updateParentalControls(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = com.musically.studio.ui.theme.MaveGreenLight, checkedTrackColor = com.musically.studio.ui.theme.MaveGreenLight.copy(alpha = 0.5f))
                        )
                    }
                )
            }

            // Payment History
            if (paymentHistory.isNotEmpty()) {
                item {
                    HorizontalDivider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Payment History", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
                
                items(paymentHistory) { payment ->
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(payment.status.replaceFirstChar { it.uppercase() }, color = Color.White) },
                        supportingContent = { 
                            val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
                            val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", locale)
                            Text(dateFormat.format(java.util.Date(payment.createdAt.seconds * 1000)), color = Color.LightGray) 
                        },
                        trailingContent = { Text("$${payment.amount}", color = Color.White, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.signOut() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f), contentColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log out")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
