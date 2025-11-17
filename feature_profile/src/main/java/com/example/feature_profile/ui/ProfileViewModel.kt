package com.example.feature_profile.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.core.domain.model.BloodRequest // Quan trọng: import BloodRequest
import com.example.feature_emergency.domain.usecase.GetMyPledgedRequestsUseCase // Quan trọng: import UseCase
import com.smartblood.core.domain.model.Appointment
import com.example.feature_map_booking.domain.usecase.GetMyAppointmentsUseCase
import com.example.feature_profile.domain.usecase.GetUserProfileUseCase
import com.example.feature_profile.domain.usecase.UpdateUserProfileUseCase
import com.smartblood.core.storage.domain.usecase.UploadImageUseCase
import com.smartblood.core.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getMyAppointmentsUseCase: GetMyAppointmentsUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val getMyPledgedRequestsUseCase: GetMyPledgedRequestsUseCase // Đảm bảo đã inject
) : ViewModel() {

    // Định nghĩa State một lần duy nhất
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

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Chạy song song 3 tác vụ để tối ưu tốc độ
            val profileDeferred = async { getUserProfileUseCase() }
            val appointmentsDeferred = async { getMyAppointmentsUseCase() }
            val pledgedRequestsDeferred = async { getMyPledgedRequestsUseCase() }

            // Chờ tất cả các tác vụ hoàn thành và lấy kết quả
            val profileResult = profileDeferred.await()
            val appointmentsResult = appointmentsDeferred.await()
            val pledgedRequestsResult = pledgedRequestsDeferred.await()

            // Xử lý kết quả và cập nhật State một lần duy nhất
            val userProfile = profileResult.getOrNull()
            val pledgedRequests = pledgedRequestsResult.getOrNull() ?: emptyList()
            val (upcoming, today, past) = appointmentsResult.getOrNull()
                ?.let { classifyAppointments(it) }
                ?: Triple(emptyList(), emptyList(), emptyList())

            // Gom tất cả các lỗi có thể xảy ra
            val errorMessage = profileResult.exceptionOrNull()?.message
                ?: appointmentsResult.exceptionOrNull()?.message
                ?: pledgedRequestsResult.exceptionOrNull()?.message

            _state.update {
                it.copy(
                    isLoading = false,
                    userProfile = userProfile,
                    upcomingAppointments = upcoming,
                    todayAppointments = today,
                    pastAppointments = past,
                    pledgedRequests = pledgedRequests,
                    error = errorMessage
                )
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