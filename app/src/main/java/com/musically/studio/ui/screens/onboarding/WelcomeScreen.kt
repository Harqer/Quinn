package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.theme.MaveStyles
import androidx.compose.foundation.style.styleable

@Composable
fun WelcomeScreen(
    viewModel: MainViewModel,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDkStbOBQ4sccmT9egT0jR8eujGoi4_MYyolBqw03Dwk6iIfxEc3a1iulaO4Jv9ApLoHaAhSBU9UnZKndnJAAHN0MKm5ywQhmRYX6K1IQYpHvg8_oXSA3-para9CAQjJy3_CEJs63DssaVOqzHnm2GmSeR-Kx8LqC_SIn4n_d_6LQY5b4FE5NgsLXmVR13UJ6z037OJ1nScbCWUcMuI3ySfq5qaciVP4h7gRIh-Z__r72VqTM51QFcyGDvI1MCPWL7LduI",
            contentDescription = "Background Collage",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.8f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF121212), Color(0xFF121212)),
                        startY = 300f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_play),
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Millions of Songs.\nFree on Spotify.",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            MaveButton(
                text = "Sign up free",
                onClick = onSignUpClick,
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.primaryButton
            )
            Spacer(modifier = Modifier.height(12.dp))
            MaveButton(
                text = "Continue with Google",
                onClick = { viewModel.triggerGoogleSignIn() },
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            Spacer(modifier = Modifier.height(12.dp))
            MaveButton(
                text = "Continue with Facebook",
                onClick = { viewModel.triggerFacebookSignIn() },
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            Spacer(modifier = Modifier.height(12.dp))
            MaveButton(
                text = "Continue with Apple",
                onClick = { viewModel.triggerAppleSignIn() },
                modifier = Modifier.fillMaxWidth(),
                style = MaveStyles.outlinedButton
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onLoginClick) {
                Text("Log in", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
