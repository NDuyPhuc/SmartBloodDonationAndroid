package com.example.feature_auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }
            is LoginEvent.OnPasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }
            LoginEvent.OnLoginClicked -> {
                login()
            }
            LoginEvent.OnErrorDismissed -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = loginUseCase(state.value.email, state.value.password)
            result.onSuccess {
                _state.update { it.copy(isLoading = false, loginSuccess = true) }
            }.onFailure { exception ->
                // --- SỬA: Dịch lỗi sang tiếng Việt ---
                val errorMessage = when {
                    exception.message?.contains("badly formatted") == true -> "Định dạng email không hợp lệ."
                    exception.message?.contains("user-not-found") == true || exception.message?.contains("There is no user") == true -> "Tài khoản không tồn tại."
                    exception.message?.contains("wrong-password") == true || exception.message?.contains("INVALID_LOGIN_CREDENTIALS") == true -> "Sai mật khẩu hoặc email."
                    exception.message?.contains("network error") == true -> "Lỗi kết nối mạng. Vui lòng kiểm tra lại."
                    else -> "Đăng nhập thất bại: ${exception.message}"
                }
                // ------------------------------------
                _state.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
            }
        }
    }
}