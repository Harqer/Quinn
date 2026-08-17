/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for MfaVerificationScreen.kt
 */

package com.musically.studio.ui.screens.onboarding

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorInfo
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.sendMfaChallengeSms
import com.musically.studio.ui.verifyMfaChallengeAndSignIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MfaVerificationScreen(
    viewModel: MainViewModel,
    resolver: com.google.firebase.auth.MultiFactorResolver,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalActivity.current ?: return
    var code by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }

    // Pick the first phone hint available
    val phoneHint = resolver.hints.firstOrNull { it is PhoneMultiFactorInfo } as? PhoneMultiFactorInfo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Two-Factor Authentication") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (phoneHint == null) {
                Text(
                    text = "No phone number configured.",
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Go Back")
                }
                return@Scaffold
            }

            if (verificationId == null) {
                Text(
                    text = "Verify it's you",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "We will send an SMS to ${phoneHint.phoneNumber}",
                    color = Color.LightGray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = {
                        isSending = true
                        error = null
                        viewModel.sendMfaChallengeSms(phoneHint, context, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                // Handled automatically on some devices, skip for manual entry
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                isSending = false
                                error = e.message
                            }

                            override fun onCodeSent(
                                backendVerificationId: String,
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                isSending = false
                                verificationId = backendVerificationId
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSending
                ) {
                    Text(if (isSending) "Sending SMS..." else "Send Code")
                }
            } else {
                Text(
                    text = "Enter Verification Code",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("6-digit code") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isVerifying = true
                        error = null
                        viewModel.verifyMfaChallengeAndSignIn(verificationId!!, code) { success, msg ->
                            isVerifying = false
                            if (success) {
                                onSuccess()
                            } else {
                                error = msg
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isVerifying && code.length == 6
                ) {
                    Text(if (isVerifying) "Verifying..." else "Sign In")
                }
                
                TextButton(onClick = { verificationId = null }) {
                    Text("Resend Code", color = Color.Gray)
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = error ?: "", color = Color.Red)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = onBack) {
                Text("Cancel Sign In", color = Color.White)
            }
        }
    }
}
