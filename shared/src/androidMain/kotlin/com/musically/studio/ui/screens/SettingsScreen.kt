package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.signOut
import com.musically.studio.ui.components.atoms.MfaSettingItem
import com.musically.studio.ui.components.atoms.PremiumSettingItem
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToPremium: () -> Unit = {},
    onNavigateToMfa: () -> Unit = {},
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val paymentHistory by viewModel.paymentHistory.collectAsState()
    val view = LocalView.current

    LaunchedEffect(Unit) {
        viewModel.stripeUrl.collectLatest { url ->
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase()
            if (uri.scheme == "https" && (host?.endsWith("stripe.com") == true || host?.endsWith("lyria.studio") == true)) {
                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(context, uri)
            } else {
                Timber.w("Blocked unsafe or non-whitelisted URL launch attempt: %s", url)
            }
        }
    }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = com.musically.studio.ui.theme.MaveBackground.copy(alpha = 0.9f))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Text("Account", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                val email = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "Not logged in"
                Text(text = "Email: $email", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }

            item {
                val hasMfa = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.multiFactor?.enrolledFactors?.isNotEmpty() ?: false
                MfaSettingItem(hasMfa = hasMfa, onNavigateToMfa = onNavigateToMfa)
            }

            item {
                HorizontalDivider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                PremiumSettingItem(isPremium = isPremium, onNavigateToPremium = onNavigateToPremium)
            }

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
                            onCheckedChange = { 
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                viewModel.updateTheme(if (it) "dark" else "light") 
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = com.musically.studio.ui.theme.MaveGreenLight, checkedTrackColor = com.musically.studio.ui.theme.MaveGreenLight.copy(alpha = 0.5f))
                        )
                    }
                )
            }

            item {
                val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
                val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    viewModel.toggleNotifications(context, isGranted)
                }

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Notifications & Reminders", color = Color.White) },
                    supportingContent = { Text("Receive daily AI music mix & podcast reminders", color = Color.LightGray) },
                    trailingContent = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { enabled -> 
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                if (enabled && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                    if (hasPermission) {
                                        viewModel.toggleNotifications(context, true)
                                    } else {
                                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    viewModel.toggleNotifications(context, enabled)
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = com.musically.studio.ui.theme.MaveGreenLight, checkedTrackColor = com.musically.studio.ui.theme.MaveGreenLight.copy(alpha = 0.5f))
                        )
                    }
                )
            }

            item {
                val appsDevicesEnabled by viewModel.appsDevicesEnabled.collectAsState()
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Apps & Devices", color = Color.White) },
                    supportingContent = { Text("Allow other apps and devices to connect", color = Color.LightGray) },
                    trailingContent = {
                        Switch(
                            checked = appsDevicesEnabled,
                            onCheckedChange = { 
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                viewModel.toggleAppsDevices(it) 
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = com.musically.studio.ui.theme.MaveGreenLight, checkedTrackColor = com.musically.studio.ui.theme.MaveGreenLight.copy(alpha = 0.5f))
                        )
                    }
                )
            }

            item {
                val offlineMode by viewModel.isOfflineMode.collectAsState()
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Offline Mode", color = Color.White) },
                    supportingContent = { Text("Only play downloaded tracks", color = Color.LightGray) },
                    trailingContent = {
                        Switch(
                            checked = offlineMode,
                            onCheckedChange = { 
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                viewModel.toggleOfflineMode(it) 
                            },
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
                            onCheckedChange = { 
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                viewModel.updateParentalControls(it) 
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = com.musically.studio.ui.theme.MaveGreenLight, checkedTrackColor = com.musically.studio.ui.theme.MaveGreenLight.copy(alpha = 0.5f))
                        )
                    }
                )
            }

            if (paymentHistory.isNotEmpty()) {
                item {
                    HorizontalDivider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Payment History", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
                
                items(paymentHistory, key = { it.createdAt.seconds }) { payment ->
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
                TextButton(
                    onClick = { com.musically.studio.ui.utils.executeDebounced { viewModel.signOut() } },
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
                ) {
                    Text(
                        text = "Log out",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.error
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
