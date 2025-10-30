//D:\SmartBloodDonationAndroid\app\src\main\java\com\example\smartblooddonationandroid\features\dashboard\DashboardScreen.kt
package com.smartblood.donation.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Thẻ thông tin cá nhân
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = "Chào mừng, ${state.userName}", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "Nhóm máu: ${state.bloodType}")
                    Text(text = state.nextDonationMessage, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Các nút Call-to-Action
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Tìm điểm hiến máu gần đây")
            }
            OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Xem yêu cầu khẩn cấp")
            }

            // Danh sách yêu cầu khẩn cấp (tạm thời)
            Text("Yêu cầu khẩn cấp gần đây:", style = MaterialTheme.typography.titleMedium)
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                Text("Chưa có yêu cầu nào.")
            }
        }
    }
}