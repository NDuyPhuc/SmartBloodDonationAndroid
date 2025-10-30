// Vị trí: feature_profile/src/main/java/com/smartblood/profile/ui/edit/EditProfileViewModel.kt 
package com.smartblood.profile.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.profile.domain.model.UserProfile
import com.smartblood.profile.domain.usecase.GetUserProfileUseCase
import com.smartblood.profile.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val error: String? = null,
    val updateSuccess: Boolean = false // Cờ để báo cho UI biết khi nào cập nhật thành công
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        // Lấy dữ liệu hiện tại của người dùng để điền vào form
        getUserProfileUseCase().take(1) // Chỉ lấy giá trị đầu tiên từ DB
            .onEach { profile ->
                _state.update { it.copy(isLoading = false, userProfile = profile) }
            }
            .launchIn(viewModelScope)
    }

    fun onProfileChange(updatedProfile: UserProfile) {
        _state.update { it.copy(userProfile = updatedProfile) }
    }

    fun saveProfile() {
        val currentProfile = state.value.userProfile
        if (currentProfile != null) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                val result = updateUserProfileUseCase(currentProfile)
                result
                    .onSuccess {
                        _state.update { it.copy(isLoading = false, updateSuccess = true) }
                    }
                    .onFailure { exception ->
                        _state.update { it.copy(isLoading = false, error = exception.message) }
                    }
            }
        }
    }
}