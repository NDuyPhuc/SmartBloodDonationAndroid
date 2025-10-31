// feature_map_booking/src/main/java/com/example/feature_map_booking/domain/ui/map/TrackAsiaMapComposable.kt

package com.example.feature_map_booking.domain.ui.map

import com.smartblood.core.R
import android.Manifest
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.feature_map_booking.domain.model.Hospital
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonParser
import com.trackasia.android.camera.CameraUpdateFactory
import com.trackasia.android.geometry.LatLng
import com.trackasia.android.location.LocationComponent
import com.trackasia.android.location.LocationComponentActivationOptions
import com.trackasia.android.location.modes.CameraMode
import com.trackasia.android.location.modes.RenderMode
import com.trackasia.android.maps.MapView
import com.trackasia.android.maps.Style
import com.trackasia.android.plugins.annotation.SymbolManager
import com.trackasia.android.plugins.annotation.SymbolOptions

@SuppressLint("MissingPermission")
@Composable
fun TrackAsiaMapComposable(
    hospitals: List<Hospital>,
    onMarkerClick: (String) -> Unit
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()

    var trackasiaMap by remember { mutableStateOf<com.trackasia.android.maps.TrackAsiaMap?>(null) }
    var symbolManager by remember { mutableStateOf<SymbolManager?>(null) }
    var locationComponent by remember { mutableStateOf<LocationComponent?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.apply {
                    onCreate(null)
                    getMapAsync { map ->
                        trackasiaMap = map
                        // LƯU Ý: Thay "YOUR_API_KEY" bằng API key của bạn
                        val styleUrl = "https://maps.track-asia.com/styles/v1/streets.json?key=52fedb6b306931761836057e5580a05be7"
                        map.setStyle(Style.Builder().fromUri(styleUrl))
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // EFFECT 1: Xử lý vị trí ban đầu và zoom
        LaunchedEffect(trackasiaMap, hasLocationPermission) {
            val map = trackasiaMap ?: return@LaunchedEffect
            if (!hasLocationPermission) return@LaunchedEffect

            map.getStyle { style ->
                if (!style.isFullyLoaded) return@getStyle

                val component = map.locationComponent
                locationComponent = component
                if (!component.isLocationComponentActivated) {
                    component.activateLocationComponent(
                        LocationComponentActivationOptions.builder(context, style).build()
                    )
                }
                component.isLocationComponentEnabled = true
                component.renderMode = RenderMode.COMPASS
                component.cameraMode = CameraMode.NONE

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 14.0),
                            2000
                        )
                    }
                }.addOnFailureListener {
                    Log.d("MapDebug", "Could not get last known location.")
                }
            }
        }

        // EFFECT 2: Xử lý vẽ marker
        LaunchedEffect(trackasiaMap, hospitals) {
            val map = trackasiaMap ?: return@LaunchedEffect

            val CUSTOM_MARKER_ID = "my-marker-icon"
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_map_marker)

            map.getStyle { style ->
                if (!style.isFullyLoaded) return@getStyle

                style.addImage(CUSTOM_MARKER_ID, bitmap)

                if (symbolManager == null) {
                    symbolManager = SymbolManager(mapView, map, style).apply {
                        addClickListener { symbol ->
                            symbol.data?.asJsonObject?.get("hospital_id")?.asString?.let(onMarkerClick)
                            true
                        }
                    }
                    symbolManager?.iconAllowOverlap = true
                    symbolManager?.iconIgnorePlacement = true

                }

                symbolManager?.deleteAll()

                if (hospitals.isNotEmpty()) {
                    val options: List<SymbolOptions> = hospitals.mapNotNull { hospital ->
                        hospital.location?.let { geoPoint ->
                            SymbolOptions()
                                .withLatLng(LatLng(geoPoint.latitude, geoPoint.longitude))
                                .withIconImage(CUSTOM_MARKER_ID)
                                .withIconSize(0.1f)
                                .withIconAnchor("bottom")
                                .withData(JsonParser.parseString("""{"hospital_id": "${hospital.id}"}"""))
                        }
                    }
                    // Dòng lệnh create bây giờ sẽ hoạt động chính xác
                    symbolManager?.create(options)
                }
            }
        }

        // Nút "Vị trí của tôi"
        FloatingActionButton(
            onClick = {
                locationComponent?.lastKnownLocation?.let {
                    trackasiaMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 14.0),
                        1000
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
    }
}

// Hàm hỗ trợ để quản lý vòng đời của MapView
@Composable
fun rememberMapViewWithLifecycle(): MapView {
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
        }
    }
    return mapView
}