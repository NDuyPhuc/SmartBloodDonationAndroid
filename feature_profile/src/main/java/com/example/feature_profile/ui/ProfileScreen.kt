package com.example.feature_profile.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.smartblood.core.domain.model.Appointment
import com.smartblood.core.domain.model.UserProfile
import com.smartblood.core.ui.theme.PrimaryRed
import com.smartblood.core.ui.theme.PrimaryRedDark
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

    // Biến quản lý trạng thái hiển thị BottomSheet chọn ảnh
    var showAvatarBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    // Lắng nghe sự kiện đăng xuất
    LaunchedEffect(state.isSignedOut) {
        if (state.isSignedOut) {
            onNavigateToLogin()
        }
    }

    // Launcher mở thư viện ảnh hệ thống
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onAvatarChange(it)
            showAvatarBottomSheet = false // Đóng sheet khi chọn xong
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryRed)
            }
        } else if (state.error != null && state.userProfile == null) {
            NotLoggedInView(error = state.error, onLoginClick = onNavigateToLogin)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. HEADER PROFILE
                item {
                    state.userProfile?.let { profile ->
                        ProfileHeaderSection(
                            profile = profile,
                            isUploading = state.isUploading,
                            onAvatarClick = { showAvatarBottomSheet = true }
                        )
                    }
                }

                // 2. CÁC NÚT CHỨC NĂNG
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionButton(
                            text = "Chỉnh sửa",
                            icon = Icons.Default.Edit,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToEditProfile
                        )
                        ActionButton(
                            text = "Lịch sử",
                            icon = Icons.Default.History,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToDonationHistory
                        )
                    }
                }

                // 3. DANH SÁCH YÊU CẦU ĐÃ CHẤP NHẬN
                if (state.pledgedRequests.isNotEmpty()) {
                    item { SectionHeader("Yêu cầu đã chấp nhận") }
                    items(state.pledgedRequests) { request ->
                        GenericInfoCard(
                            title = request.hospitalName,
                            detailLine1 = "Cần nhóm máu: ${request.bloodType}",
                            detailLine2 = "Ngày tạo: ${formatDate(request.createdAt)}",
                            status = "ĐÃ CHẤP NHẬN",
                            statusColor = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // 4. DANH SÁCH LỊCH HẸN
                if (state.todayAppointments.isNotEmpty()) {
                    item { SectionHeader("Lịch hẹn hôm nay") }
                    items(state.todayAppointments) { appt ->
                        AppointmentCard(appt, isPast = false)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (state.upcomingAppointments.isNotEmpty()) {
                    item { SectionHeader("Lịch hẹn sắp tới") }
                    items(state.upcomingAppointments) { appt ->
                        AppointmentCard(appt, isPast = false)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // 5. NÚT ĐĂNG XUẤT
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { viewModel.onEvent(ProfileEvent.OnSignOutClicked) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Red
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đăng xuất")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // --- BOTTOM SHEET CHỌN AVATAR ---
        if (showAvatarBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAvatarBottomSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                AvatarSelectionSheetContent(
                    onGalleryClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    onPresetSelected = { uri ->
                        viewModel.onAvatarChange(uri)
                        showAvatarBottomSheet = false
                    },
                    onClose = { showAvatarBottomSheet = false }
                )
            }
        }
    }
}

// --- NỘI DUNG BOTTOM SHEET (CẬP NHẬT: Nút đỏ & 24 Avatar) ---
@Composable
fun AvatarSelectionSheetContent(
    onGalleryClick: () -> Unit,
    onPresetSelected: (Uri) -> Unit,
    onClose: () -> Unit
) {
    // Danh sách 24 Avatar phong phú
    val presets = remember {
        listOf(
            // Phong cách 1: Thám hiểm
            "https://api.dicebear.com/9.x/adventurer/png?seed=Felix",
            "https://api.dicebear.com/9.x/adventurer/png?seed=Aneka",
            "https://api.dicebear.com/9.x/adventurer/png?seed=Snow",
            "https://api.dicebear.com/9.x/adventurer/png?seed=Ginger",
            "https://api.dicebear.com/9.x/adventurer/png?seed=Abner",
            "https://api.dicebear.com/9.x/adventurer/png?seed=Coco",
            // Phong cách 2: Hiện đại
            "https://api.dicebear.com/9.x/avataaars/png?seed=Sophy",
            "https://api.dicebear.com/9.x/avataaars/png?seed=Alexander",
            "https://api.dicebear.com/9.x/avataaars/png?seed=Nolan",
            "https://api.dicebear.com/9.x/avataaars/png?seed=Zoe",
            "https://api.dicebear.com/9.x/avataaars/png?seed=Midnight",
            "https://api.dicebear.com/9.x/avataaars/png?seed=Luna",
            // Phong cách 3: Nghệ thuật
            "https://api.dicebear.com/9.x/lorelei/png?seed=Robert",
            "https://api.dicebear.com/9.x/lorelei/png?seed=Mitten",
            "https://api.dicebear.com/9.x/lorelei/png?seed=Gizmo",
            "https://api.dicebear.com/9.x/lorelei/png?seed=Cali",
            "https://api.dicebear.com/9.x/lorelei/png?seed=Oreo",
            "https://api.dicebear.com/9.x/lorelei/png?seed=Boots",
            // Phong cách 4: Tối giản
            "https://api.dicebear.com/9.x/micah/png?seed=Callie",
            "https://api.dicebear.com/9.x/micah/png?seed=Simba",
            "https://api.dicebear.com/9.x/micah/png?seed=Pepper",
            "https://api.dicebear.com/9.x/micah/png?seed=Bubba",
            "https://api.dicebear.com/9.x/micah/png?seed=Missy",
            "https://api.dicebear.com/9.x/micah/png?seed=Scooter"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Đổi ảnh đại diện",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === NÚT MÀU ĐỎ (PRIMARY RED) ===
        Button(
            onClick = onGalleryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryRed, // Đỏ
                contentColor = Color.White   // Trắng
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Tải ảnh từ thư viện máy", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        // =================================

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Hoặc chọn Avatar có sẵn",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Danh sách Avatar (Lưới cao hơn để hiển thị nhiều hình)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(300.dp) // Tăng chiều cao khung nhìn
        ) {
            items(presets) { url ->
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(70.dp)
                        .clickable { onPresetSelected(Uri.parse(url)) },
                    color = Color(0xFFF0F0F0),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

// --- CÁC COMPOSABLE KHÁC GIỮ NGUYÊN ---

@Composable
fun ProfileHeaderSection(
    profile: UserProfile,
    isUploading: Boolean,
    onAvatarClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(
                    Brush.verticalGradient(colors = listOf(PrimaryRed, PrimaryRedDark))
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(128.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = profile.avatarUrl ?: "https://ui-avatars.com/api/?name=${profile.fullName}&background=random&size=256",
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .clickable { onAvatarClick() }
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-10).dp, y = (-10).dp)
                        .size(36.dp)
                        .clickable { onAvatarClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Edit Avatar",
                        modifier = Modifier.padding(8.dp),
                        tint = Color.Gray
                    )
                }

                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = profile.fullName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = PrimaryRed.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "Nhóm máu: ${profile.bloodType ?: "Chưa cập nhật"}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryRedDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
        color = Color.Black.copy(alpha = 0.8f)
    )
}

@Composable
fun GenericInfoCard(
    title: String,
    detailLine1: String,
    detailLine2: String,
    status: String,
    statusColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
            Text(text = detailLine1, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = detailLine2, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.labelLarge,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment, isPast: Boolean) {
    // Xác định trạng thái và màu sắc dựa trên dữ liệu Firebase
    val (statusText, statusColor) = when {
        isPast -> "ĐÃ KẾT THÚC" to Color.Gray
        appointment.status == "CONFIRMED" -> "ĐÃ DUYỆT" to Color(0xFF388E3C) // Màu xanh lá
        appointment.status == "PENDING" -> "CHỜ DUYỆT" to Color(0xFFFFA000) // Màu vàng cam
        appointment.status == "CANCELLED" -> "ĐÃ HỦY" to Color.Red
        else -> appointment.status to Color.Gray
    }

    GenericInfoCard(
        title = appointment.hospitalName,
        detailLine1 = appointment.hospitalAddress,
        detailLine2 = "Thời gian: ${formatDateTime(appointment.dateTime)}",
        status = statusText,
        statusColor = statusColor
    )
}

@Composable
fun NotLoggedInView(error: String?, onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bạn chưa đăng nhập",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Vui lòng đăng nhập để quản lý hồ sơ và lịch hẹn.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng nhập ngay")
        }
        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Lỗi: $error", color = Color.Red, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun formatDateTime(date: Date): String {
    return SimpleDateFormat("dd/MM/yyyy 'lúc' HH:mm", Locale.getDefault()).format(date)
}

@Composable
private fun formatDate(date: Date): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
}