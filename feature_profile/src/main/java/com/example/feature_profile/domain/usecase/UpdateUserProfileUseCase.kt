//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\usecase\UpdateUserProfileUseCase.kt
package com.example.feature_profile.domain.usecase

import com.smartblood.core.domain.model.UserProfile
import com.smartblood.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userProfile: UserProfile) = repository.updateUserProfile(userProfile)
}