//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\ui\ProfileContract.kt
package com.smartblood.profile.ui

import com.smartblood.core.domain.model.Appointment
import com.smartblood.core.domain.model.UserProfile

// Định nghĩa MỚI và DUY NHẤT cho ProfileState
data class ProfileState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val upcomingAppointments: List<Appointment> = emptyList(),
    val todayAppointments: List<Appointment> = emptyList(),
    val pastAppointments: List<Appointment> = emptyList(),
    val error: String? = null
)

// Giữ lại ProfileEvent như cũ
sealed class ProfileEvent {
    object OnEditProfileClicked : ProfileEvent()
    object OnViewDonationHistoryClicked : ProfileEvent()
}