package com.example.feature_map_booking.domain.usecase

// feature_map_booking/src/main/java/com/smartblood/mapbooking/domain/usecase/GetHospitalDetailsUseCase.kt

import com.smartblood.core.domain.model.Hospital
import com.example.feature_map_booking.domain.repository.MapBookingRepository
import javax.inject.Inject
import kotlin.Result

class GetHospitalDetailsUseCase @Inject constructor(
    private val repository: MapBookingRepository
) {
    suspend operator fun invoke(hospitalId: String): Result<Hospital> {
        if (hospitalId.isBlank()) {
            return Result.failure(IllegalArgumentException("Hospital ID cannot be empty."))
        }
        return repository.getHospitalDetails(hospitalId)
    }
}