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
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Xử lý hiển thị thông báo thành công
    LaunchedEffect(state.pledgeSuccess) {
        if (state.pledgeSuccess) {
            snackbarHostState.showSnackbar("Chấp nhận hiến máu thành công! Kiểm tra trong Hồ sơ.")
            // Báo cho ViewModel rằng thông báo đã được hiển thị để reset cờ
            viewModel.onEvent(DashboardEvent.OnPledgeSuccessMessageShown)
        }
    }

    // Xử lý hiển thị các lỗi khác
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar("Lỗi: $it")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // Kết hợp 2 trạng thái loading để hiển thị một chỉ báo tải duy nhất
        val isLoading = state.isLoadingProfile || state.isLoadingRequests

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading && state.displayableEmergencyRequests.isEmpty()) {
                // Chỉ hiển thị loading toàn màn hình khi khởi động lần đầu
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Thẻ thông tin cá nhân
                UserInfoCard(
                    userName = state.userName,
                    bloodType = state.bloodType,
                    nextDonationMessage = state.nextDonationMessage,
                    isLoading = state.isLoadingProfile
                )

                Text(
                    "Yêu cầu khẩn cấp gần đây:",
                    style = MaterialTheme.typography.titleMedium
                )

                // Vùng hiển thị danh sách yêu cầu
                Box(modifier = Modifier.weight(1f)) {
                    if (state.isLoadingRequests && state.displayableEmergencyRequests.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (state.displayableEmergencyRequests.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chưa có yêu cầu nào.")
                        }
                    } else {
                        LazyColumn(
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
}

@Composable
fun UserInfoCard(
    userName: String,
    bloodType: String,
    nextDonationMessage: String,
    isLoading: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        } else {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Chào mừng, $userName",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(text = "Nhóm máu: $bloodType")
                Text(
                    text = nextDonationMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
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