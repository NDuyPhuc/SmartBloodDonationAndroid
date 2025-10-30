package com.example.feature_map_booking.domain.usecase
// feature_map_booking/src/main/java/com/smartblood/mapbooking/domain/usecase/BookAppointmentUseCase.kt

import com.example.feature_map_booking.domain.repository.MapBookingRepository
import java.util.Date
import javax.inject.Inject
import kotlin.Result

class BookAppointmentUseCase @Inject constructor(
    private val repository: MapBookingRepository
) {
    suspend operator fun invoke(hospitalId: String, dateTime: Date): Result<Unit> {
        if (hospitalId.isBlank()) {
            return Result.failure(IllegalArgumentException("Hospital ID is required."))
        }
        if (dateTime.before(Date())) {
            return Result.failure(IllegalArgumentException("Cannot book an appointment in the past."))
        }
        return repository.bookAppointment(hospitalId, dateTime)
    }
}