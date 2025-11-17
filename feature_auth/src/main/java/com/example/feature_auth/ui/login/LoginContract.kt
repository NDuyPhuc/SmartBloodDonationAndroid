// feature_auth/src/main/java/com/smartblood/auth/ui/login/LoginContract.kt

package com.example.feature_auth.ui.login

// Định nghĩa trạng thái của màn hình
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)

// Định nghĩa các sự kiện mà người dùng có thể tạo ra
sealed class LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    object OnLoginClicked : LoginEvent()
    object OnErrorDismissed : LoginEvent()
}