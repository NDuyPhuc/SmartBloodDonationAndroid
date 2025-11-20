package com.example.smartblooddonationandroid.ui.theme

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartblooddonationandroid.navigation.AppNavHost
import com.example.smartblooddonationandroid.navigation.BottomNavItem
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navController = rememberAnimatedNavController()

    // Danh sách các màn hình cần hiện Bottom Bar
    val navItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Map,
        BottomNavItem.Profile,
    )

    // Lấy thông tin màn hình hiện tại
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // --- LOGIC KIỂM TRA: Chỉ hiện BottomBar nếu màn hình hiện tại nằm trong danh sách navItems ---
    val isBottomBarVisible = navItems.any { it.route == currentDestination?.route }
    // ---------------------------------------------------------------------------------------------

    Scaffold(
        bottomBar = {
            // Chỉ hiển thị nếu biến kiểm tra là true
            if (isBottomBarVisible) {
                NavigationBar {
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
        }
    ) { innerPadding ->
        // Nếu BottomBar bị ẩn, padding bottom sẽ là 0, giúp Splash full màn hình
        AppNavHost(
            navController = navController,
            paddingValues = innerPadding
        )
    }
}