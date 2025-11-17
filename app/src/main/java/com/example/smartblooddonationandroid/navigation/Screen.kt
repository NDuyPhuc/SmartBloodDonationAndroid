//app/src/main/java/com/smartblood/donation/navigation/Screen.kt
package com.example.smartblooddonationandroid.navigation

import com.example.feature_auth.ui.navigation.AUTH_GRAPH_ROUTE

// Định nghĩa các "địa chỉ" cho các màn hình
object Screen {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val EDIT_PROFILE = "edit_profile"
    const val DONATION_HISTORY = "donation_history"
    // Thêm các màn hình khác ở đây...
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = AUTH_GRAPH_ROUTE // Sử dụng lại route đã định nghĩa ở feature_auth
    const val MAIN = "main_graph_route"
}

