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
    val status: String = "PENDING", // PENDING, CONFIRMED, CANCELED, COMPLETED
    val registeredVolume: String = "350ml",

    // --- CÁC TRƯỜNG MỚI ---
    val actualVolume: String? = null, // Dung tích thực tế đã hiến (VD: "350ml")
    val labResult: LabResult? = null  // Kết quả xét nghiệm đính kèm
)