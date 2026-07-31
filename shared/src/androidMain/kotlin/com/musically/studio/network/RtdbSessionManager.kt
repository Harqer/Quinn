package com.musically.studio.network

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class RtdbSessionManager @Inject constructor(
    private val database: FirebaseDatabase
) {
    
    fun observeSessionState(uid: String): Flow<Map<String, Any>?> = callbackFlow {
        val ref = database.getReference("sessions").child(uid).child("state")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val value = snapshot.value as? Map<String, Any>
                trySend(value)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Failed to observe RTDB session state for $uid")
                close(error.toException())
            }
        }
        
        ref.addValueEventListener(listener)
        
        awaitClose {
            ref.removeEventListener(listener)
        }
    }
}
