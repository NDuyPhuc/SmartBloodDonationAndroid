// Vị trí: feature_profile/src/main/java/com/smartblood/profile/ui/edit/EditProfileScreen.kt
package com.smartblood.profile.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Tự động quay về màn hình trước khi cập nhật thành công
    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Chỉnh sửa Hồ sơ") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.userProfile != null) {
                val profile = state.userProfile!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = profile.fullName,
                        onValueChange = { newName ->
                            viewModel.onProfileChange(profile.copy(fullName = newName))
                        },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = profile.phoneNumber ?: "",
                        onValueChange = { newPhone ->
                            viewModel.onProfileChange(profile.copy(phoneNumber = newPhone))
                        },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = profile.bloodType ?: "",
                        onValueChange = { newBloodType ->
                            viewModel.onProfileChange(profile.copy(bloodType = newBloodType))
                        },
                        label = { Text("Nhóm máu (ví dụ: A+)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { viewModel.saveProfile() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text("Lưu thay đổi")
                    }
                }
            }

            state.error?.let { error ->
                // Có thể hiển thị lỗi bằng Snackbar hoặc Dialog
                Text(text = "Lỗi: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}