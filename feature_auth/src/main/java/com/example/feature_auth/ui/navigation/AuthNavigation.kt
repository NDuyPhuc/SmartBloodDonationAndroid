package com.example.feature_auth.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.feature_auth.ui.login.LoginScreen
import com.example.feature_auth.ui.register.RegisterScreen

const val AUTH_GRAPH_ROUTE = "auth_graph"

// SỬA: Thêm tham số onLoginSuccess vào hàm này
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onLoginSuccess: () -> Unit // <--- THÊM THAM SỐ NÀY
) {
    navigation(
        startDestination = AuthScreen.Login.route,
        route = AUTH_GRAPH_ROUTE
    ) {
        composable(route = AuthScreen.Login.route) {
            LoginScreen(
                navigateToDashboard = {
                    // Gọi callback được truyền vào thay vì tự navigate
                    onLoginSuccess()
                },
                navigateToRegister = {
                    navController.navigate(AuthScreen.Register.route)
                }
            )
        }

        composable(route = AuthScreen.Register.route) {
            RegisterScreen(
                navigateToDashboard = {
                    // Gọi callback được truyền vào
                    onLoginSuccess()
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}