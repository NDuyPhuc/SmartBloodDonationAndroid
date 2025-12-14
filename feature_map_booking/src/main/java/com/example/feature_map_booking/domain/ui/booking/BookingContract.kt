package com.example.feature_map_booking.domain.ui.booking

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/booking/BookingContract.kt

import com.smartblood.core.domain.model.TimeSlot
import java.util.Date

data class BookingState(
    val hospitalId: String = "",
    val hospitalName: String = "",
    val isLoadingSlots: Boolean = false,
    val isBooking: Boolean = false,
    val selectedDate: Date = Date(),
    val selectedVolume: String = "350ml",
    val timeSlots: List<TimeSlot> = emptyList(),
    val error: String? = null,
    val bookingSuccess: Boolean = false
)

sealed class BookingEvent {
    data class OnDateSelected(val date: Date) : BookingEvent()
    data class OnVolumeSelected(val volume: String) : BookingEvent()

    data class OnSlotSelected(val time: String) : BookingEvent()
    object OnConfirmBooking : BookingEvent()
}