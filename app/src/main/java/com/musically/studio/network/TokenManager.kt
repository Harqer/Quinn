package com.musically.studio.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import kotlinx.coroutines.tasks.await

object TokenManager {
    suspend fun getValidToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return try {
            val result: GetTokenResult = user.getIdToken(false).await()
            result.token
        } catch (e: Exception) {
            null
        }
    }
}
