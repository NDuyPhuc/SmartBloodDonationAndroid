//app/src/main/java/com/smartblood/donation/navigation/AppNavHost.kt
package com.smartblood.donation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartblood.auth.ui.splash.SplashScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.SPLASH
    ) {
        composable(Screen.SPLASH) {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(Screen.LOGIN) {
                        // Xóa SplashScreen khỏi back stack
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                },
                navigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        // Xóa SplashScreen khỏi back stack
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Tạm thời tạo các màn hình giả để điều hướng đến
        composable(Screen.LOGIN) {
            // TODO: Thay thế bằng LoginScreen thật
        }

        composable(Screen.DASHBOARD) {
            // TODO: Thay thế bằng DashboardScreen thật
        }
    }
}