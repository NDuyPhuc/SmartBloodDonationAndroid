//D:\SmartBloodDonationAndroid\app\src\main\java\com\example\smartblooddonationandroid\features\dashboard\DashboardViewModel.kt
package com.smartblood.donation.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.profile.domain.usecase.CalculateNextDonationDateUseCase
import com.smartblood.profile.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Tạm thời định nghĩa State ở đây cho gọn
data class DashboardState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val bloodType: String = "N/A",
    val nextDonationMessage: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val calculateNextDonationDateUseCase: CalculateNextDonationDateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        // Bắt đầu thu thập (collect) dữ liệu từ Flow mà UseCase trả về
        getUserProfileUseCase()
            .onEach { userProfile -> // <-- SỬA Ở ĐÂY: Dùng .onEach để xử lý mỗi item được Flow phát ra
                if (userProfile != null) {
                    // Nếu nhận được thông tin người dùng
                    _state.update {
                        it.copy(
                            isLoading = false,
                            userName = userProfile.fullName, // <-- Giờ đã hợp lệ
                            bloodType = userProfile.bloodType ?: "N/A", // <-- Giờ đã hợp lệ
                            nextDonationMessage = calculateNextDonationDateUseCase(userProfile)
                        )
                    }
                } else {
                    // Xử lý trường hợp không có dữ liệu (ví dụ: người dùng đăng xuất)
                    _state.update {
                        it.copy(isLoading = false, userName = "Không tải được dữ liệu")
                    }
                }
            }
            .launchIn(viewModelScope) // Bắt đầu coroutine để lắng nghe Flow này
    }
}