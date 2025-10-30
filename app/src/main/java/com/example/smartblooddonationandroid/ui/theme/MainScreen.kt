// Vị trí: app/src/main/java/com/smartblood/donation/ui/MainScreen.kt

package com.smartblood.donation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.smartblood.donation.features.dashboard.DashboardScreen
import com.smartblood.donation.navigation.BottomNavItem
import com.smartblood.profile.ui.edit.EditProfileScreen
import com.smartblood.profile.ui.history.DonationHistoryScreen
import com.smartblood.profile.ui.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Map, // Thêm mục Bản đồ
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
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Dashboard.route, // Bắt đầu ở Dashboard
            modifier = Modifier.padding(innerPadding)
        ) {
            // ---- ĐỊNH NGHĨA CÁC MÀN HÌNH CHÍNH CHO BOTTOM NAV ----

            // THÊM VÀO: Định nghĩa màn hình Dashboard
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen()
            }

            // THÊM VÀO: Định nghĩa màn hình Bản đồ (Tạm thời)
            composable(BottomNavItem.Map.route) {
                // TODO: Thay thế bằng MapScreen() từ feature_map_booking
                Text("Màn hình Bản đồ - Sẽ được triển khai")
            }

            // ---- CÁC MÀN HÌNH CỦA FEATURE PROFILE ----

            // Màn hình Profile chính
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onNavigateToEditProfile = { navController.navigate("edit_profile_route") },
                    onNavigateToDonationHistory = { navController.navigate("donation_history_route") }
                )
            }

            // Màn hình con: Chỉnh sửa Profile
            composable(route = "edit_profile_route") {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Màn hình con: Lịch sử hiến máu
            composable(route = "donation_history_route") {
                DonationHistoryScreen()
            }
        }
    }
}