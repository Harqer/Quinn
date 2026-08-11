@file:kotlin.OptIn(androidx.credentials.ExperimentalDigitalCredentialApi::class)
package com.musically.studio.ui

import com.musically.studio.ui.navigation.Route

import kotlinx.coroutines.tasks.await

import com.musically.studio.dataconnect.*

import com.google.firebase.auth.GoogleAuthProvider

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

    fun MainViewModel.isUserLoggedIn(): Boolean = auth.currentUser != null

    fun MainViewModel.getUserId(): String = auth.currentUser?.uid ?: ""

    fun MainViewModel.loginWithEmail(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            callback(false, "Email and password must not be empty.")
            return
        }
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                com.musically.studio.ui.RecaptchaProvider.client?.execute(com.google.android.recaptcha.RecaptchaAction.LOGIN)
                    ?.onSuccess { token ->
                        Timber.d("reCAPTCHA execute success for LOGIN")
                    }
                    ?.onFailure { e ->
                        Timber.e(e, "reCAPTCHA execute failed")
                    }
            } catch (e: Exception) {
                Timber.e(e, "reCAPTCHA execution error")
            }
            
            withContext(Dispatchers.Main) {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        _isLoading.value = false
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                com.musically.studio.logging.CrashlyticsTree.setUserId(uid)
                            }
                            startRtdbSync()
                            callback(true, null)
                        } else {
                            val exception = task.exception
                            if (exception is com.google.firebase.auth.FirebaseAuthMultiFactorException) {
                                mfaResolver = exception.resolver
                                viewModelScope.launch {
                                    _authSideEffect.emit(AuthSideEffect.LaunchMfaVerification(exception.resolver))
                                }
                                callback(false, null)
                            } else {
                                callback(false, "Check your email and password, or sign up for a new account.")
                            }
                        }
                    }
            }
        }
    }

    fun MainViewModel.guestLogin(callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    startRtdbSync()
                    callback(true, null)
                } else {
                    callback(false, "Could not start guest session. Please check your connection.")
                }
            }
    }





    fun MainViewModel.loginWithGoogle(idToken: String, rawNonce: String?, callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        viewModelScope.launch {
                            try {
                                val email = user.email ?: "${user.uid}@noemail.com"
                                val username = email.substringBefore("@")
                                com.musically.studio.dataconnect.DefaultConnector.instance.upsertUser.execute(
                                    username = username,
                                    email = email
                                ) {
                                    this.displayName = user.displayName ?: ""
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to upsert user profile on Google login")
                            }
                            _isLoading.value = false
                            startRtdbSync()
                            callback(true, null)
                        }
                    } else {
                        _isLoading.value = false
                        startRtdbSync()
                        callback(true, null)
                    }
                } else {
                    _isLoading.value = false
                    val exception = task.exception
                    if (exception is com.google.firebase.auth.FirebaseAuthMultiFactorException) {
                        mfaResolver = exception.resolver
                        viewModelScope.launch {
                            _authSideEffect.emit(AuthSideEffect.LaunchMfaVerification(exception.resolver))
                        }
                        callback(false, null)
                    } else {
                        callback(false, "Google account connection encountered an issue.")
                    }
                }
            }
    }

    fun MainViewModel.loginWithApple(activity: Activity, callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        val provider = com.google.firebase.auth.OAuthProvider.newBuilder("apple.com").build()
        auth.startActivityForSignInWithProvider(activity, provider)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    viewModelScope.launch {
                        try {
                            val email = user.email ?: "${user.uid}@noemail.com"
                            val username = email.substringBefore("@")
                            com.musically.studio.dataconnect.DefaultConnector.instance.upsertUser.execute(
                                username = username,
                                email = email
                            ) {
                                this.displayName = user.displayName ?: ""
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to upsert user profile on Apple login")
                        }
                        _isLoading.value = false
                        startRtdbSync()
                        callback(true, null)
                    }
                } else {
                    _isLoading.value = false
                    startRtdbSync()
                    callback(true, null)
                }
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
                if (exception is com.google.firebase.auth.FirebaseAuthMultiFactorException) {
                    mfaResolver = exception.resolver
                    viewModelScope.launch {
                        _authSideEffect.emit(AuthSideEffect.LaunchMfaVerification(exception.resolver))
                    }
                    callback(false, null)
                } else {
                    callback(false, "Apple sign-in encountered an issue.")
                }
            }
    }

    fun MainViewModel.ensureUserUpserted(callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                try {
                    val email = user.email ?: "${user.uid}@noemail.com"
                    val username = email.substringBefore("@")
                    com.musically.studio.dataconnect.DefaultConnector.instance.upsertUser.execute(
                        username = username,
                        email = email
                    ) {
                        this.displayName = user.displayName ?: ""
                    }
                    callback(true)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to upsert user profile")
                    callback(false)
                }
            }
        } else {
            callback(false)
        }
    }

    fun MainViewModel.completeRegistration(callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(regEmail, regPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        viewModelScope.launch {
                            try {
                                com.musically.studio.dataconnect.DefaultConnector.instance.upsertUser.execute(
                                    username = regEmail.substringBefore("@"),
                                    email = regEmail
                                ) {
                                    this.displayName = regName
                                }
                                _isLoading.value = false
                                startRtdbSync()
                                callback(true, null)
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to save profile via Data Connect")
                                _isLoading.value = false
                                callback(false, "Could not complete your profile. Please try again.")
                            }
                        }
                    }
                } else {
                    _isLoading.value = false
                    val exception = task.exception
                    if (exception is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                        callback(false, "Account already exists. Please switch to Sign In.")
                    } else {
                        callback(false, exception?.message ?: "Registration encountered an issue.")
                    }
                }
            }
    }

    fun MainViewModel.saveArtistPreferences(artists: List<String>, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                com.musically.studio.dataconnect.DefaultConnector.instance.updateUserPreferences.execute {
                    this.favoriteArtists = artists
                }
                callback(true)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save preferences via Data Connect")
                callback(false)
            }
        }
    }

    fun MainViewModel.signOut() {
        stopLiveSession()
        rtdbSyncJob?.cancel()
        rtdbSyncJob = null
        currentRtdbUid = null
        com.musically.studio.logging.CrashlyticsTree.setUserId("")
        auth.signOut()
        Timber.i("User signed out")
        viewModelScope.launch {
            _authSideEffect.emit(AuthSideEffect.SignedOut)
        }
    }

    fun MainViewModel.deleteAccount() {
        val user = auth.currentUser
        if (user == null) {
            _accountDeletionState.value = AccountDeletionState.Error("No authenticated user found.")
            return
        }
        _accountDeletionState.value = AccountDeletionState.Loading
        viewModelScope.launch {
            try {
                // 1. Remove RTDB session data before deleting the auth account.
                val uid = user.uid
                rtdb.getReference("sessions/$uid").removeValue().await()
                Timber.i("RTDB session data cleared for uid=$uid")
                
                // 1.5. Delete remote backend account profile (Now handled via Firebase Auth triggered Cloud Function).
                val backendDeleted = true

                // 2. Disconnect live session and stop all ongoing services.
                stopLiveSession()
                rtdbSyncJob?.cancel()
                rtdbSyncJob = null

                // 3. Delete the Firebase Auth account.
                user.delete().await()
                Timber.i("Firebase Auth account deleted for uid=$uid")

                _accountDeletionState.value = AccountDeletionState.Deleted
                _authSideEffect.emit(AuthSideEffect.AccountDeleted)
            } catch (e: com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                Timber.w(e, "Re-authentication required before account deletion")
                _accountDeletionState.value = AccountDeletionState.Error(
                    "For your security, please sign in again before deleting your account."
                )
            } catch (e: Exception) {
                Timber.e(e, "Account deletion failed")
                _accountDeletionState.value = AccountDeletionState.Error(
                    "Could not delete account: ${e.message}"
                )
            }
        }
    }

    fun MainViewModel.resetAccountDeletionState() {
        _accountDeletionState.value = AccountDeletionState.Idle
    }

    fun MainViewModel.sendEmailLink(email: String, callback: (Boolean, String?) -> Unit) {
        val resId = context.resources.getIdentifier("auth_deep_link_url", "string", context.packageName)
        val url = if (resId != 0) context.getString(resId) else "https://musically-studio.firebaseapp.com/finishSignUp"
        val actionCodeSettings = com.google.firebase.auth.ActionCodeSettings.newBuilder()
            .setUrl(url)
            .setHandleCodeInApp(true)
            .setAndroidPackageName(context.packageName, true, "1")
            .build()
            
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    prefs.edit().putString("email_link_email", email).apply()
                    callback(true, null)
                } else {
                    Timber.e(task.exception, "Error sending email link")
                    callback(false, task.exception?.message ?: "Failed to send email link")
                }
            }
    }

    fun MainViewModel.handleEmailLink(link: String, callback: (Boolean, String?) -> Unit) {
        if (auth.isSignInWithEmailLink(link)) {
            val email = prefs.getString("email_link_email", null)
            if (email == null) {
                // In a production app you might ask the user for their email here if not saved
                callback(false, "Could not determine email. Please try signing in again.")
                return
            }
            
            _isLoading.value = true
            auth.signInWithEmailLink(email, link)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        prefs.edit().remove("email_link_email").apply()
                        viewModelScope.launch {
                            try {
                                com.musically.studio.dataconnect.DefaultConnector.instance.upsertUser.execute(
                                    username = email.substringBefore("@"),
                                    email = email
                                ) {
                                    this.displayName = "Verified User"
                                }
                                startRtdbSync()
                                callback(true, null)
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to save profile via Data Connect")
                                callback(false, "Authentication succeeded but profile creation failed.")
                            }
                        }
                    } else {
                        val exception = task.exception
                        if (exception is com.google.firebase.auth.FirebaseAuthMultiFactorException) {
                            prefs.edit().remove("email_link_email").apply()
                            mfaResolver = exception.resolver
                            viewModelScope.launch {
                                _authSideEffect.emit(AuthSideEffect.LaunchMfaVerification(exception.resolver))
                            }
                            callback(false, null)
                        } else {
                            Timber.e(exception, "Error signing in with email link")
                            callback(false, exception?.message ?: "Failed to sign in")
                        }
                    }
                }
        } else {
            callback(false, "Invalid email link")
        }
    }

    fun MainViewModel.enrollMfaPhoneNumber(phoneNumber: String, activity: Activity, callbacks: com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        val user = auth.currentUser
        if (user == null) {
            callbacks.onVerificationFailed(com.google.firebase.FirebaseException("No authenticated user"))
            return
        }
        user.multiFactor.session.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val session = task.result
                val options = com.google.firebase.auth.PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
                    .setMultiFactorSession(session)
                    .setCallbacks(callbacks)
                    .setActivity(activity)
                    .build()
                com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber(options)
            } else {
                callbacks.onVerificationFailed(com.google.firebase.FirebaseException("Failed to start MFA enrollment session"))
            }
        }
    }

    fun MainViewModel.verifyMfaCodeAndEnroll(verificationId: String, code: String, callback: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            callback(false, "No authenticated user")
            return
        }
        _isLoading.value = true
        val credential = com.google.firebase.auth.PhoneAuthProvider.getCredential(verificationId, code)
        val assertion = com.google.firebase.auth.PhoneMultiFactorGenerator.getAssertion(credential)
        user.multiFactor.enroll(assertion, "Phone Number")
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    Timber.e(task.exception, "Error enrolling MFA")
                    callback(false, task.exception?.message ?: "Failed to enroll")
                }
            }
    }

    fun MainViewModel.sendMfaChallengeSms(hint: com.google.firebase.auth.PhoneMultiFactorInfo, activity: Activity, callbacks: com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        val resolver = mfaResolver
        if (resolver == null) {
            callbacks.onVerificationFailed(com.google.firebase.FirebaseException("No MFA resolver found"))
            return
        }
        val options = com.google.firebase.auth.PhoneAuthOptions.newBuilder(auth)
            .setMultiFactorHint(hint)
            .setMultiFactorSession(resolver.session)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .setActivity(activity)
            .build()
        com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun MainViewModel.verifyMfaChallengeAndSignIn(verificationId: String, code: String, callback: (Boolean, String?) -> Unit) {
        val resolver = mfaResolver
        if (resolver == null) {
            callback(false, "No MFA session active")
            return
        }
        _isLoading.value = true
        val credential = com.google.firebase.auth.PhoneAuthProvider.getCredential(verificationId, code)
        val assertion = com.google.firebase.auth.PhoneMultiFactorGenerator.getAssertion(credential)
        resolver.resolveSignIn(assertion)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    mfaResolver = null
                    startRtdbSync()
                    callback(true, null)
                } else {
                    Timber.e(task.exception, "Error resolving MFA sign in")
                    callback(false, task.exception?.message ?: "Failed to verify code")
                }
            }
    }
