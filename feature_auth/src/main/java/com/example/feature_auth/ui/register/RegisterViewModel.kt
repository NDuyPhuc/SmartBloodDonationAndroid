// feature_auth/src/main/java/com/smartblood/auth/ui/register/RegisterViewModel.kt

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
            is RegisterEvent.OnFullNameChanged -> {
                _state.update { it.copy(fullName = event.fullName) }
            }
            is RegisterEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }
            is RegisterEvent.OnPasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }
            RegisterEvent.OnRegisterClicked -> {
                register()
            }
            RegisterEvent.OnErrorDismissed -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentState = state.value
            val result = registerUseCase(
                fullName = currentState.fullName,
                email = currentState.email,
                password = currentState.password
            )

            result.onSuccess {
                _state.update { it.copy(isLoading = false, registrationSuccess = true) }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "An unknown error occurred."
                    )
                }
            }
        }
    }
}