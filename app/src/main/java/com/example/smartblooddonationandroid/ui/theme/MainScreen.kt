//D:\SmartBloodDonationAndroid\app\src\main\java\com\example\smartblooddonationandroid\ui\theme\MainScreen.kt
package com.smartblood.donation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feature_map_booking.domain.ui.booking.BookingScreen
import com.example.feature_map_booking.domain.ui.location_detail.LocationDetailScreen
import com.example.feature_map_booking.domain.ui.map.MapScreen
import com.example.feature_profile.ui.DonationHistoryScreen
import com.example.feature_profile.ui.EditProfileScreen
import com.smartblood.donation.features.dashboard.DashboardScreen
import com.smartblood.donation.navigation.BottomNavItem
import com.smartblood.donation.navigation.Screen
import com.smartblood.profile.ui.ProfileScreen

@Composable
fun MainScreen() {
    // NavController này chỉ quản lý việc điều hướng BÊN TRONG MainScreen
    // (giữa Dashboard, Map, Profile,...)
    val navController = rememberNavController()

    // Danh sách các mục sẽ hiển thị trên thanh điều hướng
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

                // Lặp qua danh sách các mục để tạo các NavigationBarItem
                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        // Kiểm tra xem route hiện tại có khớp với mục này không để highlight nó
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up về màn hình bắt đầu của graph để tránh chồng chất back stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Tránh tạo nhiều bản sao của cùng một màn hình
                                launchSingleTop = true
                                // Khôi phục lại trạng thái khi chọn lại một mục đã chọn trước đó
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // NavHost này sẽ là nơi hiển thị nội dung của màn hình được chọn
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Dashboard.route, // Màn hình bắt đầu là Trang chủ
            modifier = Modifier.padding(innerPadding)
        ) {
            // Định nghĩa Composable cho từng route
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen()
            }
            composable(BottomNavItem.Map.route) {
                MapScreen(
                    onNavigateToLocationDetail = { hospitalId ->
                        navController.navigate("location_detail/$hospitalId")
                    }
                )
            }
            composable(BottomNavItem.Profile.route) {
                // Sử dụng ProfileScreen đã tạo từ feature_profile
                ProfileScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.EDIT_PROFILE)
                    },
                    onNavigateToDonationHistory = {
                        navController.navigate(Screen.DONATION_HISTORY)
                    }
                )
            }
            composable(
                route = "location_detail/{hospitalId}",
                arguments = listOf(navArgument("hospitalId") { type = NavType.StringType })
            ) { backStackEntry ->
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
                    onBookingSuccess = {
                        // Quay về màn hình bản đồ sau khi đặt lịch thành công
                        navController.popBackStack(BottomNavItem.Map.route, inclusive = false)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(route = Screen.EDIT_PROFILE) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(route = Screen.DONATION_HISTORY) {
                DonationHistoryScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}