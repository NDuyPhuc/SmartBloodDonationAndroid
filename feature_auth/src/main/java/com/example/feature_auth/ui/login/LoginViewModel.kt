// feature_auth/src/main/java/com/smartblood/auth/ui/login/LoginViewModel.kt

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
            _state.update { it.copy(isLoading = true) }
            val result = loginUseCase(state.value.email, state.value.password)
            result.onSuccess { user ->
                _state.update { it.copy(isLoading = false, loginSuccess = true) }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Đã xảy ra lỗi không xác định."
                    )
                }
            }
        }
    }
}