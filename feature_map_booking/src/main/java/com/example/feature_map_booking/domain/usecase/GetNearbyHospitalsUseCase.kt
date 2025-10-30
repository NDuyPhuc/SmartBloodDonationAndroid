package com.example.feature_map_booking.domain.usecase

// feature_map_booking/src/main/java/com/smartblood/mapbooking/domain/usecase/GetNearbyHospitalsUseCase.kt

import com.example.feature_map_booking.domain.model.Hospital
import com.example.feature_map_booking.domain.repository.MapBookingRepository
import javax.inject.Inject
import kotlin.Result

class GetNearbyHospitalsUseCase @Inject constructor(
    private val repository: MapBookingRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double, radiusKm: Double = 10.0): Result<List<Hospital>> {
        return repository.getNearbyHospitals(lat, lng, radiusKm)
    }
}