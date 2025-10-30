package com.example.feature_map_booking.domain.ui.booking

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/booking/BookingViewModel.kt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_map_booking.domain.usecase.BookAppointmentUseCase
import com.example.feature_map_booking.domain.usecase.GetAvailableSlotsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val getAvailableSlotsUseCase: GetAvailableSlotsUseCase,
    private val bookAppointmentUseCase: BookAppointmentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(BookingState())
    val state = _state.asStateFlow()

    private val hospitalId: String = checkNotNull(savedStateHandle["hospitalId"])
    private var selectedTime: String? = null

    init {
        _state.update { it.copy(hospitalId = hospitalId, hospitalName = savedStateHandle["hospitalName"] ?: "") }
        fetchSlotsForDate(Date())
    }

    fun onEvent(event: BookingEvent) {
        when (event) {
            is BookingEvent.OnDateSelected -> {
                _state.update { it.copy(selectedDate = event.date) }
                fetchSlotsForDate(event.date)
            }
            is BookingEvent.OnSlotSelected -> {
                selectedTime = event.time
            }
            is BookingEvent.OnConfirmBooking -> {
                confirmBooking()
            }
        }
    }

    private fun fetchSlotsForDate(date: Date) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSlots = true) }
            getAvailableSlotsUseCase(hospitalId, date)
                .onSuccess { slots ->
                    _state.update { it.copy(isLoadingSlots = false, timeSlots = slots) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingSlots = false, error = error.message) }
                }
        }
    }

    private fun confirmBooking() {
        var time = selectedTime ?: return // Cần thông báo lỗi cho người dùng
        val calendar = Calendar.getInstance().apply {
            time = _state.value.selectedDate.toString()
            val (hour, minute) = time.split(":").map { it.toInt() }
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        val finalDateTime = calendar.time

        viewModelScope.launch {
            _state.update { it.copy(isBooking = true) }
            bookAppointmentUseCase(hospitalId, finalDateTime)
                .onSuccess {
                    _state.update { it.copy(isBooking = false, bookingSuccess = true) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isBooking = false, error = error.message) }
                }
        }
    }
}