package com.example.feature_map_booking.domain.ui.booking


// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/booking/BookingScreen.kt


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.feature_map_booking.domain.model.TimeSlot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel = hiltViewModel(),
    onBookingSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.bookingSuccess) {
        if (state.bookingSuccess) {
            onBookingSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Đặt lịch hẹn") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = state.hospitalName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Picker (Basic implementation)
            // For a real app, use a proper library or build a more complex one
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            Text("Chọn ngày: ${dateFormat.format(state.selectedDate)}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                // TODO: Show a DatePickerDialog
                // For now, we just fetch for the current date
            }) {
                Text("Đổi ngày")
            }


            Spacer(modifier = Modifier.height(24.dp))
            Text("Chọn khung giờ:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoadingSlots) {
                CircularProgressIndicator()
            } else {
                var selectedTime by remember { mutableStateOf<String?>(null) }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.timeSlots) { slot ->
                        TimeSlotItem(
                            timeSlot = slot,
                            isSelected = slot.time == selectedTime,
                            onSelect = {
                                selectedTime = it
                                viewModel.onEvent(BookingEvent.OnSlotSelected(it))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onEvent(BookingEvent.OnConfirmBooking) },
                enabled = !state.isBooking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isBooking) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Xác nhận")
                }
            }
        }
    }
}

@Composable
fun TimeSlotItem(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onSelect: (String) -> Unit
) {
    val colors = ButtonDefaults.outlinedButtonColors(
        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    )
    OutlinedButton(
        onClick = { onSelect(timeSlot.time) },
        enabled = timeSlot.isAvailable,
        colors = colors,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(text = timeSlot.time)
    }
}