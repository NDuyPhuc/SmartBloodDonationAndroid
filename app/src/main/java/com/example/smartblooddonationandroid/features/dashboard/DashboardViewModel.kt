package com.smartblood.donation.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.core.domain.model.BloodRequest
import com.example.feature_emergency.domain.usecase.AcceptEmergencyRequestUseCase
import com.example.feature_emergency.domain.usecase.GetActiveEmergencyRequestsUseCase
import com.example.feature_emergency.domain.usecase.GetMyPledgedRequestsUseCase
import com.example.feature_profile.domain.usecase.GetUserProfileUseCase
import com.smartblood.profile.domain.usecase.CalculateNextDonationDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lớp Event để giao tiếp từ UI -> ViewModel
sealed class DashboardEvent {
    data class OnAcceptRequestClicked(val requestId: String) : DashboardEvent()
    object OnPledgeSuccessMessageShown : DashboardEvent()
}

// State được cập nhật hoàn chỉnh
data class DashboardState(
    val isLoading: Boolean = true,
    val isPledging: Boolean = false,
    val userName: String = "",
    val bloodType: String = "N/A",
    val nextDonationMessage: String = "",
    val displayableEmergencyRequests: List<BloodRequest> = emptyList(),
    val error: String? = null,
    val pledgeSuccess: Boolean = false
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
        loadDashboardData()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OnAcceptRequestClicked -> acceptRequest(event.requestId)
            DashboardEvent.OnPledgeSuccessMessageShown -> _state.update { it.copy(pledgeSuccess = false) }
        }
    }

    private fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isPledging = true, error = null) }

            val profileResult = getUserProfileUseCase()
            if (profileResult.isFailure) {
                _state.update { it.copy(isPledging = false, error = "Không thể lấy thông tin người dùng để xác nhận.") }
                return@launch
            }

            val userProfile = profileResult.getOrNull()
            if (userProfile == null) {
                _state.update { it.copy(isPledging = false, error = "Không tìm thấy hồ sơ người dùng.") }
                return@launch
            }

            val acceptResult = acceptEmergencyRequestUseCase(requestId, userProfile)
            acceptResult.onSuccess {
                _state.update { it.copy(isPledging = false, pledgeSuccess = true) }
                loadDashboardData() // Tải lại toàn bộ dữ liệu để cập nhật danh sách
            }.onFailure { error ->
                _state.update { it.copy(isPledging = false, error = "Lỗi: ${error.message}") }
            }
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Chạy song song 3 tác vụ
            val profileDeferred = async { getUserProfileUseCase() }
            val activeRequestsDeferred = async { getActiveEmergencyRequestsUseCase() }
            val pledgedRequestsDeferred = async { getMyPledgedRequestsUseCase() }

            // Chờ và nhận kết quả
            val profileResult = profileDeferred.await()
            val activeRequestsResult = activeRequestsDeferred.await()
            val pledgedRequestsResult = pledgedRequestsDeferred.await()

            // Xử lý và cập nhật State
            val userProfile = profileResult.getOrNull()
            val activeRequests = activeRequestsResult.getOrNull() ?: emptyList()
            val pledgedRequestIds = pledgedRequestsResult.getOrNull()?.map { it.id }?.toSet() ?: emptySet()

            // Lọc ra danh sách yêu cầu cần hiển thị (chưa được chấp nhận bởi user này)
            val displayableRequests = activeRequests.filter { it.id !in pledgedRequestIds }

            val errorMessage = profileResult.exceptionOrNull()?.message
                ?: activeRequestsResult.exceptionOrNull()?.message
                ?: pledgedRequestsResult.exceptionOrNull()?.message

            _state.update {
                it.copy(
                    isLoading = false,
                    userName = userProfile?.fullName ?: "Khách",
                    bloodType = userProfile?.bloodType ?: "N/A",
                    nextDonationMessage = calculateNextDonationDateUseCase(userProfile),
                    displayableEmergencyRequests = displayableRequests,
                    error = errorMessage
                )
            }
        }
    }
}