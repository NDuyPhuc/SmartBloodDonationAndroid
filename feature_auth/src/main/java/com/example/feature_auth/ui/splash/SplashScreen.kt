// feature_auth/src/main/java/com/smartblood/auth/ui/splash/SplashScreen.kt

package com.smartblood.auth.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToDashboard: () -> Unit
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    // LaunchedEffect sẽ được kích hoạt khi `isAuthenticated` thay đổi giá trị từ null
    LaunchedEffect(isAuthenticated) {
        when (isAuthenticated) {
            true -> navigateToDashboard()
            false -> navigateToLogin()
            null -> { /* Do nothing, wait for the check to complete */ }
        }
    }

    // Giao diện đơn giản của Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Smart Blood Donation",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}