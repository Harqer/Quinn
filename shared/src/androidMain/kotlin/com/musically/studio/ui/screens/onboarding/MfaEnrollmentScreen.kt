/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for MfaEnrollmentScreen.kt
 */

package com.musically.studio.ui.screens.onboarding

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.enrollMfaPhoneNumber
import com.musically.studio.ui.verifyMfaCodeAndEnroll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MfaEnrollmentScreen(
    viewModel: MainViewModel,
    onEnrolled: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalActivity.current ?: return
    var phoneNumber by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Your Account") },
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
            if (verificationId == null) {
                Text(
                    text = "Add Phone Number for 2FA",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number (e.g. +1234567890)") },
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
                        isSending = true
                        error = null
                        viewModel.enrollMfaPhoneNumber(phoneNumber, context, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                // Handled automatically on some devices
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
                    enabled = !isSending && phoneNumber.isNotBlank()
                ) {
                    Text(if (isSending) "Sending..." else "Send Code")
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
                        viewModel.verifyMfaCodeAndEnroll(verificationId!!, code) { success, msg ->
                            isVerifying = false
                            if (success) {
                                onEnrolled()
                            } else {
                                error = msg
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isVerifying && code.length == 6
                ) {
                    Text(if (isVerifying) "Verifying..." else "Verify and Enable 2FA")
                }
                
                TextButton(onClick = { verificationId = null }) {
                    Text("Change Phone Number", color = Color.Gray)
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = error ?: "", color = Color.Red)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = onBack) {
                Text("Cancel", color = Color.White)
            }
        }
    }
}
