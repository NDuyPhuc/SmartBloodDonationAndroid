package com.smartblood.donation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.core.domain.model.BloodRequest
import com.example.feature_emergency.domain.usecase.AcceptEmergencyRequestUseCase
import com.example.feature_emergency.domain.usecase.GetActiveEmergencyRequestsUseCase
import com.example.feature_emergency.domain.usecase.GetMyPledgedRequestsUseCase
import com.example.feature_profile.domain.usecase.GetUserProfileUseCase
import com.smartblood.profile.domain.usecase.CalculateNextDonationDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lớp Event để giao tiếp từ UI -> ViewModel
sealed class DashboardEvent {
    data class OnAcceptRequestClicked(val requestId: String, val volume: String) : DashboardEvent()
    object OnPledgeSuccessMessageShown : DashboardEvent()
}

// State được cập nhật hoàn chỉnh
data class DashboardState(
    val isLoadingProfile: Boolean = true,
    val isLoadingRequests: Boolean = true,
    val isPledging: Boolean = false,
    val userName: String = "",
    val bloodType: String = "N/A",
    val nextDonationMessage: String = "",
    val displayableEmergencyRequests: List<BloodRequest> = emptyList(),
    val error: String? = null,
    val pledgeSuccess: Boolean = false,
    val isEligibleToDonate: Boolean = true, // Mặc định cho phép
    val daysToWait: Long = 0 // Số ngày cần chờ
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val calculateNextDonationDateUseCase: CalculateNextDonationDateUseCase,
    private val getActiveEmergencyRequestsUseCase: GetActiveEmergencyRequestsUseCase,
    private val acceptEmergencyRequestUseCase: AcceptEmergencyRequestUseCase,
    private val getMyPledgedRequestsUseCase: GetMyPledgedRequestsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        // Bắt đầu lắng nghe dữ liệu ngay khi ViewModel được tạo
        listenToDashboardData()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OnAcceptRequestClicked -> acceptRequest(event.requestId, event.volume)
            DashboardEvent.OnPledgeSuccessMessageShown -> _state.update { it.copy(pledgeSuccess = false, error = null) }
        }
    }

    private fun listenToDashboardData() {
        // 1. Lắng nghe thông tin User Profile
        viewModelScope.launch {
            _state.update { it.copy(isLoadingProfile = true) }
            val profileResult = getUserProfileUseCase() // Lấy thông tin user một lần
            profileResult.onSuccess { userProfile ->
                val (eligible, days) = checkEligibility(userProfile.lastDonationDate)
                _state.update {
                    it.copy(
                        isLoadingProfile = false,
                        userName = userProfile.fullName,
                        bloodType = userProfile.bloodType ?: "N/A",
                        nextDonationMessage = calculateNextDonationDateUseCase(userProfile)
                    )
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoadingProfile = false, error = error.message) }
            }
        }

        // 2. Lắng nghe và kết hợp dữ liệu từ hai nguồn Flow
        viewModelScope.launch {
            _state.update { it.copy(isLoadingRequests = true) }

            // Lấy Flow của các yêu cầu đang hoạt động
            val activeRequestsFlow = getActiveEmergencyRequestsUseCase()

            // Lấy Flow của các yêu cầu user đã chấp nhận
            val pledgedRequestsFlow = getMyPledgedRequestsUseCase()

            // Dùng `combine` để tự động tính toán lại danh sách hiển thị
            // mỗi khi một trong hai flow trên có dữ liệu mới
            combine(activeRequestsFlow, pledgedRequestsFlow) { activeResult, pledgedResult ->

                // Xử lý lỗi từ các Flow
                val errorMessage = activeResult.exceptionOrNull()?.message
                    ?: pledgedResult.exceptionOrNull()?.message
                if (errorMessage != null) {
                    _state.update { it.copy(isLoadingRequests = false, error = errorMessage) }
                    return@combine
                }

                val activeRequests = activeResult.getOrNull() ?: emptyList()
                val pledgedRequestIds = pledgedResult.getOrNull()?.map { it.id }?.toSet() ?: emptySet()

                // Lọc ra danh sách cần hiển thị
                val displayableRequests = activeRequests.filter { it.id !in pledgedRequestIds }

                _state.update {
                    it.copy(
                        isLoadingRequests = false,
                        displayableEmergencyRequests = displayableRequests
                    )
                }

            }.catch { e ->
                // Bắt các lỗi không mong muốn từ Flow
                _state.update { it.copy(isLoadingRequests = false, error = e.message) }
            }.collect() // Bắt đầu lắng nghe
        }
    }
    private fun checkEligibility(lastDateStr: String?): Pair<Boolean, Long> {
        if (lastDateStr.isNullOrBlank()) return Pair(true, 0)

        try {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val lastDate = dateFormat.parse(lastDateStr) ?: return Pair(true, 0)

            val calendar = java.util.Calendar.getInstance()
            calendar.time = lastDate
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 84) // Cộng 84 ngày

            val eligibleDate = calendar.time
            val today = java.util.Date()

            if (eligibleDate.after(today)) {
                val diff = eligibleDate.time - today.time
                val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff) + 1
                return Pair(false, days)
            }
        } catch (e: Exception) {
            return Pair(true, 0) // Lỗi format thì cho phép (hoặc chặn tùy logic)
        }
        return Pair(true, 0)
    }
    private fun acceptRequest(requestId: String, volume: String) {
        viewModelScope.launch {
            _state.update { it.copy(isPledging = true, error = null) }

            val profileResult = getUserProfileUseCase()
            // ... (Phần kiểm tra profile giữ nguyên) ...
            val userProfile = profileResult.getOrNull()
            if (userProfile == null) {
                _state.update { it.copy(isPledging = false, error = "Không tìm thấy hồ sơ người dùng.") }
                return@launch
            }

            // Gọi UseCase với volume
            val acceptResult = acceptEmergencyRequestUseCase(requestId, userProfile, volume)

            acceptResult.onSuccess {
                _state.update { it.copy(isPledging = false, pledgeSuccess = true) }
            }.onFailure { error ->
                _state.update { it.copy(isPledging = false, error = "Lỗi: ${error.message}") }
            }
        }
    }
}