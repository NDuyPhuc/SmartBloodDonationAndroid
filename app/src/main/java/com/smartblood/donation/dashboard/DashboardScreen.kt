package com.smartblood.donation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartblood.core.domain.model.BloodRequest
import com.smartblood.core.ui.theme.PrimaryRed
import com.smartblood.core.ui.theme.PrimaryRedDark
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // State quản lý Dialog xác nhận
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedRequest by remember { mutableStateOf<BloodRequest?>(null) }
    var selectedVolume by remember { mutableStateOf("350") } // Mặc định 350ml

    // Xử lý hiển thị thông báo thành công
    LaunchedEffect(state.pledgeSuccess) {
        if (state.pledgeSuccess) {
            snackbarHostState.showSnackbar("Cảm ơn nghĩa cử cao đẹp của bạn! Vui lòng kiểm tra lịch hẹn trong Hồ sơ.")
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
        containerColor = Color(0xFFF8F9FA), // Màu nền xám rất nhạt cho toàn màn hình
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val isLoading = state.isLoadingProfile || state.isLoadingRequests

        if (isLoading && state.displayableEmergencyRequests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryRed)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // 1. Phần Header: Thông tin cá nhân
                    HomeHeaderSection(
                        userName = state.userName,
                        bloodType = state.bloodType,
                        nextDonationMessage = state.nextDonationMessage
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Tiêu đề danh sách
                    PaddingTextTitle(text = "Cần máu khẩn cấp")

                    // 3. Danh sách yêu cầu
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.displayableEmergencyRequests.isEmpty()) {
                            EmptyStateView()
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.displayableEmergencyRequests, key = { it.id }) { request ->
                                    EnhancedEmergencyRequestCard(
                                        request = request,
                                        isPledging = state.isPledging,
                                        isEligible = state.isEligibleToDonate,
                                        daysToWait = state.daysToWait,
                                        onAcceptClick = {
                                            // KHI CLICK NÚT TRÊN CARD:
                                            // 1. Lưu request đang chọn
                                            selectedRequest = request
                                            // 2. Reset volume về mặc định hoặc theo request yêu cầu (nếu có)
                                            val preferred = request.preferredVolume.replace("ml", "").trim()
                                            selectedVolume = if (preferred.isNotEmpty() && preferred.all { char -> char.isDigit() }) preferred else "350"
                                            // 3. Hiển thị Dialog
                                            showConfirmDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // --- DIALOG XÁC NHẬN (Đặt ở đây để đè lên nội dung) ---
                if (showConfirmDialog && selectedRequest != null) {
                    AlertDialog(
                        onDismissRequest = { showConfirmDialog = false },
                        title = {
                            Text(
                                text = "Xác nhận hiến máu",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    text = "Bạn đang đăng ký hiến máu cho:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = selectedRequest!!.hospitalName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryRed
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Chọn dung tích hiến:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Hàng nút chọn dung tích
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val volumes = listOf("250", "350", "450")
                                    volumes.forEach { vol ->
                                        val isSelected = selectedVolume == vol
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedVolume = vol },
                                            label = {
                                                Text(
                                                    text = "${vol}ml",
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            leadingIcon = if (isSelected) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                            } else null
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Bệnh viện khuyến khích: ${selectedRequest!!.preferredVolume}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showConfirmDialog = false
                                    // Gửi sự kiện kèm ID và Volume đã chọn
                                    viewModel.onEvent(
                                        DashboardEvent.OnAcceptRequestClicked(
                                            requestId = selectedRequest!!.id,
                                            volume = "${selectedVolume}ml"
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                            ) {
                                Text("Xác nhận đăng ký")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { showConfirmDialog = false }) {
                                Text("Hủy bỏ")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeaderSection(
    userName: String,
    bloodType: String,
    nextDonationMessage: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryRed, PrimaryRedDark)
                )
            )
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 30.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar / Icon
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Xin chào, $userName",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Badge nhóm máu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Nhóm máu: $bloodType",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                // Thông báo ngày hiến tiếp theo
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = nextDonationMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PaddingTextTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.Black.copy(alpha = 0.8f),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun EnhancedEmergencyRequestCard(
    request: BloodRequest,
    isPledging: Boolean,
    isEligible: Boolean,
    daysToWait: Long,
    onAcceptClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Card: Nhóm máu và Số lượng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon giọt máu lớn
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nhóm ${request.bloodType}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryRed
                    )
                }
                // Badge số lượng
                Surface(
                    color = PrimaryRed.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${request.quantity} đơn vị",
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimaryRedDark,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = 0.3f)
            )

            // Body Card: Thông tin bệnh viện
            InfoRowWithIcon(
                icon = Icons.Outlined.LocalHospital,
                text = request.hospitalName,
                color = Color.Black.copy(alpha = 0.8f),
                isBold = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- HIỂN THỊ DUNG TÍCH YÊU CẦU ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRowWithIcon(
                    icon = Icons.Outlined.AccessTime,
                    text = "Ngày: ${dateFormat.format(request.createdAt)}",
                    color = Color.Gray
                )

                // Badge dung tích yêu cầu
                Surface(
                    color = Color(0xFFE3F2FD), // Màu xanh nhạt
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Yêu cầu: ${request.preferredVolume}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF1565C0), // Màu xanh đậm
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Nút hành động
            if (isEligible) {
                Button(
                    onClick = onAcceptClick,
                    enabled = !isPledging,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRed,
                        disabledContainerColor = PrimaryRed.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isPledging) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đang xử lý...")
                    } else {
                        Text("Tôi muốn hiến máu", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // HIỂN THỊ CẢNH BÁO NẾU KHÔNG ĐỦ ĐIỀU KIỆN
                Surface(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bạn cần chờ thêm $daysToWait ngày để hồi phục.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRowWithIcon(
    icon: ImageVector,
    text: String,
    color: Color,
    isBold: Boolean = false
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp) // Căn chỉnh nhẹ với dòng đầu tiên của text
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ThumbUp,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hiện chưa có yêu cầu khẩn cấp nào.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Text(
            text = "Tuyệt vời! Mọi người đều đang khỏe mạnh.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray.copy(alpha = 0.7f)
        )
    }
}