package com.smartblood.donation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.feature_auth.ui.navigation.authGraph
import com.example.feature_auth.ui.splash.SplashScreen
import com.example.feature_emergency.ui.history.EmergencyHistoryScreen
import com.example.feature_map_booking.domain.ui.booking.BookingScreen
import com.example.feature_map_booking.domain.ui.location_detail.LocationDetailScreen
import com.example.feature_map_booking.domain.ui.map.MapScreen
import com.example.feature_profile.ui.DonationHistoryScreen
import com.example.feature_profile.ui.EditProfileScreen
import com.example.feature_profile.ui.ProfileScreen
import com.smartblood.donation.dashboard.DashboardScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.SPLASH, // Bắt đầu từ Splash
        modifier = Modifier.padding(paddingValues)
    ) {
        // 1. Đăng ký luồng Authentication
        authGraph(
            navController = navController,
            onLoginSuccess = {
                // Khi đăng nhập thành công, AppNavHost sẽ điều hướng đến Dashboard
                navController.navigate(BottomNavItem.Dashboard.route) {
                    // Xóa sạch lịch sử màn hình Login/Splash để không back lại được
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )

        // 2. Màn hình Splash
        composable(Screen.SPLASH) {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                },
                navigateToDashboard = {
                    navController.navigate(BottomNavItem.Dashboard.route) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable("emergency_history_standalone") {
            // Lúc này IDE sẽ hết báo lỗi vì nó đã được sử dụng ở đây
            EmergencyHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        // 3. Màn hình Dashboard
        composable(route = BottomNavItem.Dashboard.route) {
            DashboardScreen()
        }

        // 4. Luồng Bản đồ & Đặt lịch
        composable(route = BottomNavItem.Map.route) {
            MapScreen(
                onNavigateToLocationDetail = { hospitalId ->
                    navController.navigate("location_detail/$hospitalId")
                }
            )
        }
        composable(
            route = "location_detail/{hospitalId}",
            arguments = listOf(navArgument("hospitalId") { type = NavType.StringType })
        ) {
            LocationDetailScreen(
                onNavigateToBooking = { hospitalId, hospitalName ->
                    navController.navigate("booking/$hospitalId/$hospitalName")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "booking/{hospitalId}/{hospitalName}",
            arguments = listOf(
                navArgument("hospitalId") { type = NavType.StringType },
                navArgument("hospitalName") { type = NavType.StringType }
            )
        ) {
            BookingScreen(
                onBookingSuccess = { navController.popBackStack(BottomNavItem.Map.route, inclusive = false) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 5. Luồng Hồ sơ (Profile)
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                // Tham số 1: Chuyển trang Edit Profile
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EDIT_PROFILE)
                },
                // Tham số 2: Chuyển trang Lịch sử
                onNavigateToDonationHistory = {
                    navController.navigate(Screen.DONATION_HISTORY)
                },
                // Tham số 3: Đăng xuất -> Về trang Login
                onNavigateToLogin = {
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Graph.MAIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.EDIT_PROFILE) {
            EditProfileScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.DONATION_HISTORY) {
            DonationHistoryScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}