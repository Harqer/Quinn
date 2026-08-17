/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for UserProfileScreen.kt
 */

package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.AccountDeletionState
import com.musically.studio.ui.*
import com.musically.studio.ui.components.organisms.DeleteAccountDialog
import com.musically.studio.ui.components.organisms.SignOutDialog
import com.musically.studio.ui.components.organisms.UserProfileTopBar
import com.musically.studio.ui.components.organisms.UserAccountActions
import com.musically.studio.ui.components.organisms.profileHeader
import com.musically.studio.ui.components.organisms.userSongsSection
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSignedOut: () -> Unit = onBack,
    onNavigateToAlbum: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToDevices: () -> Unit = {}
) {
    val vibes by viewModel.userVibes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val deletionState by viewModel.accountDeletionState.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    val isOwnProfile = remember(userId) { userId == viewModel.getUserId() }
    val displayName = viewModel.getUserDisplayName() ?: userSettings?.user?.displayName ?: "Studio Creator"
    val avatarUrl = viewModel.getUserPhotoUrl() ?: userSettings?.user?.avatarUrl

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showSignOutConfirmDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deletionState) {
        when (val state = deletionState) {
            is AccountDeletionState.Deleted -> {
                Timber.i("Account deleted — navigating to login")
                viewModel.resetAccountDeletionState()
                onSignedOut()
            }
            is AccountDeletionState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetAccountDeletionState()
            }
            else -> Unit
        }
    }

    LaunchedEffect(userId) {
        viewModel.fetchVibesByUserId(userId)
        viewModel.fetchUserSettings()
    }

    if (showDeleteConfirmDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteConfirmDialog = false },
            onConfirm = {
                showDeleteConfirmDialog = false
                viewModel.deleteAccount()
            }
        )
    }

    if (showSignOutConfirmDialog) {
        SignOutDialog(
            onDismiss = { showSignOutConfirmDialog = false },
            onConfirm = {
                showSignOutConfirmDialog = false
                viewModel.signOut()
                onSignedOut()
            }
        )
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UserProfileTopBar(
                isOwnProfile = isOwnProfile,
                onBack = onBack,
                onNavigateToSettings = onNavigateToSettings,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.fetchVibesByUserId(userId) },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                profileHeader(
                    avatarUrl = avatarUrl,
                    displayName = displayName,
                    userId = userId
                )

                if (isOwnProfile) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        UserAccountActions(
                            deletionState = deletionState,
                            onSignOutClick = { showSignOutConfirmDialog = true },
                            onDeleteAccountClick = { showDeleteConfirmDialog = true },
                            onNavigateToDevices = onNavigateToDevices
                        )
                    }
                }

                userSongsSection(
                    isOwnProfile = isOwnProfile,
                    isLoading = isLoading,
                    vibes = vibes,
                    onPlayTrack = { viewModel.playTrack(it) },
                    onNavigateToAlbum = onNavigateToAlbum
                )

                item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
