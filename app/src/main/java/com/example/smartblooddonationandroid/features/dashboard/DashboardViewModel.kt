package com.smartblood.donation.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_emergency.domain.model.BloodRequest
import com.example.feature_emergency.domain.usecase.AcceptEmergencyRequestUseCase
import com.example.feature_emergency.domain.usecase.GetActiveEmergencyRequestsUseCase
import com.smartblood.profile.domain.model.UserProfile
import com.smartblood.profile.domain.usecase.CalculateNextDonationDateUseCase
import com.example.feature_profile.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lớp Event để giao tiếp từ UI -> ViewModel
sealed class DashboardEvent {
    data class OnAcceptRequestClicked(val requestId: String) : DashboardEvent()
}

data class DashboardState(
    val isLoading: Boolean = true,
    val isPledging: Boolean = false,
    val userName: String = "",
    val bloodType: String = "N/A",
    val nextDonationMessage: String = "",
    val emergencyRequests: List<BloodRequest> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val calculateNextDonationDateUseCase: CalculateNextDonationDateUseCase,
    private val getActiveEmergencyRequestsUseCase: GetActiveEmergencyRequestsUseCase,
    private val acceptEmergencyRequestUseCase: AcceptEmergencyRequestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    // Hàm này được gọi từ Screen
    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OnAcceptRequestClicked -> {
                acceptRequest(event.requestId)
            }
        }
    }

    private fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isPledging = true) }

            val profileResult = getUserProfileUseCase()

            if (profileResult.isSuccess) {
                val userProfile = profileResult.getOrNull()
                if (userProfile != null) {
                    val acceptResult = acceptEmergencyRequestUseCase(requestId, userProfile)
                    acceptResult.onSuccess {
                        _state.update { it.copy(isPledging = false) }
                        // Optional: Reload data to reflect changes or show a success message
                    }.onFailure { error ->
                        _state.update { it.copy(isPledging = false, error = "Lỗi: ${error.message}") }
                    }
                } else {
                    _state.update { it.copy(isPledging = false, error = "Không tìm thấy hồ sơ người dùng.") }
                }
            } else {
                _state.update { it.copy(isPledging = false, error = "Không thể lấy thông tin người dùng.") }
            }
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val profileResult = getUserProfileUseCase()
            profileResult.onSuccess { userProfile ->
                _state.update {
                    it.copy(
                        userName = userProfile.fullName,
                        bloodType = userProfile.bloodType ?: "N/A",
                        nextDonationMessage = calculateNextDonationDateUseCase(userProfile)
                    )
                }
            }.onFailure {
                _state.update { it.copy(userName = "Không tải được dữ liệu") }
            }

            val requestsResult = getActiveEmergencyRequestsUseCase()
            requestsResult.onSuccess { requests ->
                _state.update { it.copy(isLoading = false, emergencyRequests = requests) }
            }.onFailure { exception ->
                _state.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }
}