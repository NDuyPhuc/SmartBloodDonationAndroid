//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\ui\ProfileViewModel.kt
package com.smartblood.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_profile.domain.usecase.RefreshUserProfileUseCase
import com.smartblood.profile.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    // THÊM VÀO: Inject UseCase mới
    private val refreshUserProfileUseCase: RefreshUserProfileUseCase
) : ViewModel() {

    // Bắt đầu với trạng thái isLoading = true để UI hiển thị loading indicator ngay lập tức
    private val _state = MutableStateFlow(ProfileState(isLoading = true))
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        // 1. Bắt đầu lắng nghe dữ liệu từ CSDL cục bộ (Room)
        observeLocalProfile()

        // 2. Kích hoạt việc lấy dữ liệu mới từ mạng (Firestore)
        refreshProfileData()
    }

    /**
     * Lắng nghe CSDL cục bộ.
     * Bất cứ khi nào dữ liệu người dùng trong Room thay đổi, state sẽ được cập nhật.
     */
    private fun observeLocalProfile() {
        getUserProfileUseCase()
            .onEach { userProfile ->
                // Chỉ cập nhật thông tin userProfile, giữ nguyên các trạng thái khác
                _state.update { currentState ->
                    currentState.copy(userProfile = userProfile)
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Hàm này có thể được gọi từ UI (ví dụ: pull-to-refresh) để làm mới dữ liệu.
     */
    fun refreshProfileData() {
        viewModelScope.launch {
            // Đặt lại trạng thái loading và xóa lỗi cũ
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Yêu cầu repository làm mới dữ liệu.
                // Hàm này sẽ lấy data từ Firestore và lưu vào Room.
                // Việc lưu vào Room sẽ tự động kích hoạt flow trong `observeLocalProfile`.
                refreshUserProfileUseCase()
            } catch (e: Exception) {
                // Nếu có lỗi mạng, cập nhật state lỗi
                _state.update { it.copy(error = e.message ?: "An unknown error occurred") }
            } finally {
                // Dù thành công hay thất bại, cuối cùng cũng phải tắt trạng thái loading
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}