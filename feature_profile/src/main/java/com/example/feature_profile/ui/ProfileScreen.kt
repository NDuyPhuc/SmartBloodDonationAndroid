package com.example.feature_profile.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.smartblood.core.domain.model.Appointment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToEditProfile: () -> Unit,
    onNavigateToDonationHistory: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Lắng nghe sự kiện đăng xuất thành công
    LaunchedEffect(state.isSignedOut) {
        if (state.isSignedOut) {
            onNavigateToLogin()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAvatarChange(it) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Hồ sơ của tôi") }) }
    ) { paddingValues ->
        if (state.isLoading) {
            // Trạng thái đang tải
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            // --- TRẠNG THÁI LỖI / CHƯA ĐĂNG NHẬP (ĐÃ SỬA) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bạn chưa đăng nhập",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vui lòng đăng nhập để xem hồ sơ, lịch sử hiến máu và các lịch hẹn của bạn.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Nút chuyển về màn hình Đăng nhập
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Đăng nhập ngay")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hiển thị chi tiết lỗi nhỏ ở dưới (để debug)
                Text(
                    text = "Chi tiết lỗi hệ thống: ${state.error}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
            // ------------------------------------------------
        } else {
            // Trạng thái hiển thị dữ liệu (Đã đăng nhập)
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
            ) {
                // 1. THÔNG TIN USER VÀ TẢI ẢNH
                item {
                    state.userProfile?.let { profile ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = profile.avatarUrl ?: "https://i.imgur.com/L5n5sH1.png",
                                    contentDescription = "Ảnh đại diện",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .clickable { imagePickerLauncher.launch("image/*") },
                                    contentScale = ContentScale.Crop
                                )
                                if (state.isUploading) {
                                    CircularProgressIndicator()
                                }
                            }
                            Text(text = "Nhấn vào ảnh để thay đổi", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(8.dp))
                            Text(text = profile.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Email: ${profile.email}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Nhóm máu: ${profile.bloodType ?: "Chưa cập nhật"}", style = MaterialTheme.typography.bodyLarge)

                            Spacer(Modifier.height(16.dp))
                            Button(onClick = onNavigateToEditProfile, modifier = Modifier.fillMaxWidth()) {
                                Text("Chỉnh sửa thông tin")
                            }
                            OutlinedButton(onClick = onNavigateToDonationHistory, modifier = Modifier.fillMaxWidth()) {
                                Text("Xem lịch sử hiến máu")
                            }
                        }
                    }
                }

                item { HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) }

                // 2. YÊU CẦU ĐÃ CHẤP NHẬN
                if (state.pledgedRequests.isNotEmpty()) {
                    item { SectionTitle("Yêu cầu đã chấp nhận") }
                    items(state.pledgedRequests) { request ->
                        GenericInfoCard(
                            title = request.hospitalName,
                            detailLine1 = "Cần nhóm máu: ${request.bloodType}",
                            detailLine2 = "Ngày yêu cầu: ${formatDate(request.createdAt)}",
                            status = "ĐÃ CHẤP NHẬN",
                            statusColor = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) }
                }

                // 3. LỊCH HẸN
                if (state.todayAppointments.isEmpty() && state.upcomingAppointments.isEmpty() && state.pastAppointments.isEmpty()) {
                    item { Text("Bạn chưa có lịch hẹn nào.", modifier = Modifier.padding(top = 16.dp)) }
                } else {
                    if (state.todayAppointments.isNotEmpty()) {
                        item { SectionTitle("Lịch hẹn hôm nay") }
                        items(state.todayAppointments) { appointment ->
                            AppointmentCard(appointment = appointment, isPast = false)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    if (state.upcomingAppointments.isNotEmpty()) {
                        item { SectionTitle("Lịch hẹn sắp tới") }
                        items(state.upcomingAppointments) { appointment ->
                            AppointmentCard(appointment = appointment, isPast = false)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    if (state.pastAppointments.isNotEmpty()) {
                        item { SectionTitle("Lịch hẹn đã qua") }
                        items(state.pastAppointments) { appointment ->
                            AppointmentCard(appointment = appointment, isPast = true)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // NÚT ĐĂNG XUẤT (Khi đã đăng nhập)
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
                    Button(
                        onClick = { viewModel.onEvent(ProfileEvent.OnSignOutClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Đăng xuất", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun AppointmentCard(appointment: Appointment, isPast: Boolean = false) {
    GenericInfoCard(
        title = appointment.hospitalName,
        detailLine1 = appointment.hospitalAddress,
        detailLine2 = "Thời gian: ${formatDateTime(appointment.dateTime)}",
        status = appointment.status,
        statusColor = if (appointment.status == "CONFIRMED") Color(0xFF388E3C) else Color.Gray,
        isPast = isPast
    )
}

@Composable
fun GenericInfoCard(
    title: String,
    detailLine1: String,
    detailLine2: String,
    status: String,
    statusColor: Color,
    isPast: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = detailLine1, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(text = detailLine2, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Trạng thái: $status", style = MaterialTheme.typography.bodyMedium, color = statusColor)
        }
    }
}

@Composable
private fun formatDateTime(date: Date): String {
    return remember { SimpleDateFormat("dd/MM/yyyy 'lúc' HH:mm", Locale.getDefault()) }.format(date)
}

@Composable
private fun formatDate(date: Date): String {
    return remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }.format(date)
}