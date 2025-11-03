package com.example.feature_profile.ui

// feature_profile/src/main/java/com/smartblood/profile/ui/edit/EditProfileViewModel.kt


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_profile.domain.usecase.GetUserProfileUseCase
import com.example.feature_profile.domain.usecase.UpdateUserProfileUseCase
import com.smartblood.core.storage.domain.usecase.UploadImageUseCase
import com.smartblood.profile.domain.model.UserProfile
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
    val avatarUri: Uri? = null, // <<-- THÊM: Lưu URI của ảnh được chọn
    val isUploading: Boolean = false, // <<-- THÊM: Trạng thái tải ảnh lên
    // Thêm các trường khác bạn muốn chỉnh sửa ở đây
    val originalProfile: UserProfile? = null
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
    data class OnAvatarChanged(val uri: Uri?) : EditProfileEvent() // <<-- THÊM EVENT NÀY

    object OnSaveClicked : EditProfileEvent()
}


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadImageUseCase: UploadImageUseCase
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
            is EditProfileEvent.OnAvatarChanged -> { // <<-- XỬ LÝ KHI CHỌN ẢNH
                _state.update { it.copy(avatarUri = event.uri, error = null) }
            }
            EditProfileEvent.OnSaveClicked -> {
                saveProfile()
            }
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
                            phoneNumber = profile.phoneNumber ?: "",
                            avatarUri = profile.avatarUrl?.let { Uri.parse(it)}
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
            _state.update { it.copy(isSaving = true, error = null) } // Reset error khi save
            val currentAvatarUri = _state.value.avatarUri
            var newAvatarUrl: String? = null

            // 1. Upload ảnh nếu người dùng đã chọn ảnh mới
            if (currentAvatarUri != null && currentAvatarUri != _state.value.originalProfile?.avatarUrl?.let { Uri.parse(it) } ) {
                _state.update { it.copy(isUploading = true) }
                val uploadResult = uploadImageUseCase(currentAvatarUri, "avatars/${_state.value.originalProfile?.uid}")
                uploadResult.onSuccess { url ->
                    newAvatarUrl = url
                    _state.update { it.copy(isUploading = false) }
                }.onFailure { error ->
                    _state.update { it.copy(isSaving = false, isUploading = false, error = "Lỗi tải ảnh: ${error.message}") }
                    return@launch // Dừng lại nếu lỗi upload
                }
            }

            // 2. Tạo đối tượng UserProfile với thông tin mới (bao gồm URL ảnh nếu có)
            val updatedProfile = _state.value.toUserProfile()?.copy(
                avatarUrl = newAvatarUrl ?: _state.value.originalProfile?.avatarUrl // Giữ nguyên URL cũ nếu không upload ảnh mới
            )

            if (updatedProfile == null) {
                _state.update { it.copy(isSaving = false, error = "Không thể cập nhật hồ sơ.") }
                return@launch
            }

            // 3. Cập nhật thông tin hồ sơ lên Firestore
            updateUserProfileUseCase(updatedProfile)
                .onSuccess {
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false, error = "Lỗi cập nhật hồ sơ: ${error.message}") }
                }
        }
    }
}