package com.example.feature_profile.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.core.domain.model.Appointment
import com.smartblood.core.domain.model.BloodRequest
import com.smartblood.core.domain.model.UserProfile
import com.example.feature_emergency.domain.usecase.GetMyPledgedRequestsUseCase
import com.example.feature_map_booking.domain.usecase.GetMyAppointmentsUseCase
import com.example.feature_profile.domain.usecase.GetUserProfileUseCase
import com.example.feature_profile.domain.usecase.UpdateUserProfileUseCase
import com.smartblood.core.storage.domain.usecase.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getMyAppointmentsUseCase: GetMyAppointmentsUseCase,
    private val getMyPledgedRequestsUseCase: GetMyPledgedRequestsUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    // Chỉ có MỘT data class State
    data class ProfileState(
        val isLoading: Boolean = true,
        val isUploading: Boolean = false,
        val userProfile: UserProfile? = null,
        val upcomingAppointments: List<Appointment> = emptyList(),
        val todayAppointments: List<Appointment> = emptyList(),
        val pastAppointments: List<Appointment> = emptyList(),
        val pledgedRequests: List<BloodRequest> = emptyList(),
        val error: String? = null
    )

    // Chỉ có MỘT StateFlow
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    // Chỉ có MỘT khối init
    init {
        listenToProfileData()
    }

    private fun listenToProfileData() {
        // Lấy thông tin user một lần khi khởi tạo
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getUserProfileUseCase().onSuccess { user ->
                _state.update { it.copy(userProfile = user) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }

        // Lắng nghe real-time cả hai nguồn dữ liệu
        viewModelScope.launch {
            val appointmentsFlow = getMyAppointmentsUseCase()
            val pledgedRequestsFlow = getMyPledgedRequestsUseCase()

            // Kết hợp cả hai Flow
            combine(appointmentsFlow, pledgedRequestsFlow) { appointmentsResult, pledgedResult ->
                // Tạo một cặp để xử lý kết quả
                Pair(appointmentsResult, pledgedResult)
            }.collect { (appointmentsResult, pledgedResult) ->

                // Xử lý lỗi
                val errorMessage = appointmentsResult.exceptionOrNull()?.message
                    ?: pledgedResult.exceptionOrNull()?.message

                // Lấy dữ liệu thành công
                val allAppointments = appointmentsResult.getOrNull() ?: emptyList()
                val pledgedRequests = pledgedResult.getOrNull() ?: emptyList()

                val (upcoming, today, past) = classifyAppointments(allAppointments)

                _state.update {
                    it.copy(
                        isLoading = false, // Dữ liệu đã tải xong
                        upcomingAppointments = upcoming,
                        todayAppointments = today,
                        pastAppointments = past,
                        pledgedRequests = pledgedRequests,
                        error = errorMessage
                    )
                }
            }
        }
    }

    fun onAvatarChange(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true, error = null) }
            val currentProfile = _state.value.userProfile ?: run {
                _state.update { it.copy(isUploading = false, error = "Không tìm thấy thông tin người dùng.") }
                return@launch
            }

            uploadImageUseCase(uri, "avatars/${currentProfile.uid}").onSuccess { downloadUrl ->
                val updatedProfile = currentProfile.copy(avatarUrl = downloadUrl)
                updateUserProfileUseCase(updatedProfile).onSuccess {
                    _state.update { it.copy(isUploading = false, userProfile = updatedProfile) }
                }.onFailure { error ->
                    _state.update { it.copy(isUploading = false, error = "Lỗi cập nhật profile: ${error.message}") }
                }
            }.onFailure { error ->
                _state.update { it.copy(isUploading = false, error = "Lỗi tải ảnh lên: ${error.message}") }
            }
        }
    }

    private fun classifyAppointments(appointments: List<Appointment>): Triple<List<Appointment>, List<Appointment>, List<Appointment>> {
        val upcoming = mutableListOf<Appointment>()
        val today = mutableListOf<Appointment>()
        val past = mutableListOf<Appointment>()

        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.time

        val endOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }.time

        for (appointment in appointments) {
            when {
                appointment.dateTime.after(endOfToday) -> upcoming.add(appointment)
                appointment.dateTime.before(startOfToday) -> past.add(appointment)
                else -> today.add(appointment)
            }
        }

        upcoming.sortBy { it.dateTime }
        today.sortBy { it.dateTime }
        past.sortByDescending { it.dateTime }

        return Triple(upcoming, today, past)
    }
}