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
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val paymentHistory by viewModel.paymentHistory.collectAsState()



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
                Spacer(modifier = Modifier.height(16.dp))
                Text("Lyria Premium Plans", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                
                val activity = context as? android.app.Activity
                
                // Basic Tier
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        activity?.let { viewModel.launchBillingFlow(it, "premium_basic") }
                    },
                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Basic Creator", color = com.musically.studio.ui.theme.MaveGreenLight, fontWeight = FontWeight.Bold)
                        Text("$20/mo", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("30 Songs • 60 mins Real-time • 50 Images • 10 Videos", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Pro Tier
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        activity?.let { viewModel.launchBillingFlow(it, "premium_pro") }
                    },
                    colors = CardDefaults.cardColors(containerColor = com.musically.studio.ui.theme.MaveBackground),
                    border = androidx.compose.foundation.BorderStroke(2.dp, com.musically.studio.ui.theme.MaveGreenLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("MOST POPULAR", color = Color.Black, modifier = Modifier.background(com.musically.studio.ui.theme.MaveGreenLight, shape = RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Pro Studio", color = com.musically.studio.ui.theme.MaveGreenLight, fontWeight = FontWeight.Bold)
                        Text("$50/mo", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("100 Songs • 150 mins Real-time • 200 Images • 40 Videos", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                        Text("Commercial Use • Priority Queue", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Ultra Tier
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        activity?.let { viewModel.launchBillingFlow(it, "premium_ultra") }
                    },
                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ultra Unlimited", color = com.musically.studio.ui.theme.MaveGreenLight, fontWeight = FontWeight.Bold)
                        Text("$100/mo", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Unlimited Songs • Highest Real-time • Unlimited Images/Videos", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                        Text("Commercial Use • Highest Priority", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    }
                }
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
                        supportingContent = { Text(java.util.Date(payment.createdAt.seconds * 1000).toString(), color = Color.LightGray) },
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
