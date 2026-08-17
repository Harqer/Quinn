/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for MainViewModel+Concerts.kt
 */

package com.musically.studio.ui

import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.musically.studio.data.Concert
import com.musically.studio.location.LocationManager
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

private val _concerts = MutableStateFlow<List<Concert>>(emptyList())
val MainViewModel.concerts: StateFlow<List<Concert>> get() = _concerts.asStateFlow()

private val _isSearchingConcerts = MutableStateFlow(false)
val MainViewModel.isSearchingConcerts: StateFlow<Boolean> get() = _isSearchingConcerts.asStateFlow()

private val _concertSearchError = MutableStateFlow<String?>(null)
val MainViewModel.concertSearchError: StateFlow<String?> get() = _concertSearchError.asStateFlow()

fun MainViewModel.fetchConcertsNearMe(query: String = "") {
    _isSearchingConcerts.value = true
    _concertSearchError.value = null
    
    viewModelScope.launch {
        try {
            val locationManager = LocationManager(context)
            val location = locationManager.getCurrentLocation()
            
            val functionArgs = mutableMapOf<String, Any>("q" to query)
            if (location != null) {
                functionArgs["lat"] = location.latitude
                functionArgs["lon"] = location.longitude
                functionArgs["range"] = "50mi"
            }
            
            val functions = Firebase.functions
            val result = functions.getHttpsCallable("searchConcerts").call(functionArgs).await()
            
            val data = result.data as? Map<String, Any>
            val events = data?.get("events") as? List<Map<String, Any>> ?: emptyList()
            
            val mappedConcerts = events.mapNotNull { event ->
                try {
                    val id = event["id"]?.toString() ?: return@mapNotNull null
                    val title = event["title"] as? String ?: return@mapNotNull null
                    val url = event["url"] as? String ?: ""
                    val datetimeLocal = event["datetime_local"] as? String ?: ""
                    val venue = event["venue"] as? Map<String, Any>
                    val venueName = venue?.get("name") as? String ?: ""
                    val locationStr = venue?.get("display_location") as? String ?: ""
                    
                    val performers = event["performers"] as? List<Map<String, Any>>
                    val imageUrl = performers?.firstOrNull()?.get("image") as? String
                    
                    Concert(
                        id = id,
                        title = title,
                        url = url,
                        datetimeLocal = datetimeLocal,
                        venueName = venueName,
                        location = locationStr,
                        imageUrl = imageUrl
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            _concerts.value = mappedConcerts
        } catch (e: Exception) {
            Timber.e(e, "Failed to search concerts in UI")
            _concertSearchError.value = e.message ?: "Failed to find concerts."
        } finally {
            _isSearchingConcerts.value = false
        }
    }
}
