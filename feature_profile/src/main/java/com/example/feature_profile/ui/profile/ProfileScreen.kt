// Vị trí: feature_profile/src/main/java/com/smartblood/profile/ui/profile/ProfileScreen.kt

package com.smartblood.profile.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.smartblood.profile.ui.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    // BƯỚC 1: Thêm 2 tham số này để nhận hành động từ bên ngoài
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
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // (Bạn có thể thêm AsyncImage để hiển thị avatar ở đây)

                    Text(
                        text = profile.fullName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Email: ${profile.email}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Nhóm máu: ${profile.bloodType ?: "Chưa cập nhật"}")

                    Spacer(modifier = Modifier.height(32.dp))

                    // BƯỚC 2: Kết nối nút bấm với các hàm đã truyền vào
                    Button(
                        onClick = onNavigateToEditProfile, // <--- KẾT NỐI Ở ĐÂY
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Chỉnh sửa thông tin")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onNavigateToDonationHistory, // <--- VÀ Ở ĐÂY
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Xem lịch sử hiến máu")
                    }
                }
            }
        }
    }
}