//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\usecase\GetDonationHistoryUseCase.kt
package com.smartblood.profile.domain.usecase

import com.smartblood.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetDonationHistoryUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke() = repository.getDonationHistory()
}