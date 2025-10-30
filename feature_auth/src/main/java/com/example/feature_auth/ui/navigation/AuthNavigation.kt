//D:\SmartBloodDonationAndroid\feature_auth\src\main\java\com\example\feature_auth\ui\navigation\AuthNavigation.kt
package com.smartblood.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.smartblood.auth.ui.login.LoginScreen
import com.smartblood.auth.ui.register.RegisterScreen

// Định nghĩa một route duy nhất cho cả đồ thị này
// Module :app sẽ dùng route này để gọi vào
const val AUTH_GRAPH_ROUTE = "auth_graph"

/**
 * Extension function để đóng gói toàn bộ luồng navigation của feature_auth.
 * @param onNavigateToMainGraph Callback được gọi khi xác thực thành công để điều hướng
 * ra khỏi luồng này và vào luồng chính của app.
 */
fun NavGraphBuilder.authGraph(navController: NavHostController) {
    // Sử dụng hàm navigation() để tạo một đồ thị con (nested graph)
    navigation(
        // Màn hình bắt đầu của luồng này
        startDestination = AuthScreen.Login.route,
        // Route của cả đồ thị con này
        route = AUTH_GRAPH_ROUTE
    ) {
        // Định nghĩa màn hình Login
        composable(route = AuthScreen.Login.route) {
            LoginScreen(
                navigateToDashboard = {
                    // Khi đăng nhập thành công, điều hướng ra khỏi luồng auth
                    // và xóa luồng auth khỏi back stack
                    navController.navigate("main_graph_route") { // Route này sẽ được định nghĩa ở :app
                        popUpTo(AUTH_GRAPH_ROUTE) {
                            inclusive = true // Xóa cả auth_graph
                        }
                    }
                },
                navigateToRegister = {
                    navController.navigate(AuthScreen.Register.route)
                }
            )
        }

        // Định nghĩa màn hình Register
        composable(route = AuthScreen.Register.route) {
            RegisterScreen(
                navigateToDashboard = {
                    // Tương tự, khi đăng ký thành công, điều hướng ra khỏi luồng auth
                    navController.navigate("main_graph_route") {
                        popUpTo(AUTH_GRAPH_ROUTE) {
                            inclusive = true
                        }
                    }
                },
                navigateBack = {
                    navController.popBackStack() // Quay lại màn hình trước đó (LoginScreen)
                }
            )
        }

        // Thêm các composable cho các màn hình khác như FaceAuth... tại đây
    }
}