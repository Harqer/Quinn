package com.musically.studio.ui

import androidx.lifecycle.viewModelScope
import com.musically.studio.network.MaveTrack
import kotlinx.coroutines.launch
import timber.log.Timber

fun MainViewModel.likeTrack(trackId: String) {}
fun MainViewModel.unlikeTrack(trackId: String) {}
fun MainViewModel.addTrackToPlaylist(trackId: String, playlistId: String) {}
fun MainViewModel.bookmarkTrack(trackId: String) {}
fun MainViewModel.shareTrack(trackId: String, callback: ((String?) -> Unit)? = null) {}
fun MainViewModel.recordPlay(trackId: String) {}
