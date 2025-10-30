package com.example.feature_map_booking.domain.repository

// feature_map_booking/src/main/java/com/smartblood/mapbooking/domain/repository/MapBookingRepository.kt

import com.example.feature_map_booking.domain.model.Hospital
import com.example.feature_map_booking.domain.model.TimeSlot
import java.util.Date
import kotlin.Result

interface MapBookingRepository {
    suspend fun getNearbyHospitals(lat: Double, lng: Double, radiusKm: Double): Result<List<Hospital>>
    suspend fun getHospitalDetails(hospitalId: String): Result<Hospital>
    suspend fun getAvailableSlots(hospitalId: String, date: Date): Result<List<TimeSlot>>
    suspend fun bookAppointment(
        hospitalId: String,
        dateTime: Date
    ): Result<Unit>
}