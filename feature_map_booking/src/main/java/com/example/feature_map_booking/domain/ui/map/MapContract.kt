package com.example.feature_map_booking.domain.ui.map

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/map/MapContract.kt

import com.example.feature_map_booking.domain.model.Hospital
import com.google.android.gms.maps.model.LatLng

data class MapState(
    val isLoading: Boolean = true,
    val lastKnownLocation: LatLng? = null,
    val hospitals: List<Hospital> = emptyList(),
    val error: String? = null
)

sealed class MapEvent {
    data class OnMapReady(val location: LatLng) : MapEvent()
    data class OnMarkerClick(val hospitalId: String) : MapEvent()
}