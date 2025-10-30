package com.example.feature_map_booking.domain.usecase

// feature_map_booking/src/main/java/com/smartblood/mapbooking/domain/usecase/GetAvailableSlotsUseCase.kt

import com.example.feature_map_booking.domain.model.TimeSlot
import com.example.feature_map_booking.domain.repository.MapBookingRepository
import java.util.Date
import javax.inject.Inject
import kotlin.Result

class GetAvailableSlotsUseCase @Inject constructor(
    private val repository: MapBookingRepository
) {
    suspend operator fun invoke(hospitalId: String, date: Date): Result<List<TimeSlot>> {
        return repository.getAvailableSlots(hospitalId, date)
    }
}