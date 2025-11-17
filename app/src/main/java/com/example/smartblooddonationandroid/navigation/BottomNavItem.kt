//D:\SmartBloodDonationAndroid\app\src\main\java\com\example\smartblooddonationandroid\navigation\BottomNavItem.kt
package com.example.smartblooddonationandroid.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : BottomNavItem("dashboard", "Trang chủ", Icons.Default.Home)
    object Map : BottomNavItem("map", "Bản đồ", Icons.Default.LocationOn)
    object Profile : BottomNavItem("profile", "Hồ sơ", Icons.Default.Person)
    // Thêm các mục khác sau này: Map, Emergency...
}