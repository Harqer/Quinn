import os
import re

file_path = "MainViewModel.kt"

auth_funcs = {"isUserLoggedIn", "getUserId", "getUserPhotoUrl", "getUserDisplayName", "loginWithEmail", "guestLogin", "triggerGoogleSignIn", "triggerAppleSignIn", "triggerVerifiedEmailSignIn", "loginWithVerifiedEmail", "loginWithGoogle", "completeRegistration", "saveArtistPreferences", "signOut", "deleteAccount", "verifyEmail", "parseSdJwtEmailAndName", "generateSecureRandomNonce", "resetAccountDeletionState"}
player_funcs = {"togglePlayPause", "setVolume", "setPlayingState", "stopPlayback", "playQueue", "skipNext", "skipPrevious", "seekTo", "toggleShuffle", "toggleRepeat", "toggleHapticFeedback", "requestCoverArt", "requestMusicVideo", "playTrack", "getTrack"}
live_funcs = {"startLiveSession", "clearLiveSessionHistory", "stopLiveSession", "connectToMave", "generateCoverMedia", "sendTextCommand", "generatePodcast", "recordVoice", "startRecording", "stopRecording", "applySteering", "generateMusicPrompts", "sendFrame", "onGalleryImageSelected", "clearPendingGalleryImage"}
catalog_funcs = {"fetchCatalog", "fetchCategories", "fetchPlaylists", "fetchAlbums", "fetchPodcasts", "fetchAudiobooks", "fetchLikedTracks", "fetchUserTracks", "fetchCommunityTracks", "fetchVibesByUserId", "viewArtist", "clearCatalogError"}
wearable_funcs = {"setupWearableFrameStreaming", "toggleWearableFrameStreaming", "setupWearableCollector", "setWearableConnected"}
interaction_funcs = {"bookmarkTrack", "likeTrack", "generateLyrics", "shareTrack", "addToPlaylist", "saveTrackToLibrary", "connectMusicAccount"}

all_targets = {
    "Auth": auth_funcs,
    "Player": player_funcs,
    "LiveSession": live_funcs,
    "Catalog": catalog_funcs,
    "Wearable": wearable_funcs,
    "Interaction": interaction_funcs
}

with open(file_path, "r") as f:
    lines = f.readlines()

new_lines = []
extracted = {k: [] for k in all_targets.keys()}

i = 0
while i < len(lines):
    line = lines[i]
    # Match function definition: (internal|public|) (suspend )?fun functionName(
    match = re.search(r'^(?:    )?(?:internal\s+|public\s+|override\s+|suspend\s+)*fun\s+([a-zA-Z0-9_]+)\s*\(', line.strip())
    if match:
        func_name = match.group(1)
        target_file = None
        for k, v in all_targets.items():
            if func_name in v:
                target_file = k
                break
        
        if target_file:
            # We need to extract the entire function block
            func_block = [line]
            open_braces = line.count('{') - line.count('}')
            # It might be a single line expression function: fun x() = y
            is_expression = "=" in line and "{" not in line
            
            i += 1
            while i < len(lines):
                if is_expression:
                    if lines[i].strip() == "" or re.search(r'^(?:    )?(?:internal\s+|public\s+|override\s+|suspend\s+)*fun\s+', lines[i]):
                        break # Done with expression function
                else:
                    if open_braces <= 0 and func_block:
                        if "{" in func_block[0]:
                            break
                    open_braces += lines[i].count('{') - lines[i].count('}')
                func_block.append(lines[i])
                i += 1
                if not is_expression and open_braces <= 0:
                    break
            
            # Post-process func_block to convert it to extension function
            # internal fun funcName( -> internal fun MainViewModel.funcName(
            new_func_def = re.sub(r'fun\s+([a-zA-Z0-9_]+)\s*\(', r'fun MainViewModel.\1(', func_block[0])
            func_block[0] = new_func_def
            
            extracted[target_file].extend(func_block)
            extracted[target_file].append("\n")
            continue
    new_lines.append(line)
    i += 1

with open(file_path, "w") as f:
    f.writelines(new_lines)

imports = """package com.musically.studio.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.musically.studio.network.*
import com.musically.studio.data.repository.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import android.util.Base64
"""

for k, funcs in extracted.items():
    if not funcs: continue
    with open(f"MainViewModel+{k}.kt", "w") as f:
        f.write(imports + "\n")
        f.writelines(funcs)
        
print("Extraction complete!")
