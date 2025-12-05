package com.example.feature_profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
// Import ViewModel từ module emergency
import com.example.feature_emergency.ui.history.EmergencyHistoryViewModel
import com.example.feature_emergency.domain.model.EmergencyDonationRecord
import com.smartblood.profile.domain.model.DonationRecord
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationHistoryScreen(
    // ViewModel cho lịch hẹn thường
    viewModel: DonationHistoryViewModel = hiltViewModel(),
    // ViewModel cho lịch sử khẩn cấp (Inject thêm vào đây)
    emergencyViewModel: EmergencyHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val emergencyState by emergencyViewModel.state.collectAsState()

    // Quản lý trạng thái Tab (0: Đặt lịch, 1: Khẩn cấp)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Theo lịch hẹn", "Khẩn cấp")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử hiến máu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5) // Màu nền xám nhạt
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- TAB ROW ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- NỘI DUNG THEO TAB ---
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: Lịch hẹn thông thường (Code cũ)
                    if (state.isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (state.history.isEmpty()) {
                        EmptyHistoryView("Bạn chưa có lịch sử đặt hẹn.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.history) { record ->
                                DonationHistoryItem(record = record)
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 2: Lịch sử khẩn cấp (Code mới)
                    if (emergencyState.isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (emergencyState.history.isEmpty()) {
                        EmptyHistoryView("Bạn chưa có lịch sử hiến khẩn cấp.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(emergencyState.history) { record ->
                                EmergencyHistoryItemCard(record = record)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- UI COMPONENTS ---

@Composable
fun EmptyHistoryView(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Gray)
    }
}

// Item cho Lịch hẹn thường (Giữ nguyên logic cũ nhưng cập nhật UI đẹp hơn)
@Composable
fun DonationHistoryItem(record: DonationRecord) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.hospitalName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = record.hospitalAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF388E3C) // Xanh lá
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Ngày hiến máu:", color = Color.Gray)
                Text(dateFormat.format(record.date), fontWeight = FontWeight.SemiBold)
            }

            // Nút xem chứng nhận (Nếu có)
            if (!record.certificateUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { uriHandler.openUri(record.certificateUrl!!) },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF2E7D32)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xem Chứng Nhận Hiến Máu", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// Item cho Lịch sử Khẩn cấp (MỚI)
@Composable
fun EmergencyHistoryItemCard(record: EmergencyDonationRecord) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val uriHandler = LocalUriHandler.current

    val (statusColor, statusText) = when(record.status) {
        "Completed" -> Color(0xFF4CAF50) to "Đã hiến"
        "Pending" -> Color(0xFFFF9800) to "Đang chờ"
        "Cancelled" -> Color(0xFFF44336) to "Đã hủy"
        else -> Color.Gray to record.status
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.hospitalName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Yêu cầu khẩn cấp",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))

            // Thông tin chi tiết
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Ngày tiếp nhận:", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Text(dateFormat.format(record.pledgedAt), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Nhóm máu hiến:", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Text(record.userBloodType, fontWeight = FontWeight.Bold, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
            }

            // Phần đánh giá từ bệnh viện (Chỉ hiện khi đã hoàn thành)
            if (record.status == "Completed") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp)).padding(8.dp).fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Đánh giá:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < record.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        if (!record.review.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"${record.review}\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            // Kiểm tra nếu có link chứng nhận
            record.certificateUrl?.let { url ->
                if (url.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { uriHandler.openUri(url) }, // Dùng biến 'url' đã được smart cast an toàn
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xem Chứng Nhận Hiến Máu", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}