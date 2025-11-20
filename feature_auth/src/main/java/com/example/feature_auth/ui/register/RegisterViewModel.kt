package com.example.feature_auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnFullNameChanged -> _state.update { it.copy(fullName = event.fullName) }
            is RegisterEvent.OnEmailChanged -> _state.update { it.copy(email = event.email) }
            is RegisterEvent.OnPasswordChanged -> _state.update { it.copy(password = event.password) }
            RegisterEvent.OnRegisterClicked -> register()
            RegisterEvent.OnErrorDismissed -> _state.update { it.copy(error = null) }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val currentState = state.value
            val result = registerUseCase(
                fullName = currentState.fullName,
                email = currentState.email,
                password = currentState.password
            )
            result.onSuccess {
                _state.update { it.copy(isLoading = false, registrationSuccess = true) }
            }.onFailure { exception ->
                // --- SỬA: Dịch lỗi sang tiếng Việt ---
                val errorMessage = when {
                    exception.message?.contains("email-already-in-use") == true -> "Email này đã được sử dụng."
                    exception.message?.contains("badly formatted") == true -> "Định dạng email không hợp lệ."
                    exception.message?.contains("Password should be at least") == true -> "Mật khẩu phải có ít nhất 6 ký tự."
                    exception.message?.contains("network error") == true -> "Lỗi kết nối mạng."
                    else -> "Đăng ký thất bại: ${exception.message}"
                }
                // ------------------------------------
                _state.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
            }
        }
    }
}