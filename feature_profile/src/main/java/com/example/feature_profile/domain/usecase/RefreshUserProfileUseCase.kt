//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\usecase\RefreshUserProfileUseCase.kt
package com.example.feature_profile.domain.usecase

import com.smartblood.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class RefreshUserProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke() = repository.refreshUserProfile()
}