//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\ui\ProfileScreen.kt
package com.smartblood.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToEditProfile: () -> Unit,
    onNavigateToDonationHistory: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Hồ sơ của tôi") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(text = "Lỗi: ${state.error}", color = MaterialTheme.colorScheme.error)
            } else if (state.userProfile != null) {
                val profile = state.userProfile!!
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = profile.avatarUrl ?: "https://example.com/default_avatar.png", // Thay bằng link ảnh mặc định
                        contentDescription = "Ảnh đại diện",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = profile.fullName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Email: ${profile.email}")
                    Text(text = "Nhóm máu: ${profile.bloodType ?: "Chưa cập nhật"}")

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = onNavigateToEditProfile) {
                        Text("Chỉnh sửa thông tin")
                    }
                    OutlinedButton(onClick = onNavigateToDonationHistory) {
                        Text("Xem lịch sử hiến máu")
                    }
                }
            }
        }
    }
}