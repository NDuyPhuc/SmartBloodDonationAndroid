package com.example.feature_profile.ui

import com.smartblood.core.domain.model.Appointment
import com.smartblood.core.domain.model.BloodRequest
import com.smartblood.core.domain.model.UserProfile

// Cập nhật State
data class ProfileState(
    val isLoading: Boolean = true,
    val isUploading: Boolean = false,
    val userProfile: UserProfile? = null,
    val upcomingAppointments: List<Appointment> = emptyList(),
    val todayAppointments: List<Appointment> = emptyList(),
    val pastAppointments: List<Appointment> = emptyList(),
    val pledgedRequests: List<BloodRequest> = emptyList(),
    val error: String? = null,
    val isSignedOut: Boolean = false
)

// Cập nhật Event
sealed class ProfileEvent {
    object OnEditProfileClicked : ProfileEvent()
    object OnViewDonationHistoryClicked : ProfileEvent()
    object OnSignOutClicked : ProfileEvent()
}