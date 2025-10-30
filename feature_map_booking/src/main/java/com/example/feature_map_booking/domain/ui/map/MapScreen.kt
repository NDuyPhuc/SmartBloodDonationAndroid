// feature_map_booking/src/main/java/com/example/feature_map_booking/domain/ui/map/MapScreen.kt

package com.example.feature_map_booking.domain.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonParser
import com.trackasia.android.TrackAsia
import com.trackasia.android.camera.CameraUpdateFactory
import com.trackasia.android.geometry.LatLng
import com.trackasia.android.maps.MapView
import com.trackasia.android.maps.Style
import com.trackasia.android.maps.TrackAsiaMap
import com.trackasia.android.plugins.annotation.SymbolManager
import com.trackasia.android.plugins.annotation.SymbolOptions

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToLocationDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // ** THAY ĐỔI 1: Khởi tạo TrackAsia ở đây **
    // Chúng ta gọi getInstance ngay khi Composable được tạo, tương tự như trong onCreateView
    TrackAsia.getInstance(context)

    val mapView = rememberMapViewWithLifecycle()
    var trackasiaMap by remember { mutableStateOf<TrackAsiaMap?>(null) }
    var symbolManager by remember { mutableStateOf<SymbolManager?>(null) }

    // --- Phần xử lý quyền và vị trí giữ nguyên ---
    var hasLocationPermission by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.onEvent(MapEvent.OnMapReady(LatLng(it.latitude, it.longitude)))
                }
            }
        }
    }
    // ------------------------------------------

    // ** THAY ĐỔI 2: Sử dụng Style URL từ tài liệu **
    val styleUrl = "https://maps.track-asia.com/styles/v1/streets.json?key=public_key"

    Box(Modifier.fillMaxSize()) {
        AndroidView({ mapView }) { view ->
            view.getMapAsync { map ->
                trackasiaMap = map
                map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                    symbolManager = SymbolManager(view, map, style).apply {
                        this.iconAllowOverlap = true
                        addClickListener { symbol ->
                            val hospitalId = symbol.data?.asJsonObject?.get("hospital_id")?.asString
                            hospitalId?.let(onNavigateToLocationDetail)
                            true
                        }
                    }
                }
            }
        }

        // Cập nhật vị trí camera khi có vị trí của người dùng
        LaunchedEffect(state.lastKnownLocation, trackasiaMap) {
            state.lastKnownLocation?.let { loc ->
                trackasiaMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(loc, 14.0),
                    1500
                )
            }
        }

        // Vẽ lại markers khi danh sách bệnh viện thay đổi
        LaunchedEffect(state.hospitals, symbolManager) {
            symbolManager?.let { manager ->
                manager.deleteAll()
                val options = state.hospitals.mapNotNull { hospital ->
                    hospital.location?.let { geoPoint ->
                        SymbolOptions()
                            .withLatLng(LatLng(geoPoint.latitude, geoPoint.longitude))
                            .withIconImage("attraction-15")
                            .withData(JsonParser.parseString("""{"hospital_id": "${hospital.id}"}"""))
                    }
                }
                if (options.isNotEmpty()) {
                    manager.create(options)
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

// Helper Composable để quản lý vòng đời (giữ nguyên)
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    // ... nội dung hàm này giữ nguyên như cũ ...
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            mapView.onDestroy()
        }
    }
    return mapView
}