//D:\SmartBloodDonationAndroid\app\src\main\java\com\example\smartblooddonationandroid\ui\theme\MainScreen.kt
package com.smartblood.donation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartblood.donation.features.dashboard.DashboardScreen
import com.smartblood.donation.navigation.BottomNavItem
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
                // Tạm thời hiển thị một Text giữ chỗ cho màn hình Bản đồ
                // TODO: Thay thế bằng MapScreen() từ feature_map_booking
                Text("Map Screen - Sẽ được triển khai")
            }
            composable(BottomNavItem.Profile.route) {
                // Sử dụng ProfileScreen đã tạo từ feature_profile
                ProfileScreen(
                    onNavigateToEditProfile = { /* TODO: Điều hướng đến màn hình sửa hồ sơ */ },
                    onNavigateToDonationHistory = { /* TODO: Điều hướng đến màn hình lịch sử */ }
                )
            }
        }
    }
}