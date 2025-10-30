//app/src/main/java/com/smartblood/donation/navigation/Screen.kt
package com.smartblood.donation.navigation

import com.smartblood.auth.navigation.AUTH_GRAPH_ROUTE

// Định nghĩa các "địa chỉ" cho các màn hình
object Screen {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    // Thêm các màn hình khác ở đây...
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = AUTH_GRAPH_ROUTE // Sử dụng lại route đã định nghĩa ở feature_auth
    const val MAIN = "main_graph_route"
}

