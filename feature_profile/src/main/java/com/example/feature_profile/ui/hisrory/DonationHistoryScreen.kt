//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\ui\hisrory\DonationHistoryScreen.kt
package com.smartblood.profile.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartblood.profile.domain.model.DonationRecord
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationHistoryScreen(viewModel: DonationHistoryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lịch sử Hiến máu") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading && state.history.isEmpty()) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(text = "Lỗi: ${state.error}", color = MaterialTheme.colorScheme.error)
            } else if (state.history.isEmpty()) {
                Text("Bạn chưa có lịch sử hiến máu nào.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.history) { record ->
                        DonationHistoryItem(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun DonationHistoryItem(record: DonationRecord) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(text = record.hospitalName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ngày hiến: ${dateFormat.format(record.date)}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Số đơn vị: ${record.unitsDonated}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}