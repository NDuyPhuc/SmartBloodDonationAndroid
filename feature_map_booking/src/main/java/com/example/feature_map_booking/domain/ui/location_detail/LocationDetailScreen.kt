package com.example.feature_map_booking.domain.ui.location_detail


// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/location_detail/LocationDetailScreen.kt


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartblood.core.domain.model.Hospital

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(
    viewModel: LocationDetailViewModel = hiltViewModel(),
    onNavigateToBooking: (hospitalId: String, hospitalName: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết địa điểm") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.hospital != null) {
                HospitalDetails(
                    hospital = state.hospital!!,
                    onBookAppointment = {
                        onNavigateToBooking(state.hospital!!.id, state.hospital!!.name)
                    }
                )
            } else {
                Text(
                    text = state.error ?: "Không thể tải thông tin bệnh viện.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun HospitalDetails(hospital: Hospital, onBookAppointment: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = hospital.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Divider()

        InfoRow(icon = Icons.Filled.Business, text = hospital.address)
        InfoRow(icon = Icons.Filled.Phone, text = hospital.phone)
        InfoRow(icon = Icons.Filled.Schedule, text = hospital.workingHours)
        InfoRow(
            icon = Icons.Default.Bloodtype,
            text = "Nhóm máu đang cần: ${hospital.availableBloodTypes.joinToString(", ")}"
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBookAppointment,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Đặt lịch hiến máu", fontSize = 16.sp)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}