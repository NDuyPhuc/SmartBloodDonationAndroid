// D:\SmartBloodDonationAndroid\app\src\main\java\com\smartblood\donation\ui\MainScreen.kt
package com.smartblood.donation.ui

// THÊM CÁC IMPORT MỚI TỪ THƯ VIỆN ACCOMPANIST
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
// -----------------------------------------------------------------

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.feature_map_booking.domain.ui.booking.BookingScreen
import com.example.feature_map_booking.domain.ui.location_detail.LocationDetailScreen
import com.example.feature_map_booking.domain.ui.map.MapScreen
import com.example.feature_profile.ui.DonationHistoryScreen
import com.example.feature_profile.ui.EditProfileScreen
import com.example.feature_profile.ui.ProfileScreen

// Sửa các import cũ (nếu có) để trỏ đến các package đúng
import com.example.smartblooddonationandroid.features.dashboard.DashboardScreen
import com.example.smartblooddonationandroid.navigation.BottomNavItem
import com.example.smartblooddonationandroid.navigation.Screen


// THÊM ANNOTATION NÀY
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navController = rememberAnimatedNavController()

    val navItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Map,
        BottomNavItem.Profile,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            paddingValues = innerPadding
        )
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues // Hàm này nhận `paddingValues`
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = BottomNavItem.Dashboard.route,
        modifier = Modifier.padding(paddingValues) // Và sử dụng nó ở đây
    ) {
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
                onNavigateToDonationHistory = { navController.navigate(Screen.DONATION_HISTORY) }
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