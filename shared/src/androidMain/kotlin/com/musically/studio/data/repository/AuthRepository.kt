package com.musically.studio.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    fun getUserId(): String? {
        return auth.uid
    }
    
    suspend fun signOut() {
        auth.signOut()
    }
}
