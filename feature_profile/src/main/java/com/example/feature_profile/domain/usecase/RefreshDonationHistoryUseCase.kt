//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\usecase\RefreshDonationHistoryUseCase.kt
package com.smartblood.profile.domain.usecase

import com.smartblood.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class RefreshDonationHistoryUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke() = repository.refreshDonationHistory()
}