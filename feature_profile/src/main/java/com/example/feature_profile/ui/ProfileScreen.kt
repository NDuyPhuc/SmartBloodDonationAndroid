//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\ui\ProfileScreen.kt
package com.example.feature_profile.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.feature_map_booking.domain.model.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EditProfileViewModel.EditProfileScreen(
//    onNavigateBack: () -> Unit
//) {
//    val state by state.collectAsState()
//    val scrollState = rememberScrollState() // Để cuộn nội dung
//
//    // --- LOGIC CHỌN ẢNH ---
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        onEvent(EditProfileEvent.OnAvatarChanged(uri)) // <<-- GỬI EVENT KHI CHỌN ẢNH
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Chỉnh sửa hồ sơ") },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            if (state.isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            } else if (state.error != null) {
//                Text(
//                    text = "Lỗi: ${state.error}",
//                    modifier = Modifier.align(Alignment.Center),
//                    color = MaterialTheme.colorScheme.error
//                )
//            } else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp)
//                        .verticalScroll(scrollState), // <<-- THÊM SCROLL STATE
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    // --- PHẦN THÔNG TIN USER VÀ THAY ĐỔI ẢNH ---
//                    state.userProfile?.let { profile -> // Sử dụng userProfile ban đầu để lấy URL
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Box(contentAlignment = Alignment.Center) {
//                                // Hiển thị ảnh đang được chọn hoặc ảnh cũ
//                                AsyncImage(
//                                    model = state.avatarUri ?: profile.avatarUrl ?: "https://i.imgur.com/L5n5sH1.png", // Link ảnh mặc định
//                                    contentDescription = "Ảnh đại diện",
//                                    modifier = Modifier
//                                        .size(120.dp)
//                                        .clip(CircleShape)
//                                        .clickable { imagePickerLauncher.launch("image/*") }, // Nhấn để chọn ảnh
//                                    contentScale = ContentScale.Crop
//                                )
//                                // Hiển thị tiến trình tải ảnh nếu đang upload
//                                if (state.isUploading) {
//                                    CircularProgressIndicator()
//                                }
//                            }
//                            Spacer(Modifier.height(8.dp))
//                            Text(
//                                text = "Nhấn vào ảnh để thay đổi",
//                                fontSize = 12.sp,
//                                color = Color.Gray
//                            )
//                        }
//
//                        Spacer(Modifier.height(8.dp))
//                        Text(
//                            text = profile.fullName,
//                            style = MaterialTheme.typography.headlineSmall,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            text = "Email: ${profile.email}",
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                        OutlinedTextField(
//                            value = state.bloodType, // Sử dụng state.bloodType để bind với text field
//                            onValueChange = { onEvent(EditProfileEvent.OnBloodTypeChanged(it)) },
//                            label = { Text("Nhóm máu (ví dụ: A+, O-)") },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                        OutlinedTextField(
//                            value = state.phoneNumber ?: "", // Sử dụng state.phoneNumber
//                            onValueChange = { onEvent(EditProfileEvent.OnPhoneNumberChanged(it)) },
//                            label = { Text("Số điện thoại") },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút Save xuống dưới
//
//                        Button(
//                            onClick = { onEvent(EditProfileEvent.OnSaveClicked) },
//                            enabled = !state.isSaving, // Vô hiệu hóa khi đang lưu
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(50.dp)
//                        ) {
//                            if (state.isSaving) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier.size(24.dp),
//                                    color = MaterialTheme.colorScheme.onPrimary,
//                                    strokeWidth = 2.dp
//                                )
//                            } else {
//                                Text("Lưu thay đổi")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToEditProfile: () -> Unit,
    onNavigateToDonationHistory: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    // --- LOGIC CHỌN ẢNH ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAvatarChange(it) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Hồ sơ của tôi") }) }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Lỗi: ${state.error}", color = MaterialTheme.colorScheme.error) }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
            ) {
                // --- PHẦN THÔNG TIN USER VÀ TẢI ẢNH ---
                // --- PHẦN THÔNG TIN USER VÀ HIỂN THỊ ẢNH ---
                item {
                    // <<-- SỬA Ở ĐÂY: Truy cập state.userProfile
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
                                if(state.isUploading) {
                                    CircularProgressIndicator()
                                }
                            }
                            Text(text = "Nhấn vào ảnh để thay đổi", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(8.dp))
                            // <<-- SỬA Ở ĐÂY: Truy cập profile.fullName và profile.email
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

                item { Divider(modifier = Modifier.padding(vertical = 16.dp)) }

                // --- PHẦN LỊCH HẸN ---
                if (state.todayAppointments.isEmpty() && state.upcomingAppointments.isEmpty() && state.pastAppointments.isEmpty()) {
                    item { Text("Bạn chưa có lịch hẹn nào.", modifier = Modifier.padding(top = 16.dp)) }
                } else {
                    if (state.todayAppointments.isNotEmpty()) {
                        item { SectionTitle("Lịch hẹn hôm nay") }
                        items(state.todayAppointments) { appointment -> AppointmentCard(appointment) }
                    }
                    if (state.upcomingAppointments.isNotEmpty()) {
                        item { SectionTitle("Lịch hẹn sắp tới") }
                        items(state.upcomingAppointments) { appointment -> AppointmentCard(appointment) }
                    }
                    if (state.pastAppointments.isNotEmpty()) {
                        item { SectionTitle("Lịch hẹn đã qua") }
                        items(state.pastAppointments) { appointment -> AppointmentCard(appointment, isPast = true) }
                    }
                }
            }
        }
    }
}

// Composable để hiển thị tiêu đề cho mỗi section
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

// Composable để hiển thị thông tin của một lịch hẹn
@Composable
fun AppointmentCard(appointment: Appointment, isPast: Boolean = false) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy 'lúc' HH:mm", Locale.getDefault()) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = appointment.hospitalName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = appointment.hospitalAddress, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(text = "Thời gian: ${dateFormat.format(appointment.dateTime)}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Trạng thái: ${appointment.status}", style = MaterialTheme.typography.bodyMedium, color = if(appointment.status == "CONFIRMED") Color(0xFF388E3C) else Color.Gray)
        }
    }
}