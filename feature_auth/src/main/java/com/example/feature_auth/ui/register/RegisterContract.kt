// feature_auth/src/main/java/com/smartblood/auth/ui/register/RegisterContract.kt

package com.smartblood.auth.ui.register

// Định nghĩa trạng thái của màn hình
data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false
)

// Định nghĩa các sự kiện mà người dùng có thể tạo ra
sealed class RegisterEvent {
    data class OnFullNameChanged(val fullName: String) : RegisterEvent()
    data class OnEmailChanged(val email: String) : RegisterEvent()
    data class OnPasswordChanged(val password: String) : RegisterEvent()
    object OnRegisterClicked : RegisterEvent()
    object OnErrorDismissed : RegisterEvent()
}