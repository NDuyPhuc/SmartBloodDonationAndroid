// feature_map_booking/src/main/java/com/example/feature_map_booking/domain/ui/map/MapScreen.kt

package com.example.feature_map_booking.domain.ui.map

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToLocationDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    Log.d("MapDebug", "MapScreen recomposed. Hospitals count: ${state.hospitals.size}")

    Box(Modifier.fillMaxSize()) {
        // Gọi Composable bản đồ và truyền dữ liệu vào
        TrackAsiaMapComposable(
            hospitals = state.hospitals,
            onMarkerClick = onNavigateToLocationDetail,
//            onMapReady = { viewModel.onEvent(MapEvent.OnMapLoaded) }
        )

        // Vẫn hiển thị loading indicator từ ViewModel
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    // Trigger ViewModel tải dữ liệu bệnh viện (dữ liệu giả)
    LaunchedEffect(Unit) {
        viewModel.onEvent(MapEvent.OnMapLoaded)
    }
}