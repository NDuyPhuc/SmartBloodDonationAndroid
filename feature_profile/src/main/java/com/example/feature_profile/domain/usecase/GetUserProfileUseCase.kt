//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\usecase\GetUserProfileUseCase.kt
package com.smartblood.profile.domain.usecase

import com.smartblood.profile.domain.model.UserProfile
import com.smartblood.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<UserProfile?> = repository.getUserProfile()
}