// feature_map_booking/src/main/java/com/example/feature_map_booking/domain/ui/map/MapScreen.kt

package com.example.feature_map_booking.domain.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonParser
import com.trackasia.android.TrackAsia
import com.trackasia.android.camera.CameraUpdateFactory
import com.trackasia.android.geometry.LatLng
import com.trackasia.android.location.LocationComponent
import com.trackasia.android.location.LocationComponentActivationOptions
import com.trackasia.android.location.modes.CameraMode
import com.trackasia.android.location.modes.RenderMode
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
    val mapView = rememberMapViewWithLifecycle()
    var trackasiaMap by remember { mutableStateOf<TrackAsiaMap?>(null) }
    var locationComponent by remember { mutableStateOf<LocationComponent?>(null) }

    // --- Xử lý quyền ---
    var hasLocationPermission by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    // --------------------

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                TrackAsia.getInstance(it)
                mapView.apply {
                    onCreate(null)
                    getMapAsync { map ->
                        trackasiaMap = map
                        val styleUrl = "https://maps.track-asia.com/styles/v1/streets.json?key=public_key"
                        map.setStyle(Style.Builder().fromUri(styleUrl))
                    }
                }
            },
            update = { view ->
                view.getMapAsync { map ->
                    map.getStyle { style ->
                        if (style.isFullyLoaded) {
                            // 1. Cập nhật vị trí người dùng
                            if (hasLocationPermission) {
                                val component = map.locationComponent
                                if (!component.isLocationComponentActivated) {
                                    component.activateLocationComponent(
                                        LocationComponentActivationOptions.builder(context, style).build()
                                    )
                                }
                                component.isLocationComponentEnabled = true
                                // ** THAY ĐỔI QUAN TRỌNG: KHÔNG KHÓA CAMERA **
                                component.cameraMode = CameraMode.NONE
                                component.renderMode = RenderMode.COMPASS
                                locationComponent = component
                            }

                            // 2. Cập nhật markers bệnh viện (giữ nguyên)
                            val symbolManager = SymbolManager(view, map, style)
                            // ... (code symbol manager giữ nguyên)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        ManageMapViewLifecycle(mapView = mapView)

        // ** THÊM NÚT "VỊ TRÍ CỦA TÔI" **
        FloatingActionButton(
            onClick = {
                locationComponent?.lastKnownLocation?.let {
                    trackasiaMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            14.0 // Mức zoom khi nhấn nút
                        ),
                        1000 // Animation 1 giây
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Vị trí của tôi")
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    // ** THAY ĐỔI QUAN TRỌNG: LOGIC ZOOM BAN ĐẦU **
    // Chỉ chạy MỘT LẦN khi có quyền và có bản đồ
    LaunchedEffect(hasLocationPermission, trackasiaMap) {
        if (hasLocationPermission && trackasiaMap != null) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    trackasiaMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            14.0
                        ),
                        2000 // Zoom trong 2 giây
                    )
                }
            }
        }
    }

    // Trigger ViewModel tải dữ liệu (giữ nguyên)
    LaunchedEffect(Unit) {
        viewModel.onEvent(MapEvent.OnMapLoaded)
    }
}

@Composable
private fun ManageMapViewLifecycle(mapView: MapView) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mapView) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    return remember {
        MapView(context)
    }
}