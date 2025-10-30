// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/map/MapScreen.kt

package com.example.feature_map_booking.domain.ui.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.feature_map_booking.domain.ui.map.MapEvent
import com.example.feature_map_booking.domain.ui.map.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission") // Chúng ta đã xử lý quyền bên dưới
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToLocationDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var hasLocationPermission by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.onEvent(MapEvent.OnMapReady(LatLng(it.latitude, it.longitude)))
                }
            }
        }
    }

    val defaultLocation = LatLng(10.762622, 106.660172) // TP.HCM
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(state.lastKnownLocation ?: defaultLocation, 12f)
    }

    LaunchedEffect(state.lastKnownLocation) {
        state.lastKnownLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1000
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            state.hospitals.forEach { hospital ->
                hospital.location?.let { geoPoint ->
                    Marker(
                        state = MarkerState(position = LatLng(geoPoint.latitude, geoPoint.longitude)),
                        title = hospital.name,
                        snippet = hospital.address,
                        onInfoWindowClick = {
                            onNavigateToLocationDetail(hospital.id)
                        }
                    )
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        state.error?.let {
            // TODO: Hiển thị thông báo lỗi thân thiện hơn, ví dụ như một SnackBar
        }

        // TODO: Thêm UI cho bộ lọc và tìm kiếm ở đây (ví dụ: một nút ở góc màn hình)
    }
}