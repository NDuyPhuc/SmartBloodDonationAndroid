// feature_auth/src/main/java/com/smartblood/auth/ui/splash/SplashViewModel.kt

package com.example.feature_auth.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.auth.domain.usecase.CheckUserAuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkUserAuthenticationUseCase: CheckUserAuthenticationUseCase
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            // Thêm một khoảng trễ nhỏ (ví dụ 2 giây) để người dùng có thể thấy splash screen
            // Điều này cũng cho Firebase SDK thời gian để khởi tạo và kiểm tra trạng thái đăng nhập.
            delay(2000L)
            _isAuthenticated.value = checkUserAuthenticationUseCase()
        }
    }
}