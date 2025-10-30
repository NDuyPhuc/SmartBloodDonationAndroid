package com.example.feature_profile.ui

// feature_profile/src/main/java/com/smartblood/profile/ui/edit/EditProfileViewModel.kt


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.profile.domain.model.UserProfile
import com.smartblood.profile.domain.usecase.GetUserProfileUseCase
import com.smartblood.profile.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val fullName: String = "",
    val bloodType: String = "",
    val phoneNumber: String = "",
    // Thêm các trường khác bạn muốn chỉnh sửa ở đây
    private val originalProfile: UserProfile? = null
) {
    // Hàm này tạo ra đối tượng UserProfile mới từ state hiện tại của form
    fun toUserProfile(): UserProfile? {
        return originalProfile?.copy(
            fullName = fullName,
            bloodType = bloodType.ifBlank { null },
            phoneNumber = phoneNumber.ifBlank { null }
        )
    }
}

sealed class EditProfileEvent {
    data class OnFullNameChanged(val value: String) : EditProfileEvent()
    data class OnBloodTypeChanged(val value: String) : EditProfileEvent()
    data class OnPhoneNumberChanged(val value: String) : EditProfileEvent()
    object OnSaveClicked : EditProfileEvent()
}


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        loadInitialProfile()
    }

    fun onEvent(event: EditProfileEvent) {
        when(event) {
            is EditProfileEvent.OnFullNameChanged -> _state.update { it.copy(fullName = event.value) }
            is EditProfileEvent.OnBloodTypeChanged -> _state.update { it.copy(bloodType = event.value) }
            is EditProfileEvent.OnPhoneNumberChanged -> _state.update { it.copy(phoneNumber = event.value) }
            EditProfileEvent.OnSaveClicked -> saveProfile()
        }
    }

    private fun loadInitialProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getUserProfileUseCase()
                .onSuccess { profile ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            originalProfile = profile,
                            fullName = profile.fullName,
                            bloodType = profile.bloodType ?: "",
                            phoneNumber = profile.phoneNumber ?: ""
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val updatedProfile = _state.value.toUserProfile()
            if (updatedProfile == null) {
                _state.update { it.copy(isSaving = false, error = "Không thể cập nhật hồ sơ.") }
                return@launch
            }

            updateUserProfileUseCase(updatedProfile)
                .onSuccess {
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false, error = error.message) }
                }
        }
    }
}