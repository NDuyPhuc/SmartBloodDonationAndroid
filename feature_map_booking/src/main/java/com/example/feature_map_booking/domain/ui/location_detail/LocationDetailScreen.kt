package com.example.feature_map_booking.domain.ui.location_detail


// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/location_detail/LocationDetailScreen.kt


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Thêm scroll vì nội dung có thể dài
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = hospital.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        // --- THÊM MỚI: Section Kho Máu ---
        InventorySection(inventory = hospital.inventory)

        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        InfoRow(icon = Icons.Filled.Business, text = hospital.address)
        InfoRow(icon = Icons.Filled.Phone, text = hospital.phone)
        InfoRow(icon = Icons.Filled.Schedule, text = hospital.workingHours)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBookAppointment,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Đặt lịch hiến máu", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class) // Cần OptIn cho FlowRow
@Composable
fun InventorySection(inventory: Map<String, Int>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Bloodtype,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tình trạng kho máu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (inventory.isEmpty()) {
            Text("Chưa có dữ liệu tồn kho.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        } else {
            // Sử dụng FlowRow để các item tự xuống dòng
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sắp xếp để nhóm máu thiếu (critical) lên đầu
                val sortedInventory = inventory.entries.sortedBy { it.value }

                sortedInventory.forEach { (type, count) ->
                    val isCritical = count < 5 // Ngưỡng báo động đỏ

                    val backgroundColor = if (isCritical) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    val borderColor = if (isCritical) Color(0xFFEF5350) else Color(0xFF66BB6A)
                    val textColor = if (isCritical) Color(0xFFC62828) else Color(0xFF2E7D32)
                    val statusText = if (isCritical) "Thiếu ($count)" else "Ổn ($count)"

                    Surface(
                        color = backgroundColor,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor.copy(alpha = 0.8f)
                            )
                            if (isCritical) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Critical",
                                    tint = textColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
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