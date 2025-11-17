package com.example.feature_emergency.domain.usecase

import com.example.feature_emergency.domain.repository.EmergencyRepository
import javax.inject.Inject

class GetMyPledgedRequestsUseCase @Inject constructor(
    private val repository: EmergencyRepository
) {
    suspend operator fun invoke() = repository.getMyPledgedRequests()
}