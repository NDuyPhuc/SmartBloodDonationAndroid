//D:\SmartBloodDonationAndroid\feature_auth\src\main\java\com\example\feature_auth\ui\navigation\AuthScreen.kt
package com.smartblood.auth.navigation


// Định nghĩa các route cụ thể bên trong luồng xác thực
sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login_screen")
    object Register : AuthScreen("register_screen")
    // Thêm các màn hình khác nếu có, ví dụ:
    // object ForgotPassword : AuthScreen("forgot_password_screen")
    // object FaceAuthGuide : AuthScreen("face_auth_guide_screen")
}