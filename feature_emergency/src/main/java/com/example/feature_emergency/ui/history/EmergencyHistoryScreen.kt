package com.example.feature_emergency.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.feature_emergency.domain.model.EmergencyDonationRecord
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyHistoryScreen(
    viewModel: EmergencyHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử hiến máu khẩn cấp") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.history.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có lịch sử hiến máu khẩn cấp.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.history) { record ->
                    EmergencyHistoryItem(record = record)
                }
            }
        }
    }
}

/**
 * Component hiển thị item lịch sử khẩn cấp.
 * Được public để DonationHistoryScreen (Module Profile) có thể gọi dùng chung.
 */
@Composable
fun EmergencyHistoryItem(record: EmergencyDonationRecord) {
    val dateFormat = remember { SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()) }
    val uriHandler = LocalUriHandler.current

    // --- 1. CHUẨN HÓA DỮ LIỆU ---
    // Xử lý status linh hoạt, không phân biệt hoa thường
    val status = record.status.trim()

    // --- 2. XÁC ĐỊNH TRẠNG THÁI TỪ CHỐI ---
    // Kiểm tra kỹ: Hoặc status là từ chối, HOẶC có lý do từ chối đi kèm
    val isRejected = status.equals("Rejected", ignoreCase = true) ||
            status.equals("Cancelled", ignoreCase = true) ||
            status.equals("Bị từ chối", ignoreCase = true) ||
            !record.rejectionReason.isNullOrBlank()

    // --- 3. CẤU HÌNH MÀU SẮC VÀ TEXT ---
    val (statusColor, statusBgColor, statusText) = when {
        // Hoàn thành
        status.equals("Completed", ignoreCase = true) || status.equals("Đã hiến", ignoreCase = true) ->
            Triple(Color(0xFF4CAF50), Color(0xFFE8F5E9), "Đã hiến")

        // Từ chối
        isRejected ->
            Triple(Color(0xFFD32F2F), Color(0xFFFFEBEE), "Đã bị từ chối")

        // Đang chờ
        status.equals("Pending", ignoreCase = true) || status.equals("Pledged", ignoreCase = true) ->
            Triple(Color(0xFFFF9800), Color(0xFFFFF3E0), "Đang chờ")

        // Mặc định (giữ nguyên text gốc nếu không khớp)
        else -> Triple(Color.Gray, Color(0xFFF5F5F5), record.status)
    }

    // Viền đỏ nếu bị từ chối
    val cardBorder = if (isRejected) BorderStroke(1.dp, Color(0xFFFFCDD2)) else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = cardBorder
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- HEADER: Tên bệnh viện & Badge trạng thái ---
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.hospitalName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Chỉ hiện label "Yêu cầu khẩn cấp" nếu không bị từ chối (cho đỡ rối)
                    if (!isRejected) {
                        Text(
                            text = "Yêu cầu khẩn cấp",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red
                        )
                    }
                }
                Surface(
                    color = statusBgColor,
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

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = 0.3f)
            )

            // --- THÔNG TIN CHI TIẾT ---
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tiếp nhận lúc:", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Text(dateFormat.format(record.pledgedAt), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nhóm máu:", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Text(record.userBloodType, fontWeight = FontWeight.Bold, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
            }

            // --- PHẦN 1: HIỂN THỊ LÝ DO TỪ CHỐI (FIX LỖI 2) ---
            if (isRejected && !record.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF5F5), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lý do từ chối:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = record.rejectionReason ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB71C1C),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // --- PHẦN 2: HIỂN THỊ KẾT QUẢ XÉT NGHIỆM (FIX LỖI 1) ---
            // Chỉ hiển thị nếu KHÔNG bị từ chối và CÓ dữ liệu labResult
            if (!isRejected && record.labResult != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Kết quả xét nghiệm",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    }

                    // Hiển thị lời dặn bác sĩ
                    if (!record.labResult.conclusion.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bác sĩ: ${record.labResult.conclusion}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }

                    // Nút xem file PDF
                    val docUrl = record.labResult.documentUrl
                    if (!docUrl.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                try {
                                    uriHandler.openUri(docUrl)
                                } catch (e: Exception) {
                                    // Log lỗi nếu không mở được link
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            // Icon PDF nhỏ
                            Icon(Icons.Default.Description, null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Xem file kết quả (PDF)", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Hiển thị nút chứng nhận (logic cũ - giữ lại nếu cần)
            else if (!isRejected && !record.certificateUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { uriHandler.openUri(record.certificateUrl) },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF388E3C)),
                    border = BorderStroke(1.dp, Color(0xFF388E3C).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chứng Nhận Hiến Máu", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}