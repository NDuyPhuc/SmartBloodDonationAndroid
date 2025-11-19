package com.example.smartblooddonationandroid.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.feature_map_booking.domain.ui.booking.BookingScreen
import com.example.feature_map_booking.domain.ui.location_detail.LocationDetailScreen
import com.example.feature_map_booking.domain.ui.map.MapScreen
import com.example.feature_profile.ui.DonationHistoryScreen
import com.example.feature_profile.ui.EditProfileScreen
import com.example.feature_profile.ui.ProfileScreen
import com.example.smartblooddonationandroid.features.dashboard.DashboardScreen
// --- QUAN TRỌNG: Import hàm authGraph từ module feature_auth ---
import com.example.feature_auth.ui.navigation.authGraph
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
        startDestination = BottomNavItem.Dashboard.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        // --- QUAN TRỌNG: Đăng ký luồng Auth vào đây ---
        authGraph(navController)
        // ----------------------------------------------

        composable(route = BottomNavItem.Dashboard.route) {
            DashboardScreen()
        }
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

        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = { navController.navigate(Screen.EDIT_PROFILE) },
                onNavigateToDonationHistory = { navController.navigate(Screen.DONATION_HISTORY) },
                onNavigateToLogin = {
                    // Điều hướng về luồng Authentication
                    navController.navigate(Graph.AUTHENTICATION) {
                        // Xóa sạch back stack để không thể quay lại
                        popUpTo(Graph.MAIN) {
                            inclusive = true
                        }
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