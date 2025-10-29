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
import androidx.navigation.compose.rememberNavController
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
                    navController.navigate(Screen.LOGIN) {
                        // Xóa SplashScreen khỏi back stack
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                },
                navigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        // Xóa SplashScreen khỏi back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.LOGIN) {
            LoginScreen(
                navigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                navigateToRegister = {
                    navController.navigate(Screen.REGISTER)
                }
            )
        }

        composable(Screen.REGISTER) {
            RegisterScreen(
                navigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        // Xóa toàn bộ back stack xác thực
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                },
                navigateBack = {
                    navController.popBackStack() // Quay lại màn hình trước đó (LoginScreen)
                }
            )
        }

        composable(Screen.DASHBOARD) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "DASHBOARD SCREEN")
            }        }
    }
}