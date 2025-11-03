//app/src/main/java/com/smartblood/donation/navigation/AppNavHost.kt
package com.smartblood.donation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartblood.auth.navigation.authGraph
import com.example.feature_auth.ui.splash.SplashScreen
import com.smartblood.donation.ui.MainScreen

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
                    // Điều hướng đến đồ thị xác thực
                    navController.navigate(Graph.AUTHENTICATION) {
                        // Xóa SplashScreen khỏi back stack
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                },
                navigateToDashboard = {
                    // Điều hướng đến đồ thị chính
                    navController.navigate(Graph.MAIN) {
                        // Xóa SplashScreen khỏi back stack
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }
            )
        }


        // --- ĐÂY LÀ NƠI KẾT NỐI ---
        authGraph(navController)

        composable(Graph.MAIN) {
            MainScreen()
        }
    }
}