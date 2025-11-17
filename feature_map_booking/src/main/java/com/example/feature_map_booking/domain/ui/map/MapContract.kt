package com.example.feature_map_booking.domain.ui.map

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/map/MapContract.kt

import com.trackasia.android.geometry.LatLng
import com.smartblood.core.domain.model.Hospital

data class MapState(
    val isLoading: Boolean = true,
    // SỬA KIỂU DỮ LIỆU Ở ĐÂY
    val lastKnownLocation: LatLng? = null,
    val hospitals: List<Hospital> = emptyList(),
    val error: String? = null
)

sealed class MapEvent {
    object OnMapLoaded : MapEvent()
    data class OnMapReady(val location: LatLng) : MapEvent()
    data class OnMarkerClick(val hospitalId: String) : MapEvent()
}