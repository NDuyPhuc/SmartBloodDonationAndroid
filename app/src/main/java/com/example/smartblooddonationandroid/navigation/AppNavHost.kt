//app/src/main/java/com/smartblood/donation/navigation/AppNavHost.kt
package com.smartblood.donation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import com.smartblood.auth.ui.register.RegisterScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.smartblood.auth.navigation.authGraph
import com.smartblood.auth.ui.login.LoginScreen
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

        navigation(
            route = Graph.MAIN,
            startDestination = Screen.DASHBOARD
        ) {
            composable(Screen.DASHBOARD) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "DASHBOARD SCREEN - WELCOME!")
                }
            }
        }
    }
}