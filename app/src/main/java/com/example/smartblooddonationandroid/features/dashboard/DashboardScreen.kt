package com.smartblood.donation.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartblood.core.domain.model.BloodRequest
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Xử lý hiển thị Snackbar
    LaunchedEffect(state.pledgeSuccess) {
        if (state.pledgeSuccess) {
            snackbarHostState.showSnackbar("Chấp nhận hiến máu thành công!")
            viewModel.onEvent(DashboardEvent.OnPledgeSuccessMessageShown)
        }
    }

    // Xử lý hiển thị lỗi
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar("Lỗi: $it")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Sử dụng padding từ Scaffold
                .padding(16.dp), // Thêm padding riêng cho nội dung
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Thẻ thông tin cá nhân
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Chào mừng, ${state.userName}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(text = "Nhóm máu: ${state.bloodType}")
                        Text(
                            text = state.nextDonationMessage,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text(
                    "Yêu cầu khẩn cấp gần đây:",
                    style = MaterialTheme.typography.titleMedium
                )

                if (state.displayableEmergencyRequests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chưa có yêu cầu nào.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.displayableEmergencyRequests, key = { it.id }) { request ->
                            EmergencyRequestCard(
                                request = request,
                                isPledging = state.isPledging,
                                onAcceptClick = {
                                    viewModel.onEvent(DashboardEvent.OnAcceptRequestClicked(request.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun EmergencyRequestCard(
    request: BloodRequest,
    isPledging: Boolean,
    onAcceptClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cần nhóm máu: ${request.bloodType}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Số lượng: ${request.quantity} đơn vị",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "Tại: ${request.hospitalName}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Ngày tạo: ${dateFormat.format(request.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAcceptClick,
                enabled = !isPledging,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (isPledging) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Chấp nhận hiến máu")
                }
            }
        }
    }
}