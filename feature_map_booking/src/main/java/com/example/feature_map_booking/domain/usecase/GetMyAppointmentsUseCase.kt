package com.example.feature_map_booking.domain.usecase

import com.example.feature_map_booking.domain.model.Appointment
import com.example.feature_map_booking.domain.repository.MapBookingRepository
import javax.inject.Inject
import kotlin.Result

class GetMyAppointmentsUseCase @Inject constructor(
    private val repository: MapBookingRepository
) {
    suspend operator fun invoke(): Result<List<Appointment>> {
        return repository.getMyAppointments()
    }
}