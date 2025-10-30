//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\ui\ProfileContract.kt
package com.smartblood.profile.ui

import com.smartblood.profile.domain.model.UserProfile

data class ProfileState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String? = null
)

sealed class ProfileEvent {
    object OnEditProfileClicked : ProfileEvent()
    object OnViewDonationHistoryClicked : ProfileEvent()
}