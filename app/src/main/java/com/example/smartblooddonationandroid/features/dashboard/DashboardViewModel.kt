//D:\SmartBloodDonationAndroid\app\src\main\java\com\example\smartblooddonationandroid\features\dashboard\DashboardViewModel.kt
package com.smartblood.donation.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_profile.domain.usecase.GetUserProfileUseCase
import com.smartblood.profile.domain.usecase.CalculateNextDonationDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getUserProfileUseCase().onSuccess { userProfile ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        userName = userProfile.fullName,
                        bloodType = userProfile.bloodType ?: "N/A",
                        nextDonationMessage = calculateNextDonationDateUseCase(userProfile)
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false, userName = "Không tải được dữ liệu") }
            }
        }
    }
}