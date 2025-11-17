package com.smartblood.core.domain.model

// feature_map_booking/src/main/java/com/smartblood/mapbooking/domain/model/Appointment.kt

import java.util.Date

data class Appointment(
    val id: String = "",
    val userId: String = "",
    val hospitalId: String = "",
    val hospitalName: String = "",
    val hospitalAddress: String = "",
    val dateTime: Date = Date(),
    val status: String = "PENDING" // PENDING, CONFIRMED, CANCELED, COMPLETED
)