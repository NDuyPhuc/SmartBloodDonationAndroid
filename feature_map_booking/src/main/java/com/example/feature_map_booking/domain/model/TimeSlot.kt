// feature_map_booking/src/main/java/com/smartblood/mapbooking/domain/model/TimeSlot.kt
package com.example.feature_map_booking.domain.model
data class TimeSlot(
    val time: String = "", // e.g., "09:00"
    val isAvailable: Boolean = true
)